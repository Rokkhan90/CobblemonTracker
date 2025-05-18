package com.rokkhan.trackmon.client;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.rokkhan.trackmon.Config;
import com.rokkhan.trackmon.data.TrackingStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.stream.Collectors;

public class TrackmonScreen extends Screen {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final List<String> allPokemon;
    private List<String> filteredPokemon;
    private final Map<String, TrackmonCheckbox> checkboxes = new HashMap<>();
    private final int checkboxWidth = 150;
    private final int checkboxHeight = 20;
    private final int spacing = 2;

    private int scrollOffset = 0;
    private int maxVisible;
    private EditBox searchBox;

    public TrackmonScreen() {
        super(Component.literal("Trackmon GUI"));

        this.allPokemon = PokemonSpecies.INSTANCE.getSpecies().stream()
                .map(Species::getName)
                .map(String::toLowerCase)
                .filter(name -> Config.excludedPokemon.stream()
                        .map(String::toLowerCase)
                        .noneMatch(excluded -> excluded.equals(name)))
                .map(this::capitalize)
                .sorted()
                .collect(Collectors.toList());

        this.filteredPokemon = new ArrayList<>(allPokemon);
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    @Override
    protected void init() {
        int listTop = 50;
        int listHeight = this.height - 170;
        maxVisible = listHeight / (checkboxHeight + spacing);

        UUID playerUUID = minecraft.player != null ? minecraft.player.getUUID() : null;
        Set<String> trackedSet = playerUUID != null
                ? new HashSet<>(TrackingStorage.getTracked(playerUUID))
                : Set.of();

        searchBox = new EditBox(this.font, this.width / 2 - 75, 20, 150, 20, Component.literal("Suche..."));
        searchBox.setResponder(this::updateFilter);
        this.addRenderableWidget(searchBox);

        for (String name : allPokemon) {
            TrackmonCheckbox checkbox = new TrackmonCheckbox(0, 0, checkboxWidth, checkboxHeight, Component.literal(name), trackedSet.contains(name));
            checkboxes.put(name, checkbox);
        }

        int scrollAreaX = this.width / 2 - checkboxWidth / 2;
        int scrollAreaY = listTop;

        this.addRenderableWidget(Button.builder(Component.literal("↑"), btn -> {
            if (scrollOffset > 0) scrollOffset--;
        }).bounds(scrollAreaX + checkboxWidth + 5, scrollAreaY, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("↓"), btn -> {
            if (scrollOffset < filteredPokemon.size() - maxVisible) scrollOffset++;
        }).bounds(scrollAreaX + checkboxWidth + 5, scrollAreaY + listHeight - 20, 20, 20).build());

        int buttonY = this.height - 80;
        int buttonWidth = 120;

        this.addRenderableWidget(Button.builder(Component.literal("Start Tracking"), btn -> {
            List<String> selected = getSelectedPokemon();
            sendCommand("trackmon", selected);
            if (playerUUID != null) {
                TrackingStorage.setTracked(playerUUID, selected);
            }
        }).bounds(this.width / 2 - buttonWidth - spacing, buttonY, buttonWidth, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Stop Tracking"), btn -> {
            List<String> selected = getSelectedPokemon();
            sendCommand("trackmon stop", selected);
            if (playerUUID != null) {
                List<String> current = new ArrayList<>(TrackingStorage.getTracked(playerUUID));
                current.removeAll(selected);
                TrackingStorage.setTracked(playerUUID, current);
            }
        }).bounds(this.width / 2 + spacing, buttonY, buttonWidth, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Clear"), btn -> {
            sendCommand("trackmon clear", List.of());
            if (playerUUID != null) {
                TrackingStorage.clear(playerUUID);
            }
            checkboxes.values().forEach(cb -> cb.setChecked(false));
        }).bounds(this.width / 2 - buttonWidth - spacing, buttonY + 25, buttonWidth, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Schließen"), btn -> {
            this.onClose();
        }).bounds(this.width / 2 + spacing, buttonY + 25, buttonWidth, 20).build());
    }

    private void updateFilter(String input) {
        scrollOffset = 0;
        filteredPokemon = allPokemon.stream()
                .filter(name -> name.toLowerCase().contains(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> getSelectedPokemon() {
        return filteredPokemon.stream()
                .filter(name -> checkboxes.get(name).isChecked())
                .collect(Collectors.toList());
    }

    private void sendCommand(String baseCommand, List<String> pokemonList) {
        if (minecraft.player == null) return;

        if (pokemonList.isEmpty() && baseCommand.endsWith("clear")) {
            minecraft.player.connection.sendCommand(baseCommand);
        } else {
            for (String name : pokemonList) {
                minecraft.player.connection.sendCommand(baseCommand + " " + name);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int scrollAreaX = this.width / 2 - checkboxWidth / 2;
        int scrollAreaY = 50;
        int scrollAreaWidth = checkboxWidth;
        int scrollAreaHeight = maxVisible * (checkboxHeight + spacing);

        if (mouseX >= scrollAreaX && mouseX <= scrollAreaX + scrollAreaWidth &&
                mouseY >= scrollAreaY && mouseY <= scrollAreaY + scrollAreaHeight) {

            if (verticalAmount > 0 && scrollOffset > 0) {
                scrollOffset--;
            } else if (verticalAmount < 0 && scrollOffset < filteredPokemon.size() - maxVisible) {
                scrollOffset++;
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int scrollAreaX = this.width / 2 - checkboxWidth / 2;
        int scrollAreaY = 50;

        int start = scrollOffset;
        int end = Math.min(start + maxVisible, filteredPokemon.size());

        for (int i = start; i < end; i++) {
            String name = filteredPokemon.get(i);
            TrackmonCheckbox checkbox = checkboxes.get(name);
            int x = scrollAreaX;
            int y = scrollAreaY + (i - start) * (checkboxHeight + spacing);
            if (mouseX >= x && mouseX <= x + checkboxWidth && mouseY >= y && mouseY <= y + checkboxHeight) {
                checkbox.onClick(mouseX, mouseY);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int scrollAreaX = this.width / 2 - checkboxWidth / 2;
        int scrollAreaY = 50;

        int start = scrollOffset;
        int end = Math.min(start + maxVisible, filteredPokemon.size());

        for (int i = start; i < end; i++) {
            String name = filteredPokemon.get(i);
            TrackmonCheckbox checkbox = checkboxes.get(name);
            checkbox.setX(scrollAreaX);
            checkbox.setY(scrollAreaY + (i - start) * (checkboxHeight + spacing));
            checkbox.render(graphics, mouseX, mouseY, partialTick);
        }

        searchBox.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TrackmonScreen());
    }

    public static class ClientEvents {
        @net.neoforged.bus.api.SubscribeEvent
        public static void onKeyInput(net.neoforged.neoforge.client.event.InputEvent.Key event) {
            if (TrackmonKeybinds.OPEN_GUI_KEY.consumeClick()) {
                Minecraft.getInstance().setScreen(new TrackmonScreen());
            }
        }
    }

    public static void registerKeybinds(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) {
        event.register(TrackmonKeybinds.OPEN_GUI_KEY);
    }
}
