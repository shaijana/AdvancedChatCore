/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui

import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.render.RenderUtils
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.Colors
import lombok.EqualsAndHashCode
import lombok.ToString
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

/** A simple button  */
@EqualsAndHashCode(callSuper = false)
@ToString
@Environment(EnvType.CLIENT)
open class CleanButton(x: Int, y: Int, width: Int, height: Int, baseColor: Color?, text: String?) :
	ButtonBase(x, y, width, height, text) {

	protected var baseColor: Color?

	private val client: MinecraftClient = MinecraftClient.getInstance()

	/**
	 * Constructs a new simple clean button
	 *
	 * @param x X
	 * @param y Y
	 * @param width Width
	 * @param height Height
	 * @param baseColor Color that it should render when not hovered
	 * @param text Text to render
	 */
	init {
		this.x = x
		this.y = y
		this.baseColor = baseColor
	}

	override fun render(mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext) {
		val relMX = mouseX - x
		val relMY = mouseY - y
		hovered = relMX >= 0 && relMX <= width && relMY >= 0 && relMY <= height
		var color = baseColor
		if (hovered) {
			color = Colors.Companion.getInstance().getColor("white").get().withAlpha(color!!.alpha())
		}
		RenderUtils.drawRect(x, y, width, height, color!!.color())
		drawCenteredString(
			(x + (width / 2)),
			(y + (height / 2) - 3),
			Colors.Companion.getInstance().getColorOrWhite("white").color(),
			displayString,
			context)
	}
}
