package io.github.darkkronicle.advancedchatcore.util

import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * An interface to provide a way to get the text that should be replaced based off of the
 * current [Text] and the current [StringMatch]
 */
interface StringInsert {

	/**
	 * Return's the [MutableText] that should be inserted.
	 *
	 * @param current The current [Text]
	 * @param match The current [StringMatch]
	 * @return
	 */
	fun getText(current: Text?, match: StringMatch?): MutableText?
}
