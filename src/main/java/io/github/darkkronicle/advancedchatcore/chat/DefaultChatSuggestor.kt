/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.interfaces.AdvancedChatScreenSection
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatInputSuggestor

/** Handles the CommandSuggestor for the chat  */
@Environment(EnvType.CLIENT)
class DefaultChatSuggestor(screen: AdvancedChatScreen?) : AdvancedChatScreenSection(screen) {

	private lateinit var commandSuggestor: ChatInputSuggestor

	override fun onChatFieldUpdate(chatText: String?, text: String) {
		commandSuggestor.setWindowActive(text != screen?.originalChatText)
		commandSuggestor.refresh()
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return commandSuggestor.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
		commandSuggestor.render(context, mouseX, mouseY)
	}

	override fun setChatFromHistory(hist: String?) {
		commandSuggestor.setWindowActive(false)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
		return commandSuggestor.mouseScrolled(verticalAmount)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return commandSuggestor.mouseClicked(mouseX, mouseY, button)
	}

	override fun resize(width: Int, height: Int) {
		commandSuggestor.refresh()
	}

	override fun initGui() {
		val client = MinecraftClient.getInstance()
		val screen = screen
		this.commandSuggestor = ChatInputSuggestor(
				client,
				screen,
				screen?.chatField,
				client.textRenderer,
				false,
				false,
				1,
				10,
				true,
				-805306368)
		commandSuggestor.refresh()
	}
}
