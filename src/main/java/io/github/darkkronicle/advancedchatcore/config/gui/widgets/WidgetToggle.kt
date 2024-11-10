/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui.widgets

import fi.dy.masa.malilib.gui.button.ButtonOnOff
import lombok.Getter

class WidgetToggle(
	x: Int,
	y: Int,
	width: Int,
	rightAlign: Boolean,
	translationKey: String?,
	@field:Getter private var currentlyOn: Boolean,
	vararg hoverStrings: String?
) : ButtonOnOff(x, y, width, rightAlign, translationKey, currentlyOn, *hoverStrings) {

	override fun onMouseClickedImpl(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		this.currentlyOn = !this.currentlyOn
		this.updateDisplayString(this.currentlyOn)
		return super.onMouseClickedImpl(mouseX, mouseY, mouseButton)
	}

	fun setOn(on: Boolean) {
		this.currentlyOn = on
		this.updateDisplayString(on)
	}
}
