/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui

import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetToggle
import io.github.darkkronicle.advancedchatcore.interfaces.Translatable
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map

class OnOffListListener<T : Translatable?>(private val button: ButtonGeneric, private val toggle: WidgetToggle, onOff: HashMap<T, Boolean>) :
	IButtonActionListener {

	private var current: T? = null
	private val onOff: HashMap<T?, Boolean>
	private val order: List<T?>

	init {
		this.onOff = HashMap(onOff)
		this.order = ArrayList(onOff.keys)
		next()
	}

	val buttonListener: IButtonActionListener
		get() {
			return IButtonActionListener { button1: ButtonBase?, mouseButton: Int ->
				onToggled()
			}
		}

	private fun onToggled() {
		onOff.put(current, toggle.isCurrentlyOn())
	}

	private fun next() {
		var i: Int = order.indexOf(current) + 1
		if (i >= order.size) {
			i = 0
		}
		current = order.get(i)
		button.setDisplayString(current!!.translate())
		toggle.setOn(onOff.get(current)!!)
	}

	val on: List<T?>
		get() {
			val list: ArrayList<T?> = ArrayList()
			for (entry: Map.Entry<T?, Boolean> in onOff.entries) {
				if (entry.value) {
					list.add(entry.key)
				}
			}
			return list
		}

	override fun actionPerformedWithButton(button: ButtonBase, mouseButton: Int) {
		next()
	}

	companion object {

		fun <T : Translatable?> getOnOff(
			all: List<T>, active: List<T>
		): HashMap<T, Boolean> {
			val map: HashMap<T, Boolean> = HashMap()
			for (a: T in all) {
				map.put(a, active.contains(a))
			}
			return map
		}
	}
}
