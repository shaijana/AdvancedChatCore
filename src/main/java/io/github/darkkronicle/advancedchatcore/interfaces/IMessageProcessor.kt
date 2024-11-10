/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.interfaces

import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.network.message.MessageSignatureData
import net.minecraft.text.Text
import java.util.*

/** An interface for taking text and processing it.  */
interface IMessageProcessor : IMessageFilter {

	/**
	 * Processes text without the unfiltered text.
	 *
	 *
	 * Deprecated because it won't return anything. If unfiltered doesn't exist, insert null into
	 * process.
	 *
	 * @param text Text to modify
	 * @return Empty
	 */
	@Deprecated("")
	override fun filter(text: Text): Optional<Text> {
		process(text, null)
		return Optional.empty()
	}

	/**
	 * Consumes text.
	 *
	 * @param text Final text to process
	 * @param unfiltered Original text (if available)
	 * @return If the processing was a success
	 */
	fun process(text: Text, @Nullable unfiltered: Text?): Boolean

	fun process(text: Text, @Nullable unfilterered: Text?, @Nullable signature: MessageSignatureData?, @Nullable indicator: MessageIndicator?): Boolean {
		return process(text, unfilterered)
	}
}
