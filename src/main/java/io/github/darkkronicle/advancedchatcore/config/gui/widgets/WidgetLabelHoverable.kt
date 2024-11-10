/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui.widgets

import fi.dy.masa.malilib.gui.widgets.WidgetLabel
import fi.dy.masa.malilib.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import java.util.*

class WidgetLabelHoverable : WidgetLabel {

	private var hoverLines: List<String>? = null

	constructor(x: Int, y: Int, width: Int, height: Int, textColor: Int, lines: List<String?>) : super(x, y, width, height, textColor, lines)

	constructor(x: Int, y: Int, width: Int, height: Int, textColor: Int, vararg text: String?) : super(x, y, width, height, textColor, *text)

	fun setHoverLines(vararg hoverLines: String?) {
		this.hoverLines = Arrays.asList(*hoverLines)
	}

	override fun postRenderHovered(
		mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext
	) {
		super.postRenderHovered(mouseX, mouseY, selected, context)

		if (hoverLines == null) {
			return
		}

		if (mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
			RenderUtils.drawHoverText(mouseX, mouseY, this.hoverLines, context)
		}
	}
}
