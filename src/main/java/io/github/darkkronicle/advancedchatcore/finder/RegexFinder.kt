/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.finder

import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import io.github.darkkronicle.advancedchatcore.util.FindType
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import io.github.darkkronicle.advancedchatcore.util.TextUtil
import net.minecraft.text.ClickEvent
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import java.util.*
import java.util.function.BiFunction
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class RegexFinder : PatternFinder() {

	override fun getPattern(toMatch: String): Pattern? {
		try {
			return Pattern.compile(toMatch)
		} catch (e: PatternSyntaxException) {
			AdvancedChatCore.Companion.logger.error("The regex " + toMatch + " is invalid!")
			return null
		}
	}

	override fun getMatches(input: Text, toMatch: String): List<StringMatch?> {
		// Find named groups
		val optionalGroups: Optional<List<StringMatch>> = findMatches(toMatch, "\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>", FindType.REGEX)
		if (optionalGroups.isEmpty()) {
			// No named groups, go back to just text
			return getMatches(input.getString(), toMatch)
		}
		val string: String = input.getString()

		val pattern: Pattern? = getPattern(toMatch)
		if (pattern == null) {
			return ArrayList()
		}
		val matcher: Matcher = pattern.matcher(string)

		val groups: List<String> = optionalGroups.get().stream().map<String> { match: StringMatch -> match.match.substring(3, match.end!! - match.start!! - 1) }
			.filter { match: String -> match.startsWith("adv") }.toList()
		val matches: MutableList<StringMatch?> = ArrayList()

		while (matcher.find()) {
			val total: String = matcher.group()
			val start: Int = matcher.start()
			val stop: Int = matcher.end()
			if (groups.isEmpty()) {
				matches.add(StringMatch(total, start, stop))
				continue
			}
			var stillMatches: Boolean = true
			for (group: String in groups) {
				try {
					if (!isAllowed(input, group, matcher)) {
						stillMatches = false
						break
					}
				} catch (e: IllegalArgumentException) {
					// Group does not exist
				}
			}
			if (stillMatches) {
				matches.add(StringMatch(total, start, stop))
			}
		}

		matcher.reset()
		return matches
	}

	companion object {

		fun isAllowed(input: Text, group: String, matcher: Matcher): Boolean {
			var group: String = group
			group = group.lowercase()
			val groupText: String? = matcher.group(group)
			var groupCondition: String = group.substring(3)
			if (groupText == null || groupCondition.isEmpty() || groupText.isEmpty()) {
				return true
			}
			val start: Int = matcher.start(group)
			val end: Int = matcher.end(group)
			if (groupCondition.startsWith("0")) {
				groupCondition = groupCondition.substring(1)
				while (groupCondition.length != 0) {
					val truncated: MutableText = TextUtil.Companion.truncate(input, StringMatch("", start, end))
					val `val`: Char = groupCondition.get(0)
					groupCondition = groupCondition.substring(1)
					if (`val` == 'l') {
						if (!TextUtil.Companion.styleChanges(truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style -> style1.isBold() && style2.isBold() })) {
							return true
						}
						continue
					}
					if (`val` == 'o') {
						if (!TextUtil.Companion.styleChanges(truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style -> style1.isItalic() && style2.isItalic() })) {
							return true
						}
						continue
					}
					if (`val` == 'k') {
						if (!TextUtil.Companion.styleChanges(truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style -> style1.isObfuscated() && style2.isObfuscated() })) {
							return true
						}
						continue
					}
					if (`val` == 'n') {
						if (!TextUtil.Companion.styleChanges(truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style -> style1.isUnderlined() && style2.isUnderlined() })) {
							return true
						}
						continue
					}
					if (`val` == 'm') {
						if (!TextUtil.Companion.styleChanges(truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style -> style1.isStrikethrough() && style2.isStrikethrough() })) {
							return true
						}
						continue
					}
					if (`val` == 'z') {
						if (!TextUtil.Companion.styleChanges(
								truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style ->
									style1.getClickEvent() != null && style1.getClickEvent()!!
										.getAction() == ClickEvent.Action.OPEN_URL && style2.getClickEvent() != null && style2.getClickEvent()!!
										.getAction() == ClickEvent.Action.OPEN_URL
								})
						) {
							return true
						}
						continue
					}
					if (`val` == 'x') {
						if (!TextUtil.Companion.styleChanges(
								truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style ->
									style1.getClickEvent() != null && style1.getClickEvent()!!
										.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD && style2.getClickEvent() != null && style2.getClickEvent()!!
										.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD
								})
						) {
							return true
						}
						continue
					}
					if (`val` == 'y') {
						if (!TextUtil.Companion.styleChanges(
								truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style ->
									style1.getClickEvent() != null && style1.getClickEvent()!!
										.getAction() == ClickEvent.Action.OPEN_FILE && style2.getClickEvent() != null && style2.getClickEvent()!!
										.getAction() == ClickEvent.Action.OPEN_FILE
								})
						) {
							return true
						}
						continue
					}
					if (`val` == 'w') {
						if (!TextUtil.Companion.styleChanges(
								truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style ->
									style1.getClickEvent() != null && style1.getClickEvent()!!
										.getAction() == ClickEvent.Action.RUN_COMMAND && style2.getClickEvent() != null && style2.getClickEvent()!!
										.getAction() == ClickEvent.Action.RUN_COMMAND
								})
						) {
							return true
						}
						continue
					}
					if (`val` == 'v') {
						if (!TextUtil.Companion.styleChanges(
								truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style ->
									style1.getClickEvent() != null && style1.getClickEvent()!!
										.getAction() == ClickEvent.Action.SUGGEST_COMMAND && style2.getClickEvent() != null && style2.getClickEvent()!!
										.getAction() == ClickEvent.Action.SUGGEST_COMMAND
								})
						) {
							return true
						}
						continue
					}
					if (`val` == 'h') {
						if (!TextUtil.Companion.styleChanges(
								truncated,
								BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style ->
									style1.getHoverEvent() != null
											&& style2.getHoverEvent() != null
								})
						) {
							return true
						}
						continue
					}
					if (TextUtil.Companion.styleChanges(truncated,
							BiFunction<Style, Style, Boolean?> { style1: Style, style2: Style -> style1.getColor() == style2.getColor() })) {
						continue
					}
					val current: Style = truncated.getStyle()
					val color: TextColor? = current.getColor()
					if (color == null) {
						if (`val` == 'f') {
							return true
						}
						continue
					}
					val formatting: Formatting? = Formatting.byCode(`val`)
					if (formatting == null || !formatting.isColor()) {
						continue
					}
					if (color.getRgb() == formatting.getColorValue()) {
						return true
					}
				}
				return false
			}
			return true
		}
	}
}
