package com.thesoulless.secretspecplugin.go;

import com.goide.execution.GoRunConfigurationBase;
import com.goide.execution.extension.GoRunConfigurationExtension;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
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
 * Provides UI tab for SecretSpec settings in GoLand run configurations
 * Command modification is handled by SecretSpecExecutionListener
 */
public class SecretSpecGoRunConfigurationExtension extends GoRunConfigurationExtension {
    
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
        SettingsEditor<P> editor = (SettingsEditor<P>) new SecretSpecGoSettingsEditor();
        return editor;
    }
    
    @Override
    protected void patchCommandLine(@NotNull GoRunConfigurationBase<?> configuration, 
                                   @Nullable RunnerSettings runnerSettings, 
                                   @NotNull GeneralCommandLine cmdLine, 
                                   @NotNull String runnerId) throws ExecutionException {
        // Command modification is handled by SecretSpecExecutionListener
        // This method is required by GoRunConfigurationExtension but we don't use it
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