package com.thesoulless.secretspecplugin.go;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.thesoulless.secretspecplugin.api.SecretSpecRunConfigurationExtensionBase;
import com.thesoulless.secretspecplugin.common.SecretSpecRunSettings;
import com.thesoulless.secretspecplugin.common.SecretSpecSettingsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Settings editor for SecretSpec tab in Go run configurations
 */
public class SecretSpecGoSettingsEditor<T extends RunConfigurationBase<?>> extends SettingsEditor<T> {
    
    private final SecretSpecSettingsPanel panel;
    
    public SecretSpecGoSettingsEditor() {
        this.panel = new SecretSpecSettingsPanel();
    }
    
    @Override
    protected void resetEditorFrom(@NotNull T configuration) {
        SecretSpecRunSettings settings = SecretSpecRunConfigurationExtensionBase.getOrCreateSettings(configuration);
        panel.resetFrom(settings);
    }
    
    @Override
    protected void applyEditorTo(@NotNull T configuration) throws ConfigurationException {
        SecretSpecRunSettings settings = SecretSpecRunConfigurationExtensionBase.getOrCreateSettings(configuration);
        panel.applyTo(settings);
        SecretSpecRunConfigurationExtensionBase.setSettings(configuration, settings);
    }
    
    @Override
    protected @NotNull JComponent createEditor() {
        return panel;
    }
}
