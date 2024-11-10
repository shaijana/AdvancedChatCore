/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import io.github.darkkronicle.advancedchatcore.interfaces.RegistryOption
import lombok.Getter
import java.util.*
import java.util.function.Supplier

/**
 * Create a registry with options that can be added from anywhere.
 *
 * @param <TYPE> Class to be wrapped
 * @param <OPTION> Wrapper option for the class
</OPTION></TYPE> */
abstract class AbstractRegistry<TYPE, OPTION : RegistryOption<TYPE>?> {

	private val options: MutableList<OPTION> = ArrayList()

	val all: List<OPTION>
		get() = options

	@Getter
	private var defaultOption: OPTION? = null

	/**
	 * Add's an option directly. Recommended to use register
	 *
	 * @param option Option to add
	 */
	protected fun add(option: OPTION) {
		if (defaultOption == null) {
			defaultOption = option
		}
		options.add(option)
	}

	@kotlin.jvm.JvmOverloads
	fun register(
		replace: Supplier<TYPE>,
		saveString: String,
		translation: String?,
		infoTranslation: String?,
		active: Boolean = true,
		setDefault: Boolean = false,
		hidden: Boolean = false
	) {
		val option =
			constructOption(
				replace,
				saveString,
				translation,
				infoTranslation,
				active,
				setDefault,
				hidden)
		options.add(option)
		if (setDefault || defaultOption == null) {
			defaultOption = option
		}
	}

	abstract override fun clone(): AbstractRegistry<TYPE, OPTION>

	abstract fun constructOption(
		type: Supplier<TYPE>,
		saveString: String,
		translation: String?,
		infoTranslation: String?,
		active: Boolean,
		setDefault: Boolean,
		hidden: Boolean
	): OPTION

	fun setDefaultOption(newDefault: OPTION) {
		defaultOption = newDefault
	}

	fun fromString(string: String): OPTION? {
		for (m in options) {
			if (m.getSaveString() == string) {
				return m
			}
		}
		return defaultOption
	}

	fun getNext(option: OPTION, forward: Boolean): OPTION? {
		if (options.size == 0) {
			return null
		}
		var i = options.indexOf(option)
		if (i < 0) {
			return options[0]
		}
		if (forward) {
			i = i + 1
			if (i >= options.size) {
				return options[0]
			}
		} else {
			i = i - 1
			if (i < 0) {
				return options[options.size - 1]
			}
		}
		return options[i]
	}
}
