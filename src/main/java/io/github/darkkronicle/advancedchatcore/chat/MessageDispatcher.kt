/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageProcessor
import io.github.darkkronicle.advancedchatcore.util.FindType
import io.github.darkkronicle.advancedchatcore.util.SearchResult
import io.github.darkkronicle.advancedchatcore.util.StringInsert
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import io.github.darkkronicle.advancedchatcore.util.StyleFormatter
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.network.message.MessageSignatureData
import net.minecraft.text.ClickEvent
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.apache.logging.log4j.LogManager
import java.util.*

/**
 * A class to handle chat events.
 *
 *
 * Different events and hooks can be registered in here.
 */
@Environment(EnvType.CLIENT)
class MessageDispatcher private constructor() {

	private val processors = ArrayList<IMessageProcessor>()
	private val preFilters = ArrayList<IMessageFilter>()

	init {
		// We don't really want this to be reconstructed or changed because it will lead to problems
		// of not having everything registered
		registerPreFilter({ text: Text ->
			Optional.of<Text>(StyleFormatter.Companion.formatText(text))
		}, -1)

		registerPreFilter(
			{ text: Text ->
				var text = text
				val string = text.string
				if (string.isEmpty()) {
					return@registerPreFilter Optional.empty<Text>()
				}
				val search: SearchResult =
					SearchResult.Companion.searchOf(
						string,
						"(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)",
						FindType.REGEX)
				if (search.size() == 0) {
					return@registerPreFilter Optional.empty<Text>()
				}
				val insert: MutableMap<StringMatch, StringInsert> = HashMap()
				for (match in search.matches) {
					insert.put(
						match,
						StringInsert { current: Text, match1: StringMatch ->
							var url = match1.match
							if (!isMatch(
									match1.match, "(http(s)?:\\/\\/.)", FindType.REGEX)) {
								url = "https://$url"
							}
							if (current.style.clickEvent == null) {
								return@put Text.literal(match1.match).fillStyle(current.style.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url)))
							}
							MutableText.of(current.content).fillStyle(current.style)
						})
				}
				text = replaceStrings(text, insert)
				Optional.of<Text>(text)
			},
			-1)
		registerPreFilter(
			IMessageProcessor { text: Text, orig: Text? ->
				LogManager.getLogger()
					.info(
						"[CHAT] {}",
						text.string
							.replace("\r".toRegex(), "\\\\r")
							.replace("\n".toRegex(), "\\\\n"))
				true
			} as IMessageProcessor,
			-1)
	}

	/**
	 * This is ONLY used for new messages in chat
	 *
	 *
	 * Note: It is not recommended to call this method to force add new text. Typically, grabbing
	 * the [net.minecraft.client.gui.hud.ChatHud] from [ ] and calling addText is a safer way.
	 *
	 * @param text Text that is received
	 */
	fun handleText(text: Text, @Nullable signature: MessageSignatureData, @Nullable indicator: MessageIndicator) {
		var text = text
		val previouslyBlank = text.string.length == 0
		text = preFilter(text, signature, indicator)
		if (text.string.length == 0 && !previouslyBlank) {
			// No more
			return
		}
		process(text, signature, indicator)
	}

	private fun preFilter(text: Text, @Nullable signature: MessageSignatureData, @Nullable indicator: MessageIndicator): Text {
		var text = text
		for (f in preFilters) {
			val t = f.filter(text)
			if (t!!.isPresent) {
				text = t.get()
			}
		}
		return text
	}

	private fun process(text: Text, @Nullable signature: MessageSignatureData, @Nullable indicator: MessageIndicator) {
		for (f in processors) {
			f.filter(text)
		}
	}

	/**
	 * Registers a [IMessageFilter] to be called to modify the text. This is to keep
	 * formatting consistent, or to stop a message from being sent.
	 *
	 *
	 * If text of zero length is returned by the MessageFilter the text won't be sent to the
	 * processors.
	 *
	 *
	 * Note: It's discouraged to add a IMessageFilter that doesn't modify text. For that use
	 * registerProcess
	 *
	 * @param processor IMessageFilter to modify text
	 * @param index Index to add it. Supplying a negative value will put it at the end.
	 */
	fun registerPreFilter(processor: IMessageFilter, index: Int) {
		var index = index
		if (index < 0) {
			index = preFilters.size
		}
		if (!preFilters.contains(processor)) {
			preFilters.add(index, processor)
		}
	}

	/**
	 * Register's a [IMessageProcessor] to handle a chat event after the message has been
	 * preprocessed.
	 *
	 * @param processor IMessageProcessor to get called back
	 * @param index Index that it will be added to. Supplying a negative value will put it at the
	 * end.
	 */
	fun register(processor: IMessageProcessor, index: Int) {
		var index = index
		if (index < 0) {
			index = processors.size
		}
		if (!processors.contains(processor)) {
			processors.add(index, processor)
		}
	}

	companion object {

		val instance: MessageDispatcher = MessageDispatcher()
	}
}
