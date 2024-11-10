/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui

import fi.dy.masa.malilib.gui.widgets.WidgetListBase
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfig
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler

/** GUI list base to work in a configuration screen  */
abstract class ConfigGuiListBase<TYPE, WIDGET : WidgetListEntryBase<TYPE>?, WIDGETLIST : WidgetListBase<TYPE, WIDGET>?>
@kotlin.jvm.JvmOverloads constructor(listX: Int = 10, listY: Int = 60, tabButtons: List<GuiConfigHandler.TabButton?>? = null) :
	CoreGuiListBase<TYPE, WIDGET, WIDGETLIST>(listX, listY) {

	constructor(tabButtons: List<GuiConfigHandler.TabButton?>?) : this(10, 60, tabButtons)

	override fun initGui() {
		super.initGui()

		val x = 10
		var y = 26

		y += (22 * GuiConfig.Companion.addTabButtons(this, 10, y))
		if (GuiConfig.Companion.TAB.getChildren() != null && GuiConfig.Companion.TAB.getChildren().size > 0) {
			y += (22 * GuiConfig.Companion.addAllChildrenButtons(this,
				GuiConfig.Companion.TAB!!, 10, y))
		}
		this.setListPosition(this.listX, y)
		this.reCreateListWidget()
		this.listWidget!!.refreshEntries()

		y += 24

		initGuiConfig(x, y)
	}

	/** Method when the GUI is initialized. This one takes an X,Y that is away from the buttons  */
	fun initGuiConfig(x: Int, y: Int) {}
}
