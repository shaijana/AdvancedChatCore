/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import io.github.darkkronicle.advancedchatcore.finder.RegexFinder
import io.github.darkkronicle.advancedchatcore.interfaces.IFinder
import lombok.Getter
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import java.util.*

/** An object that holds information about a search.  */
@Environment(EnvType.CLIENT)
class SearchResult(
	/** The input string  */
	@field:Getter private val input: String,
	/** The condition search  */
	@field:Getter private val search: String,
	/** The finder used to find matches  */
	@field:Getter private val finder: IFinder?, matches: List<StringMatch?>
) {

	/** All the [StringMatch] that were found in the input  */
	@Getter
	private val matches: List<StringMatch?> = ArrayList(matches)

	/**
	 * Constructs a search result based off of found information.
	 *
	 * @param input Input to search
	 * @param search Search value
	 * @param finder [IFinder] used to search
	 * @param matches [List] of [StringMatch] that were found
	 */
	init {
		Collections.sort(this.matches)
	}

	fun size(): Int {
		return matches.size
	}

	/**
	 * Get's a group from the result. This only works if it's a [RegexFinder], otherwise it
	 * returns the entire string.
	 *
	 * @param num Group number
	 * @return StringMatch from group. It references the original string.
	 */
	fun getGroup(match: StringMatch, num: Int): StringMatch? {
		if (!matches.contains(match)) {
			return null
		}
		if (finder !is RegexFinder) {
			return match
		}
		try {
			val p = finder.getPattern(input) ?: return null
			val matcher = p.matcher(match.match)
			val group = matcher.group(num)
			val start = matcher.start(num)
			val end = matcher.start(num)
			return StringMatch(group, start, end)
		} catch (e: Exception) {
			return null
		}
	}

	/**
	 * Replaces the groups with a specified match
	 *
	 * @param string Contents to replace to
	 * @param matchIndex If it will replace/return only the first group. -1 will return the full
	 * string.
	 * @return The replaced values in context. If onlyFirst it will only do the context of the first
	 * group.
	 */
	fun getGroupReplacements(string: String, matchIndex: Int): String {
		if (matchIndex >= 0) {
			if (finder is RegexFinder) {
				try {
					val p = finder.getPattern(search)
					if (p != null) {
						return p
							.matcher(matches[matchIndex]!!.match)
							.replaceAll(string)
					}
				} catch (e: Exception) {
					// Didn't work
				}
			}
			return replaceGroups(listOf<StringMatch>(matches[0]), string)
		}
		if (finder is RegexFinder) {
			try {
				val p = finder.getPattern(search)
				if (p != null) {
					return p.matcher(input).replaceAll(string)
				}
			} catch (e: Exception) {
				// Did not work
			}
		}
		return replaceGroups(matches, string)
	}

	companion object {

		/**
		 * A method to construct a SearchResult based off of an input, condition, and [FindType]
		 *
		 * @param input Input string to match from
		 * @param match Search string
		 * @param type [FindType] way to search
		 * @return SearchResult with compiled searches
		 */
		fun searchOf(input: String, match: String, type: FindType): SearchResult {
			val finder = type.finder
			val matches = finder!!.getMatches(input, match)
			return SearchResult(input, match, finder, matches!!)
		}

		/**
		 * A method to construct a SearchResult based off of an input, condition, and [FindType]
		 *
		 * @param input Input string to match from
		 * @param match Search text
		 * @param type [FindType] way to search
		 * @return SearchResult with compiled searches
		 */
		fun searchOf(input: Text, match: String, type: FindType): SearchResult {
			val finder = type.finder
			val matches = finder!!.getMatches(input, match)
			return SearchResult(input.string, match, finder, matches!!)
		}
	}
}
