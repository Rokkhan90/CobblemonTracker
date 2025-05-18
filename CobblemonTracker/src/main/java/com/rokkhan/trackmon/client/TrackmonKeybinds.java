package com.rokkhan.trackmon.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class TrackmonKeybinds {

    // Taste zum Öffnen des Trackmon-GUIs (standardmäßig "G")
    public static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.trackmon.open_gui",       // Übersetzungsschlüssel (für Sprachdateien)
            GLFW.GLFW_KEY_G,               // Standardtaste
            "key.categories.misc"          // Kategorie im Steuerungsmenü
    );
}
