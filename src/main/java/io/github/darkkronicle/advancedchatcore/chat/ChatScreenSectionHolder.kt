/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import io.github.darkkronicle.advancedchatcore.interfaces.AdvancedChatScreenSection
import lombok.Getter
import java.util.function.Function

/**
 * A class to handle the construction and distribution of [AdvancedChatScreenSection] when
 * [AdvancedChatScreen] is created.
 */
object ChatScreenSectionHolder {

	/** All suppliers for the sections  */
	val sectionSuppliers = mutableListOf<Function<AdvancedChatScreen, AdvancedChatScreenSection>>()
}
