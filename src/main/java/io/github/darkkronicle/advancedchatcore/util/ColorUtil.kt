/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import lombok.experimental.UtilityClass
import net.minecraft.util.Formatting

/**
 * A static utility class that helps when dealing with colors that use bit shifting, like Minecraft.
 * In here there is a color storage class that makes getting int and creating new colors simple.
 */
@UtilityClass
class ColorUtil {
	// Probably my best hope for color...
	// https://github.com/parzivail/StarWarsMod/blob/master/src/main/java/com/parzivail/util/ui/GLPalette.java
	// I don't like color ints :(
	// intToColor and colorToInt from parzivail https://github.com/parzivail (slightly modified to
	// account for Alpha)
	/**
	 * Turns a packed RGB color into a Color
	 *
	 * @param rgb The color to unpack
	 * @return The new Color
	 */
	fun intToColor4f(rgb: Int): Color {
		val alpha = rgb shr 24 and 0xFF
		val red = rgb shr 16 and 0xFF
		val green = rgb shr 8 and 0xFF
		val blue = rgb and 0xFF
		return Color(red, green, blue, alpha)
	}

	/**
	 * Turns a Color into a packed RGB int
	 *
	 * @param c The color to pack
	 * @return The packed int
	 */
	fun colorToInt4f(c: Color): Int {
		var rgb = c.alpha()
		rgb = (rgb shl 8) + c.red()
		rgb = (rgb shl 8) + c.green()
		rgb = (rgb shl 8) + c.blue()
		return rgb
	}

	fun fade(color: Color, percent: Float): Color {
		val alpha = color.alpha().toFloat()
		return color.withAlpha(floor((alpha * percent)) as Int)
	}

	fun colorFromFormatting(formatting: Formatting): Color {
		return Color(formatting.colorValue!!)
	}
}
