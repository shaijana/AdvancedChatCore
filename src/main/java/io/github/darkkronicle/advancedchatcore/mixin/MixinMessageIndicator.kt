package io.github.darkkronicle.advancedchatcore.mixin

import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import net.minecraft.client.gui.hud.MessageIndicator
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(MessageIndicator::class)
class MixinMessageIndicator {

	@Inject(method = "indicatorColor", at = At("HEAD"), cancellable = true)
	private fun getColor(ci: CallbackInfoReturnable<Int>) {
		val indicator = (this as MessageIndicator)
		val name = indicator.loggedName()
		ci.setReturnValue(when (name) {
			"Modified" -> ConfigStorage.ChatScreen.MODIFIED.config.color.intValue
			"Filtered" -> ConfigStorage.ChatScreen.FILTERED.config.color.intValue
			"Not Secure" -> ConfigStorage.ChatScreen.NOT_SECURE.config.color.intValue
			else ->  // And "System"
				ConfigStorage.ChatScreen.SYSTEM.config.color.intValue
		})
	}

	@Inject(method = "icon", at = At("HEAD"), cancellable = true)
	private fun getIcon(ci: CallbackInfoReturnable<MessageIndicator.Icon>) {
		if (!ConfigStorage.ChatScreen.SHOW_CHAT_ICONS.config.booleanValue) {
			ci.setReturnValue(null)
		}
	}
}
