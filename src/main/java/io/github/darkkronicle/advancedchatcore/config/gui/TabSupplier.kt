package io.github.darkkronicle.advancedchatcore.config.gui

import fi.dy.masa.malilib.config.IConfigBase
import fi.dy.masa.malilib.util.StringUtils
import lombok.Getter
import lombok.Setter
import net.minecraft.client.gui.screen.Screen
import java.util.*

open class TabSupplier(@field:Getter private val name: String?, private val translationKey: String?) {

	@Getter
	@Setter
	private var nestedSelection: TabSupplier? = null

	@Getter
	private val children: MutableList<TabSupplier> = ArrayList()

	val displayName: String
		get() = StringUtils.translate(translationKey)

	open val options: List<IConfigBase?>?
		get() = null

	open fun getScreen(parent: Screen?): Screen? {
		return null
	}

	val isSelectable: Boolean
		get() = true

	override fun equals(obj: Any?): Boolean {
		if (obj !is TabSupplier) {
			return false
		}
		return (obj.getName() == getName())
	}

	fun addChild(supplier: TabSupplier) {
		if (nestedSelection == null) {
			if (supplier.isSelectable) {
				nestedSelection = supplier
			}
		}
		children.add(supplier)
	}

	fun get(name: String): TabSupplier? {
		for (child in children) {
			if (name == child.getName()) {
				return child
			}
		}
		return null
	}
}
