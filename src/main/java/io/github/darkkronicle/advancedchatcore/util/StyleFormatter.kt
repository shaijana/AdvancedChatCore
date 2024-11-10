/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Unit
import java.util.*

/** Class to format text without losing data  */
@Environment(EnvType.CLIENT)
class StyleFormatter(private val visitor: FormattingVisitable, private val length: Int) {

	/**
	 * An interface to take multiple inputs from a string that has the Section Symbol formatting
	 * combined with standard [Text] formatting.
	 */
	interface FormattingVisitable {

		/**
		 * Accepts a character with information about current formatting
		 *
		 * @param c Current character
		 * @param currentIndex The current index of the raw string
		 * @param realIndex The current index without formatting symbols
		 * @param textStyle The style that the text currently has
		 * @param formattingStyle The style that combines text formatting and formatting symbols
		 * @return Whether to continue
		 */
		fun accept(
			c: Char, currentIndex: Int, realIndex: Int, textStyle: Style?, formattingStyle: Style?
		): Boolean
	}

	private var currentIndex = 0
	private var realIndex = 0
	private var skipBy = 0
	private var currentStyle: Style?
	private var lastTextStyle: Style? = null

	/** Results of different parts of formatting  */
	private enum class Result {

		/** Go up a character  */
		INCREMENT,

		/** Go to the next [StringVisitable]  */
		SKIP,

		/** STOP  */
		TERMINATE,
	}

	/**
	 * Creates a StyleFormatter for a given length and a [FormattingVisitable]
	 *
	 *
	 * This class is meant to be updated with a [StringVisitable.StyledVisitor]
	 *
	 * @param visitor [FormattingVisitable] to get updated with each visible character
	 * @param length Length of the string
	 */
	init {
		this.currentStyle = Style.EMPTY
	}

	/** Sends the current character with the current information to the visitor.  */
	private fun sendToVisitor(c: Char, textStyle: Style): Boolean {
		return visitor.accept(c, currentIndex, realIndex, textStyle, currentStyle)
	}

	/** Handles how section symbols get processed  */
	private fun updateSection(textStyle: Style, nextChar: Char?, rest: String): Result {
		if (nextChar == null) {
			return Result.SKIP
		}
		if (nextChar == '#') {
			if (rest.length > 6) {
				val format: String = rest.substring(1, 7)
				if (!isMatch(format, "^[0-9a-fA-F]{6}", FindType.REGEX)) {
					currentIndex++
					return Result.INCREMENT
				}
				val red: Int = format.substring(0, 2).toInt(16)
				val green: Int = format.substring(2, 4).toInt(16)
				val blue: Int = format.substring(4, 6).toInt(16)
				val color = TextColor.fromRgb(Color(red, green, blue, 255).color())
				currentStyle = if (currentStyle == Style.EMPTY || currentStyle == textStyle) {
					// If it's empty or different rely on just the current text style
					// Arbitrary color
					textStyle.withExclusiveFormatting(Formatting.BLACK)
				} else {
					// Styles are different so we take what happened before. This allows us to chain
					// formatting symbols.

					// Arbitrary color to reset

					currentStyle!!.withExclusiveFormatting(Formatting.BLACK)
				}
				currentStyle = currentStyle.withColor(color)
				currentIndex += 7
				skipBy = 6
			}
			return Result.INCREMENT
		}
		val formatting = Formatting.byCode(nextChar)
		if (formatting != null) {
			currentStyle = if (formatting == Formatting.RESET) {
				// If it resets, just go to what the current text is.
				textStyle
			} else {
				if (currentStyle == Style.EMPTY || currentStyle == textStyle) {
					// If it's empty or different rely on just the current text style
					textStyle.withExclusiveFormatting(formatting)
				} else {
					// Styles are different so we take what happened before. This allows us to chain
					// formatting symbols.
					currentStyle!!.withExclusiveFormatting(formatting)
				}
			}
			if (currentStyle == Style.EMPTY) {
				currentStyle = textStyle
			}
		}
		currentIndex++
		return Result.INCREMENT
	}

	/**
	 * Updates current visitable data as well as signifies whether to end.
	 *
	 *
	 * Calling this method will result in each 'visible' character being sent to the [ ]
	 *
	 * @param textStyle Style of the current string
	 * @param string The current string
	 * @return Value to terminate. Follows [StringVisitable.StyledVisitor] return values.
	 */
	fun updateStyle(textStyle: Style, string: String): Optional<Optional<Unit>> {
		if (lastTextStyle == null) {
			lastTextStyle = textStyle
		}
		currentStyle = textStyle
		val stringLength = string.length
		var i = 0
		while (i < stringLength) {
			val c = string[i]
			var nextChar: Char? = null
			if (i + 1 < stringLength) {
				nextChar = string[i + 1]
			}
			if (c == '§') {
				skipBy = 0
				when (updateSection(textStyle, nextChar, string.substring(i + 1))) {
					Result.SKIP -> return Optional.empty()
					Result.TERMINATE -> return Optional.of(StringVisitable.TERMINATE_VISIT)
					Result.INCREMENT -> i++
				}
				i += skipBy
			} else if (sendToVisitor(c, textStyle)) {
				realIndex++
			} else {
				return Optional.of(StringVisitable.TERMINATE_VISIT)
			}
			currentIndex++
			i++
		}
		lastTextStyle = textStyle
		return Optional.empty()
	}

	companion object {

		/**
		 * Formats text that contains styling data as well as formatting symbols
		 *
		 *
		 * This method is used to remove section symbols while maintaining previous formatting as
		 * well as new formatting.
		 *
		 * @param text Text to reformat
		 * @return Formatted text
		 */
		fun formatText(text: Text): MutableText {
			val t = Text.empty()
			val length = text.string.length
			val formatter =
				StyleFormatter(
					FormattingVisitable { c: Char, index: Int, formattedIndex: Int, style: Style?, formattedStyle: Style? ->
						t.append(Text.literal(c.toString()).fillStyle(formattedStyle))
						true
					},
					length)
			text.visit(
				{ textStyle: Style, string: String -> formatter.updateStyle(textStyle, string) }, Style.EMPTY)
			return flattenText(t)
		}

		fun flattenText(text: Text): MutableText {
			val newSiblings: MutableList<Text> = ArrayList()
			var last = text.style
			var content: StringBuilder = StringBuilder(TextUtil.Companion.getContent(text.content))
			for (t in text.siblings) {
				if (t.style == last) {
					content.append(TextUtil.Companion.getContent(t.content))
					continue
				}
				newSiblings.add(Text.literal(content.toString()).fillStyle(last))
				content = StringBuilder(TextUtil.Companion.getContent(t.content))
				last = t.style
			}
			newSiblings.add(Text.literal(content.toString()).fillStyle(last))
			val newText = Text.empty()
			for (sibling in newSiblings) {
				newText.append(sibling)
			}
			return newText
		}

		/**
		 * Wraps text into multiple lines
		 *
		 * @param textRenderer TextRenderer to handle text
		 * @param scaledWidth Maximum width before the line breaks
		 * @param text Text to break up
		 * @return List of MutableText of the new lines
		 */
		fun wrapText(textRenderer: TextRenderer, scaledWidth: Int, text: Text): List<Text> {
			val lines = ArrayList<Text>()
			for (breakRenderedChatMessageLine in ChatMessages.breakRenderedChatMessageLines(text, scaledWidth, textRenderer)) {
				lines.add(TextBuilder().append(breakRenderedChatMessageLine).build())
			}
			return lines
		}
	}
}
