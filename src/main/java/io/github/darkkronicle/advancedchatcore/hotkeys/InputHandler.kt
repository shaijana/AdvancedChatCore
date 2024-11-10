package io.github.darkkronicle.advancedchatcore.hotkeys

import fi.dy.masa.malilib.config.options.ConfigHotkey
import fi.dy.masa.malilib.event.InputEventHandler
import fi.dy.masa.malilib.hotkeys.IHotkey
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback
import fi.dy.masa.malilib.hotkeys.IKeybindManager
import fi.dy.masa.malilib.hotkeys.IKeybindProvider
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler
import lombok.Getter

class InputHandler private constructor() : IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {

	@Getter
	private val hotkeys: MutableMap<String, MutableList<ConfigHotkey>> = HashMap()

	@Getter
	private val translation: MutableMap<String, String> = HashMap()

	fun clear(modId: String) {
		hotkeys.remove(modId)
	}

	fun addDisplayName(modId: String, displayName: String) {
		translation.put(modId, displayName)
	}

	fun add(modId: String, hotkey: ConfigHotkey) {
		if (!hotkeys.containsKey(modId)) {
			hotkeys.put(modId, ArrayList())
		}
		hotkeys.get(modId)!!.add(hotkey)
	}

	fun add(modId: String, hotkey: ConfigHotkey, callback: IHotkeyCallback?) {
		hotkey.getKeybind().setCallback(callback)
		add(modId, hotkey)
	}

	override fun addKeysToMap(manager: IKeybindManager) {
		for (hots: List<ConfigHotkey> in hotkeys.values) {
			for (hotkey: IHotkey in hots) {
				manager.addKeybindToMap(hotkey.getKeybind())
			}
		}
	}

	override fun addHotkeys(manager: IKeybindManager) {
		for (hots: Map.Entry<String, List<ConfigHotkey>> in hotkeys.entries) {
			manager.addHotkeysForCategory(hots.key, "hotkeys", hots.value)
		}
	}

	fun reload() {
		InputEventHandler.getKeybindManager().updateUsedKeys()
	}

	fun getDisplayName(key: String): String {
		return translation.getOrDefault(key, key)
	}

	companion object {

		val instance: InputHandler = InputHandler()
	}
}
