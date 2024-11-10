/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.mixin

import io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen
import io.github.darkkronicle.advancedchatcore.chat.AdvancedSleepingChatScreen
import io.github.darkkronicle.advancedchatcore.chat.ChatHistory
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyArg
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient::class)
class MixinMinecraftClient {

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = At("RETURN"))
	fun onDisconnect(screen: Screen?, ci: CallbackInfo?) {
		// Clear data on disconnect
		if (ConfigStorage.General.CLEAR_ON_DISCONNECT.config.booleanValue) {
			ChatHistory.Companion.getInstance().clearAll()
		}
	}

	@Inject(method = "openChatScreen(Ljava/lang/String;)V", at = At(value = "HEAD"), cancellable = true)
	fun openChatScreen(text: String, ci: CallbackInfo) {
		MinecraftClient.getInstance().setScreen(AdvancedChatScreen(text))
		ci.cancel()
	}

	@ModifyArg(method = "tick()V",
		at = At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
	fun openSleepingChatScreen(@Nullable screen: Screen?): Screen {
		return AdvancedSleepingChatScreen()
	}
}
