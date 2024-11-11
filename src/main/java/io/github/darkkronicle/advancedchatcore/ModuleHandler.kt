/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore

import fi.dy.masa.malilib.interfaces.IInitializationHandler
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.CustomValue
import java.util.*

object ModuleHandler {

	val modules = mutableListOf<Module>()

	private var toLoad = mutableListOf<LoadOrder>()

	fun registerModules() {
		modules.clear()
		FabricLoader.getInstance().allMods.forEach { mod: ModContainer ->
			// Check if in "custom" it has "acmodule": true
			val acData: CustomValue = mod.metadata.getCustomValue("acmodule")
				?: return@forEach
			if (acData.type == CustomValue.CvType.BOOLEAN && acData.asBoolean) {
				// Add the module
				val module = Module().apply {
					modId = mod.metadata.id
					authors = mod.metadata.authors
				}
				modules += module
			}
		}
	}

	fun registerInitHandler(name: String, priority: Int, handler: IInitializationHandler) {
		toLoad += LoadOrder(name, priority, handler)
	}

	/** Do not call  */
	fun load() {
		toLoad.sort()
		toLoad.forEach { load: LoadOrder ->
			load.handler.registerModHandlers()
		}
		toLoad.clear()
	}

	/**
	 * Retrieves a [Module] based off of a mod ID.
	 *
	 *
	 * This is useful for incompatible features or enabling others.
	 *
	 * @param modID Mod id of the mod
	 * @return An optional containing the module if found.
	 */
	fun fromId(modID: String): Optional<Module> {
		modules.forEach { module: Module ->
			if (module.modId == modID) {
				return Optional.of(module)
			}
		}
		return Optional.empty()
	}

	class LoadOrder(
		var name: String,
		var order: Int,
		var handler: IInitializationHandler,
	) : Comparable<LoadOrder> {

		override fun compareTo(other: LoadOrder): Int {
			val compared: Int = order.compareTo(other.order)
			if (compared == 0) {
				return name.compareTo(other.name)
			}
			return compared
		}
	}
}
