/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui.widgets

import com.mojang.blaze3d.systems.RenderSystem
import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.ButtonOnOff
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase
import fi.dy.masa.malilib.render.RenderUtils
import fi.dy.masa.malilib.util.StringUtils
import io.github.darkkronicle.advancedchatcore.interfaces.ConfigRegistryOption
import io.github.darkkronicle.advancedchatcore.util.Colors
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext

@Environment(EnvType.CLIENT)
class WidgetRegistryOptionEntry<T : ConfigRegistryOption<*>?>
	(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	isOdd: Boolean,
	registryOption: T,
	listIndex: Int,
	parent: WidgetListRegistryOption<T>
) : WidgetListEntryBase<T>(x, y, width, height, registryOption, listIndex) {

	private val parent: WidgetListRegistryOption<T>
	private val isOdd: Boolean
	private val hoverLines: List<String?>?
	private val buttonStartX: Int
	private val option: T

	init {
		var y = y
		this.parent = parent
		this.isOdd = isOdd
		this.hoverLines = registryOption.getHoverLines()
		this.option = registryOption

		y += 1

		var pos = x + width - 2
		pos -= addOnOffButton(pos, y, ButtonListener.Type.ACTIVE, option!!.isActive)
		if (option.getScreen(parent) != null) {
			pos -= addButton(pos, y, ButtonListener.Type.CONFIGURE)
		}

		buttonStartX = pos
	}

	protected fun addButton(x: Int, y: Int, type: ButtonListener.Type): Int {
		val button = ButtonGeneric(x, y, -1, true, type.getDisplayName())
		this.addButton(button, ButtonListener(type, this))

		return button.width + 1
	}

	private fun addOnOffButton(xRight: Int, y: Int, type: ButtonListener.Type, isCurrentlyOn: Boolean): Int {
		val button = ButtonOnOff(xRight, y, -1, true, type.translate, isCurrentlyOn)
		this.addButton(button, ButtonListener(type, this))

		return button.width + 1
	}

	override fun render(mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext) {
		RenderUtils.color(1f, 1f, 1f, 1f)

		// Draw a lighter background for the hovered and the selected entry
		if (selected || this.isMouseOver(mouseX, mouseY)) {
			RenderUtils.drawRect(
				this.x,
				this.y,
				this.width,
				this.height,
				Colors.Companion.getInstance().getColorOrWhite("white").withAlpha(150).color())
		} else if (this.isOdd) {
			RenderUtils.drawRect(
				this.x,
				this.y,
				this.width,
				this.height,
				Colors.Companion.getInstance().getColorOrWhite("white").withAlpha(70).color())
		} else {
			RenderUtils.drawRect(
				this.x,
				this.y,
				this.width,
				this.height,
				Colors.Companion.getInstance().getColorOrWhite("white").withAlpha(50).color())
		}
		val name = option!!.displayName
		this.drawString(
			this.x + 4,
			this.y + 7,
			Colors.Companion.getInstance().getColorOrWhite("white").color(),
			name,
			context)

		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderSystem.disableBlend()

		super.render(mouseX, mouseY, selected, context)

		RenderUtils.disableDiffuseLighting()
	}

	override fun postRenderHovered(
		mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext
	) {
		super.postRenderHovered(mouseX, mouseY, selected, context)

		if (hoverLines == null) {
			return
		}
		if (mouseX >= this.x && mouseX < this.buttonStartX && mouseY >= this.y && mouseY <= this.y + this.height) {
			RenderUtils.drawHoverText(mouseX, mouseY, this.hoverLines, context)
		}
	}

	class ButtonListener<T : ConfigRegistryOption<*>?>
		(private val type: Type, private val parent: WidgetRegistryOptionEntry<T>) :
		IButtonActionListener {

		override fun actionPerformedWithButton(button: ButtonBase, mouseButton: Int) {
			if (type == Type.ACTIVE) {
				parent
					.option
					.getActive()
					.config.booleanValue = !parent.option!!.isActive
				parent.parent.refreshEntries()
			} else if (type == Type.CONFIGURE) {
				val screen = parent.option!!.getScreen(parent.parent.parent)!!.get()
				if (screen != null) {
					GuiBase.openGui(screen)
				}
			}
		}

		enum class Type(name: String) {
			CONFIGURE("configure"),
			ACTIVE("active");

			val translate: String

			init {
				this.translate = translate(name)
			}

			val displayName: String
				get() = StringUtils.translate(translate)

			companion object {

				private fun translate(key: String): String {
					return "advancedchat.config.button.$key"
				}
			}
		}
	}
}
