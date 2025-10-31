# PhantomVouchers

PhantomVouchers is a Minecraft plugin designed to provide a robust and flexible voucher system for servers. It focuses on simplicity, performance, and extensibility, allowing server administrators to create and manage vouchers with ease. The plugin integrates various features such as custom actions, configurable settings, and a user-friendly admin interface.

## Features

- **Voucher System**: Create vouchers with unique properties and actions.
- **Custom Actions**: Execute commands, play sounds, and more using a flexible action system.
- **Base-64 Support**: Use Base64 encoded textures for custom item appearances.
- **Admin Preview Menu**: View and manage all vouchers through a paginated inventory interface.
- **Confirmation System**: Prevent accidental voucher redemption with a two-step confirmation process.
- **Cooldown Management**: Cache cooldown values for efficient performance.
- **Region & World Support**: Restrict voucher usage based on regions and worlds.
- **Randomized Rewards**: Support for chance-based rewards with infinite structure possibilities.
- **PlaceholderAPI Integration**: Use placeholders for dynamic content.
- **Configuration Management**: Fully configurable via YAML files.

## Installation

1. Download the latest version of PhantomVouchers from the [releases page](#).
2. Place the `.jar` file into your server's `plugins` folder.
3. Restart your server to generate the configuration files.
4. Configure the plugin by editing the YAML files in the `plugins/PhantomVouchers` directory.

## Configuration

PhantomVouchers provides several configuration files to customize its behavior:

- **`settings.yml`**: General plugin settings.
- **`commands.yml`**: Command aliases and permissions.
- **`messages.yml`**: Customizable messages.
- **`vouchers/`**: Folder containing individual voucher files.

### Example Voucher Configuration

```yaml
material: PAPER
display-name: "&6Special Voucher"
lore:
  - "&7Right-click to redeem!"
base64: "<Base64_String>"
redeem-actions:
  - "[RANDOM] 10:[SOUND] BLOCK_BREWING_STAND_BREW;0.5;0.5|[COMMAND] give %player% diamond 1"
  - "[RANDOM] 5:[COMMAND] give %player% emerald 1"
```

## Commands

- `/voucher give <player> <voucher>`: Give a voucher to a player.
- `/voucher reload`: Reload the plugin configuration.
- `/voucher preview`: Open the admin preview menu.

### Permissions

- `phantomvouchers.give`: Access to the `/voucher give` command.
- `phantomvouchers.reload`: Access to the `/voucher reload` command.
- `phantomvouchers.preview`: Access to the `/voucher preview` command.

## Developer Notes

PhantomVouchers is built with extensibility in mind. Developers can:

- Add custom actions by implementing the `IAction` interface.
- Use the `ItemBuilder` utility for creating custom items.
- Leverage the `ActionManager` for managing and executing actions.

## Known Issues

- Spamming right-click during the confirmation window may cause unexpected behavior.
- Base64 textures may override display names if not set in the correct order.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes. Ensure your code follows the existing style and includes proper documentation.

## License

PhantomVouchers is licensed under the MIT License. See the [LICENSE](#) file for details.
