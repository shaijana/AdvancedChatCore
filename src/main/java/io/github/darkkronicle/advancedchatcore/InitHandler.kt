/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore

import fi.dy.masa.malilib.config.ConfigManager
import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.config.options.ConfigHotkey
import fi.dy.masa.malilib.event.InputEventHandler
import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback
import fi.dy.masa.malilib.hotkeys.IKeybind
import fi.dy.masa.malilib.hotkeys.KeyAction
import fi.dy.masa.malilib.interfaces.IInitializationHandler
import fi.dy.masa.malilib.util.InfoUtils
import io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen
import io.github.darkkronicle.advancedchatcore.chat.ChatHistoryProcessor
import io.github.darkkronicle.advancedchatcore.chat.ChatScreenSectionHolder
import io.github.darkkronicle.advancedchatcore.chat.DefaultChatSuggestor
import io.github.darkkronicle.advancedchatcore.chat.MessageDispatcher
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfig
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler
import io.github.darkkronicle.advancedchatcore.config.gui.TabSupplier
import io.github.darkkronicle.advancedchatcore.finder.CustomFinder
import io.github.darkkronicle.advancedchatcore.finder.custom.ProfanityFinder
import io.github.darkkronicle.advancedchatcore.hotkeys.InputHandler
import io.github.darkkronicle.advancedchatcore.interfaces.AdvancedChatScreenSection
import io.github.darkkronicle.advancedchatcore.interfaces.IFinder
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter
import io.github.darkkronicle.advancedchatcore.util.ProfanityUtil
import io.github.darkkronicle.advancedchatcore.util.StringInsert
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class InitHandler : IInitializationHandler {

	override fun registerModHandlers() {
		// Setup modules
		ModuleHandler.Companion.getInstance().registerModules()
		ConfigManager.getInstance()
			.registerConfigHandler(AdvancedChatCore.Companion.MOD_ID, ConfigStorage())
		// Setup chat history
		MessageDispatcher.Companion.getInstance().register(ChatHistoryProcessor(), -1)

		GuiConfigHandler.Companion.getInstance().addTab(
			GuiConfigHandler.Companion.children(
				"advancedchatcore",
				"advancedchat.tab.advancedchatcore",
				GuiConfigHandler.Companion.wrapOptions(
					"core_general",
					"advancedchatcore.tab.general",
					ConfigStorage.General.OPTIONS.stream().map<IConfigBase?> { saveableConfig: SaveableConfig<out IConfigBase?>? -> saveableConfig!!.config }
						.toList()
				),
				GuiConfigHandler.Companion.wrapOptions(
					"chatscreen",
					"advancedchatcore.tab.chatscreen",
					ConfigStorage.ChatScreen.OPTIONS.stream().map<IConfigBase?> { saveableConfig: SaveableConfig<out IConfigBase?>? -> saveableConfig!!.config }
						.toList()
				))
		)

		ProfanityUtil.Companion.getInstance().loadConfigs()
		MessageDispatcher.Companion.getInstance().registerPreFilter(IMessageFilter { text: Text ->
			var text: Text = text
			if (ConfigStorage.General.FILTER_PROFANITY.config.getBooleanValue()) {
				val profanity: List<StringMatch?> =
					ProfanityUtil.Companion.getInstance().getBadWords(text.getString(), ConfigStorage.General.PROFANITY_ABOVE.config.getDoubleValue().toFloat(),
						ConfigStorage.General.PROFANITY_ON_WORD_BOUNDARIES.config.getBooleanValue())
				if (profanity.size == 0) {
					return@registerPreFilter Optional.empty<Text>()
				}
				val insertions: MutableMap<StringMatch?, StringInsert> =
					HashMap()
				for (bad: StringMatch? in profanity) {
					insertions.put(bad, StringInsert { current: Text, match: StringMatch? ->
						Text.literal("*".repeat(
							bad!!.end!! - bad.start!!)).fillStyle(current.getStyle())
					}
					)
				}
				text = replaceStrings(text, insertions)
				return@registerPreFilter Optional.of<Text>(text)
			}
			Optional.empty<Text?>()
		}, -1)

		// This constructs the default chat suggestor
		ChatScreenSectionHolder.Companion.getInstance()
			.addSectionSupplier(
				(Function<AdvancedChatScreen, AdvancedChatScreenSection?> { advancedChatScreen: AdvancedChatScreen? ->
					if (AdvancedChatCore.Companion.CREATE_SUGGESTOR) {
						return@Function DefaultChatSuggestor(advancedChatScreen)
					}
					null
				}))

		CustomFinder.Companion.getInstance()
			.register(
				Supplier<IFinder> { ProfanityFinder() },
				"profanity",
				"advancedchatcore.findtype.custom.profanity",
				"advancedchatcore.findtype.custom.info.profanity")

		InputHandler.Companion.getInstance().addDisplayName("core_general", "advancedchatcore.config.tab.hotkeysgeneral")
		InputHandler.Companion.getInstance().add("core_general", ConfigStorage.Hotkeys.OPEN_CHAT.config,
			IHotkeyCallback { action: KeyAction?, key: IKeybind? ->
				if (MinecraftClient.getInstance().world == null) {
					return@add true
				}
				GuiBase.openGui(AdvancedChatScreen(""))
				true
			})
		InputHandler.Companion.getInstance().add("core_general", ConfigStorage.Hotkeys.OPEN_CHAT_WITH_LAST.config,
			IHotkeyCallback { action: KeyAction?, key: IKeybind? ->
				if (MinecraftClient.getInstance().world == null) {
					return@add true
				}
				GuiBase.openGui(AdvancedChatScreen(0))
				true
			})
		InputHandler.Companion.getInstance().add("core_general", ConfigStorage.Hotkeys.OPEN_CHAT_FREE_MOVEMENT.config,
			IHotkeyCallback { action: KeyAction?, key: IKeybind? ->
				if (MinecraftClient.getInstance().world == null) {
					return@add true
				}
				// Manually update stuff so that movement keys are continued to be pressed
				val client: MinecraftClient = MinecraftClient.getInstance()
				if (client.currentScreen != null) {
					client.currentScreen!!.removed()
				}
				client.currentScreen = AdvancedChatScreen(true)
				client.mouse.unlockCursor()
				client.currentScreen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight())
				client.skipGameRender = false

				client.updateWindowTitle()
				true
			})
		InputHandler.Companion.getInstance().add("core_general", ConfigStorage.Hotkeys.TOGGLE_PERMANENT.config,
			IHotkeyCallback { action: KeyAction?, key: IKeybind? ->
				AdvancedChatScreen.Companion.PERMANENT_FOCUS = !AdvancedChatScreen.Companion.PERMANENT_FOCUS
				InfoUtils.printActionbarMessage("advancedchatcore.message.togglepermanent")
				true
			})
		InputHandler.Companion.getInstance().add("core_general", ConfigStorage.Hotkeys.OPEN_SETTINGS.config,
			IHotkeyCallback { action: KeyAction?, key: IKeybind? ->
				GuiBase.openGui(GuiConfig())
				true
			})
		ModuleHandler.Companion.getInstance().load()
		InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.Companion.getInstance())
		InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.Companion.getInstance())
		InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.Companion.getInstance())

		val children: MutableList<TabSupplier> = ArrayList()

		for (hotkeys: Map.Entry<String, List<ConfigHotkey?>> in InputHandler.Companion.getInstance().getHotkeys().entries) {
			val configs: List<IConfigBase?> = hotkeys.value.stream().map { hotkey: ConfigHotkey? -> hotkey }.toList()
			children.add(GuiConfigHandler.Companion.wrapOptions(hotkeys.key, InputHandler.Companion.getInstance().getDisplayName(hotkeys.key), configs))
		}

		GuiConfigHandler.Companion.getInstance().addTab(
			GuiConfigHandler.Companion.children(
				"hotkeys",
				"advancedchat.tab.hotkeys",
				*children.toTypedArray<TabSupplier>()
			)
		)
	}
}
