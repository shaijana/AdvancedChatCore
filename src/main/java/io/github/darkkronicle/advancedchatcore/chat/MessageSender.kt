/*
 * Copyright (C) 2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.interfaces.IStringFilter
import net.minecraft.client.MinecraftClient
import org.apache.logging.log4j.Level
import java.util.*

class MessageSender private constructor() {

	private val client: MinecraftClient = MinecraftClient.getInstance()

	private val filters: MutableList<IStringFilter> = ArrayList()

	fun addFilter(filter: IStringFilter) {
		filters.add(filter)
	}

	fun addFilter(filter: IStringFilter, index: Int) {
		filters.add(index, filter)
	}

	fun sendMessage(string: String) {
		var string = string
		val unfiltered = string
		for (filter in filters) {
			val filtered = filter.filter(string)
			if (filtered!!.isPresent) {
				string = filtered.get().trim()
			}
		}
		if (string.length > 256) {
			string = string.substring(0, 256)
		}
		client.inGameHud.chatHud.addToMessageHistory(unfiltered)

		if (string.length == 0) {
			AdvancedChatCore.Companion.LOGGER.log(Level.WARN,
				"Blank message was attempted to be sent. $unfiltered")
			return
		}

		if (client.player != null) {
			if (string.startsWith("/")) {
				client.networkHandler!!.sendChatCommand(string.substring(1))
			} else {
				client.networkHandler!!.sendChatMessage(string)
			}
		}
	}

	companion object {

		val instance: MessageSender = MessageSender()
	}
}
