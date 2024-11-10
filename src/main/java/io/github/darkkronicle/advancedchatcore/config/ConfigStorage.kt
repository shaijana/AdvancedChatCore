/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config

import com.google.common.collect.ImmutableList
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import fi.dy.masa.malilib.MaLiLib
import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.config.IConfigHandler
import fi.dy.masa.malilib.config.IConfigOptionListEntry
import fi.dy.masa.malilib.config.options.ConfigBoolean
import fi.dy.masa.malilib.config.options.ConfigDouble
import fi.dy.masa.malilib.config.options.ConfigHotkey
import fi.dy.masa.malilib.config.options.ConfigInteger
import fi.dy.masa.malilib.config.options.ConfigString
import fi.dy.masa.malilib.hotkeys.KeyAction
import fi.dy.masa.malilib.hotkeys.KeybindSettings
import fi.dy.masa.malilib.util.FileUtils
import fi.dy.masa.malilib.util.JsonUtils
import fi.dy.masa.malilib.util.StringUtils
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.config.options.ConfigColor
import io.github.darkkronicle.advancedchatcore.interfaces.ConfigRegistryOption
import io.github.darkkronicle.advancedchatcore.util.AbstractRegistry
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.Colors
import io.github.darkkronicle.advancedchatcore.util.EasingMethod
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

// Used to store values into config.json
@Environment(EnvType.CLIENT)
class ConfigStorage : IConfigHandler {

	object General {

		const val NAME: String = "general"

		fun translate(key: String): String {
			return StringUtils.translate("advancedchat.config.general." + key)
		}

		val TIME_FORMAT: SaveableConfig<ConfigString> = SaveableConfig.Companion.fromConfig<ConfigString>(
			"timeFormat",
			ConfigString(
				translate("timeformat"), "hh:mm", translate("info.timeformat")))

		val TIME_TEXT_FORMAT: SaveableConfig<ConfigString> = SaveableConfig.Companion.fromConfig<ConfigString>(
			"timeTextFormat",
			ConfigString(
				translate("timetextformat"),
				"[%TIME%] ",
				translate("info.timetextformat")))

		val TIME_COLOR: SaveableConfig<ConfigColor> = SaveableConfig.Companion.fromConfig<ConfigColor>(
			"time_color",
			ConfigColor(
				translate("timecolor"),
				Colors.Companion.getInstance().getColor("white").get(),
				translate("info.timecolor")))

		val SHOW_TIME: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"show_time",
			ConfigBoolean(
				translate("showtime"), false, translate("info.showtime")))

		val CLEAR_ON_DISCONNECT: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"clearOnDisconnect",
			ConfigBoolean(
				translate("clearondisconnect"),
				true,
				translate("info.clearondisconnect")))

		val CHAT_STACK: SaveableConfig<ConfigInteger> = SaveableConfig.Companion.fromConfig<ConfigInteger>(
			"chatStack",
			ConfigInteger(
				translate("chatstack"), 0, 0, 20, translate("info.chatstack")))

		val CHAT_STACK_UPDATE: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"chatStackUpdate",
			ConfigBoolean(
				translate("chatstackupdate"),
				false,
				translate("info.chatstackupdate")))

		val MESSAGE_OWNER_REGEX: SaveableConfig<ConfigString> = SaveableConfig.Companion.fromConfig<ConfigString>(
			"messageOwnerRegex",
			ConfigString(
				translate("messageownerregex"),
				"(?<!\\[)\\b[A-Za-z0-9_§]{3,16}\\b(?!\\])",
				translate("info.messageownerregex")))

		val FILTER_PROFANITY: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"filterProfanity",
			ConfigBoolean(
				translate("filterprofanity"),
				false,
				translate("info.filterprofanity")))

		val PROFANITY_ON_WORD_BOUNDARIES: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"profanityWordBoundaries",
			ConfigBoolean(
				translate("profanitywordboundaries"),
				false,
				translate("info.profanitywordboundaries")))

		val PROFANITY_ABOVE: SaveableConfig<ConfigDouble> = SaveableConfig.Companion.fromConfig<ConfigDouble>(
			"profanityAbove",
			ConfigDouble(
				translate("profanityabove"),
				0.0,
				0.0,
				3.0,
				translate("info.profanityabove")))

		val OPTIONS: ImmutableList<SaveableConfig<out IConfigBase>> = ImmutableList.of(
			TIME_FORMAT,
			TIME_TEXT_FORMAT,
			TIME_COLOR,
			SHOW_TIME,
			CLEAR_ON_DISCONNECT,
			CHAT_STACK,
			CHAT_STACK_UPDATE,
			MESSAGE_OWNER_REGEX,
			FILTER_PROFANITY,
			PROFANITY_ABOVE,
			PROFANITY_ON_WORD_BOUNDARIES
		)
	}

	object ChatScreen {

		const val NAME: String = "chatscreen"

		fun translate(key: String): String {
			return StringUtils.translate("advancedchat.config.chatscreen." + key)
		}

		val PERSISTENT_TEXT: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"persistentText",
			ConfigBoolean(
				translate("persistenttext"),
				false,
				translate("info.persistenttext")))

		val COLOR: SaveableConfig<ConfigColor> = SaveableConfig.Companion.fromConfig<ConfigColor>(
			"color",
			ConfigColor(
				translate("color"),
				Colors.Companion.getInstance().getColor("black").get().withAlpha(100),
				translate("info.color")))

		val MORE_TEXT: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"moreText",
			ConfigBoolean(
				translate("moretext"), false, translate("info.moretext")))

		val SHOW_CHAT_ICONS: SaveableConfig<ConfigBoolean> = SaveableConfig.Companion.fromConfig<ConfigBoolean>(
			"showChatIcons",
			ConfigBoolean(
				translate("showchaticons"), true, translate("info.showchaticons")))

		val MODIFIED: SaveableConfig<ConfigColor> = SaveableConfig.Companion.fromConfig<ConfigColor>(
			"modified",
			ConfigColor(
				translate("modified"), Color(15386724), translate("info.modified")))

		val SYSTEM: SaveableConfig<ConfigColor> = SaveableConfig.Companion.fromConfig<ConfigColor>(
			"system",
			ConfigColor(
				translate("system"), Color(10526880), translate("info.system")))

		val FILTERED: SaveableConfig<ConfigColor> = SaveableConfig.Companion.fromConfig<ConfigColor>(
			"filtered",
			ConfigColor(
				translate("filtered"), Color(15386724), translate("info.filtered")))

		val NOT_SECURE: SaveableConfig<ConfigColor> = SaveableConfig.Companion.fromConfig<ConfigColor>(
			"notSecure",
			ConfigColor(
				translate("notsecure"), Color(15224664), translate("info.notsecure")))


		val OPTIONS: ImmutableList<SaveableConfig<out IConfigBase>> = ImmutableList.of(
			PERSISTENT_TEXT, COLOR, MORE_TEXT, SHOW_CHAT_ICONS, MODIFIED, SYSTEM, FILTERED, NOT_SECURE)
	}

	object Hotkeys {

		const val NAME: String = "hotkeys"

		fun translate(key: String): String {
			return StringUtils.translate("advancedchat.config.hotkeys." + key)
		}

		val OPEN_CHAT: SaveableConfig<ConfigHotkey> = SaveableConfig.Companion.fromConfig<ConfigHotkey>("openChat",
			ConfigHotkey(translate("openchat"), "", KeybindSettings.create(
				KeybindSettings.Context.INGAME, KeyAction.PRESS, false, true, false, true
			), translate("info.openchat")))

		val TOGGLE_PERMANENT: SaveableConfig<ConfigHotkey> = SaveableConfig.Companion.fromConfig<ConfigHotkey>("togglePermanent",
			ConfigHotkey(translate("togglepermanentfocus"), "", KeybindSettings.create(
				KeybindSettings.Context.INGAME, KeyAction.PRESS, false, true, false, true
			), translate("info.togglepermanentfocus")))

		val OPEN_CHAT_WITH_LAST: SaveableConfig<ConfigHotkey> = SaveableConfig.Companion.fromConfig<ConfigHotkey>("openChatWithLast",
			ConfigHotkey(translate("openchatwithlast"), "UP", KeybindSettings.create(
				KeybindSettings.Context.INGAME, KeyAction.PRESS, true, true, false, true
			), translate("info.openchatwithlast")))

		val OPEN_CHAT_FREE_MOVEMENT: SaveableConfig<ConfigHotkey> = SaveableConfig.Companion.fromConfig<ConfigHotkey>("openChatFreeMovement",
			ConfigHotkey(translate("openchatfreemovement"), "", KeybindSettings.create(
				KeybindSettings.Context.INGAME, KeyAction.PRESS, true, true, false, true
			), translate("info.openchatfreemovement")))

		val OPEN_SETTINGS: SaveableConfig<ConfigHotkey> = SaveableConfig.Companion.fromConfig<ConfigHotkey>("openSettings",
			ConfigHotkey(translate("opensettings"), "", KeybindSettings.create(
				KeybindSettings.Context.ANY, KeyAction.PRESS, false, true, false, true
			), translate("info.opensettings")))

		val OPTIONS: ImmutableList<SaveableConfig<out IConfigBase>> = ImmutableList.of(
			OPEN_SETTINGS, OPEN_CHAT, OPEN_CHAT_FREE_MOVEMENT, TOGGLE_PERMANENT, OPEN_CHAT_WITH_LAST)
	}

	override fun load() {
		loadFromFile()
	}

	override fun save() {
		saveFromFile()
	}

	/** Serializable easing data  */
	enum class Easing(val configString: String, val ease: EasingMethod) : IConfigOptionListEntry,
		EasingMethod {

		LINEAR("linear", EasingMethod.Method.LINEAR),
		SINE("sine", EasingMethod.Method.SINE),
		QUAD("quad", EasingMethod.Method.QUAD),
		QUART("quart", EasingMethod.Method.QUART),
		CIRC("circ", EasingMethod.Method.CIRC);

		override fun getStringValue(): String {
			return configString
		}

		override fun getDisplayName(): String {
			return translate(configString)
		}

		override fun cycle(forward: Boolean): IConfigOptionListEntry {
			var id: Int = this.ordinal
			if (forward) {
				id++
			} else {
				id--
			}
			if (id >= entries.size) {
				id = 0
			} else if (id < 0) {
				id = entries.size - 1
			}
			return entries.get(id % entries.size)
		}

		override fun fromString(value: String): IConfigOptionListEntry {
			return fromEasingString(value)
		}

		override fun apply(v: Double): Double {
			return ease.apply(v)
		}

		companion object {

			private fun translate(key: String): String {
				return StringUtils.translate("advancedchat.config.easing." + key)
			}

			fun fromEasingString(visibility: String): Easing {
				for (e: Easing in entries) {
					if (e.configString == visibility) {
						return e
					}
				}
				return LINEAR
			}
		}
	}

	companion object {

		val CONFIG_FILE_NAME: String = AdvancedChatCore.Companion.MOD_ID + ".json"
		private const val CONFIG_VERSION: Int = 1

		fun loadFromFile() {
			val v3: File = FileUtils.getConfigDirectory().toPath().resolve(CONFIG_FILE_NAME).toFile()
			val configFile: File
			if (v3.exists()
				&& !FileUtils.getConfigDirectory()
					.toPath()
					.resolve("advancedchat")
					.resolve(CONFIG_FILE_NAME)
					.toFile()
					.exists()) {
				configFile = v3
			} else {
				configFile =
					FileUtils.getConfigDirectory()
						.toPath()
						.resolve("advancedchat")
						.resolve(CONFIG_FILE_NAME)
						.toFile()
			}

			if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
				val element: JsonElement? = parseJsonFile(configFile)

				if (element != null && element.isJsonObject()) {
					val root: JsonObject = element.getAsJsonObject()

					readOptions(root, General.NAME, General.OPTIONS)
					readOptions(root, ChatScreen.NAME, ChatScreen.OPTIONS)
					readOptions(root, Hotkeys.NAME, Hotkeys.OPTIONS)

					val version: Int = JsonUtils.getIntegerOrDefault(root, "configVersion", 0)
				}
			}
		}

		/**
		 * Applies a JSON element into a registry
		 *
		 * @param element Element in key
		 * @param registry Registry to apply too
		 */
		fun applyRegistry(
			element: JsonElement?, registry: AbstractRegistry<*, out ConfigRegistryOption<*>>
		) {
			if (element == null || !element.isJsonObject()) {
				return
			}
			val obj: JsonObject = element.getAsJsonObject()
			for (option: ConfigRegistryOption<*> in registry.getAll()) {
				if (obj.has(option.getSaveString())) {
					option.load(obj.get(option.getSaveString()))
				}
			}
		}

		/**
		 * Creates a [JsonObject] containing registry data
		 *
		 * @param registry
		 * @return
		 */
		fun saveRegistry(
			registry: AbstractRegistry<*, out ConfigRegistryOption<*>>
		): JsonObject {
			val `object`: JsonObject = JsonObject()
			for (option: ConfigRegistryOption<*> in registry.getAll()) {
				`object`.add(option.getSaveString(), option.save())
			}
			return `object`
		}

		fun saveFromFile() {
			val dir: File = FileUtils.getConfigDirectory().toPath().resolve("advancedchat").toFile()

			if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
				val root: JsonObject = JsonObject()

				writeOptions(root, General.NAME, General.OPTIONS)
				writeOptions(root, ChatScreen.NAME, ChatScreen.OPTIONS)
				writeOptions(root, Hotkeys.NAME, Hotkeys.OPTIONS)

				root.add("config_version", JsonPrimitive(CONFIG_VERSION))

				writeJsonToFile(root, File(dir, CONFIG_FILE_NAME))
			}
		}

		fun readOptions(
			root: JsonObject, category: String?, options: List<SaveableConfig<*>>
		) {
			val obj: JsonObject? = JsonUtils.getNestedObject(root, category, false)

			if (obj != null) {
				for (conf: SaveableConfig<*> in options) {
					val option: IConfigBase? = conf.config
					if (obj.has(conf.key)) {
						option!!.setValueFromJsonElement(obj.get(conf.key))
					}
				}
			}
		}

		// WINDOWS BAD AND MINECRAFT LIKES UTF-16
		fun parseJsonFile(file: File?): JsonElement? {
			if (file != null && file.exists() && file.isFile() && file.canRead()) {
				val fileName: String = file.getAbsolutePath()

				try {
					val parser: JsonParser = JsonParser()
					val sets: Array<Charset> =
						arrayOf(
							StandardCharsets.UTF_8, Charset.defaultCharset(),
						)
					// Start to enforce UTF 8. Old files may be UTF-16
					for (s: Charset in sets) {
						var element: JsonElement?
						val reader: InputStreamReader = InputStreamReader(FileInputStream(file), s)
						try {
							element = parser.parse(reader)
						} catch (e: Exception) {
							reader.close()
							MaLiLib.logger.error(
								"Failed to parse the JSON file '{}'. Attempting different charset."
										+ " ",
								fileName,
								e)
							continue
						}
						reader.close()

						return element
					}
				} catch (e: Exception) {
					MaLiLib.logger.error("Failed to parse the JSON file '{}'", fileName, e)
				}
			}

			return null
		}

		// WINDOWS BAD AND MINECRAFT LIKES UTF-16
		fun writeJsonToFile(root: JsonObject?, file: File): Boolean {
			var writer: OutputStreamWriter? = null

			try {
				writer = OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)
				writer.write(JsonUtils.GSON.toJson(root))
				writer.close()

				return true
			} catch (e: IOException) {
				MaLiLib.logger.warn(
					"Failed to write JSON data to file '{}'", file.getAbsolutePath(), e)
			} finally {
				try {
					if (writer != null) {
						writer.close()
					}
				} catch (e: Exception) {
					MaLiLib.logger.warn("Failed to close JSON file", e)
				}
			}

			return false
		}

		fun writeOptions(
			root: JsonObject, category: String?, options: List<SaveableConfig<*>>
		) {
			val obj: JsonObject? = JsonUtils.getNestedObject(root, category, true)

			for (option: SaveableConfig<*> in options) {
				obj!!.add(option.key, option.config!!.getAsJsonElement())
			}
		}
	}
}
