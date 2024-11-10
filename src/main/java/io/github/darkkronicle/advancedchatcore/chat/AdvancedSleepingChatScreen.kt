/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class AdvancedSleepingChatScreen extends AdvancedChatScreen {

    public AdvancedSleepingChatScreen() {
        super("");
    }

    public void initGui() {
        super.initGui();
        ButtonGeneric stopSleep =
                new ButtonGeneric(
                        this.width / 2 - 100,
                        this.height - 40,
                        200,
                        20,
                        StringUtils.translate("multiplayer.stopSleeping"));
        this.addButton(stopSleep, (button, mouseButton) -> stopSleeping());
    }

    public void onClose() {
        this.stopSleeping();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyCodes.KEY_ESCAPE) {
            this.stopSleeping();
        } else if (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_KP_ENTER) {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                MessageSender.getInstance().sendMessage(string);
            }

            this.chatField.setText("");
            this.client.inGameHud.getChatHud().resetScroll();
            // Prevents really weird interactions with chat history
            resetCurrentMessage();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void stopSleeping() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        clientPlayNetworkHandler.sendPacket(
                new ClientCommandC2SPacket(
                        this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
        GuiBase.openGui(null);
    }
}
