/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui

import fi.dy.masa.malilib.gui.GuiListBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListBase
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase
import io.github.darkkronicle.advancedchatcore.interfaces.IClosable

abstract class CoreGuiListBase<TYPE, WIDGET : WidgetListEntryBase<TYPE>?, WIDGETLIST : WidgetListBase<TYPE, WIDGET>?>
	(listX: Int, listY: Int) : GuiListBase<TYPE, WIDGET, WIDGETLIST?>(listX, listY),
	ISelectionListener<TYPE>, IClosable {

	override fun createListWidget(listX: Int, listY: Int): WIDGETLIST? {
		return null
	}

	override fun getBrowserWidth(): Int {
		return this.width - 20
	}

	override fun getBrowserHeight(): Int {
		return this.height - 6 - this.listY
	}

	override fun onSelectionChange(entry: TYPE) {}

	override fun close(button: ButtonBase?) {
		this.closeGui(true)
	}
}
