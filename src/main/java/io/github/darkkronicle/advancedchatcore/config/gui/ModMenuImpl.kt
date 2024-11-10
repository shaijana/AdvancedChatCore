/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
class ModMenuImpl : ModMenuApi {

	override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
		return ConfigScreenFactory<Screen> { parent: Screen? -> GuiConfigHandler.Companion.getInstance().getDefaultScreen() }
	}
}
