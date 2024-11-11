/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import fi.dy.masa.malilib.util.FileUtils
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import lombok.Getter
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.logging.log4j.Level
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

/**
 * https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941
 *
 * @author PimDeWitte
 */
@Environment(EnvType.CLIENT)
object ProfanityUtil {

	@Getter
	private val words: MutableMap<Float, MutableList<String>> = HashMap()

	@Getter
	private var largestWordLength = 0

	fun loadConfigs() {
		try {
			var lines: List<String?>
			val file = FileUtils.getConfigDirectory()
				.toPath()
				.resolve("advancedchat")
				.resolve("swear_words.csv")
				.toFile()
			val fileReader: Reader = if (!file.exists()) {
				// Use built in
				InputStreamReader(AdvancedChatCore.Companion.getResource("swear_words.csv"),
					StandardCharsets.UTF_8)
			} else {
				FileReader(file)
			}
			val csv: CSVParser = CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())
			var counter = 0
			for (record in csv) {
				counter++
				try {
					val word: String = record.get("text")
					val severity: Float = record.get("severity_rating").toFloat()

					if (word.length > largestWordLength) {
						largestWordLength = word.length
					}
					if (!words.containsKey(severity)) {
						words.put(severity, ArrayList())
					}
					words[severity]!!.add(word)
				} catch (e: Exception) {
					AdvancedChatCore.Companion.logger.log(
						Level.ERROR, "Error while initializing profanity words", e)
				}
			}
			AdvancedChatCore.Companion.logger.log(
				Level.INFO, "Loaded $counter words to profanity filter.")
		} catch (e: URISyntaxException) {
			AdvancedChatCore.Companion.logger.log(Level.ERROR, "Error loading swear_words.csv", e)
		} catch (e: IOException) {
			AdvancedChatCore.Companion.logger.log(Level.ERROR, "Error loading swear_words.csv", e)
		}
	}

	/**
	 * Iterates over a String input and checks whether a cuss word was found in a list, then checks
	 * if the word should be ignored (e.g. bass contains the word *ss).
	 */
	fun getBadWords(input: String?, severity: Float, onlyWordBoundaries: Boolean): List<StringMatch?> {
		var input = input ?: return ArrayList()

		val badWords: MutableList<StringMatch?> = ArrayList()
		input = input.lowercase(Locale.getDefault())

		val wordBoundaries: List<Int>
		if (onlyWordBoundaries) {
			wordBoundaries = findMatches(input, "\\b", FindType.REGEX)
				.map<List<Int>>(
					Function<List<StringMatch>, List<Int?>> { matches: List<StringMatch> -> matches.stream().map<Int?> { m: StringMatch -> m.start }.toList() })
				.orElseGet(Supplier<List<Int>> { ArrayList() })
			if (wordBoundaries.size == 0) {
				return ArrayList()
			}
		} else {
			wordBoundaries = ArrayList()
		}


		val wordsToFind = getAboveSeverity(severity)

		// iterate over each letter in the word
		var boundaryIndex = 0
		var index = if (onlyWordBoundaries) wordBoundaries[0] else 0
		while (index < input.length) {
			// from each letter, keep going to find bad words until either the end of the sentence
			// is reached, or the max word length is reached.
			var offset = 1
			while (offset < (input.length + 1 - index) && offset < largestWordLength) {
				val wordToCheck: String = input.substring(index, index + offset)
				if (wordsToFind.contains(wordToCheck) && (!onlyWordBoundaries || (wordBoundaries.contains(index + offset)))) {
					// for example, if you want to say the word bass, that should be possible.
					badWords.add(StringMatch(wordToCheck, index, index + offset))
				}
				offset++
			}
			if (onlyWordBoundaries) {
				boundaryIndex++
				if (boundaryIndex >= wordBoundaries.size) {
					break
				}
				index = wordBoundaries[boundaryIndex]
			} else {
				index++
			}
		}
		return badWords
	}

	fun getAboveSeverity(severity: Float): List<String> {
		val list: MutableList<String> = ArrayList()
		for ((key, value) in words) {
			if (key >= severity) {
				list.addAll(value)
			}
		}
		return list
	}
}
