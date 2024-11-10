/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui

import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.gui.GuiConfigsBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.util.Color4f
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore

// Based off of
// https://github.com/maruohon/minihud/blob/fabric_1.16_snapshots_temp/src/main/java/fi/dy/masa/minihud/gui/GuiConfigs.java
// Released under GNU LGPL
class GuiConfig() : GuiConfigsBase(10, 50, AdvancedChatCore.Companion.MOD_ID, null, "advancedchat.screen.main") {

	@Deprecated("")
	constructor(tabButtons: List<GuiConfigHandler.TabButton?>?, configs: List<IConfigBase?>?) : this()

	override fun initGui() {
		if (TAB == null) {
			// Should be general
			for (i in GuiConfigHandler.Companion.getInstance().getTabs().indices) {
				val tab: TabSupplier = GuiConfigHandler.Companion.getInstance().getTabs().get(i)
				if (tab.isSelectable) {
					TAB = tab
					break
				}
			}
			if (TAB == null) {
				// sucks to suck lol
				TAB = GuiConfigHandler.Companion.getInstance().getTabs().get(0)
			}
		}
		val children = TAB.getChildren() != null && TAB.getChildren().size != 0

		val child = getFullyNestedSupplier(
			TAB!!).getScreen(this)
		if (child != null) {
			openGui(child)
			return
		}

		clearElements()
		val x = 10
		var y = 26
		val rows = addTabButtons(this, x, y)
		y += rows * 22
		if (children) {
			y += (addAllChildrenButtons(this,
				TAB!!, x, y) * 22)
		}
		setListPosition(listX, y + 10)
		if (this.listWidget != null) {
			this.listWidget!!.setSize(this.browserWidth, this.browserHeight)
			this.listWidget!!.initGui()
		}
	}

	override fun getConfigs(): List<ConfigOptionWrapper> {
		return ConfigOptionWrapper.createFor(getFullyNestedSupplier(
			TAB!!).options)
	}

	class ButtonListenerTab(private val tab: TabSupplier, private val parent: TabSupplier?) :
		IButtonActionListener {

		override fun actionPerformedWithButton(button: ButtonBase, mouseButton: Int) {
			if (parent == null) {
				TAB = this.tab
			} else {
				parent.nestedSelection = tab
			}
			openGui(GuiConfig())
		}
	}

	companion object {

		var TAB: TabSupplier? = null

		fun getFullyNestedSupplier(supplier: TabSupplier): TabSupplier {
			if (supplier.children == null || supplier.children.size == 0) {
				return supplier
			}
			return getFullyNestedSupplier(supplier.nestedSelection)
		}

		fun addAllChildrenButtons(screen: GuiBase, supplier: TabSupplier, x: Int, y: Int): Int {
			var x = x
			var y = y
			var rows = 0
			if (supplier.children != null && supplier.children.size != 0) {
				x += 2
				screen.addLabel(x, y, 10, 22, Color4f(1f, 1f, 1f, 1f).intValue, ">")
				x += 8
				addNestedTabButtons(screen, supplier, x, y)
				y += 22
				rows++
				if (supplier.nestedSelection != null) {
					rows += addAllChildrenButtons(screen, supplier.nestedSelection, x, y)
				}
			}
			return rows
		}

		/**
		 * Adds the category buttons to the selected screen
		 * @param screen Screen to apply to
		 * @return Amount of rows it created
		 */
		fun addTabButtons(screen: GuiBase, x: Int, y: Int): Int {
			var x = x
			var y = y
			var rows = 1
			for (tab in GuiConfigHandler.Companion.getInstance().getTabs()) {
				val width = screen.getStringWidth(tab.displayName) + 10

				if (x >= screen.width - width - 10) {
					x = 10
					y += 22
					++rows
				}

				x += createTabButton(screen, x, y, width, tab)
			}
			return rows
		}

		fun addNestedTabButtons(screen: GuiBase, supplier: TabSupplier, x: Int, y: Int): Int {
			var x = x
			var y = y
			var rows = 1
			for (tab in supplier.children) {
				val width = screen.getStringWidth(tab.displayName) + 10

				if (x >= screen.width - width - 10) {
					x = 10
					y += 22
					++rows
				}

				x += createTabButton(screen, x, y, width, tab, supplier)
			}
			return rows
		}

		private fun createTabButton(screen: GuiBase, x: Int, y: Int, width: Int, tab: TabSupplier, parent: TabSupplier? = null): Int {
			val button = ButtonGeneric(x, y, width, 20, tab.displayName)
			if (parent == null) {
				button.setEnabled(TAB !== tab)
			} else {
				button.setEnabled(parent.nestedSelection !== tab)
			}
			screen.addButton(button, ButtonListenerTab(tab, parent))

			return button.width + 2
		}
	}
}
