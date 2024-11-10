/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.finder.custom

import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import io.github.darkkronicle.advancedchatcore.interfaces.IFinder
import io.github.darkkronicle.advancedchatcore.util.ProfanityUtil
import io.github.darkkronicle.advancedchatcore.util.StringMatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ProfanityFinder : IFinder {

	override fun isMatch(input: String, toMatch: String): Boolean {
		return getMatches(input, toMatch).size != 0
	}

	override fun getMatches(input: String, toMatch: String): List<StringMatch?> {
		return ProfanityUtil.Companion.getInstance().getBadWords(input, ConfigStorage.General.PROFANITY_ABOVE.config.getDoubleValue().toFloat(),
			ConfigStorage.General.PROFANITY_ON_WORD_BOUNDARIES.config.getBooleanValue())
	}
}
