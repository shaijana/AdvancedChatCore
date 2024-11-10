/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config

import fi.dy.masa.malilib.config.IConfigBase

class SaveableConfig<T : IConfigBase?> private constructor(val key: String, val config: T) {
	companion object {

		fun <C : IConfigBase?> fromConfig(key: String, config: C): SaveableConfig<C> {
			return SaveableConfig(key, config)
		}
	}
}
