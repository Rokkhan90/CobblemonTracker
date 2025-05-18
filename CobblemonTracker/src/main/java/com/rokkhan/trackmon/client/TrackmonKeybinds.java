package com.rokkhan.trackmon.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class TrackmonKeybinds {

    // Keybinding to open the Trackmon GUI (default: "G")
    public static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.trackmon.open_gui",       // Translation key for localization files
            GLFW.GLFW_KEY_G,               // Default key: G
            "key.categories.misc"          // Category shown in the controls menu
    );
}
