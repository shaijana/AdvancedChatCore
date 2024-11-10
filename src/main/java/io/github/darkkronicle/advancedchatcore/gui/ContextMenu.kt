package io.github.darkkronicle.advancedchatcore.gui

import fi.dy.masa.malilib.gui.widgets.WidgetBase
import io.github.darkkronicle.advancedchatcore.util.Color
import lombok.Getter
import lombok.Setter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import java.util.*

class ContextMenu @kotlin.jvm.JvmOverloads constructor(
	@field:Getter private val contextX: Int, @field:Getter private val contextY: Int, private val options: LinkedHashMap<Text, ContextConsumer>,
	close: Runnable, background: Color = Color(0, 0, 0, 200), hover: Color = Color(255, 255, 255, 100)
) :
	WidgetBase(contextX, contextY, 10, 10) {

	private var hoveredEntry: Text? = null

	@Getter
	@Setter
	private val close: Runnable

	@Setter
	@Getter
	private val background: Color

	@Setter
	@Getter
	private val hover: Color

	init {
		updateDimensions()
		this.close = close
		this.background = background
		this.hover = hover
	}

	fun updateDimensions() {
		setWidth(getMaxLengthString(options.keys.stream().map<String> { obj: Text -> obj.string }.toList()) + 4)
		setHeight(options.size * (textRenderer.fontHeight + 2))
		val windowWidth = MinecraftClient.getInstance().window.scaledWidth
		val windowHeight = MinecraftClient.getInstance().window.scaledHeight
		if (x + width > windowWidth) {
			x = windowWidth - width
		}
		if (y + height > windowHeight) {
			y = windowHeight - height
		}
	}

	override fun onMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		val success = super.onMouseClicked(mouseX, mouseY, mouseButton)
		if (success) {
			return true
		}
		// Didn't click on this
		close.run()
		return false
	}

	override fun onMouseClickedImpl(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		if (mouseButton != 0) {
			return false
		}
		if (hoveredEntry == null) {
			return false
		}
		options[hoveredEntry!!]!!.takeAction(contextX, contextY)
		close.run()
		return true
	}

	override fun render(mouseX: Int, mouseY: Int, selected: Boolean, context: DrawContext) {
		drawRect(context, x, y, width, height, background.color())
		val rX = x + 2
		var rY = y + 2
		hoveredEntry = null
		for (option in options.keys) {
			if (mouseX >= x && mouseX <= x + width && mouseY >= rY - 2 && mouseY < rY + fontHeight + 1) {
				hoveredEntry = option
				drawRect(context, rX - 2, rY - 2, width, textRenderer.fontHeight + 2, hover.color())
			}
			context.drawTextWithShadow(textRenderer, option, rX, rY, -1)
			rY += textRenderer.fontHeight + 2
		}
	}

	interface ContextConsumer {

		fun takeAction(x: Int, y: Int)
	}

	companion object {

		private fun drawRect(context: DrawContext, x: Int, y: Int, width: Int, height: Int, color: Int) {
			context.fill(x, y, x + width, y + height, color)
		}
	}
}
