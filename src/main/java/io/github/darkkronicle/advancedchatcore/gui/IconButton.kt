package io.github.darkkronicle.advancedchatcore.gui

import fi.dy.masa.malilib.render.RenderUtils
import io.github.darkkronicle.advancedchatcore.util.Color
import io.github.darkkronicle.advancedchatcore.util.Colors
import lombok.Getter
import lombok.Setter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import java.util.function.Consumer

class IconButton(
	x: Int, y: Int, width: Int, height: Int, @field:Getter @field:Setter private val padding: Int, @field:Getter
	@field:Setter private val iconWidth: Int, @field:Getter @field:Setter private val iconHeight: Int, @field:Getter
	@field:Setter private val icon: Identifier?, @field:Getter @field:Setter private val onClick: Consumer<IconButton?>, @field:Setter
	@field:Getter private val onHover: String?
) :
	CleanButton(x, y, width, height, null, null) {

	constructor(x: Int, y: Int, sideLength: Int, iconLength: Int, icon: Identifier?, mouseClick: Consumer<IconButton?>) : this(x, y, sideLength, sideLength,
		iconLength, iconLength, icon, mouseClick)

	constructor(x: Int, y: Int, width: Int, height: Int, iconWidth: Int, iconHeight: Int, icon: Identifier?, mouseClick: Consumer<IconButton?>) : this(x, y,
		width, height, 2, iconWidth, iconHeight, icon, mouseClick, null)

	override fun render(mouseX: Int, mouseY: Int, unused: Boolean, context: DrawContext) {
		val relMX = mouseX - x
		val relMY = mouseY - y
		hovered = relMX >= 0 && relMX <= width && relMY >= 0 && relMY <= height

		var plusBack: Color = Colors.Companion.getInstance().getColorOrWhite("background").withAlpha(100)
		if (hovered) {
			plusBack = Colors.Companion.getInstance().getColorOrWhite("hover").withAlpha(plusBack.alpha())
		}

		RenderUtils.drawRect(x, y, width, height, plusBack.color())

		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderUtils.bindTexture(icon)
		context.drawTexture({ texture: Identifier? -> RenderLayer.getGuiTextured(texture) },
			icon, x + padding, y + padding, 0f, 0f, width - (padding * 2), height - (padding * 2),
			iconWidth, iconHeight, iconWidth, iconHeight)

		if (hovered && onHover != null) {
			context.drawCenteredTextWithShadow(
				MinecraftClient.getInstance().textRenderer,
				onHover,
				mouseX + 4,
				mouseY - 16,
				Colors.Companion.getInstance().getColorOrWhite("white").color())
		}
	}

	override fun onMouseClickedImpl(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
		onClick.accept(this)
		return true
	}
}
