/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import io.github.darkkronicle.advancedchatcore.interfaces.IChatMessageProcessor
import lombok.Getter
import lombok.Setter
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import java.util.stream.Collectors

/** A utility class to maintain the storage of the chat.  */
@Environment(EnvType.CLIENT)
class ChatHistory private constructor() {

	/** Stored lines  */
	@Getter
	private val messages: MutableList<ChatMessage> = ArrayList()

	/** Maximum lines for storage  */
	@Getter
	@Setter
	private val maxLines = 500

	/** Runnable's to run when chat history is cleared  */
	@Getter
	private val onClear: MutableList<Runnable> = ArrayList()

	/** [IChatMessageProcessor] for when history is updated.  */
	@Getter
	private val onUpdate: MutableList<IChatMessageProcessor> = ArrayList()

	/**
	 * Add's a runnable that will trigger when all chat messages should be cleared.
	 *
	 * @param runnable Runnable to run
	 */
	fun addOnClear(runnable: Runnable) {
		onClear.add(runnable)
	}

	/**
	 * Add's a [IChatMessageProcessor] that get's called on new messages, added messages,
	 * stacked messages, or removed messages.
	 *
	 * @param processor Processor ot add
	 */
	fun addOnUpdate(processor: IChatMessageProcessor) {
		onUpdate.add(processor)
	}

	/** Goes through and clears all message data from everywhere.  */
	fun clearAll() {
		messages.clear()
		for (r in onClear) {
			r.run()
		}
	}

	/** Clear's all the chat messages from the history  */
	fun clear() {
		messages.clear()
	}

	private fun sendUpdate(message: ChatMessage, type: IChatMessageProcessor.UpdateType) {
		for (consumer in onUpdate) {
			consumer.onMessageUpdate(message, type)
		}
	}

	/**
	 * Add's a chat message to the history.
	 *
	 * @param message
	 */
	fun add(message: ChatMessage): Boolean {
		sendUpdate(message, IChatMessageProcessor.UpdateType.NEW)
		var i = 0
		while (i < ConfigStorage.General.CHAT_STACK.config.integerValue
			&& i < messages.size
		) {
			// Check for stacks
			val chatLine = messages[i]
			if (message.isSimilar(chatLine)) {
				chatLine.setStacks(chatLine.getStacks() + 1)
				sendUpdate(chatLine, IChatMessageProcessor.UpdateType.STACK)
				return false
			}
			i++
		}
		sendUpdate(message, IChatMessageProcessor.UpdateType.ADDED)
		messages.add(0, message)
		while (messages.size > maxLines) {
			sendUpdate(
				messages.removeAt(messages.size - 1),
				IChatMessageProcessor.UpdateType.REMOVE)
		}
		return true
	}

	/**
	 * Remove's a message based off of it's messageId.
	 *
	 * @param messageId Message ID to find and remove
	 */
	fun removeMessage(messageId: Int) {
		val toRemove =
			messages.stream()
				.filter { line: ChatMessage -> line.getId() == messageId }
				.collect(Collectors.toList())
		messages.removeAll(toRemove)
		for (m in toRemove) {
			sendUpdate(m, IChatMessageProcessor.UpdateType.REMOVE)
		}
	}

	companion object {

		val instance: ChatHistory = ChatHistory()
	}
}
