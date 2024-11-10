/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui.buttons

import fi.dy.masa.malilib.util.StringUtils

enum class Buttons(private val translationString: String) {
	BACK("advancedchat.gui.button.back");

	fun createButton(x: Int, y: Int): NamedSimpleButton {
		return NamedSimpleButton(x, y, StringUtils.translate(translationString))
	}
}
