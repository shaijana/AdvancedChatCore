/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.finder

import io.github.darkkronicle.advancedchatcore.interfaces.IFinder
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class PatternFinder : IFinder {

	abstract fun getPattern(toMatch: String): Pattern?

	override fun isMatch(input: String, toMatch: String): Boolean {
		val pattern: Pattern? = getPattern(toMatch)
		if (pattern == null) {
			return false
		}
		return pattern.matcher(input).find()
	}

	override fun getMatches(input: String, toMatch: String): List<StringMatch?> {
		val matches: MutableList<StringMatch?> = ArrayList()
		val pattern: Pattern? = getPattern(toMatch)
		if (pattern == null) {
			return matches
		}
		val matcher: Matcher = pattern.matcher(input)
		while (matcher.find()) {
			matches.add(StringMatch(matcher.group(), matcher.start(), matcher.end()))
		}
		matcher.reset()
		return matches
	}
}
