/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.StyleFormatter
import lombok.Builder
import lombok.Data
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.network.message.MessageSignatureData
import net.minecraft.text.Text
import java.time.LocalTime
import java.util.*

/** A message from chat with data stored within it.  */
@Environment(EnvType.CLIENT)
@Data
class ChatMessage @Builder protected constructor(
	/** Tick the message was created.  */
	protected var creationTick: Int,
	/** The text that will be displayed on render.  */
	protected var displayText: Text,
	originalText: Text?,
	/** ID of the message.  */
	var id: Int,
	/** The time the message was created.  */
	protected var time: LocalTime,
	/** The background color of the message.  */
	protected var backgroundColor: Color,
	width: Int,
	/** The owner of the message.  */
	@field:Nullable protected var owner: MessageOwner,
	@field:Nullable @param:Nullable protected var signature: MessageSignatureData,
	@Nullable indicator: MessageIndicator?
) {

	/** The unmodified original text. Used to keep time stamp off of.  */
	protected var originalText: Text = originalText ?: displayText

	/** The amount of times the message has been stacked.  */
	var stacks: Int = 0

	/** Unique ID of the message.  */
	protected var uuid: UUID = UUID.randomUUID()

	/** Split up lines for line breaks.  */
	protected var lines: MutableList<AdvancedChatLine>? = null

	protected var indicator: MessageIndicator = indicator ?: MessageIndicator.system()

	/**
	 * Set's the display text of the message and formats the line breaks.
	 *
	 * @param text Text to set to
	 * @param width The width that a line break should be enforced
	 */
	fun setDisplayText(text: Text, width: Int) {
		this.displayText = text
		formatChildren(width)
	}

	/**
	 * Clones the object
	 *
	 * @param width Width for the line breaks to be enforced
	 * @return Cloned object
	 */
	fun shallowClone(width: Int): ChatMessage {
		val message = ChatMessage(creationTick, displayText, originalText, id, time, backgroundColor, width, owner, signature, indicator).also {
			it.stacks = stacks
		}
		return message
	}

	/** A sub section of [ChatMessage] which contains a renderable line.  */
	@Data
	class AdvancedChatLine(
		private val parent: ChatMessage,
		/** Render text  */
		private val text: Text
	) {

		private val width = MinecraftClient.getInstance().textRenderer.getWidth(text)

		override fun toString(): String {
			return ("AdvancedChatLine{text=$text, width=$width}")
		}
	}

	init {
		formatChildren(width)
	}

	/**
	 * Reformat's the line breaks
	 *
	 * @param width Width that the line breaks should be enforced
	 */
	fun formatChildren(width: Int) {
		this.lines = ArrayList()
		if (width == 0) {
			lines.add(AdvancedChatLine(this, displayText))
		} else {
			for (t in StyleFormatter.Companion.wrapText(
				MinecraftClient.getInstance().textRenderer, width, displayText)) {
				lines.add(AdvancedChatLine(this, t))
			}
		}
	}

	/**
	 * Check if the original text is similar to another's original text
	 *
	 * @param message Message to compare to
	 * @return If it's similar
	 */
	fun isSimilar(message: ChatMessage): Boolean {
		return message.getOriginalText().getString() == this.getOriginalText().getString()
	}

	val lineCount: Int
		/**
		 * Get's the total amount of lines
		 *
		 * @return Line count
		 */
		get() = lines!!.size
}
