/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fi.dy.masa.malilib.config.IConfigOptionListEntry
import fi.dy.masa.malilib.config.options.ConfigBoolean
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * A [RegistryOption] that can be saved and loaded from a JSON file.
 *
 * @param <TYPE>
</TYPE> */
@Environment(EnvType.CLIENT)
interface ConfigRegistryOption<TYPE>
	: RegistryOption<TYPE>, IConfigOptionListEntry, IJsonApplier {

	/**
	 * Get's a configurable boolean for whether or not the option is active.
	 *
	 * @return Configurable boolean
	 */
	val active: SaveableConfig<ConfigBoolean>

	/**
	 * Get's if the option is currently active.
	 *
	 * @return If the option is active
	 */
	override fun isActive(): Boolean {
		return active.config.getBooleanValue()
	}

	/**
	 * Save's the config option and the object that it is wrapping.
	 *
	 *
	 * By default it will only save if the option is active or not, but if the [TYPE]
	 * implements [IJsonApplier] it will also save/load that object.
	 *
	 * @return Serialized object
	 */
	override fun save(): JsonObject {
		val obj: JsonObject = JsonObject()
		obj.add(active.key, active.config.getAsJsonElement())
		var extra: JsonObject? = null
		if (getOption() is IJsonApplier) {
			extra = (getOption() as IJsonApplier).save()
		}
		if (extra != null) {
			for (e: Map.Entry<String?, JsonElement?> in extra.entrySet()) {
				obj.add(e.key, e.value)
			}
		}
		return obj
	}

	/**
	 * Load's the config option and the object that it is wrapping.
	 *
	 *
	 * By default it will only load if the option is active or not, but if the [TYPE]
	 * implements [IJsonApplier] it will also save/load that object.
	 */
	override fun load(element: JsonElement?) {
		if (element == null || !element.isJsonObject()) {
			return
		}
		val obj: JsonObject = element.getAsJsonObject()
		active.config.setValueFromJsonElement(obj.get(active.key))
		if (getOption() is IJsonApplier) {
			(getOption() as IJsonApplier).load(obj)
		}
	}
}
