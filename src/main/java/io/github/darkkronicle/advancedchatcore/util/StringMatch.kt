/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode
import lombok.ToString

/**
 * A class to store data about a match.
 *
 *
 * This class is comparable based on where it starts.
 */
@EqualsAndHashCode
@ToString
@AllArgsConstructor
class StringMatch : Comparable<StringMatch> {

	/** The content that was matched  */
	var match: String? = null

	/** The index of the start of the match  */
	var start: Int? = null

	/** The index of the end of the match  */
	var end: Int? = null

	override fun compareTo(o: StringMatch): Int {
		return start!!.compareTo(o.start!!)
	}
}
