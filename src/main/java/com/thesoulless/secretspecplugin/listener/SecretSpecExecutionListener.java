package com.thesoulless.secretspecplugin.listener;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.thesoulless.secretspecplugin.api.SecretSpecRunConfigurationExtensionBase;
import com.thesoulless.secretspecplugin.common.SecretSpecRunSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Execution listener that logs when applications are executed with SecretSpec
 * Now works with the tab-based SecretSpec configuration
 */
public class SecretSpecExecutionListener implements ExecutionListener {
    
    private static final Logger LOG = Logger.getInstance(SecretSpecExecutionListener.class);
    
    @Override
    public void processStartScheduled(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        SecretSpecRunSettings settings = getSecretSpecSettings(env);
        if (settings != null && settings.isEnabled()) {
            String configType = getConfigurationType(env);
            LOG.info("SecretSpec-enabled execution scheduled: " + env.getRunProfile().getName() + " (" + configType + ")");
        }
    }
    
    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        SecretSpecRunSettings settings = getSecretSpecSettings(env);
        if (settings != null && settings.isEnabled()) {
            String commandPrefix = settings.buildCommandPrefix();
            String configType = getConfigurationType(env);
            LOG.info("Process started with SecretSpec integration: " + commandPrefix + " (type: " + configType + ")");
            
            // Log the configuration for debugging
            Project project = env.getProject();
            if (project != null) {
                LOG.info("Project: " + project.getName() + 
                        ", Profile: " + settings.getProfile() + 
                        ", Provider: " + settings.getProvider());
            }
        }
    }
    
    @Override
    public void processTerminated(@NotNull String executorId, 
                                 @NotNull ExecutionEnvironment env, 
                                 @NotNull ProcessHandler handler, 
                                 int exitCode) {
        SecretSpecRunSettings settings = getSecretSpecSettings(env);
        if (settings != null && settings.isEnabled()) {
            String configType = getConfigurationType(env);
            LOG.info("SecretSpec-enabled process terminated with exit code: " + exitCode + " (type: " + configType + ")");
        }
    }
    
    private String getConfigurationType(@NotNull ExecutionEnvironment env) {
        RunProfile profile = env.getRunProfile();
        if (profile == null) {
            return "Unknown";
        }
        
        String className = profile.getClass().getSimpleName();
        
        // Check for Go configuration safely (without importing Go-specific classes)
        if (className.contains("Go") || profile.getClass().getName().contains("goide")) {
            return "Go";
        }
        
        // Other common configuration types
        if (className.contains("Application")) {
            return "Application";
        } else if (className.contains("Jar")) {
            return "JAR Application";
        } else if (className.contains("Remote")) {
            return "Remote";
        } else if (className.contains("Test")) {
            return "Test";
        } else {
            return className;
        }
    }
    
    private SecretSpecRunSettings getSecretSpecSettings(@NotNull ExecutionEnvironment env) {
        RunProfile profile = env.getRunProfile();
        if (profile instanceof RunConfigurationBase) {
            return SecretSpecRunConfigurationExtensionBase.getSettings((RunConfigurationBase<?>) profile);
        }
        return null;
    }
}
