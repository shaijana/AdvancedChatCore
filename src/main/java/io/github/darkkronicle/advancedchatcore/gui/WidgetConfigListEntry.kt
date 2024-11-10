/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui

import com.mojang.blaze3d.systems.RenderSystem
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper
import fi.dy.masa.malilib.render.RenderUtils
import io.github.darkkronicle.advancedchatcore.util.Colors
import lombok.Getter
import lombok.Setter
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext

@Environment(EnvType.CLIENT)
abstract class WidgetConfigListEntry<TYPE> @kotlin.jvm.JvmOverloads constructor(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	private val odd: Boolean,
	entry: TYPE,
	listIndex: Int,
	private val hoverLines: List<String>? = null
) : WidgetListEntryBase<TYPE>(x, y, width, height, entry, listIndex) {

	@Setter
	@Getter
	private val buttonStartX = x + width

	val name: String
		/** Get's the name to render for the entry.  */
		get() = ""

	val textFields: List<TextFieldWrapper<GuiTextFieldGeneric>>?
		get() = null

	override fun render(mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext) {
		RenderUtils.color(1f, 1f, 1f, 1f)

		// Draw a lighter background for the hovered and the selected entry
		if (selected || this.isMouseOver(mouseX, mouseY)) {
			RenderUtils.drawRect(
				this.x,
				this.y,
				this.width,
				this.height,
				Colors.Companion.getInstance().getColorOrWhite("white").withAlpha(150).color())
		} else if (this.odd) {
			RenderUtils.drawRect(
				this.x,
				this.y,
				this.width,
				this.height,
				Colors.Companion.getInstance().getColorOrWhite("white").withAlpha(70).color())
		} else {
			RenderUtils.drawRect(
				this.x,
				this.y,
				this.width,
				this.height,
				Colors.Companion.getInstance().getColorOrWhite("white").withAlpha(50).color())
		}

		renderEntry(mouseX, mouseY, selected, context)

		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderSystem.disableBlend()

		this.drawTextFields(mouseX, mouseY, context)

		super.render(mouseX, mouseY, selected, context)

		RenderUtils.disableDiffuseLighting()
	}

	/**
	 * Render's in the middle of the rendering cycle. After the background, but before it goes to
	 * super.
	 */
	fun renderEntry(mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext?) {
		val name = name
		this.drawString(
			this.x + 4,
			this.y + 7,
			Colors.Companion.getInstance().getColorOrWhite("white").color(),
			name,
			context)
	}

	override fun postRenderHovered(
		mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext
	) {
		super.postRenderHovered(mouseX, mouseY, selected, context)
		if (hoverLines == null) {
			return
		}

		if (mouseX >= this.x && mouseX < this.buttonStartX && mouseY >= this.y && mouseY <= this.y + this.height) {
			RenderUtils.drawHoverText(mouseX, mouseY, this.hoverLines, context)
		}
	}

	override fun onKeyTypedImpl(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (textFields == null) {
			return false
		}
		for (field in textFields!!) {
			if (field != null && field.isFocused) {
				return field.onKeyTyped(keyCode, scanCode, modifiers)
			}
		}
		return false
	}

	override fun onCharTypedImpl(charIn: Char, modifiers: Int): Boolean {
		if (textFields != null) {
			for (field in textFields!!) {
				if (field != null && field.onCharTyped(charIn, modifiers)) {
					return true
				}
			}
		}

		return super.onCharTypedImpl(charIn, modifiers)
	}

	override fun onMouseClickedImpl(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		if (super.onMouseClickedImpl(mouseX, mouseY, mouseButton)) {
			return true
		}

		var ret = false

		if (textFields != null) {
			for (field in textFields!!) {
				if (field != null) {
					ret = field.textField.mouseClicked(mouseX.toDouble(), mouseY.toDouble(), mouseButton)
				}
			}
		}

		if (!subWidgets.isEmpty()) {
			for (widget in this.subWidgets) {
				ret = ret or (
						widget.isMouseOver(mouseX, mouseY)
								&& widget.onMouseClicked(mouseX, mouseY, mouseButton))
			}
		}

		return ret
	}

	protected fun drawTextFields(mouseX: Int, mouseY: Int, context: DrawContext?) {
		if (textFields == null) {
			return
		}
		for (field in textFields!!) {
			field.textField.render(context, mouseX, mouseY, 0f)
		}
	}
}
