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
   git clone https://github.com/thesoulless/secretspecplugin.git
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

Configure the plugin through GoLand settings:

1. Go to `File` → `Settings` → `Tools` → `SecretSpec Integration`
2. Configure the following options:
   - **Enable SecretSpec Integration**: Toggle the plugin on/off
   - **Profile**: Optional. Specify which SecretSpec profile to use (e.g., `development`, `production`)
   - **Provider**: Optional. Specify which provider backend to use (e.g., `keyring`, `dotenv`, `env`, `onepassword`, `lastpass`)

### Example Configurations

**With Profile and Provider:**
```bash
secretspec run --profile production --provider dotenv -- go run main.go
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

The plugin integrates with GoLand's execution system by:

1. **Intercepting Go Run Configurations**: Uses a custom `ProgramRunner` to detect Go run configurations
2. **Command Modification**: Wraps the original Go command with the SecretSpec prefix
3. **Settings Integration**: Applies user-configured profile and provider settings
4. **Transparent Execution**: Maintains all original functionality while adding SecretSpec support

## Project Structure

```
src/main/java/com/thesoulless/secretspecplugin/
├── settings/
│   ├── SecretSpecSettings.java          # Settings storage and state management
│   ├── SecretSpecSettingsConfigurable.java # Settings page configuration
│   └── SecretSpecSettingsComponent.java     # Settings UI components
├── runner/
│   ├── SecretSpecGoRunner.java             # Main program runner
│   └── SecretSpecGoRunProfileState.java   # Command line modification logic
└── listener/
    └── SecretSpecExecutionListener.java   # Execution event logging
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

**Plugin Settings:**
- Profile: `development`
- Provider: `keyring`

### Production Setup
```toml
[profiles.production]
DATABASE_URL = { description = "Production database URL", required = true }
API_KEY = { description = "Production API key", required = true }
REDIS_URL = { description = "Redis connection string", required = true }
```

**Plugin Settings:**
- Profile: `production` 
- Provider: `dotenv`

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
2. **Verify Settings**: Go to `Settings` → `Tools` → `SecretSpec Integration` and ensure the plugin is enabled
3. **Check Logs**: View `Help` → `Show Log in Finder/Explorer` for any error messages

### Invalid Profile/Provider
**Error**: Command execution fails with invalid profile or provider

**Solution**: 
1. Verify your `secretspec.toml` configuration
2. Run `secretspec check` to validate your setup
3. Ensure the profile/provider specified in plugin settings exists

## Development

### Building from Source

1. **Prerequisites:**
   - JDK 11 or higher
   - IntelliJ IDEA Ultimate or GoLand for development

2. **Setup:**
   ```bash
   git clone https://github.com/thesoulless/secretspecplugin.git
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

- **Issues**: Report bugs and request features on [GitHub Issues](https://github.com/thesoulless/secretspecplugin/issues)
- **Documentation**: Visit [SecretSpec Documentation](https://secretspec.dev/)
- **Community**: Join discussions on [Discord](https://discord.gg/secretspec)

## Changelog

### v1.0.0 (Initial Release)
- ✅ Automatic Go command wrapping with SecretSpec
- ✅ Configurable profile and provider settings
- ✅ Settings UI integration
- ✅ GoLand compatibility (2023.1+)
