/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import io.github.darkkronicle.advancedchatcore.util.StringMatch
import net.minecraft.text.Text

interface IFinder {

	fun isMatch(input: String, toMatch: String): Boolean

	fun isMatch(input: Text, toMatch: String): Boolean {
		return isMatch(input.getString(), toMatch)
	}

	fun getMatches(input: String, toMatch: String): List<StringMatch?>

	fun getMatches(input: Text, toMatch: String): List<StringMatch?> {
		return getMatches(input.getString(), toMatch)
	}
}
