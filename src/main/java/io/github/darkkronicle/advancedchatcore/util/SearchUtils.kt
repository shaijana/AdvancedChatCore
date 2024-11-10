/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import io.github.darkkronicle.advancedchatcore.chat.MessageOwner
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import lombok.experimental.UtilityClass
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.text.Text
import java.util.*

/**
 * A class used for helping filters find matches and act on them. Helps with Regular Expressions and
 * means that we don't need this in each class.
 */
@Environment(EnvType.CLIENT)
@UtilityClass
class SearchUtils {

	/**
	 * Method to see if there is a match somewhere with a string with an expression. Is similar to
	 * [.findMatches] just less expensive since it doesn't need to
	 * find every match.
	 *
	 * @param input String to search.
	 * @param toMatch Expression to find.
	 * @param type How toMatch should be interpreted.
	 * @return If a match is found.
	 */
	fun isMatch(input: String, toMatch: String, type: FindType): Boolean {
		val finder = type.finder ?: return false
		return finder.isMatch(input, toMatch)
	}

	/**
	 * Method to see if there is a match somewhere with a string with an expression. Is similar to
	 * [.findMatches] just less expensive since it doesn't need to
	 * find every match.
	 *
	 * @param input String to search.
	 * @param toMatch Expression to find.
	 * @param type How toMatch should be interpreted.
	 * @return If a match is found.
	 */
	fun isMatch(input: Text, toMatch: String, type: FindType): Boolean {
		val finder = type.finder ?: return false
		return finder.isMatch(input, toMatch)
	}

	/**
	 * Get's replacements for a string and matches following the format $\<number></number>\>
	 *
	 * @param groups Matches that are found, will replace
	 * @param input Input with group replacements
	 * @return String with replaced groups
	 */
	fun replaceGroups(groups: List<StringMatch?>, input: String): String {
		// Checks to make it so we don't always have to regex
		if (input.length < 2 || !input.contains("$")) {
			return input
		}
		val replace = findMatches(input, "\\$[0-9]", FindType.REGEX)
		if (replace.isEmpty) {
			return input
		}
		// Ensure sort
		val replaceMatches = TreeSet(replace.get())
		var last = 0
		val edited = StringBuilder()
		for (m in replaceMatches) {
			val digit: Int = m.match.substring(1, 2).toInt()
			if (digit == 0 || digit > groups.size) {
				continue
			}
			edited.append(input, last, m.start!!).append(groups[digit])
			last = m.end!!
		}
		if (last != input.length) {
			edited.append(input.substring(last))
		}
		return edited.toString()
	}

	/**
	 * Method to find all matches within a string. Is similar to [.isMatch]}. This method just finds every match and returns it.
	 *
	 * @param input String to search.
	 * @param toMatch Expression to find.
	 * @param type How toMatch should be interpreted.
	 * @return An Optional containing a list of [StringMatch]
	 */
	fun findMatches(input: String, toMatch: String, type: FindType): Optional<List<StringMatch>> {
		val finder = type.finder ?: return Optional.empty()
		val matches: Set<StringMatch?> = TreeSet(finder.getMatches(input, toMatch))
		if (matches.size != 0) {
			return Optional.of<List<StringMatch>>(ArrayList(matches))
		}
		return Optional.empty()
	}

	/**
	 * Method to find all matches within a text. Is similar to [.isMatch]}. This method just finds every match and returns it.
	 *
	 * @param input Text to search.
	 * @param toMatch Expression to find.
	 * @param type How toMatch should be interpreted.
	 * @return An Optional containing a list of [StringMatch]
	 */
	fun findMatches(input: Text, toMatch: String, type: FindType): Optional<List<StringMatch?>> {
		val finder = type.finder ?: return Optional.empty()
		val matches: Set<StringMatch?> = TreeSet(finder.getMatches(input, toMatch))
		if (matches.size != 0) {
			return Optional.of(ArrayList(matches))
		}
		return Optional.empty()
	}

	/**
	 * Gets first match found based off of conditions
	 *
	 * @param input String to search
	 * @param toMatch Search content
	 * @param type [FindType] way to search
	 * @return Optional of a [StringMatch] if found
	 */
	fun getMatch(input: String, toMatch: String, type: FindType): Optional<StringMatch> {
		val finder = type.finder ?: return Optional.empty()
		// Use treeset to sort the matches
		val matches: Set<StringMatch?> = TreeSet(finder.getMatches(input, toMatch))
		// Add and sort matches
		if (matches.size != 0) {
			return Optional.of<StringMatch>(matches.toTypedArray<StringMatch>().get(0))
		}
		return Optional.empty()
	}

	/**
	 * Gets first match found based off of conditions
	 *
	 * @param input String to search
	 * @param toMatch Search content
	 * @param type [FindType] way to search
	 * @return Optional of a [StringMatch] if found
	 */
	fun getMatch(input: Text, toMatch: String, type: FindType): Optional<StringMatch> {
		val finder = type.finder ?: return Optional.empty()
		// Use treeset to sort the matches
		val matches: Set<StringMatch?> = TreeSet(finder.getMatches(input, toMatch))
		// Add and sort matches
		if (matches.size != 0) {
			return Optional.of<StringMatch>(matches.toTypedArray<StringMatch>().get(0))
		}
		return Optional.empty()
	}

	/**
	 * Get the author of a message using regex
	 *
	 * @param networkHandler Network handler to get player data
	 * @param text Text to search
	 * @return Owner of the message
	 */
	fun getAuthor(networkHandler: ClientPlayNetworkHandler?, text: String): MessageOwner? {
		if (networkHandler == null) {
			return null
		}
		val words =
			findMatches(
				stripColorCodes(text),
				ConfigStorage.General.MESSAGE_OWNER_REGEX.config.stringValue,
				FindType.REGEX)
		if (!words.isPresent) {
			return null
		}
		// Start by just checking names and such
		var player: PlayerListEntry? = null
		var match: StringMatch? = null
		for (m in words.get()) {
			if (player != null) {
				break
			}
			for (e in networkHandler.playerList) {
				// Easy mode
				if ((e.displayName != null
							&& m.match == stripColorCodes(e.displayName!!.string))
					|| m.match == e.profile.name) {
					player = e
					match = m
					break
				}
			}
		}
		// Check for ***everything***
		val entryMatches = HashMap<PlayerListEntry, List<StringMatch>>()
		for (e in networkHandler.playerList) {
			val name =
				stripColorCodes(
					if (e.displayName == null)
						e.profile.name
					else
						e.displayName!!.string)
			val nameWords =
				findMatches(
					name,
					ConfigStorage.General.MESSAGE_OWNER_REGEX.config.stringValue,
					FindType.REGEX)
			if (!nameWords.isPresent) {
				continue
			}
			entryMatches.put(e, nameWords.get())
		}
		for (m in words.get()) {
			for ((key, value) in entryMatches) {
				for (nm in value) {
					if (nm.match == m.match) {
						if (player != null && match!!.start!! <= m.start!!) {
							return MessageOwner(match.match, player)
						}
						return MessageOwner(nm.match, key)
					}
				}
			}
		}
		return null
	}

	/**
	 * Strip color codes from a String
	 *
	 * @param string String to strip
	 * @return String stripped of colorcodes
	 */
	fun stripColorCodes(string: String): String {
		return string.replace("§.".toRegex(), "")
	}

	private val map = TreeMap<Int, String>()

	/**
	 * Turns a number into a Roman Numeral.
	 *
	 *
	 * Example: 4 -> IV
	 *
	 *
	 * https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java/12968022
	 *
	 * @param number Example to convert to
	 * @return String or Roman Numeral
	 */
	fun toRoman(number: Int): String? {
		var number = number
		var neg = false
		if (number == 0) {
			return "O"
		}
		if (number < 0) {
			neg = true
			number = -1 * number
		}
		val l = map.floorKey(number)
		if (number == l) {
			return map[number]
		}
		return if (neg) {
			"-" + map[l] + toRoman(number - l)
		} else {
			map[l] + toRoman(number - l)
		}
	}

	companion object {
		init {
			map.put(1000, "M")
			map.put(900, "CM")
			map.put(500, "D")
			map.put(400, "CD")
			map.put(100, "C")
			map.put(90, "XC")
			map.put(50, "L")
			map.put(40, "XL")
			map.put(10, "X")
			map.put(9, "IX")
			map.put(5, "V")
			map.put(4, "IV")
			map.put(1, "I")
		}
	}
}
