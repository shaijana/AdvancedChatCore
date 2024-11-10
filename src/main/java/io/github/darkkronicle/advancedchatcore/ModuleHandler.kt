/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore

import fi.dy.masa.malilib.interfaces.IInitializationHandler
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.Value
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.CustomValue
import java.util.*

class ModuleHandler private constructor() {

	@Getter
	private val modules: MutableList<Module> = ArrayList()

	private var toLoad: MutableList<LoadOrder>? = ArrayList()

	fun registerModules() {
		modules.clear()
		for (mod: ModContainer in FabricLoader.getInstance().getAllMods()) {
			// Check if in "custom" it has "acmodule": true
			val acData: CustomValue? = mod.getMetadata().getCustomValue("acmodule")
			if (acData == null) {
				continue
			}
			if (acData.getType() == CustomValue.CvType.BOOLEAN && acData.getAsBoolean()) {
				// Add the module
				modules.add(Module(mod.getMetadata().getId(), mod.getMetadata().getAuthors()))
			}
		}
	}

	fun registerInitHandler(name: String, priority: Int, handler: IInitializationHandler) {
		toLoad!!.add(LoadOrder(name, priority, handler))
	}

	/** Do not call  */
	fun load() {
		Collections.sort(toLoad)
		for (load: LoadOrder in toLoad!!) {
			load.getHandler().registerModHandlers()
		}
		toLoad = null
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
		for (m: Module in modules) {
			if (m.getModId() == modID) {
				return Optional.of(m)
			}
		}
		return Optional.empty()
	}

	@AllArgsConstructor
	@Value
	class LoadOrder : Comparable<LoadOrder> {

		var name: String? = null
		var order: Int? = null
		var handler: IInitializationHandler? = null

		override fun compareTo(o: LoadOrder): Int {
			val compared: Int = order!!.compareTo(o.order!!)
			if (compared == 0) {
				return name!!.compareTo(o.getName())
			}
			return compared
		}
	}

	companion object {

		val instance: ModuleHandler = ModuleHandler()
	}
}
