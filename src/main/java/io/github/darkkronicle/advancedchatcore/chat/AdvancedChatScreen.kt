/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.util.KeyCodes
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler
import io.github.darkkronicle.advancedchatcore.gui.IconButton
import io.github.darkkronicle.advancedchatcore.interfaces.AdvancedChatScreenSection
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.RowList
import lombok.Getter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.MathHelper.clamp
import kotlin.math.max

open class AdvancedChatScreen() : GuiBase() {

	private var finalHistory = ""
	private var messageHistorySize = -1
	private var startHistory = -1
	private var passEvents = false

	/** Chat field at the bottom of the screen  */
	lateinit var chatField: AdvancedTextField

	/** What the chat box started out with  */
	var originalChatText = ""

	private val sections = mutableListOf<AdvancedChatScreenSection>()

	val rightSideButtons = RowList<ButtonBase>()

	@Getter
	private val leftSideButtons = RowList<ButtonBase>()

	override fun closeGui(showParent: Boolean) {
		if (ConfigStorage.ChatScreen.PERSISTENT_TEXT.config.booleanValue) {
			last = chatField.text
		}
		super.closeGui(showParent)
	}

	init {
		setupSections()
	}

	constructor(passEvents: Boolean) : this() {
		this.passEvents = passEvents
	}

	constructor(indexOfLast: Int) : this() {
		startHistory = indexOfLast
	}

	constructor(originalChatText: String) : this() {
		this.originalChatText = originalChatText
	}

	private fun setupSections() {
		ChatScreenSectionHolder.sectionSuppliers.forEach { supplier ->
			val section: AdvancedChatScreenSection = supplier.apply(this)
			sections.add(section)
		}
	}

	private val color: Color?
		get() = ConfigStorage.ChatScreen.COLOR.config.get()

	fun resetCurrentMessage() {
		this.messageHistorySize = client!!.inGameHud.chatHud.messageHistory.size
	}

	override fun charTyped(charIn: Char, modifiers: Int): Boolean {
		if (passEvents) {
			return true
		}
		return super.charTyped(charIn, modifiers)
	}

	override fun initGui() {
		super.initGui()
		rightSideButtons.clear()
		leftSideButtons.clear()
		resetCurrentMessage()
		chatField =
			object : AdvancedTextField(
				textRenderer,
				4,
				height - 12,
				width - 10,
				12,
				Text.translatable("chat.editBox")) {
				override fun getNarrationMessage(): MutableText? {
					return null
				}
			}
		if (ConfigStorage.ChatScreen.MORE_TEXT.config.booleanValue) {
			chatField.setMaxLength(64000)
		} else {
			chatField.setMaxLength(256)
		}
		chatField.setDrawsBackground(false)
		if (this.originalChatText.isNotEmpty()) {
			chatField.setText(this.originalChatText)
		} else if (ConfigStorage.ChatScreen.PERSISTENT_TEXT.config.booleanValue && last.isNotEmpty()) {
			chatField.setText(last)
		}
		chatField.setChangedListener { chatText: String -> this.onChatFieldUpdate(chatText) }

		// Add settings button
		rightSideButtons.add("settings", IconButton(0, 0, 14, 64, Identifier.of(AdvancedChatCore.Companion.MOD_ID, "textures/gui/settings.png")
		) { button: IconButton? ->
			openGui(
				GuiConfigHandler.defaultScreen)
		})

		this.addSelectableChild(chatField)

		this.setInitialFocus(chatField)

		sections.forEach { section ->
			section.initGui()
		}

		var originalX = client!!.window.scaledWidth - 1
		var y = client!!.window.scaledHeight - 30
		for (i in 0 until rightSideButtons.rowSize()) {
			val buttonList: List<ButtonBase> = rightSideButtons.get(i)
				?: continue
			var maxHeight = 0
			var x = originalX
			buttonList.forEach { button ->
				maxHeight = max(maxHeight, button.height)
				x -= button.width + 1
				button.setPosition(x, y)
				addButton(button, null)
			}
			y -= maxHeight + 1
		}
		originalX = 1
		y = client!!.window.scaledHeight - 30
		for (i in 0 until leftSideButtons.rowSize()) {
			val buttonList: List<ButtonBase> = leftSideButtons.get(i)
				?: continue
			var maxHeight = 0
			var x = originalX
			buttonList.forEach { button ->
				maxHeight = max(maxHeight, button.height)
				button.setPosition(x, y)
				addButton(button, null)
				x += button.width + 1
			}
			y -= maxHeight + 1
		}
		if (startHistory >= 0) {
			setChatFromHistory(-startHistory - 1)
		}
	}

	override fun resize(client: MinecraftClient, width: Int, height: Int) {
		val string = chatField.text
		this.init(client, width, height)
		this.setText(string)
		sections.forEach { section ->
			section.resize(width, height)
		}
	}

	override fun removed() {
		sections.forEach { section ->
			section.removed()
		}
	}

	override fun tick() {
		chatField.tick()
	}

	private fun onChatFieldUpdate(chatText: String) {
		val string = chatField.text
		sections.forEach { section ->
			section.onChatFieldUpdate(chatText, string)
		}
	}

	override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (passEvents) {
			val key = InputUtil.fromKeyCode(keyCode, scanCode)
			KeyBinding.setKeyPressed(key, false)
		}
		return false
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (!passEvents) {
			sections.forEach { section ->
				if (section.keyPressed(keyCode, scanCode, modifiers)) {
					return true
				}
			}
			if (super.keyPressed(keyCode, scanCode, modifiers)) {
				return true
			}
		}

		when(keyCode) {
			KeyCodes.KEY_ESCAPE -> {
				// Exit out
				openGui(null)
				return true
			}
			KeyCodes.KEY_ENTER,
			KeyCodes.KEY_KP_ENTER -> {
				val string: String = chatField.text.trim { it <= ' ' }
				// Strip message and send
				MessageSender.sendMessage(string)
				chatField.text = ""
				last = ""
				// Exit
				openGui(null)
				return true
			}
			KeyCodes.KEY_UP -> {
				// Go through previous history
				this.setChatFromHistory(-1)
				return true
			}
			KeyCodes.KEY_DOWN -> {
				// Go through previous history
				this.setChatFromHistory(1)
				return true
			}
			KeyCodes.KEY_PAGE_UP -> {
				// Scroll
				client!!.inGameHud
					.chatHud
					.scroll(client!!.inGameHud.chatHud.visibleLineCount - 1)
				return true
			}
			KeyCodes.KEY_PAGE_DOWN -> {
				// Scroll
				client!!.inGameHud
					.chatHud
					.scroll(-client!!.inGameHud.chatHud.visibleLineCount + 1)
				return true
			}
		}
		if (passEvents) {
			chatField.text = ""
			val key = InputUtil.fromKeyCode(keyCode, scanCode)
			KeyBinding.setKeyPressed(key, true)
			KeyBinding.onKeyPressed(key)
			return true
		}
		return false
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
		var changedVerticalAmount = verticalAmount
		if (changedVerticalAmount > 1.0) {
			changedVerticalAmount = 1.0
		}

		if (changedVerticalAmount < -1.0) {
			changedVerticalAmount = -1.0
		}

		sections.forEach { section ->
			if (section.mouseScrolled(mouseX, mouseY, horizontalAmount, changedVerticalAmount)) {
				return true
			}
		}
		if (!hasShiftDown()) {
			changedVerticalAmount *= 7.0
		}

		// Send to hud to scroll
		client!!.inGameHud.chatHud.scroll(changedVerticalAmount.toInt())
		return true
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		sections.forEach { section ->
			if (section.mouseClicked(mouseX, mouseY, button)) {
				return true
			}
		}
		val hud = client!!.inGameHud.chatHud
		if (hud.mouseClicked(mouseX, mouseY)) {
			return true
		}
		val style = hud.getTextStyleAt(mouseX, mouseY)
		if (style != null && style.clickEvent != null) {
			if (this.handleTextClick(style)) {
				return true
			}
		}
		return (chatField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button))
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
		sections.forEach { section ->
			if (section.mouseReleased(mouseX, mouseY, mouseButton)) {
				return true
			}
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton)
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
		sections.forEach { section ->
			if (section.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
				return true
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
	}

	override fun insertText(text: String, override: Boolean) {
		if (override) {
			chatField.text = text
		} else {
			chatField.write(text)
		}
	}

	private fun setChatFromHistory(i: Int) {
		var targetIndex = this.messageHistorySize + i
		val maxIndex = client!!.inGameHud.chatHud.messageHistory.size
		targetIndex = clamp(targetIndex, 0, maxIndex)
		if (targetIndex != this.messageHistorySize) {
			if (targetIndex == maxIndex) {
				this.messageHistorySize = maxIndex
				chatField.text = this.finalHistory
			} else {
				if (this.messageHistorySize == maxIndex) {
					this.finalHistory = chatField.text
				}

				val hist = client!!.inGameHud.chatHud.messageHistory[targetIndex]
				chatField.text = hist
				sections.forEach { section ->
					section.setChatFromHistory(hist)
				}
				this.messageHistorySize = targetIndex
			}
		}
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
		val hud = client!!.inGameHud.chatHud
		this.focused = this.chatField
		chatField.isFocused = true
		chatField.render(context, mouseX, mouseY, partialTicks)
		renderWithoutBackground(context, mouseX, mouseY, partialTicks)
		sections.forEach { section ->
			section.render(context, mouseX, mouseY, partialTicks)
		}
		val style = hud.getTextStyleAt(mouseX.toDouble(), mouseY.toDouble())
		if (style != null && style.hoverEvent != null) {
			context.drawHoverEvent(textRenderer, style, mouseX, mouseY)
		}
	}

	//Copied from GuiBase, but removed background rendering.
	private fun renderWithoutBackground(drawContext: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if (this.drawContext == null || this.drawContext != drawContext) {
			this.drawContext = drawContext
		}

		this.drawTitle(drawContext, mouseX, mouseY, partialTicks)

		// Draw base widgets
		this.drawWidgets(mouseX, mouseY, drawContext)
		this.drawTextFields(mouseX, mouseY, drawContext)
		this.drawButtons(mouseX, mouseY, partialTicks, drawContext)

		this.drawContents(drawContext, mouseX, mouseY, partialTicks)

		this.drawButtonHoverTexts(mouseX, mouseY, partialTicks, drawContext)
		this.drawHoveredWidget(mouseX, mouseY, drawContext)
		this.drawGuiMessages(drawContext)
	}

	override fun drawScreenBackground(mouseX: Int, mouseY: Int) {
	}

	private fun setText(text: String) {
		chatField.text = text
	}

	companion object {

		var PERMANENT_FOCUS: Boolean = false

		private var last = ""
	}
}
