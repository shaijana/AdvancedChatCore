/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import fi.dy.masa.malilib.util.KeyCodes
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import io.github.darkkronicle.advancedchatcore.util.StyleFormatter
import io.github.darkkronicle.advancedchatcore.util.TextBuilder
import io.github.darkkronicle.advancedchatcore.util.TextUtil
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import java.util.*
import java.util.function.BiFunction

open class AdvancedTextField(
	textRenderer: TextRenderer,
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	copyFrom: TextFieldWidget?,
	text: Text?
) : TextFieldWidget(textRenderer, x, y, width, height, copyFrom, text) {

	/**
	 * Stores the last saved snapshot of the box. This ensures that not every character update is put in, but instead groups.
	 */
	private var lastSaved = ""

	/** Snapshots of chat box  */
	private val history = mutableListOf<String>()
	private var focusedTicks = 0
	private var renderLines = mutableListOf<Text>()
	private val textRenderer: TextRenderer
	private var suggestion: String? = null
	private var maxLength = 32
	private var selectionEnd = 0
	private var selectionStart = 0

	// TODO Split?
	private var renderTextProvider =
		BiFunction { string: String, _: Int -> OrderedText.styledForwardsVisitedString(string, Style.EMPTY) }

	private var historyIndex = -1

	constructor(textRenderer: TextRenderer, x: Int, y: Int, width: Int, height: Int, text: Text?) : this(textRenderer, x, y, width, height, null, text)

	init {
		history.add("")
		this.textRenderer = textRenderer
		updateRender()
	}

	fun tick() {
		focusedTicks++
	}

	override fun setRenderTextProvider(renderTextProvider: BiFunction<String, Int, OrderedText>) {
		this.renderTextProvider = renderTextProvider
	}

	override fun setMaxLength(maxLength: Int) {
		this.maxLength = maxLength
		super.setMaxLength(maxLength)
	}

	/** Triggers undo for the text box  */
	fun undo() {
		// Save the current snapshot if it's been edited
		if (this.lastSaved != this.text && historyIndex < 0) {
			addToHistory(text)
		}
		// History index < 0 means not in the middle of undoing
		if (historyIndex < 0) {
			historyIndex = history.size - 1
		}
		// Check that we're not at index 0
		if (historyIndex != 0) {
			historyIndex--
		}
		// Set the text but don't update
		setText(history[historyIndex], false)
	}

	fun redo() {
		if (historyIndex < 0 || historyIndex >= history.size - 1) {
			// No stuff to redo...
			return
		}
		historyIndex++
		setText(history[historyIndex], false)
	}

	override fun write(text: String) {
		super.write(text)
		updateHistory()
		updateRender()
	}

	override fun eraseCharacters(characterOffset: Int) {
		super.eraseCharacters(characterOffset)
		updateHistory()
		updateRender()
	}

	override fun setSuggestion(suggestion: String?) {
		this.suggestion = suggestion
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		val renderY = y - (renderLines.size - 1) * (textRenderer.fontHeight + 2)
		if (mouseY < renderY - 2 || mouseY > y + height + 2 || mouseX < x - 2 || mouseX > x + width + 4) {
			return false
		}
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		val color = 0xE0E0E0
		val cursor = cursor
		var cursorRow = renderLines.size - 1
		val renderCursor = this.isFocused && focusedTicks / 6 % 2 == 0
		var renderY = y - (renderLines.size - 1) * (textRenderer.fontHeight + 2)
		var endX = 0
		var charCount = 0
		var cursorX = -1
		val selection = selectionStart != selectionEnd
		var started = false
		var ended = false
		val selStart: Int
		val selEnd: Int
		when {
			this.selectionStart < this.selectionEnd -> {
				selStart = this.selectionStart
				selEnd = this.selectionEnd
			}
			else -> {
				selStart = this.selectionEnd
				selEnd = this.selectionStart
			}
		}
		val x = x
		val y = y
		context.fill(getX() - 2, renderY - 2, getX() + width + 4, getY() + height + 4, ConfigStorage.ChatScreen.COLOR.config.get().color)

		renderLines.indices.forEach { line ->
			val text = renderLines[line]
			if (cursor >= charCount && cursor < text.string.length + charCount) {
				cursorX = textRenderer.getWidth(text.string.substring(0, cursor - charCount))
				cursorRow = line
			}
			endX = context.drawTextWithShadow(textRenderer, text, x, renderY, color)
			if (selection) {
				if (!started && selStart >= charCount && selStart <= text.string.length + charCount) {
					started = true
					val startX: Int = textRenderer.getWidth(TextUtil.truncate(text, StringMatch("", 0, selStart - charCount)))
					if (selEnd > charCount && selEnd <= text.string.length + charCount) {
						ended = true
						val sEndX: Int = textRenderer.getWidth(TextUtil.truncate(text, StringMatch("", 0, selEnd - charCount)))
						drawSelectionHighlight(x + startX, renderY - 1, x + sEndX, renderY + textRenderer.fontHeight)
					} else {
						val sEndX = textRenderer.getWidth(text)
						drawSelectionHighlight(x + startX, renderY - 1, x + sEndX, renderY + textRenderer.fontHeight)
					}
				} else if (started && !ended) {
					if (selEnd >= charCount && selEnd <= text.string.length + charCount) {
						ended = true
						val sEndX: Int = textRenderer.getWidth(TextUtil.truncate(text, StringMatch("", 0, selEnd - charCount)))
						drawSelectionHighlight(x, renderY - 1, x + sEndX, renderY + textRenderer.fontHeight)
					} else {
						val sEndX = textRenderer.getWidth(text)
						drawSelectionHighlight(x, renderY - 1, x + sEndX, renderY + textRenderer.fontHeight)
					}
				}
			}
			renderY += textRenderer.fontHeight + 2
			charCount += text.string.length
		}
		if (cursorX < 0) {
			cursorX = endX
		}
		val cursorAtEnd = getCursor() == text.length
		if (!cursorAtEnd && this.suggestion != null) {
			context.drawTextWithShadow(textRenderer, this.suggestion, endX - 1, y, -8355712)
		}
		if (renderCursor) {
			val cursorY = y - (renderLines.size - 1 - cursorRow) * (textRenderer.fontHeight + 2)
			if (cursorAtEnd) {
				context.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + 1 + textRenderer.fontHeight, -3092272)
			} else {
				context.drawTextWithShadow(textRenderer, "_", x + cursorX, cursorY, color)
			}
		}
	}

	private fun drawSelectionHighlight(x1: Int, y1: Int, x2: Int, y2: Int) {
		var x1 = x1
		var y1 = y1
		var x2 = x2
		var y2 = y2
		val x = x
		val y = y
		var i: Int
		if (x1 < x2) {
			i = x1
			x1 = x2
			x2 = i
		}
		if (y1 < y2) {
			i = y1
			y1 = y2
			y2 = i
		}
		if (x2 > x + this.width) {
			x2 = x + this.width
		}
		if (x1 > x + this.width) {
			x1 = x + this.width
		}
		val tessellator = Tessellator.getInstance()
		val bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)
		RenderSystem.setShader(ShaderProgramKeys.POSITION)
		RenderSystem.setShaderColor(0.0f, 0.0f, 1.0f, 1.0f)
		//        RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp()
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE)
		bufferBuilder.vertex(x1.toFloat(), y2.toFloat(), 0.0f)
		bufferBuilder.vertex(x2.toFloat(), y2.toFloat(), 0.0f)
		bufferBuilder.vertex(x2.toFloat(), y1.toFloat(), 0.0f)
		bufferBuilder.vertex(x1.toFloat(), y1.toFloat(), 0.0f)
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
		RenderSystem.disableColorLogicOp()
		//        RenderSystem.enableTexture();
	}

	override fun setSelectionStart(cursor: Int) {
		this.selectionStart = MathHelper.clamp(cursor, 0, text.length)
		super.setSelectionStart(cursor)
	}

	override fun setSelectionEnd(index: Int) {
		val i = text.length
		this.selectionEnd = MathHelper.clamp(index, 0, i)
		super.setSelectionEnd(index)
	}

	override fun getType(): Selectable.SelectionType {
		return super.getType()
	}

	/**
	 * Sets the text for the text field
	 *
	 * @param text Text to set
	 * @param update Updates the history
	 */
	fun setText(text: String?, update: Boolean) {
		// Wrapper class for setText
		super.setText(text)
		if (update) {
			updateHistory()
		}
		updateRender()
	}

	override fun setText(text: String) {
		setText(text, true)
	}

	private fun updateRender() {
		val formatted = renderTextProvider.apply(text, 0)
		renderLines = StyleFormatter.Companion.wrapText(textRenderer, getWidth(), TextBuilder().append(formatted).build())
	}

	private fun updateHistory() {
		if (historyIndex >= 0) {
			// Remove all history after what has gone back
			pruneHistory(historyIndex + 1)
			historyIndex = -1
		}
		// Check to see if it should log
		val dif = text.length - lastSaved.length
		val sim: Double = similarity(text, lastSaved)
		if (sim >= .3 && (dif < 5 && dif * -1 < 5) || (sim >= .9)) {
			return
		}
		addToHistory(text)
	}

	private fun addToHistory(text: String) {
		this.lastSaved = text
		history.add(text)
		while (history.size > MAX_HISTORY) {
			history.removeAt(0)
		}
	}

	/**
	 * Remove's all history past a certain index
	 *
	 * @param index Index to prune from. Non-inclusive.
	 */
	private fun pruneHistory(index: Int) {
		if (index == 0) {
			history.clear()
			return
		}
		while (history.size > index) {
			history.removeAt(history.size - 1)
		}
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (!this.isActive) {
			return false
		}
		if (!isUndo(keyCode)) {
			return super.keyPressed(keyCode, scanCode, modifiers)
		}
		if (Screen.hasShiftDown()) {
			redo()
		} else {
			undo()
		}
		return true
	}

	override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
		// Crashes here because Text is null
	}

	companion object {

		private const val MAX_HISTORY = 50

		fun isUndo(code: Int): Boolean {
			// Undo (Ctrl + Z)
			return code == KeyCodes.KEY_Z && Screen.hasControlDown() && !Screen.hasAltDown()
		}
	}
}
