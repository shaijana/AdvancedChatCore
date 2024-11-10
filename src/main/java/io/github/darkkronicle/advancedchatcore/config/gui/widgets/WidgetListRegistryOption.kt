/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui.widgets

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListBase
import io.github.darkkronicle.advancedchatcore.interfaces.ConfigRegistryOption
import io.github.darkkronicle.advancedchatcore.util.AbstractRegistry
import net.minecraft.client.gui.screen.Screen
import java.util.stream.Collectors

class WidgetListRegistryOption<T : ConfigRegistryOption<*>?>
	(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	@Nullable selectionListener: ISelectionListener<T>?,
	registry: AbstractRegistry<*, T>,
	parent: Screen?
) :
	WidgetListBase<T?, WidgetRegistryOptionEntry<T?>>(x, y, width, height, selectionListener) {

	private val registry: AbstractRegistry<*, T?>

	init {
		this.browserEntryHeight = 22
		this.setParent(parent)
		this.registry = registry
	}

	override fun createListEntryWidget(
		x: Int, y: Int, listIndex: Int, isOdd: Boolean, entry: T?
	): WidgetRegistryOptionEntry<T?> {
		return WidgetRegistryOptionEntry(
			x,
			y,
			this.browserEntryWidth,
			this.getBrowserEntryHeightFor(entry),
			isOdd,
			entry,
			listIndex,
			this)
	}

	override fun getAllEntries(): Collection<T?> {
		return registry.getAll().stream()
			.filter { option: T -> !option!!.isHidden }
			.collect(Collectors.toList())
	}
}
