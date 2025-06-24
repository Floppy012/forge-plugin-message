# forge-plugin-message

This mod adds the `/pluginmsg` command that allows to send a plugin message to the `bungeecord:main` plugin command
channel.

## Supported Proxies

* BungeeCord
* Velocity (via [BungeeCord channel compatibility](https://docs.papermc.io/velocity/dev/plugin-messaging/#bungeecord-channel-compatibility))

## Usage

With OP or in a command block (or datapack function) run the `/pluginmsg <player> <messageType> [args...]` command to send a 
plugin message.

The `<player>` is required because plugin messages piggyback on player connections. It is the player whose connection
will be used to send the message.

See [this document](https://docs.papermc.io/paper/dev/plugin-messaging/#plugin-message-types) for the supported message
types by velocity and what args to provide (if any).
