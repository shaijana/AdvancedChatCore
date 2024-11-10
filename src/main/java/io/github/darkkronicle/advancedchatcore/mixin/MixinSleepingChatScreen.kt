/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.mixin

import io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.SleepingChatScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyArg

@Environment(EnvType.CLIENT)
@Mixin(SleepingChatScreen::class)
class MixinSleepingChatScreen : ChatScreen("") {

	@ModifyArg(method = "closeChatIfEmpty",
		at = At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
	fun openSleepingChatScreen(@Nullable screen: Screen?): Screen {
		return AdvancedChatScreen(chatField.text)
	}
}
