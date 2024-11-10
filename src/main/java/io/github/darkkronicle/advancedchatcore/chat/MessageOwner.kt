/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat

import lombok.AllArgsConstructor
import lombok.Data
import lombok.Value
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.util.Identifier

/** Stores data about a message owner  */
@Data
@Value
@AllArgsConstructor
@Environment(EnvType.CLIENT)
class MessageOwner {

	/** Player name  */
	var name: String? = null

	/** Entry that has player data  */
	var entry: PlayerListEntry? = null

	val texture: Identifier
		/**
		 * The texture of the player's skin
		 *
		 * @return Identifier with texture data
		 */
		get() = entry!!.skinTextures.texture()
}
