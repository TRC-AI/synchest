# Synced Chests

Chests that share the same custom name share one inventory. Every group of same-named chests is
backed by a single store on the server: the same in **all dimensions**, and kept permanently.

## Naming a chest syncs it

A single chest with a custom name becomes *synced*: its contents belong to the name, not to the
block. Every synced chest with exactly the same name shows and shares that one inventory. A chest
without a custom name stays an ordinary chest.

| Method   | How                                                                                                                                                                                       |
|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Name tag | Sneak + right-click a **renamed** name tag onto a **single, empty** chest. The chest<br/>takes the tag's name and starts syncing; one name tag is used up. An unrenamed tag does nothing. |
| Anvil    | Rename a chest item in an anvil and place it. It syncs right away.                                                                                                                        |

The name tag only works on an empty chest, because naming a full one would replace its contents
with the group's inventory.

- **Exact names only:** two chests sync only when their names are exactly equal. Upper and lower
  case, spaces and text formatting all count.
- **Breaking keeps the items:** the contents of a synced chest do not drop, they stay with the
  name. The chest drops as a named chest item, so placing it anywhere links it to the same
  inventory again.
- **No duplication:** all chests of a name use the very same inventory, so an item taken from one
  is gone from all of them. Hoppers and comparators see the shared contents too.

## Double chests are never synced

- **Placing a chest next to a named chest** makes a normal, unsynced double chest. The named
  chest's items stay in its shared inventory and are not part of the double chest. It does not
  matter which side is named.
- **Breaking one half of such a double chest** drops all items of the double chest at the broken
  spot. The remaining half becomes a single chest again and, if it is named, syncs again.

Ordinary unnamed double chests behave like vanilla.

## Settings

In the TRC settings screen (press **U**, or the TRC button in the main menu) under
**Synced Chests**, or with `/trc`. The setting name is available in English, German and Pirate
Speak.

| Setting           | Id                  | Default   | Effect                        |
|-------------------|---------------------|-----------|-------------------------------|
| Sync named chests | `sync_named_chests` | on        | Turns the whole mod on or off |

While it is off, named chests behave like ordinary chests: their contents come from the block
again, breaking one drops them, and name tags no longer link chests. Nothing is deleted: the
shared inventories stay in the save and come back when the setting is turned on again. The change
applies immediately, without reloading the world.

Example: `/trc synchest sync_named_chests false`

## Requirements

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [TRC Core & Libs](https://modrinth.com/mod/trc-core) 1.2 or newer
- Java 25

There is one file per Minecraft version. Install the mod on the server, or in your game for
singleplayer. Players joining a server do not need it.

## License

MIT, see [LICENSE](LICENSE).