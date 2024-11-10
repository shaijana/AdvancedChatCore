package io.github.darkkronicle.advancedchatcore.util

import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class TextBuilder {

	private val siblings: MutableList<RawText> = ArrayList()

	fun append(string: String): TextBuilder {
		siblings.add(RawText.Companion.of(string))
		return this
	}

	fun append(string: String, style: Style?): TextBuilder {
		siblings.add(RawText.Companion.of(string, style!!))
		return this
	}

	val texts: List<RawText>
		get() = siblings

	fun append(text: OrderedText): TextBuilder {
		val last = AtomicReference<Style?>(null)
		val builder = AtomicReference(StringBuilder())
		text.accept { index: Int, style: Style, codePoint: Int ->
			if (last.get() == null) {
				last.set(style)
				builder.get().append(Character.toChars(codePoint))
				return@accept true
			} else if (last.get() == style) {
				builder.get().append(Character.toChars(codePoint))
				return@accept true
			}
			append(builder.get().toString(), last.get())
			last.set(style)
			builder.set(StringBuilder().append(Character.toChars(codePoint)))
			true
		}
		if (!builder.get().isEmpty()) {
			append(builder.get().toString(), last.get())
		}
		return this
	}

	fun append(text: Text): TextBuilder {
		val last = AtomicReference<Style?>(null)
		val builder = AtomicReference(StringBuilder())
		text.visit<Any?>({ style: Style, asString: String? ->
			if (last.get() == null) {
				last.set(style)
				builder.get().append(asString)
				return@visit Optional.empty<Any>()
			} else if (last.get() == style) {
				builder.get().append(asString)
				return@visit Optional.empty<Any>()
			}
			append(builder.get().toString(), last.get())
			last.set(style)
			builder.set(StringBuilder(asString))
			Optional.empty<Any?>()
		}, Style.EMPTY)
		if (!builder.get().isEmpty()) {
			append(builder.get().toString(), last.get())
		}
		return this
	}

	fun build(): MutableText {
		val newText = Text.empty()
		for (sib in siblings) {
			newText.append(Text.literal(sib.content).fillStyle(sib.style))
		}
		return newText
	}
}
