/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.util.KeyCodes
import fi.dy.masa.malilib.util.StringUtils
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

class AdvancedSleepingChatScreen : AdvancedChatScreen("") {

	override fun initGui() {
		super.initGui()
		val stopSleep =
			ButtonGeneric(
				this.width / 2 - 100,
				this.height - 40,
				200,
				20,
				StringUtils.translate("multiplayer.stopSleeping"))
		this.addButton(stopSleep
		) { button: ButtonBase?, mouseButton: Int -> stopSleeping() }
	}

	fun onClose() {
		this.stopSleeping()
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (keyCode == KeyCodes.KEY_ESCAPE) {
			this.stopSleeping()
		} else if (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_KP_ENTER) {
			val string: String = chatField!!.text.trim { it <= ' ' }
			if (!string.isEmpty()) {
				MessageSender.Companion.getInstance().sendMessage(string)
			}

			chatField!!.text = ""
			client!!.inGameHud.chatHud.resetScroll()
			// Prevents really weird interactions with chat history
			resetCurrentMessage()
			return true
		}

		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	private fun stopSleeping() {
		val clientPlayNetworkHandler = client!!.player!!.networkHandler
		clientPlayNetworkHandler.sendPacket(
			ClientCommandC2SPacket(
				client!!.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING))
		openGui(null)
	}
}
