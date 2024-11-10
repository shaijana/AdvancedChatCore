/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageProcessor
import io.github.darkkronicle.advancedchatcore.mixin.MixinChatHudInvoker
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.network.message.MessageSignatureData
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Environment(EnvType.CLIENT)
class ChatHistoryProcessor : IMessageProcessor {

	override fun process(text: Text, @Nullable unfiltered: Text?): Boolean {
		return process(text, unfiltered, null, MessageIndicator.system())
	}

	override fun process(text: Text, @Nullable unfiltered: Text?, @Nullable signature: MessageSignatureData?, @Nullable indicator: MessageIndicator?): Boolean {
		var unfiltered = unfiltered
		if (unfiltered == null) {
			unfiltered = text
		}

		// Put the time in
		val time = LocalTime.now()
		val showtime = ConfigStorage.General.SHOW_TIME.config.booleanValue
		// Store original so we can get stuff without the time
		val original: Text = text.copy()
		if (showtime) {
			val format =
				DateTimeFormatter.ofPattern(
					ConfigStorage.General.TIME_FORMAT.config.stringValue)
			val replaceFormat: String =
				ConfigStorage.General.TIME_TEXT_FORMAT.config.stringValue.replace("&".toRegex(), "§")
			val color = ConfigStorage.General.TIME_COLOR.config.get()
			var style = Style.EMPTY
			val textColor = TextColor.fromRgb(color!!.color())
			style = style.withColor(textColor)
			text.siblings.add(0, Text.literal(replaceFormat.replace("%TIME%".toRegex(), time.format(format))).fillStyle(style))
		}

		val width = 0
		// Find player
		val player: MessageOwner =
			getAuthor(
				MinecraftClient.getInstance().networkHandler, unfiltered.string)
		val line = ChatMessage.builder()
			.displayText(text)
			.originalText(original)
			.owner(player)
			.id(0)
			.width(width)
			.creationTick(MinecraftClient.getInstance().inGameHud.ticks)
			.time(time)
			.backgroundColor(null)
			.build()
		if (ChatHistory.Companion.getInstance().add(line)) {
			sendToHud(line.getDisplayText(), line.getSignature(), line.getIndicator())
		}
		return true
	}

	companion object {

		private fun sendToHud(text: Text, @Nullable signature: MessageSignatureData, indicator: MessageIndicator): Boolean {
			if (AdvancedChatCore.Companion.FORWARD_TO_HUD) {
				val chatHudLine = ChatHudLine(MinecraftClient.getInstance().inGameHud.ticks, text, signature, indicator)
				(MinecraftClient.getInstance().inGameHud.chatHud as MixinChatHudInvoker).invokeAddVisibleMessage(chatHudLine)
				return true
			}
			return false
		}
	}
}
