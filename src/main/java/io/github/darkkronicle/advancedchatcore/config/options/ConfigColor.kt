/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.options

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import fi.dy.masa.malilib.MaLiLib
import fi.dy.masa.malilib.config.options.ConfigColor
import fi.dy.masa.malilib.util.StringUtils
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.Colors
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import java.util.*

@Environment(EnvType.CLIENT)
class ConfigColor : ConfigColor {

	private var color: Color
	private val defaultReference: String?
	private var reference: String? = null

	constructor(name: String, defaultValue: Color, comment: String?) : super(name, defaultValue.getString(), comment) {
		this.color = defaultValue
		this.defaultReference = null
	}

	constructor(name: String, referenceDefault: String, comment: String?) : super(name,
		Colors.Companion.getInstance().getColorOrWhite(referenceDefault).toString(), comment) {
		this.color = Colors.Companion.getInstance().getColorOrWhite(referenceDefault)
		this.defaultReference = referenceDefault
	}

	override fun resetToDefault() {
		if (defaultReference != null) {
			this.setValueFromString(defaultReference)
		} else {
			this.setValueFromString(Color(defaultValue).getString())
		}
		onValueChanged()
	}

	override fun getDefaultStringValue(): String {
		if (defaultReference == null) {
			return super.getDefaultStringValue()
		}
		return defaultReference
	}

	override fun setValueFromString(value: String) {
		val color: Optional<Color> = Colors.Companion.getInstance().getColor(value)
		if (color.isPresent()) {
			this.setIntegerValue(color.get().color())
			this.reference = value
			this.setColor()
			return
		}
		this.reference = null
		super.setValueFromString(value)
		this.setColor()
	}

	override fun getStringValue(): String {
		if (reference != null) {
			return reference!!
		}
		return super.getStringValue()
	}

	private fun setColor() {
		this.color = Color(getIntegerValue())
	}

	override fun setValueFromJsonElement(element: JsonElement) {
		try {
			if (element.isJsonPrimitive()) {
				val value: String = element.getAsString()
				val color: Optional<Color> = Colors.Companion.getInstance().getColor(value)
				if (color.isPresent()) {
					this.setIntegerValue(color.get().color())
					this.reference = value
					this.setColor()
					return
				}
				this.value = this.getClampedValue(StringUtils.getColor(value, 0))
				this.setIntegerValue(this.value)
				this.setColor()
			} else {
				MaLiLib.logger.warn(
					"Failed to set config value for '{}' from the JSON element '{}'",
					this.getName(),
					element)
			}
		} catch (e: Exception) {
			MaLiLib.logger.warn(
				"Failed to set config value for '{}' from the JSON element '{}'",
				this.getName(),
				element,
				e)
		}
	}

	override fun getAsJsonElement(): JsonElement {
		return JsonPrimitive(getStringValue())
	}

	fun get(): Color {
		return color
	}
}
