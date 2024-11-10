/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore

import lombok.Value
import net.fabricmc.loader.api.metadata.Person

@Value
class Module {

	/** The Mod ID of the module  */
	var modId: String? = null

	/** A [Collection] of [Person]'s  */
	var authors: Collection<Person>? = null
}
