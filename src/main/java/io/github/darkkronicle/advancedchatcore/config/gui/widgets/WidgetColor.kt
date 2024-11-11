/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui.widgets

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric
import fi.dy.masa.malilib.render.RenderUtils
import fi.dy.masa.malilib.util.StringUtils
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.Colors
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import java.util.*

class WidgetColor(x: Int, y: Int, width: Int, height: Int, private var currentColor: Color, textRenderer: TextRenderer?) :
	GuiTextFieldGeneric(x, y, width - 22, height, textRenderer) {

	private val colorX = x + width - 20

	init {
		text = String.format("#%08X", currentColor.color)
	}

	override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
		val y = this.y
		RenderUtils.drawRect(this.colorX, y, 19, 19, -0x1)
		RenderUtils.drawRect(this.colorX + 1, y + 1, 17, 17, -0x1000000)
		RenderUtils.drawRect(this.colorX + 2, y + 2, 15, 15, currentColor.color)
	}

	override fun write(text: String) {
		super.write(text)
		andRefreshColor4f
	}

	override fun getWidth(): Int {
		return super.getWidth() + 22
	}

	private val andRefreshColor4f: Color
		get() {
			val color: Optional<Color> =
				Colors.getColor(text)
			if (color.isPresent) {
				this.currentColor = color.get()
				return this.currentColor
			}
			this.currentColor = Color(StringUtils.getColor(text, 0))
			return this.currentColor
		}
}
