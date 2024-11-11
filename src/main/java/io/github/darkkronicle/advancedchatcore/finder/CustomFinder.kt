/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.finder

import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.interfaces.IFinder
import io.github.darkkronicle.advancedchatcore.interfaces.RegistryOption
import io.github.darkkronicle.advancedchatcore.util.AbstractRegistry
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.apache.logging.log4j.Level
import java.util.*
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class CustomFinder private constructor() :
	AbstractRegistry<IFinder, CustomFinder.CustomFinderOption>(),
	IFinder {

	override fun isMatch(input: String, toMatch: String): Boolean {
		val option: Optional<IFinder> = getFinder(toMatch)
		if (option.isEmpty()) {
			// Invalid :(
			AdvancedChatCore.Companion.logger.log(Level.WARN, getHelp(toMatch))
			return false
		}
		return option.get().isMatch(input, toMatch)
	}

	private fun getHelp(toMatch: String): String {
		val builder: StringBuilder =
			StringBuilder()
				.append("Custom find type was used but the match ")
				.append(toMatch)
				.append(" does not exist in the registry! Possible correct options: ")
		for (o: CustomFinderOption in getAll()) {
			builder.append(o.saveString).append(", ")
		}
		return builder.substring(0, builder.length - 2)
	}

	override fun getMatches(input: String, toMatch: String): List<StringMatch?> {
		val option: Optional<IFinder> = getFinder(toMatch)
		if (option.isEmpty()) {
			// Invalid :(
			AdvancedChatCore.Companion.logger.log(Level.WARN, getHelp(toMatch))
			return ArrayList()
		}
		return option.get().getMatches(input, toMatch)
	}

	fun getFinder(toMatch: String): Optional<IFinder> {
		for (o: CustomFinderOption in getAll()) {
			if (toMatch.startsWith(o.saveString)) {
				return Optional.of(o.option)
			}
		}
		return Optional.empty()
	}

	override fun clone(): CustomFinder {
		val finder: CustomFinder = CustomFinder()
		for (o: CustomFinderOption in getAll()) {
			finder.add(o.copy(this))
		}
		return finder
	}

	override fun constructOption(
		type: Supplier<IFinder>,
		saveString: String,
		translation: String?,
		infoTranslation: String?,
		active: Boolean,
		setDefault: Boolean,
		hidden: Boolean
	): CustomFinderOption {
		return CustomFinderOption(
			type, saveString, translation, infoTranslation, active, hidden, this)
	}

	class CustomFinderOption // Only register
	private constructor(
		override val option: IFinder,
		override val saveString: String,
		private val translation: String?,
		private val infoTranslation: String?,
		override val isActive: Boolean,
		private val hidden: Boolean,
		private val registry: CustomFinder
	) : RegistryOption<IFinder> {

		constructor(
			finder: Supplier<IFinder>,
			saveString: String,
			translation: String?,
			infoTranslation: String?,
			active: Boolean,
			hidden: Boolean,
			registry: CustomFinder
		) : this(finder.get(), saveString, translation, infoTranslation, active, hidden, registry)

		override fun copy(registry: AbstractRegistry<IFinder?, *>?): CustomFinderOption {
			return CustomFinderOption(
				option,
				saveString,
				translation,
				infoTranslation,
				isActive,
				isHidden(),
				if (registry == null) this.registry else registry as CustomFinder)
		}
	}

	companion object {

		val instance: CustomFinder = CustomFinder()

		const val NAME: String = "customfind"
	}
}
