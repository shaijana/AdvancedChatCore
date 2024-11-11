/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen
import lombok.Getter
import net.minecraft.client.gui.Drawable
import net.minecraft.client.util.math.MatrixStack

/**
 * A class meant to extend onto the [AdvancedChatScreen]
 *
 *
 * This is used so that many modules can add onto the screen without problems occuring.
 */
abstract class AdvancedChatScreenSection(
	/** The [AdvancedChatScreen] that is linked to this section  */
	val screen: AdvancedChatScreen?
) : Drawable {

	/** Triggers when the gui is initiated  */
	open fun initGui() {}

	/**
	 * Triggered when the window is resized
	 *
	 * @param width Window width
	 * @param height Window height
	 */
	open fun resize(width: Int, height: Int) {}

	/** Triggered when the GUI is closed  */
	fun removed() {}

	/**
	 * Triggered when the chatfield text is pudated
	 *
	 * @param chatText Updated value (?)
	 * @param text The text of the chatfield
	 */
	open fun onChatFieldUpdate(chatText: String?, text: String) {}

	/**
	 * Triggered when a key is pressed
	 *
	 * @param keyCode Keycode
	 * @param scanCode Scancode
	 * @param modifiers Modifiers
	 * @return If it was handled and should stop.
	 */
	open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return false
	}

	/**
	 * Triggered when the mouse is scrolled
	 *
	 * @param mouseX MouseX
	 * @param mouseY MouseY
	 * @param amount Scroll amount
	 * @return If it was handled and should stop.
	 */
	open fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
		return false
	}

	/**
	 * Triggered when the mouse is clicked
	 *
	 * @param mouseX MouseX
	 * @param mouseY MouseY
	 * @param button Mouse button
	 * @return If it was handled and should stop.
	 */
	open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return false
	}

	/**
	 * Triggered when the mouse click is released
	 *
	 * @param mouseX MouseX
	 * @param mouseY MouseY
	 * @param mouseButton Mouse button
	 * @return If it was handled and should stop.
	 */
	fun mouseReleased(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
		return false
	}

	/**
	 * @param mouseX
	 * @param mouseY
	 * @param button
	 * @param deltaX
	 * @param deltaY
	 * @return If it was handled and should stop.
	 */
	fun mouseDragged(
		mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double
	): Boolean {
		return false
	}

	/**
	 * Called when the chat field gets set due to history (up arrows)
	 *
	 * @param hist History set from
	 */
	open fun setChatFromHistory(hist: String?) {}

	/**
	 * Called when the screen renders.
	 *
	 * @param matrixStack MatrixStack
	 * @param mouseX MouseX
	 * @param mouseY MouseY
	 * @param partialTicks Partial tick from the last tick
	 */
	fun render(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {}
}
