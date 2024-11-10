/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui.buttons

import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import io.github.darkkronicle.advancedchatcore.interfaces.IClosable

class BackButtonListener(private val closable: IClosable) : IButtonActionListener {

	override fun actionPerformedWithButton(button: ButtonBase, mouseButton: Int) {
		closable.close(button)
	}
}
