/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import lombok.AccessLevel
import lombok.Getter
import lombok.Value
import lombok.With
import lombok.experimental.Accessors

@Value
@Accessors(fluent = true)
class Color {

	@Getter
	var red: Int

	@Getter
	var green: Int

	@Getter
	var blue: Int

	@Getter
	@With(AccessLevel.PUBLIC)
	var alpha: Int

	@Getter
	var color: Int

	constructor(color: Int) {
		this.color = color
		val completeColor: Color = intToColor4f(color)
		this.red = completeColor.red()
		this.green = completeColor.green()
		this.blue = completeColor.blue()
		this.alpha = completeColor.alpha()
	}

	constructor(red: Int, green: Int, blue: Int, alpha: Int) {
		var red = red
		var green = green
		var blue = blue
		var alpha = alpha
		if (red > 255) {
			red = 255
		}
		if (green > 255) {
			green = 255
		}
		if (blue > 255) {
			blue = 255
		}
		if (alpha > 255) {
			alpha = 255
		}
		this.red = red
		this.green = green
		this.blue = blue
		this.alpha = alpha
		this.color = colorToInt4f(this)
	}

	/** Generated for use of Lombok's @With  */
	constructor(red: Int, green: Int, blue: Int, alpha: Int, color: Int) {
		this.red = red
		this.green = green
		this.blue = blue
		this.alpha = alpha
		this.color = colorToInt4f(this)
	}

	val string: String
		get() = String.format("#%08X", color)
}
