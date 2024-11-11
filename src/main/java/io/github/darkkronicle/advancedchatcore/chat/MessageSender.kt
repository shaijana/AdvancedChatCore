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

object MessageSender {

	private val client: MinecraftClient = MinecraftClient.getInstance()

	val filters = mutableListOf<IStringFilter>()

	fun addFilter(filter: IStringFilter, index: Int) {
		filters[index] = filter
	}

	fun sendMessage(originMessageString: String) {
		var messageString = originMessageString
		val unfilteredMessageString = messageString
		filters.forEach { filter ->
			val filtered = filter.filter(messageString)
				?: return@forEach
			if (filtered.isPresent) {
				messageString = filtered.get().trim()
			}
		}
		if (messageString.length > 256) {
			messageString = messageString.substring(0, 256)
		}
		client.inGameHud.chatHud.addToMessageHistory(unfilteredMessageString)

		if (messageString.isEmpty()) {
			AdvancedChatCore.Companion.logger.log(Level.WARN,
				"Blank message was attempted to be sent. $unfilteredMessageString")
			return
		}

		if (client.player != null) {
			if (messageString.startsWith("/")) {
				client.networkHandler!!.sendChatCommand(messageString.substring(1))
			} else {
				client.networkHandler!!.sendChatMessage(messageString)
			}
		}
	}
}
