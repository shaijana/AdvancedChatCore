/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.mixin;

import io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen;
import io.github.darkkronicle.advancedchatcore.chat.MessageDispatcher;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChatHud.class, priority = 1050)
public class MixinChatHud {

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo ci) {
        // Pass forward messages to dispatcher
        MessageDispatcher.getInstance().handleText(message, signature, indicator);
        ci.cancel();
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void clearMessages(boolean clearTextHistory, CallbackInfo ci) {
        if (!clearTextHistory) {
            // This only gets called if it is the keybind f3 + d
            return;
        }
        if (!ConfigStorage.General.CLEAR_ON_DISCONNECT.config.getBooleanValue()) {
            // Cancel clearing if it's turned off
            ci.cancel();
        }
    }

    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    private void isChatFocused(CallbackInfoReturnable<Boolean> ci) {
        // If the chat is focused
        ci.setReturnValue(AdvancedChatScreen.PERMANENT_FOCUS || client.currentScreen instanceof AdvancedChatScreen);
    }
}
