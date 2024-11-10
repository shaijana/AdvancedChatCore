/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import fi.dy.masa.malilib.config.IConfigOptionListEntry
import fi.dy.masa.malilib.util.StringUtils
import io.github.darkkronicle.advancedchatcore.finder.CustomFinder
import io.github.darkkronicle.advancedchatcore.finder.LiteralFinder
import io.github.darkkronicle.advancedchatcore.finder.RegexFinder
import io.github.darkkronicle.advancedchatcore.finder.UpperLowerFinder
import io.github.darkkronicle.advancedchatcore.interfaces.IFinder
import java.util.function.Supplier

/** Different methods of searching strings for matches.  */
enum class FindType(
	/** Serialized name of the [FindType]  */
	val configString: String, private val finder: Supplier<IFinder>
) : IConfigOptionListEntry {

	/** An exact match found in the input  */
	LITERAL("literal", Supplier<IFinder> { LiteralFinder() }),

	/** A match found in the input that is case insensitive  */
	UPPERLOWER("upperlower", Supplier<IFinder> { UpperLowerFinder() }),

	/** A regex match found in the input  */
	REGEX("regex", Supplier<IFinder> { RegexFinder() }),

	/**
	 * Use custom ones that mods can create. Defined in [ ]
	 */
	CUSTOM("custom", Supplier<IFinder> { CustomFinder.Companion.getInstance() });

	/** Get's the finder associated with this  */
	fun getFinder(): IFinder {
		return finder.get()
	}

	/**
	 * Get's the serialized name of the object.
	 *
	 * @return The config string
	 */
	override fun getStringValue(): String {
		return configString
	}

	/**
	 * Get's the human readable form of the object.
	 *
	 * @return String that is for the display name.
	 */
	override fun getDisplayName(): String {
		return translate(configString)
	}

	/**
	 * Get's the next [FindType] from the previous one.
	 *
	 * @param forward Should cycle forward
	 * @return Next [FindType]
	 */
	override fun cycle(forward: Boolean): FindType {
		var id = this.ordinal
		if (forward) {
			id++
		} else {
			id--
		}
		if (id >= entries.size) {
			id = 0
		} else if (id < 0) {
			id = entries.size - 1
		}
		return entries[id % entries.size]
	}

	/**
	 * De-serializes a string to [FindType]
	 *
	 * @param value Serialized string
	 * @return The found [FindType], null if none
	 */
	override fun fromString(value: String): FindType {
		return fromFindType(value)
	}

	companion object {

		private fun translate(key: String): String {
			return StringUtils.translate("advancedchat.config.findtype.$key")
		}

		fun fromFindType(findtype: String): FindType {
			for (r in entries) {
				if (r.configString == findtype) {
					return r
				}
			}
			return LITERAL
		}
	}
}
