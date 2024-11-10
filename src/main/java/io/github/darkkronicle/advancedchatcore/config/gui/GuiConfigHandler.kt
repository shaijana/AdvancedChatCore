/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui

import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.util.StringUtils
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig
import io.github.darkkronicle.advancedchatcore.gui.buttons.ConfigTabsButtonListener
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.Value
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class GuiConfigHandler private constructor() {

	@Deprecated("")
	var activeTab: String? = ""

	@Getter
	private val tabs: MutableList<TabSupplier> = ArrayList()

	@Deprecated("")
	fun isTabActive(button: Tab): Boolean {
		return button.name == activeTab
	}

	@Deprecated("")
	fun addGuiSection(section: Tab) {
		if (section is GuiConfigSection) {
			addTab(object : TabSupplier(
				section.name, StringUtils.translate(section.name)) {
				override val options: List<IConfigBase?>?
					get() = section.options
			})
			return
		}
		tabs.add(object : TabSupplier(section.name, section.name) {
			override fun getScreen(parent: Screen?): Screen? {
				return section.getScreen(this.buttons)
			}
		})
	}

	fun addTab(tab: TabSupplier) {
		tabs.add(tab)
	}

	fun get(name: String): TabSupplier? {
		for (child: TabSupplier in tabs) {
			if (child.name == name) {
				return child
			}
		}
		return null
	}

	@get:Deprecated("")
	val buttons: List<TabButton?>
		get() {
			var x: Int = 10
			var y: Int = 26
			var rows: Int = 1
			val buttons: ArrayList<TabButton?> =
				ArrayList()
			val client: MinecraftClient = MinecraftClient.getInstance()
			val windowWidth: Int = client.window.scaledWidth
			for (tab: TabSupplier in tabs) {
				val width: Int = client.textRenderer.getWidth(tab.name) + 10

				if (x >= windowWidth - width - 10) {
					x = 10
					y += 22
					rows++
				}

				val button: ButtonGeneric = this.createButton(x, y, width, tab)
				x += button.width + 2
				buttons.add(TabButton(tab, button))
			}
			return buttons
		}

	private fun createButton(x: Int, y: Int, width: Int, tab: TabSupplier): ButtonGeneric {
		val button: ButtonGeneric = ButtonGeneric(x, y, width, 20, tab.name)
		button.setEnabled(GuiConfig.Companion.TAB != tab)
		return button
	}

	fun getTab(name: String): Tab {
		val supplier: TabSupplier? = getTabSupplier(name)
		return object : Tab {
			override val name: String?
				get() = supplier.getDisplayName()

			override fun getScreen(buttons: List<TabButton?>?): Screen {
				return this.defaultScreen
			}
		}
	}

	fun getTabSupplier(name: String): TabSupplier? {
		for (b: TabSupplier in tabs) {
			if (b.name == name) {
				return b
			}
		}
		return null
	}

	val defaultScreen: Screen
		get() = GuiConfig()

	@Deprecated("")
	@AllArgsConstructor
	@Value
	class TabButton {

		var tabSupplier: TabSupplier? = null
		var button: ButtonGeneric? = null

		@get:Deprecated("")
		val tab: Tab
			get() = object : Tab {
				override val name: String?
					get() = tabSupplier.getDisplayName()

				override fun getScreen(
					buttons: List<TabButton?>?
				): Screen {
					return GuiConfig()
				}
			}

		fun createListener(): ConfigTabsButtonListener {
			return ConfigTabsButtonListener(this)
		}
	}

	@Deprecated("")
	interface Tab {

		val name: String?

		fun getScreen(buttons: List<TabButton?>?): Screen
	}

	@Deprecated("")
	interface GuiConfigSection : Tab {

		val options: List<IConfigBase?>?

		override val name: String?

		override fun getScreen(buttons: List<TabButton?>?): Screen {
			instance.activeTab = this.name
			return GuiConfig()
		}
	}

	companion object {

		val instance: GuiConfigHandler = GuiConfigHandler()

		@Deprecated("")
		fun createGuiConfigSection(name: String?, configs: List<SaveableConfig<out IConfigBase?>>): GuiConfigSection {
			val configBases: MutableList<IConfigBase?> = ArrayList()
			for (saveable: SaveableConfig<out IConfigBase?> in configs) {
				configBases.add(saveable.config)
			}
			return object : GuiConfigSection {
				override val options: List<IConfigBase?>?
					get() {
						return configBases
					}

				override val name: String?
					get() {
						return StringUtils.translate(name)
					}
			}
		}

		fun wrapSaveableOptions(name: String?, translationKey: String?, supplier: Supplier<List<SaveableConfig<out IConfigBase?>>>): TabSupplier {
			val configSupplier: Supplier<List<IConfigBase?>> =
				Supplier {
					val config: MutableList<IConfigBase?> = ArrayList()
					val options: List<SaveableConfig<out IConfigBase?>> = supplier.get()
					for (s: SaveableConfig<out IConfigBase?> in options) {
						config.add(s.config)
					}
					config
				}
			return wrapOptions(name, translationKey, configSupplier)
		}

		fun wrapSaveableOptions(name: String?, translationKey: String?, options: List<SaveableConfig<out IConfigBase?>>): TabSupplier {
			val config: MutableList<IConfigBase?> = ArrayList()
			for (s: SaveableConfig<out IConfigBase?> in options) {
				config.add(s.config)
			}
			return wrapOptions(name, translationKey, config)
		}

		fun wrapOptions(name: String?, translationKey: String?, configs: List<IConfigBase?>): TabSupplier {
			return wrapOptions(name, translationKey,
				{ configs })
		}

		fun wrapOptions(name: String?, translationKey: String?, options: Supplier<List<IConfigBase?>>): TabSupplier {
			return object : TabSupplier(name, translationKey) {
				override val options: List<IConfigBase?>?
					get() {
						return options.get()
					}
			}
		}

		fun wrapScreen(name: String?, translationKey: String?, screenSupplier: Function<Screen?, Screen?>): TabSupplier {
			return object : TabSupplier(name, translationKey) {
				override fun getScreen(parent: Screen?): Screen? {
					return screenSupplier.apply(parent)
				}
			}
		}

		fun children(name: String?, translationKey: String?, vararg children: TabSupplier): TabSupplier {
			val tab: TabSupplier = object : TabSupplier(name, translationKey) {
				val name: String
					get() {
						return super.getName()
					}
			}
			for (child: TabSupplier in children) {
				tab.addChild(child)
			}
			return tab
		}
	}
}
