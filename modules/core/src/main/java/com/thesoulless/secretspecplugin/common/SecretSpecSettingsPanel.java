package com.thesoulless.secretspecplugin.common;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * UI panel for the SecretSpec tab in run configuration dialogs
 * Shared across all platform implementations
 */
public class SecretSpecSettingsPanel extends JPanel {
    
    private final JBCheckBox enabledCheckBox = new JBCheckBox("Enable SecretSpec for this run configuration");
    private final JBTextField profileField = new JBTextField();
    private final JBTextField providerField = new JBTextField();
    
    public SecretSpecSettingsPanel() {
        super(new BorderLayout());
        setupUI();
    }
    
    private void setupUI() {
        setBorder(JBUI.Borders.empty(10));
        
        // Header
        JLabel headerLabel = new JLabel("<html><b>SecretSpec Configuration</b><br/>" +
                "SecretSpec injects secrets from your secretspec.toml configuration into your application at runtime.</html>");
        add(headerLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(JBUI.Borders.empty(15, 0, 0, 0));
        
        // Enable checkbox at the top
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        enablePanel.add(enabledCheckBox);
        contentPanel.add(enablePanel, BorderLayout.NORTH);
        
        // Settings form
        JPanel settingsPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Profile:"), createProfilePanel(), 1, false)
                .addLabeledComponent(new JBLabel("Provider:"), createProviderPanel(), 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        
        contentPanel.add(settingsPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        
        // Example panel at bottom
        JPanel examplePanel = createExamplePanel();
        add(examplePanel, BorderLayout.SOUTH);
        
        setupEventListeners();
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(profileField, BorderLayout.CENTER);
        
        JLabel helpLabel = new JLabel("<html><small><i>Optional. Environment profile from secretspec.toml (e.g., development, production, default)</i></small></html>");
        helpLabel.setForeground(Color.GRAY);
        panel.add(helpLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProviderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(providerField, BorderLayout.CENTER);
        
        JLabel helpLabel = new JLabel("<html><small><i>Optional. Secret provider backend (keyring, onepassword, dotenv, env, lastpass)</i></small></html>");
        helpLabel.setForeground(Color.GRAY);
        panel.add(helpLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createExamplePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.customLine(Color.LIGHT_GRAY, 1, 0, 0, 0));
        panel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        
        JLabel titleLabel = new JLabel("<html><b>Example commands:</b></html>");
        JTextArea exampleArea = new JTextArea(4, 0);
        exampleArea.setEditable(false);
        exampleArea.setOpaque(false);
        exampleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        exampleArea.setText(
                "Basic: secretspec run -- go run main.go\n" +
                "With profile: secretspec run --profile development -- go run main.go\n" +
                "With provider: secretspec run --provider keyring -- go run main.go\n" +
                "Full example: secretspec run --profile production --provider onepassword -- go run main.go"
        );
        
        JPanel exampleContent = new JPanel(new BorderLayout());
        exampleContent.setBorder(JBUI.Borders.empty(5));
        exampleContent.add(titleLabel, BorderLayout.NORTH);
        exampleContent.add(exampleArea, BorderLayout.CENTER);
        
        panel.add(exampleContent, BorderLayout.CENTER);
        return panel;
    }
    
    private void setupEventListeners() {
        enabledCheckBox.addActionListener(e -> {
            boolean enabled = enabledCheckBox.isSelected();
            profileField.setEnabled(enabled);
            providerField.setEnabled(enabled);
        });
    }
    
    /**
     * Load settings from SecretSpecRunSettings into the UI
     */
    public void resetFrom(@NotNull SecretSpecRunSettings settings) {
        enabledCheckBox.setSelected(settings.isEnabled());
        profileField.setText(settings.getProfile());
        providerField.setText(settings.getProvider());
        
        // Update field states
        boolean enabled = settings.isEnabled();
        profileField.setEnabled(enabled);
        providerField.setEnabled(enabled);
    }
    
    /**
     * Save UI values to SecretSpecRunSettings
     */
    public void applyTo(@NotNull SecretSpecRunSettings settings) {
        settings.setEnabled(enabledCheckBox.isSelected());
        settings.setProfile(profileField.getText().trim());
        settings.setProvider(providerField.getText().trim());
    }
}
