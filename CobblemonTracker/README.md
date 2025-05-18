# Cobblemon Trackmon

Cobblemon Trackmon is an addon for the Cobblemon mod that allows players to track specific Cobblemon using chat commands and a GUI. This mod is designed to enhance your gameplay by notifying you when specific Cobblemon spawn nearby.

## Features

- Track specific Cobblemon using chat commands.
- Add all starter Cobblemon to the tracking list with a single command.
- Remove specific Cobblemon from the tracking list.
- Clear the entire tracking list.
- GUI for managing tracked Cobblemon with search and selection features.
- Cobblemon Trackmon is designed to be compatible with other Cobblemon addons. It interacts with Cobblemon entities in a non-intrusive way, ensuring that it can be used alongside other
  mods that extend or modify Cobblemon functionality.

## Commands

- `/trackmon track <pokemon>`: Add a specific Cobblemon to the tracking list.
- `/trackmon starter`: Add all starter Cobblemon to the tracking list.
- `/trackmon stop <pokemon>`: Remove a specific Cobblemon from the tracking list.
- `/trackmon clear`: Clear the entire tracking list.
- `/trackmon list`: Shows the tracking list.

## GUI Usage

- Open the GUI with the `G` key.
- View the tracking list and scroll through it using the mouse wheel.
- Use the search field to find Cobblemon by name.
- Select multiple Cobblemon and click "Start Tracking" to add them to the tracking list.
- Use the "Stop Tracking" button to stop tracking selected Cobblemon.
- Use the "Clear Tracking" button to clear the entire tracking list.

## Requirements

- Minecraft 1.21.1
- NeoForge 1.21
- Cobblemon mod

## Installation

1. Ensure you have Minecraft 1.21.1 and NeoForge 1.21 installed.
2. Download and install the Cobblemon mod.
3. Download the Cobblemon Trackmon mod.
4. Place the Cobblemon Trackmon mod `.jar` file into your Minecraft `mods` folder.

## Configuration

The `trackmon-common.toml` file is used to configure the behavior of the Cobblemon Trackmon mod. This file is located in the `config` folder of your Minecraft directory, alongside the `mods` folder.

### Configuration Options

- `trackingRadius`: Radius in blocks for tracking Cobblemon spawns. Default is 100. Range is 1 to 1000.
- `starterPokemon`: List of starter Cobblemon names to track when using `/trackmon starter`.  
  If this list is empty, a default internal list will be used.  
  Example: `["charmander", "bulbasaur", "squirtle"]`
- `enableTrackingLog`: Whether to log tracked Cobblemon spawns to the console. Default is `true`.
- `excludedPokemon`: List of Cobblemon names to exclude from the GUI.  
  Example: `["rattata", "pidgey"]`

### Example `trackmon-common.toml`

```toml
# Radius in blocks for tracking Pokémon spawns
# Default: 100
# Range: 1 ~ 1000
trackingRadius = 100

# List of starter Pokémon names
# If empty, a default internal list will be used
# Example: ["charmander", "bulbasaur", "squirtle"]
starterPokemon = []

# Log tracked Pokémon spawns to console
enableTrackingLog = false
# List of Pokémon names to exclude from the GUI
# Example: ["rattata", "pidgey"]
excludedPokemon = []
