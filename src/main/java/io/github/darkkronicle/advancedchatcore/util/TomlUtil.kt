/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util

import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.core.io.ParsingMode
import com.electronwill.nightconfig.toml.TomlFormat
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore
import lombok.experimental.UtilityClass
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.apache.logging.log4j.Level
import java.io.File

@Environment(EnvType.CLIENT)
@UtilityClass
class TomlUtil {

	fun loadFile(file: File): FileConfig {
		val config = FileConfig.of(file)
		config.load()
		return config
	}

	fun loadFileWithDefaults(file: File, defaultName: String): FileConfig {
		val tomlFormat = TomlFormat.instance()
		val tomlParser = tomlFormat.createParser()
		val config = loadFile(file)
		try {
			// Layer on top
			tomlParser.parse(AdvancedChatCore.Companion.getResource(defaultName), config, ParsingMode.ADD)
		} catch (e: Exception) {
			AdvancedChatCore.Companion.logger.log(
				Level.ERROR, "Could not load default settings into $defaultName", e)
		}
		return config
	}
}
