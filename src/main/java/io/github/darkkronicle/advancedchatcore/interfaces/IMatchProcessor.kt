/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor.Result
import io.github.darkkronicle.advancedchatcore.util.SearchResult
import net.minecraft.text.Text

/**
 * An interface to receive text and matches to process.
 *
 *
 * Similar to [IMessageProcessor] but it takes matches and can return a [Result]
 */
interface IMatchProcessor : IMessageProcessor {

	/** Different outcome's the processor can have  */
	enum class Result(val success: Boolean, val forward: Boolean, val force: Boolean) {

		FAIL(false, true, false),
		PROCESSED(true, false, false),
		FORCE_FORWARD(true, true, true),
		FORCE_STOP(true, false, true);

		companion object {

			fun getFromBool(success: Boolean): Result {
				if (!success) {
					return FAIL
				}
				return PROCESSED
			}
		}
	}

	override fun process(text: Text, unfiltered: Text?): Boolean {
		return processMatches(text, unfiltered, null).success
	}

	/**
	 * Process specific matches and return how the rest of the processors should be handled
	 *
	 * @param text Final text
	 * @param unfiltered Unfiltered version of text. If not available null.
	 * @param search [SearchResult] matches
	 * @return The [Result] that the method performed
	 */
	fun processMatches(
		text: Text?, @Nullable unfiltered: Text?, @Nullable search: SearchResult?
	): Result

	/**
	 * Whether or not this processor should only trigger when matches are present. If false [ ] can be null.
	 *
	 * @return If this processor should only trigger when matches are present
	 */
	fun matchesOnly(): Boolean {
		return true
	}
}
