# SecretSpec Plugin for GoLand

A GoLand plugin that automatically integrates [SecretSpec](https://secretspec.dev/) with your Go run configurations, allowing you to seamlessly manage secrets during development.

## Features

- 🔒 **Automatic Integration**: Wraps Go run/debug commands with `secretspec run --`
- ⚙️ **Configurable**: Set custom profile and provider options
- 🎯 **Seamless**: Works with existing Go run configurations
- 🚀 **Zero Configuration**: Works out of the box with sensible defaults

## Prerequisites

1. **SecretSpec Installation**: Make sure SecretSpec is installed and available in your PATH:
   ```bash
   curl -sSL https://install.secretspec.dev | sh
   ```

2. **SecretSpec Configuration**: Initialize SecretSpec in your Go project:
   ```bash
   secretspec init
   secretspec config init
   ```

## Installation

### From Plugin Marketplace (Coming Soon)
1. Open GoLand
2. Go to `File` → `Settings` → `Plugins`
3. Search for "SecretSpec Integration"
4. Click `Install`

### From Source
1. Clone this repository:
   ```bash
   git clone https://github.com/thesoulless/secretspec-intellij-plugin.git
   cd secretspecplugin
   ```

2. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

3. Install the plugin:
   - Go to `File` → `Settings` → `Plugins`
   - Click the gear icon → `Install Plugin from Disk...`
   - Select `build/distributions/secretspecplugin-1.0.0.zip`

## Usage

### Basic Usage

Once installed and enabled, the plugin automatically modifies your Go run/debug commands:

**Before:**
```bash
go run main.go
```

**After:**
```bash
secretspec run -- go run main.go
```

### Configuration

Configure SecretSpec settings per run configuration:

1. **Create or Edit a Run Configuration**:
   - Go to `Run` → `Edit Configurations...`
   - Select an existing configuration or create a new one

2. **Configure SecretSpec Settings**:
   - Click on the **"SecretSpec"** tab in the run configuration dialog
   - **Enable SecretSpec for this run configuration**: Toggle SecretSpec integration on/off
   - **Profile**: Optional. Environment profile from secretspec.toml (e.g., `development`, `production`, `default`)
   - **Provider**: Optional. Secret provider backend (e.g., `keyring`, `onepassword`, `dotenv`, `env`, `lastpass`)

3. **Apply and Run**: Click Apply/OK and run your configuration as usual

### Example Configurations

**With Profile and Provider:**
```bash
secretspec run --profile production --provider onepassword -- go run main.go
```

**With Profile Only:**
```bash
secretspec run --profile development -- go run main.go
```

**Basic (no additional options):**
```bash
secretspec run -- go run main.go
```

## How It Works

The plugin integrates with JetBrains IDEs using a dual approach:

1. **Settings UI**: Platform-specific run configuration extensions add a "SecretSpec" tab to run/debug configuration dialogs
2. **Universal Command Modification**: A universal execution listener (`SecretSpecExecutionListener`) intercepts ALL run configurations and modifies commands when SecretSpec is enabled
3. **Per-Configuration Storage**: Settings are stored individually with each run configuration, not globally
4. **Transparent Execution**: Maintains all original IDE functionality while seamlessly injecting SecretSpec

## Project Structure

```
src/main/java/com/thesoulless/secretspecplugin/
└── listener/
    └── SecretSpecExecutionListener.java   # Universal execution listener

modules/
├── core/src/main/java/com/thesoulless/secretspecplugin/
│   ├── api/
│   │   └── SecretSpecRunConfigurationExtensionBase.java  # Base extension class
│   └── common/
│       ├── SecretSpecRunSettings.java    # Settings data model
│       └── SecretSpecSettingsPanel.java  # Shared UI components
└── platform-go/src/main/java/com/thesoulless/secretspecplugin/go/
    ├── SecretSpecGoRunConfigurationExtension.java  # Go-specific extension
    └── SecretSpecGoSettingsEditor.java            # Go settings editor

src/main/resources/META-INF/
├── plugin.xml              # Main plugin descriptor
└── goland-secretspec.xml   # Go-specific configurations
```

## Configuration Examples

### Development Setup
```toml
# secretspec.toml
[project]
name = "my-go-app"
revision = "1.0"

[profiles.development]
DATABASE_URL = { default = "sqlite://./dev.db" }
API_KEY = { description = "Development API key", required = true }
```

**Run Configuration Settings:**
- Enable SecretSpec: ✅ Checked
- Profile: `development`
- Provider: `keyring`

### Production Setup
```toml
[profiles.production]
DATABASE_URL = { description = "Production database URL", required = true }
API_KEY = { description = "Production API key", required = true }
REDIS_URL = { description = "Redis connection string", required = true }
```

**Run Configuration Settings:**
- Enable SecretSpec: ✅ Checked
- Profile: `production` 
- Provider: `onepassword`

## Troubleshooting

### SecretSpec Command Not Found
**Error**: `secretspec: command not found`

**Solution**: Make sure SecretSpec is installed and available in your PATH:
```bash
# Install SecretSpec
curl -sSL https://install.secretspec.dev | sh

# Verify installation
which secretspec
secretspec --version
```

### Plugin Not Working
1. **Check Plugin Status**: Go to `Settings` → `Plugins` and ensure "SecretSpec Integration" is enabled
2. **Verify Configuration**: Check your run configuration's "SecretSpec" tab and ensure it's enabled
3. **Check Logs**: View `Help` → `Show Log in Finder/Explorer` for any error messages

### Invalid Profile/Provider
**Error**: Command execution fails with invalid profile or provider

**Solution**: 
1. Verify your `secretspec.toml` configuration
2. Run `secretspec check` to validate your setup
3. Ensure the profile/provider specified in your run configuration's SecretSpec tab exists

## Development

### Building from Source

1. **Prerequisites:**
   - JDK 17 or higher
   - IntelliJ IDEA or GoLand for development

2. **Setup:**
   ```bash
   git clone https://github.com/thesoulless/secretspec-intellij-plugin.git
   cd secretspecplugin
   ./gradlew build
   ```

3. **Running in Development:**
   ```bash
   ./gradlew runIde
   ```

4. **Building Distribution:**
   ```bash
   ./gradlew buildPlugin
   ```

### Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is released into the public domain under The Unlicense - see the [LICENSE](LICENSE) file for details. You are free to do whatever you want with this code.

## Support

- **Issues**: Report bugs and request features on [GitHub Issues](https://github.com/thesoulless/secretspec-intellij-plugin/issues)
- **Documentation**: Visit [SecretSpec Documentation](https://secretspec.dev/)
