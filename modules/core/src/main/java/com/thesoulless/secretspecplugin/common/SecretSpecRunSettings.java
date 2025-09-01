package com.thesoulless.secretspecplugin.common;

import com.intellij.openapi.util.JDOMExternalizerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Settings for SecretSpec run configuration - shared across all platforms
 */
public class SecretSpecRunSettings {
    
    private boolean enabled = false;
    private String profile = "";
    private String provider = "";
    
    private static final String ENABLED_FIELD = "ENABLED";
    private static final String PROFILE_FIELD = "PROFILE";
    private static final String PROVIDER_FIELD = "PROVIDER";
    
    public SecretSpecRunSettings() {
        // Default constructor
    }
    
    public SecretSpecRunSettings(boolean enabled, String profile, String provider) {
        this.enabled = enabled;
        this.profile = profile != null ? profile : "";
        this.provider = provider != null ? provider : "";
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getProfile() {
        return profile != null ? profile : "";
    }
    
    public void setProfile(String profile) {
        this.profile = profile;
    }
    
    public String getProvider() {
        return provider != null ? provider : "";
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public boolean hasProfile() {
        return profile != null && !profile.trim().isEmpty();
    }
    
    public boolean hasProvider() {
        return provider != null && !provider.trim().isEmpty();
    }
    
    /**
     * Build the secretspec command prefix based on current settings
     */
    public String buildCommandPrefix() {
        StringBuilder sb = new StringBuilder("secretspec run");
        
        if (hasProfile()) {
            sb.append(" --profile ").append(profile.trim());
        }
        
        if (hasProvider()) {
            sb.append(" --provider ").append(provider.trim());
        }
        
        sb.append(" --");
        
        return sb.toString();
    }
    
    /**
     * Read settings from XML element
     */
    public void readExternal(@NotNull Element element) {
        String enabledStr = JDOMExternalizerUtil.readField(element, ENABLED_FIELD);
        this.enabled = Boolean.parseBoolean(enabledStr);
        this.profile = JDOMExternalizerUtil.readField(element, PROFILE_FIELD, "");
        this.provider = JDOMExternalizerUtil.readField(element, PROVIDER_FIELD, "");
    }
    
    /**
     * Write settings to XML element
     */
    public void writeExternal(@NotNull Element element) {
        JDOMExternalizerUtil.writeField(element, ENABLED_FIELD, String.valueOf(enabled));
        JDOMExternalizerUtil.writeField(element, PROFILE_FIELD, profile);
        JDOMExternalizerUtil.writeField(element, PROVIDER_FIELD, provider);
    }
    
    @Override
    public String toString() {
        return "SecretSpecRunSettings{" +
                "enabled=" + enabled +
                ", profile='" + profile + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}
