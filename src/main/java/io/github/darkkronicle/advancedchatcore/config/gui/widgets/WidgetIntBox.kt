/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui.widgets

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric
import io.github.darkkronicle.advancedchatcore.util.FindType
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import lombok.Getter
import lombok.Setter
import net.minecraft.client.font.TextRenderer
import java.util.*

class WidgetIntBox(x: Int, y: Int, width: Int, height: Int, textRenderer: TextRenderer?) :
	GuiTextFieldGeneric(x, y, width, height, textRenderer) {

	val apply: Runnable? = null

	init {
		this.setTextPredicate { text: String ->
			if (text == "") {
				return@setTextPredicate true
			}
			try {
				// Only allow numbers!
				text.toInt()
			} catch (e: NumberFormatException) {
				return@setTextPredicate false
			}
			true
		}
		this.setDrawsBackground(true)
	}

	val int: Int?
		get() {
			val text = this.text
			if (text == null || text.length == 0) {
				return null
			}
			try {
				return text.toInt()
			} catch (e: NumberFormatException) {
				// Extra catch
				val omatches: Optional<List<StringMatch>> =
					findMatches(text, "[0-9]+", FindType.REGEX)
				if (!omatches.isPresent) {
					return null
				}
				for (m in omatches.get()) {
					return try {
						m.match.toInt()
					} catch (err: NumberFormatException) {
						null
					}
				}
			}
			return null
		}
}
