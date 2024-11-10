/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore

import fi.dy.masa.malilib.event.InitializationHandler
import fi.dy.masa.malilib.gui.GuiBase
import io.github.darkkronicle.advancedchatcore.chat.AdvancedSleepingChatScreen
import io.github.darkkronicle.advancedchatcore.util.Colors
import io.github.darkkronicle.advancedchatcore.util.SyncTaskQueue
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Paths
import java.util.*

@Environment(EnvType.CLIENT)
class AdvancedChatCore : ClientModInitializer {

	override fun onInitializeClient() {
		// Important to get first since configuration options depend on colors
		Colors.Companion.getInstance().load()
		InitializationHandler.getInstance().registerInitializationHandler(InitHandler())
		val client: MinecraftClient = MinecraftClient.getInstance()
		ClientTickEvents.START_CLIENT_TICK.register(
			ClientTickEvents.StartTick { s: MinecraftClient ->
				// Allow for delayed tasks to be added
				SyncTaskQueue.Companion.getInstance().update(s.inGameHud.getTicks())
				// Make sure we're not in the sleeping screen while awake
				if (client.currentScreen is AdvancedSleepingChatScreen
					&& !client.player!!.isSleeping()) {
					GuiBase.openGui(null)
				}
			})
	}

	companion object {

		const val MOD_ID: String = "advancedchatcore"

		/**
		 * Whether or not messages should be sent to the HUD. Used for other modules overwriting HUD.
		 */
		var FORWARD_TO_HUD: Boolean = true

		/**
		 * Whether or not the default chat suggestor should be created. Used for modules overwriting the
		 * suggestor.
		 */
		var CREATE_SUGGESTOR: Boolean = true

		val LOGGER: Logger = LogManager.getLogger(MOD_ID)

		private val RANDOM: Random = Random()

		private val RANDOM_STRINGS: Array<String> = arrayOf("yes", "maybe", "no", "potentially", "hello", "goodbye", "tail", "pop", "water",
			"headphone", "head", "scissor", "paper", "burger", "clock", "peg", "speaker",
			"computer", "mouse", "mat", "keyboard", "soda", "mac", "cheese", "home",
			"pillow", "couch", "drums", "drumstick", "math", "Euler", "Chronos", "DarkKronicle",
			"Kron", "pain", "suffer", "bridge", "Annevdl", "MaLiLib", "pog", "music",
			"pants", "glockenspiel", "marimba", "chimes", "vibraphone", "vibe", "snare",
			"monkeymode", "shades", "cactus", "shaker", "pit", "band", "percussion",
			"foot", "leg", "Kurt", "bruh", "gamer", "gaming"
		)

		/**
		 * Get's a resource from src/resources. Works in a emulated environment.
		 *
		 * @param path Path from the resources to get
		 * @return Stream of the resource
		 * @throws URISyntaxException If the resource doesn't exist
		 * @throws IOException Can't be opened
		 */
		@kotlin.Throws(URISyntaxException::class, IOException::class)
		fun getResource(path: String?): InputStream? {
			val uri: URI = Thread.currentThread().getContextClassLoader().getResource(path).toURI()
			if (uri.getScheme() != "file") {
				// it's not a file
				return Thread.currentThread().getContextClassLoader().getResourceAsStream(path)
			} else {
				// it's a file - try to access it directly!
				return FileInputStream(Paths.get(uri).toFile())
			}
		}

		val randomString: String
			/**
			 * Get's a random string.
			 *
			 * @return Random generated string.
			 */
			get() {
				return RANDOM_STRINGS.get(
					RANDOM.nextInt(
						RANDOM_STRINGS.size))
			}

		val server: String
			/**
			 * Returns the server address that the client is currently connected to.
			 * @return The server address if connected, 'singleplayer' if singleplayer, 'none' if none.
			 */
			get() {
				val client: MinecraftClient = MinecraftClient.getInstance()
				if (client.isInSingleplayer()) {
					return "singleplayer"
				}
				if (client.getCurrentServerEntry() == null) {
					return "none"
				}
				return client.getCurrentServerEntry()!!.address
			}
	}
}
