/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import io.github.darkkronicle.advancedchatcore.chat.AdvancedSleepingChatScreen;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatcore.util.SyncTaskQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Random;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class AdvancedChatCore implements ClientModInitializer {

    public static final String MOD_ID = "advancedchatcore";

    /**
     * Whether or not messages should be sent to the HUD. Used for other modules overwriting HUD.
     */
    public static boolean FORWARD_TO_HUD = true;

    /**
     * Whether or not the default chat suggestor should be created. Used for modules overwriting the
     * suggestor.
     */
    public static boolean CREATE_SUGGESTOR = true;

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static final Random RANDOM = new Random();

    private static final String[] RANDOM_STRINGS = {
        "yes", "maybe", "no", "potentially", "hello", "goodbye", "tail", "pop", "water",
        "headphone", "head", "scissor", "paper", "burger", "clock", "peg", "speaker",
        "computer", "mouse", "mat", "keyboard", "soda", "mac", "cheese", "home",
        "pillow", "couch", "drums", "drumstick", "math", "Euler", "Chronos", "DarkKronicle",
        "Kron", "pain", "suffer", "bridge", "Annevdl", "MaLiLib", "pog", "music",
        "pants", "glockenspiel", "marimba", "chimes", "vibraphone", "vibe", "snare",
        "monkeymode", "shades", "cactus", "shaker", "pit", "band", "percussion",
        "foot", "leg", "Kurt", "bruh", "gamer", "gaming"
    };

    @Override
    public void onInitializeClient() {
        // Important to get first since configuration options depend on colors
        Colors.getInstance().load();
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        MinecraftClient client = MinecraftClient.getInstance();
        ClientTickEvents.START_CLIENT_TICK.register(
                s -> {
                    // Allow for delayed tasks to be added
                    SyncTaskQueue.getInstance().update(s.inGameHud.getTicks());
                    // Make sure we're not in the sleeping screen while awake
                    if (client.currentScreen instanceof AdvancedSleepingChatScreen
                            && !client.player.isSleeping()) {
                        GuiBase.openGui(null);
                    }
                });
    }

    /**
     * Get's a resource from src/resources. Works in a emulated environment.
     *
     * @param path Path from the resources to get
     * @return Stream of the resource
     * @throws URISyntaxException If the resource doesn't exist
     * @throws IOException Can't be opened
     */
    public static InputStream getResource(String path) throws URISyntaxException, IOException {
        URI uri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();
        if (!uri.getScheme().equals("file")) {
            // it's not a file
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        } else {
            // it's a file - try to access it directly!
            return new FileInputStream(Paths.get(uri).toFile());
        }
    }

    /**
     * Get's a random string.
     *
     * @return Random generated string.
     */
    public static String getRandomString() {
        return RANDOM_STRINGS[RANDOM.nextInt(RANDOM_STRINGS.length)];
    }

    /**
     * Returns the server address that the client is currently connected to.
     * @return The server address if connected, 'singleplayer' if singleplayer, 'none' if none.
     */
    public static String getServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            return "singleplayer";
        }
        if (client.getCurrentServerEntry() == null) {
            return "none";
        }
        return client.getCurrentServerEntry().address;
    }
}
