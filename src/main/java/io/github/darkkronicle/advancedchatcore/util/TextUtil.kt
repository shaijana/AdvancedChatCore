/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import fi.dy.masa.malilib.util.StringUtils
import lombok.experimental.UtilityClass
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextContent
import java.util.*
import java.util.function.BiFunction

@UtilityClass
class TextUtil {

	private val SUPERSCRIPTS = charArrayOf('\u2070', '\u00B9', '\u00B2', '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077',
		'\u2078', '\u2079'
	)

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 *
	 *
	 * https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
	 */
	fun similarity(s1: String, s2: String): Double {
		var longer = s1
		var shorter = s2
		if (s1.length < s2.length) { // longer should always have greater length
			longer = s2
			shorter = s1
		}
		val longerLength = longer.length
		if (longerLength == 0) {
			return 1.0 /* both strings are zero length */
		}
		/* // If you have Apache Commons Text, you can use it to calculate the edit distance:
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
		return (longerLength - editDistance(longer, shorter)) / longerLength.toDouble()
	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	/** https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java  */
	fun editDistance(s1: String, s2: String): Int {
		var s1 = s1
		var s2 = s2
		s1 = s1.lowercase(Locale.getDefault())
		s2 = s2.lowercase(Locale.getDefault())

		val costs = IntArray(s2.length + 1)
		for (i in 0 .. s1.length) {
			var lastValue = i
			for (j in 0 .. s2.length) {
				if (i == 0) costs[j] = j
				else {
					if (j > 0) {
						var newValue = costs[j - 1]
						if (s1[i - 1] != s2[j - 1]) newValue = min(min(newValue, lastValue), costs[j]) + 1
						costs[j - 1] = lastValue
						lastValue = newValue
					}
				}
			}
			if (i > 0) costs[s2.length] = lastValue
		}
		return costs[s2.length]
	}

	/** Get's a superscript from a number  */
	fun toSuperscript(num: Int): String {
		var num = num
		val sb = StringBuilder()
		do {
			sb.append(SUPERSCRIPTS[num % 10])
		} while ((10.let { num /= it; num }) > 0)
		return sb.reverse().toString()
	}

	/** Get's the maximum width of a list of translations  */
	fun getMaxLengthTranslation(translations: Collection<String?>): Int {
		return getMaxLengthTranslation(*translations.toTypedArray<String>())
	}

	/** Get's the maximum width of a list of translations  */
	fun getMaxLengthTranslation(vararg translations: String?): Int {
		val translated: MutableList<String> = ArrayList()
		for (translation in translations) {
			translated.add(StringUtils.translate(translation))
		}
		return getMaxLengthString(translated)
	}

	/** Get's the maximum width of a list of strings  */
	fun getMaxLengthString(strings: Collection<String>): Int {
		return getMaxLengthString(*strings.toTypedArray<String>())
	}

	/** Get's the maximum width of a list of strings  */
	fun getMaxLengthString(vararg strings: String?): Int {
		var max = 0
		for (text in strings) {
			val width = StringUtils.getStringWidth(text)
			if (width > max) {
				max = width
			}
		}
		return max
	}

	private fun filterMatches(
		matches: Map<StringMatch, StringInsert>
	): TreeMap<StringMatch, StringInsert> {
		// Filters through matches that don't make sense
		val map = TreeMap(matches)
		val search: Iterator<StringMatch> = TreeMap(map).keys.iterator()
		var lastEnd = 0
		while (search.hasNext()) {
			val m = search.next()
			// Remove overlaps
			if (m.start!! < lastEnd) {
				map.remove(m)
			} else {
				lastEnd = m.end!!
			}
		}
		return map
	}

	/**
	 * Complex method used to split up the split text in this class and replace matches to a string.
	 *
	 * @param matches Map containing a match and a FluidText provider
	 */
	fun replaceStrings(input: Text, matches: Map<StringMatch, StringInsert>): Text {
		// If there's no matches nothing should get replaced.
		if (matches.size == 0) {
			return input
		}
		// Sort the matches and then get a nice easy iterator for navigation
		val sortedMatches: Iterator<Map.Entry<StringMatch, StringInsert>> =
			filterMatches(matches).entries.iterator()
		if (!sortedMatches.hasNext()) {
			return input
		}
		// List of new RawText to form a new FluidText.
		val newSiblings = TextBuilder()
		// What match this is currently on.
		var match: Map.Entry<StringMatch, StringInsert>? = sortedMatches.next()

		// Total number of chars went through. Used to find where the match end and beginning is.
		var totalchar = 0
		var inMatch = false
		for (text in TextBuilder().append(input).texts) {
			if (text.string == null || text.string.length <= 0) {
				continue
			}
			if (match == null) {
				// No more replacing...
				newSiblings.append(text)
				continue
			}
			val length = text.string.length
			var last = 0
			while (true) {
				if (length + totalchar <= match!!.key.start!!) {
					newSiblings.append(text.string.substring(last), text.style)
					break
				}
				val start = match.key.start!! - totalchar
				val end = match.key.end!! - totalchar
				if (inMatch) {
					if (end <= length) {
						inMatch = false
						newSiblings.append(text.string.substring(end), text.style)
						last = end
						if (!sortedMatches.hasNext()) {
							match = null
							break
						}
						match = sortedMatches.next()
					} else {
						break
					}
				} else if (start < length) {
					// End will go onto another string
					if (start > 0) {
						// Add previous string section
						newSiblings.append(text.string.substring(last, start), text.style)
					}
					if (end >= length) {
						newSiblings.append(match.value.getText(text, match.key)!!)
						if (end == length) {
							if (!sortedMatches.hasNext()) {
								match = null
								break
							}
							match = sortedMatches.next()
						} else {
							inMatch = true
						}
						break
					}
					newSiblings.append(match.value.getText(text, match.key)!!)
					match = if (!sortedMatches.hasNext()) {
						null
					} else {
						sortedMatches.next()
					}
					last = end
					if (match == null || match.key.start!! - totalchar > length) {
						newSiblings.append(text.string.substring(end), text.style)
						break
					}
				} else {
					break
				}
				if (match == null) {
					break
				}
			}
			totalchar = totalchar + length
		}

		return newSiblings.build()
	}

	companion object {

		/**
		 * Splits off the text that is held by a [StringMatch]
		 *
		 * @param match Match to grab text from
		 * @return MutableText of text
		 */
		fun truncate(input: Text, match: StringMatch): MutableText {
			val newSiblings = ArrayList<Text>()
			var start = false
			// Total number of chars went through. Used to find where the match end and beginning is.
			var totalchar = 0
			val siblings = input.siblings
			siblings.add(0, MutableText.of(input.content).fillStyle(input.style))
			for (text in siblings) {
				if (text.content == null || text.string.length <= 0) {
					continue
				}

				val length = text.string.length

				// Checks to see if current text contains the match.start.
				if (totalchar + length > match.start!!) {
					if (totalchar + length >= match.end!!) {
						if (!start) {
							newSiblings.add(
								Text.literal(
									text.string
										.substring(
											match.start!! - totalchar,
											match.end!! - totalchar)).fillStyle(text.style))
						} else {
							newSiblings.add(
								Text.literal(
									text.string.substring(0, match.end!! - totalchar)).fillStyle(text.style))
						}
						val newtext = Text.empty()
						for (sibling in newSiblings) {
							newtext.append(sibling)
						}
						return newtext
					} else {
						if (!start) {
							newSiblings.add(
								Text.literal(
									text.string.substring(match.start!! - totalchar)).fillStyle(text.style))
							start = true
						} else {
							newSiblings.add(text)
						}
					}
				}

				totalchar = totalchar + length
			}

			// At the end we take the siblings created in this method and return them.
			val newtext = Text.empty()
			for (sibling in newSiblings) {
				newtext.append(sibling)
			}
			return newtext
		}

		/**
		 * See's if style changes for specified fluid text
		 * @param text Text to test
		 * @return If style changes
		 */
		fun styleChanges(text: Text): Boolean {
			var style: Style? = null
			if (text.siblings.size == 1) {
				return false
			}
			for (raw in text.siblings) {
				if (style == null) {
					style = raw.style
				} else if (style != raw.style) {
					return true
				}
			}
			return false
		}

		/**
		 * See's if style changes for specified fluid text
		 * @param text Text to test
		 * @param predicate Predicate to see if style has changed enough. Previous, current, different
		 * @return If style changes
		 */
		fun styleChanges(text: Text, predicate: BiFunction<Style, Style, Boolean?>): Boolean {
			var previous: Style? = null
			if (text.siblings.size == 1) {
				return !predicate.apply(text.siblings[0].style, text.siblings[0].style)!!
			}
			for (raw in text.siblings) {
				if (previous == null) {
					previous = raw.style
				} else if (previous != raw.style) {
					if (!predicate.apply(previous, raw.style)!!) {
						return true
					}
					previous = raw.style
				}
			}
			return false
		}

		fun getContent(content: TextContent): String {
			val builder = StringBuilder()
			content.visit<Any?> { s: String? ->
				builder.append(s)
				Optional.empty<Any?>()
			}
			return builder.toString()
		}
	}
}
