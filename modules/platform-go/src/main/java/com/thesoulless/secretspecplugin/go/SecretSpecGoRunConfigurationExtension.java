package com.thesoulless.secretspecplugin.go;

import com.goide.execution.GoRunConfigurationBase;
import com.goide.execution.extension.GoRunConfigurationExtension;
import com.goide.execution.GoBuildingRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.thesoulless.secretspecplugin.api.SecretSpecRunConfigurationExtensionBase;
import com.thesoulless.secretspecplugin.common.SecretSpecRunSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Go-specific SecretSpec run configuration extension
 * 
 * NOTE: This class extends GoRunConfigurationExtension which has a deprecated patchCommandLine method.
 * We continue to use this approach because:
 * 1. It's the only way to properly integrate with GoLand's run configuration system
 * 2. The alternative RunConfigurationExtensionBase doesn't provide Go-specific integration
 * 3. No clear modern replacement exists as of IntelliJ Platform 2023.1
 * 
 * This should be revisited when JetBrains provides a clear migration path.
 */
public class SecretSpecGoRunConfigurationExtension extends GoRunConfigurationExtension {
    
    private static final Logger LOG = Logger.getInstance(SecretSpecGoRunConfigurationExtension.class);
    private static final String SETTINGS_TAG = "secretspec-settings";
    
    @Override
    protected @NotNull String getEditorTitle() {
        return "SecretSpec";
    }
    
    @Override
    public boolean isApplicableFor(@NotNull GoRunConfigurationBase<?> configuration) {
        return true;
    }
    
    @Override
    public boolean isEnabledFor(@NotNull GoRunConfigurationBase<?> configuration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
    
    @Override
    public <P extends GoRunConfigurationBase<?>> @Nullable SettingsEditor<P> createEditor(@NotNull P configuration) {
        @SuppressWarnings("unchecked")
        SettingsEditor<P> editor = (SettingsEditor<P>) new SecretSpecGoSettingsEditor<P>();
        return editor;
    }
    
    @Override
    @SuppressWarnings("deprecation") // Using deprecated API until clear replacement is available
    protected void patchCommandLine(@NotNull GoRunConfigurationBase<?> configuration,
                                   @Nullable RunnerSettings runnerSettings,
                                   @NotNull GeneralCommandLine cmdLine,
                                   @NotNull String runnerId) throws ExecutionException {
        
        SecretSpecRunSettings settings = SecretSpecRunConfigurationExtensionBase.getSettings(configuration);
        if (settings == null || !settings.isEnabled()) {
            return; // SecretSpec not enabled for this configuration
        }
        
        LOG.info("SecretSpec enabled - runnerId: " + runnerId + ", configuration: " + configuration.getName());
        
        // Check if this is a debug execution
        boolean isDebugExecution = "Debug".equals(runnerId) || runnerId.contains("Debug");
        
        if (isDebugExecution) {
            // For debugging, inject SecretSpec environment variables instead of wrapping command
            // This allows the debugger to attach to the actual Go process
            LOG.info("Debug execution detected - injecting environment variables instead of command wrapping");
            injectSecretSpecEnvironment(cmdLine, settings, configuration);
        } else {
            // For run execution, wrap with SecretSpec
            LOG.info("Run execution detected - wrapping command with SecretSpec");
            modifyCommandLine(cmdLine, settings, configuration);
        }
    }
    
    /**
     * Inject SecretSpec environment variables for debug execution
     */
    private void injectSecretSpecEnvironment(@NotNull GeneralCommandLine cmdLine, 
                                           @NotNull SecretSpecRunSettings settings,
                                           @NotNull GoRunConfigurationBase<?> configuration) {
        // Add SECRETSPEC_PROFILE environment variable if profile is set
        if (settings.hasProfile()) {
            cmdLine.getEnvironment().put("SECRETSPEC_PROFILE", settings.getProfile());
        }
        
        // Add SECRETSPEC_PROVIDER environment variable if provider is set  
        if (settings.hasProvider()) {
            cmdLine.getEnvironment().put("SECRETSPEC_PROVIDER", settings.getProvider());
        }
        
        // Set flag to indicate SecretSpec should be used
        cmdLine.getEnvironment().put("SECRETSPEC_ENABLED", "true");
        
        LOG.info("Injected SecretSpec environment variables for debug execution: " + 
                "SECRETSPEC_ENABLED=true" +
                (settings.hasProfile() ? ", SECRETSPEC_PROFILE=" + settings.getProfile() : "") +
                (settings.hasProvider() ? ", SECRETSPEC_PROVIDER=" + settings.getProvider() : ""));
    }
    
    /**
     * Modify the command line to add SecretSpec prefix
     */
    private void modifyCommandLine(@NotNull GeneralCommandLine cmdLine, @NotNull SecretSpecRunSettings settings, @NotNull GoRunConfigurationBase<?> configuration) {
        // Store original command details
        String originalExePath = cmdLine.getExePath();
        var originalParameters = cmdLine.getParametersList().getList().toArray(new String[0]);
        
        // Get working directory from the Go configuration itself (not the command line)
        String workingDirectory = getWorkingDirectoryFromConfiguration(configuration);
        if (workingDirectory == null) {
            // Fallback to command line working directory
            workingDirectory = cmdLine.getWorkDirectory() != null ? 
                cmdLine.getWorkDirectory().getAbsolutePath() : System.getProperty("user.dir");
        }
        
        // Log original command details for debugging
        LOG.info("=== SecretSpec Command Modification Debug ===");
        LOG.info("Original exe path: " + originalExePath);
        LOG.info("Original parameters: " + String.join(" ", originalParameters));
        LOG.info("Working directory: " + workingDirectory);
        
        // Check for secretspec.toml in the working directory
        java.io.File secretSpecFile = new java.io.File(workingDirectory, "secretspec.toml");
        LOG.info("Looking for secretspec.toml at: " + secretSpecFile.getAbsolutePath());
        LOG.info("secretspec.toml exists: " + secretSpecFile.exists());
        
        if (!secretSpecFile.exists()) {
            LOG.warn("WARNING: secretspec.toml not found in working directory: " + workingDirectory);
            LOG.warn("SecretSpec may fail to load configuration. Make sure secretspec.toml exists in the working directory.");
        }
        
        // Replace with SecretSpec command (keeping the same working directory)
        cmdLine.setExePath("secretspec");
        cmdLine.getParametersList().clearAll();
        
        // Add SecretSpec parameters
        cmdLine.addParameter("run");
        
        if (settings.hasProfile()) {
            cmdLine.addParameter("--profile");
            cmdLine.addParameter(settings.getProfile());
            LOG.info("Using profile: " + settings.getProfile());
        }
        
        if (settings.hasProvider()) {
            cmdLine.addParameter("--provider");
            cmdLine.addParameter(settings.getProvider());
            LOG.info("Using provider: " + settings.getProvider());
        }
        
        cmdLine.addParameter("--");
        
        // Add original command
        cmdLine.addParameter(originalExePath);
        cmdLine.addParameters(originalParameters);
        
        // Set the correct working directory (from the Go configuration)
        if (workingDirectory != null) {
            cmdLine.setWorkDirectory(new java.io.File(workingDirectory));
        }
        
        LOG.info("Final command: " + cmdLine.getCommandLineString());
        LOG.info("Final working directory: " + (cmdLine.getWorkDirectory() != null ? 
            cmdLine.getWorkDirectory().getAbsolutePath() : "null"));
        LOG.info("=== End Debug Info ===");
    }
    
    /**
     * Get working directory from the Go build configuration
     * This is more reliable than getting it from the command line
     */
    private String getWorkingDirectoryFromConfiguration(@NotNull GoRunConfigurationBase<?> configuration) {
        try {
            // Check if this is a GoBuildingRunConfiguration (most common case)
            if (configuration instanceof GoBuildingRunConfiguration) {
                GoBuildingRunConfiguration goBuildConfig = (GoBuildingRunConfiguration) configuration;
                String workingDir = goBuildConfig.getWorkingDirectory();
                LOG.info("Got working directory from GoBuildingRunConfiguration: " + workingDir);
                return workingDir;
            }
            
            // For other Go configuration types, log what we found
            LOG.info("Configuration type: " + configuration.getClass().getSimpleName());
            LOG.info("Unable to extract working directory from configuration type: " + configuration.getClass().getName());
            
        } catch (Exception e) {
            LOG.warn("Error getting working directory from configuration: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    @Override
    protected void readExternal(@NotNull GoRunConfigurationBase<?> runConfiguration, @NotNull Element element)
            throws InvalidDataException {
        Element settingsElement = element.getChild(SETTINGS_TAG);
        if (settingsElement != null) {
            SecretSpecRunSettings settings = SecretSpecRunConfigurationExtensionBase.getOrCreateSettings(runConfiguration);
            settings.readExternal(settingsElement);
        }
    }
    
    @Override
    protected void writeExternal(@NotNull GoRunConfigurationBase<?> runConfiguration, @NotNull Element element)
            throws WriteExternalException {
        SecretSpecRunSettings settings = SecretSpecRunConfigurationExtensionBase.getSettings(runConfiguration);
        if (settings != null) {
            Element settingsElement = new Element(SETTINGS_TAG);
            settings.writeExternal(settingsElement);
            element.addContent(settingsElement);
        }
    }
    
    @Override
    protected @NotNull String getSerializationId() {
        return "secretspec-go-extension";
    }
}