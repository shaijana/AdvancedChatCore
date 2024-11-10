/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.gui

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListBase
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper
import net.minecraft.client.gui.screen.Screen

abstract class WidgetConfigList<TYPE, WIDGET : WidgetConfigListEntry<TYPE>?>
	(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	selectionListener: ISelectionListener<TYPE>?,
	parent: Screen?
) : WidgetListBase<TYPE, WIDGET>(x, y, width, height, selectionListener) {

	protected var textFields: MutableList<TextFieldWrapper<GuiTextFieldGeneric>> = ArrayList()

	init {
		this.browserEntryHeight = 22
		this.setParent(parent)
	}

	override fun reCreateListEntryWidgets() {
		textFields.clear()
		super.reCreateListEntryWidgets()
	}

	fun addTextField(text: TextFieldWrapper<GuiTextFieldGeneric>) {
		textFields.add(text)
	}

	protected fun clearTextFieldFocus() {
		for (field in this.textFields) {
			val textField = field.textField

			if (textField.isFocused) {
				textField.isFocused = false
				break
			}
		}
	}

	override fun onMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		clearTextFieldFocus()
		return super.onMouseClicked(mouseX, mouseY, mouseButton)
	}

	override fun onKeyTyped(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		for (widget in this.listWidgets) {
			if (widget.onKeyTyped(keyCode, scanCode, modifiers)) {
				return true
			}
		}
		return super.onKeyTyped(keyCode, scanCode, modifiers)
	}
}
