package io.github.darkkronicle.advancedchatcore.util

import net.minecraft.text.OrderedText
import net.minecraft.text.PlainTextContent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextContent
import net.minecraft.util.Language
import java.util.*

@kotlin.jvm.JvmRecord
data class RawText(val content: String, val style: Style) : Text {

	override fun getStyle(): Style {
		return style
	}

	override fun getContent(): TextContent {
		return PlainTextContent.Literal(content)
	}

	override fun getString(): String {
		return content
	}

	override fun getSiblings(): List<Text> {
		return ArrayList()
	}

	override fun asOrderedText(): OrderedText {
		val language = Language.getInstance()
		return language.reorder(this)
	}

	fun withString(string: String): RawText {
		return of(string, style)
	}

	companion object {

		fun of(string: String): RawText {
			return of(string, Style.EMPTY)
		}

		fun of(string: String, style: Style): RawText {
			return RawText(string, style)
		}
	}
}
