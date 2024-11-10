/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import io.github.darkkronicle.advancedchatcore.util.AbstractRegistry
import net.minecraft.client.gui.screen.Screen
import java.util.function.Supplier

/**
 * An interface to get a RegistryOption that can be saved/configured in game.
 *
 * @param <TYPE> Object type to be wrapped
</TYPE> */
interface RegistryOption<TYPE> {

	/**
	 * Get's the object that this option is wrapping
	 *
	 * @return Object that is wrapped
	 */
	val option: TYPE

	/**
	 * Whether or not this option is currently active
	 *
	 * @return If it is active
	 */
	val isActive: Boolean

	/**
	 * Get's the string that will be saved inside of the JSON.
	 *
	 * @return Save string
	 */
	val saveString: String

	/**
	 * Copies the registry option without a parent registry
	 *
	 * @return A copy of this object
	 */
	fun copy(): RegistryOption<TYPE> {
		return copy(null)
	}

	val isHidden: Boolean
		/**
		 * If this value should be hidden in the menu
		 *
		 * @return If it should be hidden
		 */
		get() {
			return false
		}

	/**
	 * Copies the registry option with a parent registry that it will be tied to.
	 *
	 *
	 * Used for copying registries so that options can be modified easily.
	 *
	 * @param registry Registry that will be the parent
	 * @return Copied object
	 */
	fun copy(registry: AbstractRegistry<TYPE, *>?): RegistryOption<TYPE>

	/**
	 * Get's the configuration screen of this option. If this isn't null a button will be generated
	 * if in a GUI.
	 *
	 * @param parent Parent screen
	 * @return Supplier for the config screen. Null if it doesn't exist.
	 */
	fun getScreen(parent: Screen?): Supplier<Screen?>? {
		if (option !is IScreenSupplier) {
			return null
		}
		return (option as IScreenSupplier).getScreen(parent)
	}

	val hoverLines: List<String>?
		/**
		 * Hover lines to show when in the GUI and hovering.
		 *
		 * @return List of strings that contain the hover lines
		 */
		get() {
			return null
		}
}
