package com.thesoulless.secretspecplugin.api;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configuration.RunConfigurationExtensionBase;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import com.thesoulless.secretspecplugin.common.SecretSpecRunSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for platform-specific SecretSpec run configuration extensions
 * This provides the common functionality that all platforms share
 */
public abstract class SecretSpecRunConfigurationExtensionBase<T extends RunConfigurationBase<?>> 
        extends RunConfigurationExtensionBase<T> {
    
    private static final Logger LOG = Logger.getInstance(SecretSpecRunConfigurationExtensionBase.class);
    protected static final Key<SecretSpecRunSettings> SETTINGS_KEY = Key.create("secretspec.settings");
    private static final String SETTINGS_TAG = "secretspec-settings";
    
    @Override
    protected @NotNull String getEditorTitle() {
        return "SecretSpec";
    }
    
    @Override
    protected void readExternal(@NotNull T runConfiguration, @NotNull Element element)
            throws InvalidDataException {
        Element settingsElement = element.getChild(SETTINGS_TAG);
        if (settingsElement != null) {
            SecretSpecRunSettings settings = getOrCreateSettings(runConfiguration);
            settings.readExternal(settingsElement);
        }
    }
    
    @Override
    protected void writeExternal(@NotNull T runConfiguration, @NotNull Element element)
            throws WriteExternalException {
        SecretSpecRunSettings settings = getSettings(runConfiguration);
        if (settings != null) {
            Element settingsElement = new Element(SETTINGS_TAG);
            settings.writeExternal(settingsElement);
            element.addContent(settingsElement);
        }
    }
    
    @Override
    protected @NotNull String getSerializationId() {
        return "secretspec-extension";
    }
    
    /**
     * Modify the command line to add SecretSpec prefix
     */
    protected void modifyCommandLine(@NotNull GeneralCommandLine cmdLine, @NotNull SecretSpecRunSettings settings) {
        // Store original command
        String originalExePath = cmdLine.getExePath();
        var originalParameters = cmdLine.getParametersList().getList().toArray(new String[0]);
        
        // Replace with SecretSpec command
        cmdLine.setExePath("secretspec");
        cmdLine.getParametersList().clearAll();
        
        // Add SecretSpec parameters
        cmdLine.addParameter("run");
        
        if (settings.hasProfile()) {
            cmdLine.addParameter("--profile");
            cmdLine.addParameter(settings.getProfile());
        }
        
        if (settings.hasProvider()) {
            cmdLine.addParameter("--provider");
            cmdLine.addParameter(settings.getProvider());
        }
        
        cmdLine.addParameter("--");
        
        // Add original command
        cmdLine.addParameter(originalExePath);
        cmdLine.addParameters(originalParameters);
        
        LOG.info("Modified command line with SecretSpec: " + cmdLine.getCommandLineString());
    }
    
    /**
     * Get SecretSpec settings for a run configuration
     */
    public static @Nullable SecretSpecRunSettings getSettings(@NotNull RunConfigurationBase<?> configuration) {
        return configuration.getCopyableUserData(SETTINGS_KEY);
    }
    
    /**
     * Get or create SecretSpec settings for a run configuration
     */
    public static @NotNull SecretSpecRunSettings getOrCreateSettings(@NotNull RunConfigurationBase<?> configuration) {
        SecretSpecRunSettings settings = getSettings(configuration);
        if (settings == null) {
            settings = new SecretSpecRunSettings();
            configuration.putCopyableUserData(SETTINGS_KEY, settings);
        }
        return settings;
    }
    
    /**
     * Set SecretSpec settings for a run configuration
     */
    public static void setSettings(@NotNull RunConfigurationBase<?> configuration, @NotNull SecretSpecRunSettings settings) {
        configuration.putCopyableUserData(SETTINGS_KEY, settings);
    }
}
