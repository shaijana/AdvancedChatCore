/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import fi.dy.masa.malilib.util.FileUtils
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import lombok.Getter
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.apache.logging.log4j.Level
import java.util.*

/** A class storing data of colors as defined in colors.toml  */
@Environment(EnvType.CLIENT)
class Colors private constructor() {

	@Getter
	private val colors: MutableMap<String, Color> = HashMap()

	@Getter
	private val palettes: MutableMap<String, Palette> = HashMap()
	private var defaultPalette = ""

	/**
	 * Loads configuration from colors.toml
	 *
	 *
	 * If the file doesn't exist in the configuration directory, it's copied from resources
	 */
	fun load() {
		colors.clear()
		palettes.clear()

		// Get file or create if it doesn't exist
		val file = FileUtils.getConfigDirectory()
			.toPath()
			.resolve("advancedchat")
			.resolve("colors.toml")
			.toFile()
		if (!file.exists()) {
			try {
				org.apache.commons.io.FileUtils.copyInputStreamToFile(AdvancedChatCore.Companion.getResource("colors.toml"), file)
			} catch (e: Exception) {
				// Rip
				AdvancedChatCore.Companion.LOGGER.log(Level.ERROR, "Colors could not be loaded correctly!", e)
				return
			}
		}

		// Use night-config toml parsing
		val config: FileConfig = loadFileWithDefaults(file, "colors.toml")

		// Assign colors
		val customColors = config.getOptional<Config>("color")
		if (customColors.isPresent) {
			for (entry in customColors.get().entrySet()) {
				colors.put(entry.key, hexToSimple(entry.getValue()))
			}
		}

		val palettes = config.getOptional<Config>("palettes")
		if (palettes.isPresent) {
			// Nested configuration
			for (entry in palettes.get().entrySet()) {
				val colors = ArrayList<Color?>()
				for (c in entry.getValue<Any>() as List<String>) {
					if (this.colors.containsKey(c)) {
						// Allow color reference
						colors.add(this.colors[c])
					} else {
						colors.add(hexToSimple(c))
					}
				}
				this.palettes.put(entry.key, Palette(colors))
			}
		}

		// Set default
		val defaultPalette = config.getOptional<String>("default_palette")
		defaultPalette.ifPresent { s: String -> this.defaultPalette = s }
		config.close()
	}

	val default: Palette?
		/**
		 * Get's the default palette specified by the user.
		 *
		 * @return Palette user specified
		 */
		get() {
			if (palettes.containsKey(defaultPalette)) {
				return palettes[defaultPalette]
			}
			AdvancedChatCore.Companion.LOGGER.log(
				Level.WARN, "Default Palette $defaultPalette does not exist!")
			return palettes.values.toTypedArray<Palette>().get(0)
		}

	/**
	 * Get's a palette by name. If it doesn't exist, an empty optional is returned
	 *
	 * @param name Name of the palette from colors.toml
	 * @return Palette
	 */
	fun get(name: String): Optional<Palette> {
		val palette = palettes[name]
		if (palette != null) {
			return Optional.of(palette)
		}
		return Optional.empty()
	}

	fun getColor(key: String): Optional<Color> {
		if (colors.containsKey(key)) {
			return Optional.of(colors[key]!!)
		}
		return Optional.empty()
	}

	fun getColorOrWhite(key: String): Color {
		return getColor(key).orElse(Color(255, 255, 255, 255))
	}

	class Palette(@field:Getter private val colors: List<Color?>)
	companion object {

		val instance: Colors = Colors()

		private fun hexToSimple(string: String): Color {
			var string = string
			if (string.length != 7 && string.length != 9) {
				// Not #ffffff (so invalid!)
				AdvancedChatCore.Companion.LOGGER.log(
					Level.WARN,
					"Color $string isn't formatted correctly! (#ffffff) (#ffffffff)")
				return Color(255, 255, 255, 255)
			}
			string = string.substring(1)
			try {
				val red: Int = string.substring(0, 2).toInt(16)
				val green: Int = string.substring(2, 4).toInt(16)
				val blue: Int = string.substring(4, 6).toInt(16)
				var alpha = 255
				if (string.length == 8) {
					alpha = string.substring(6).toInt()
				}
				return Color(red, green, blue, alpha)
			} catch (e: Exception) {
				AdvancedChatCore.Companion.LOGGER.log(
					Level.WARN, "Couldn't convert $string into a color!", e)
			}
			return Color(255, 255, 255, 255)
		}
	}
}
