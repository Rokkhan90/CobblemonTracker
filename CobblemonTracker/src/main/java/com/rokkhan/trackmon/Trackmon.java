package com.rokkhan.trackmon;

import com.mojang.logging.LogUtils;
import com.rokkhan.trackmon.client.TrackmonScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * Main mod class for Trackmon.
 * This class is automatically loaded by NeoForge when the mod is initialized.
 */
@Mod(Trackmon.MODID)
public class Trackmon {

    // The mod ID used in mod metadata and resource locations
    public static final String MODID = "trackmon";

    // Logger for debug/info output
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Constructor called during mod loading.
     * Registers setup methods and event listeners.
     */
    public Trackmon(IEventBus modEventBus, ModContainer modContainer) {
        // Register common setup method
        modEventBus.addListener(this::commonSetup);

        // Register global event listeners
        NeoForge.EVENT_BUS.register(CommandHandler.class);
        NeoForge.EVENT_BUS.register(SpawnListener.class);

        // Register mod configuration
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * Called during the common setup phase (both client and server).
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Trackmon setup complete.");
    }

    /**
     * Client-only event handlers.
     * Registered automatically by NeoForge.
     */
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        /**
         * Called during the client setup phase.
         * Registers GUI-related event handlers.
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Trackmon client setup complete.");

            event.enqueueWork(() -> {
                // Register key input handler for opening the GUI
                NeoForge.EVENT_BUS.register(TrackmonScreen.ClientEvents.class);
            });
        }

        /**
         * Called to register custom key bindings.
         */
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            TrackmonScreen.registerKeybinds(event);
        }
    }
}
