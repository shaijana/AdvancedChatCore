/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import net.minecraft.client.gui.screen.Screen
import java.util.function.Supplier

/** An interface to supply a screen.  */
interface IScreenSupplier {

	/**
	 * Get's a supplier of a screen based off of a parent.
	 *
	 * @param parent Parent screen (nullable)
	 * @return Supplier of a screen
	 */
	fun getScreen(@Nullable parent: Screen?): Supplier<Screen?>?
}
