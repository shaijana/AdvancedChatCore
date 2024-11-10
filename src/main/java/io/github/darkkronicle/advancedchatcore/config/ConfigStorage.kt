/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore;
import io.github.darkkronicle.advancedchatcore.config.options.ConfigColor;
import io.github.darkkronicle.advancedchatcore.interfaces.ConfigRegistryOption;
import io.github.darkkronicle.advancedchatcore.util.AbstractRegistry;
import io.github.darkkronicle.advancedchatcore.util.Color;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatcore.util.EasingMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Used to store values into config.json
@Environment(EnvType.CLIENT)
public class ConfigStorage implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = AdvancedChatCore.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static class General {

        public static final String NAME = "general";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.general." + key);
        }

        public static final SaveableConfig<ConfigString> TIME_FORMAT =
                SaveableConfig.fromConfig(
                        "timeFormat",
                        new ConfigString(
                                translate("timeformat"), "hh:mm", translate("info.timeformat")));

        public static final SaveableConfig<ConfigString> TIME_TEXT_FORMAT =
                SaveableConfig.fromConfig(
                        "timeTextFormat",
                        new ConfigString(
                                translate("timetextformat"),
                                "[%TIME%] ",
                                translate("info.timetextformat")));

        public static final SaveableConfig<ConfigColor> TIME_COLOR =
                SaveableConfig.fromConfig(
                        "time_color",
                        new ConfigColor(
                                translate("timecolor"),
                                Colors.getInstance().getColor("white").get(),
                                translate("info.timecolor")));

        public static final SaveableConfig<ConfigBoolean> SHOW_TIME =
                SaveableConfig.fromConfig(
                        "show_time",
                        new ConfigBoolean(
                                translate("showtime"), false, translate("info.showtime")));

        public static final SaveableConfig<ConfigBoolean> CLEAR_ON_DISCONNECT =
                SaveableConfig.fromConfig(
                        "clearOnDisconnect",
                        new ConfigBoolean(
                                translate("clearondisconnect"),
                                true,
                                translate("info.clearondisconnect")));

        public static final SaveableConfig<ConfigInteger> CHAT_STACK =
                SaveableConfig.fromConfig(
                        "chatStack",
                        new ConfigInteger(
                                translate("chatstack"), 0, 0, 20, translate("info.chatstack")));

        public static final SaveableConfig<ConfigBoolean> CHAT_STACK_UPDATE =
                SaveableConfig.fromConfig(
                        "chatStackUpdate",
                        new ConfigBoolean(
                                translate("chatstackupdate"),
                                false,
                                translate("info.chatstackupdate")));

        public static final SaveableConfig<ConfigString> MESSAGE_OWNER_REGEX =
                SaveableConfig.fromConfig(
                        "messageOwnerRegex",
                        new ConfigString(
                                translate("messageownerregex"),
                                "(?<!\\[)\\b[A-Za-z0-9_§]{3,16}\\b(?!\\])",
                                translate("info.messageownerregex")));

        public static final SaveableConfig<ConfigBoolean> FILTER_PROFANITY =
                SaveableConfig.fromConfig(
                        "filterProfanity",
                        new ConfigBoolean(
                                translate("filterprofanity"),
                                false,
                                translate("info.filterprofanity")));

        public static final SaveableConfig<ConfigBoolean> PROFANITY_ON_WORD_BOUNDARIES =
                SaveableConfig.fromConfig(
                        "profanityWordBoundaries",
                        new ConfigBoolean(
                                translate("profanitywordboundaries"),
                                false,
                                translate("info.profanitywordboundaries")));

        public static final SaveableConfig<ConfigDouble> PROFANITY_ABOVE =
                SaveableConfig.fromConfig(
                        "profanityAbove",
                        new ConfigDouble(
                                translate("profanityabove"),
                                0,
                                0,
                                3,
                                translate("info.profanityabove")));

        public static final ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS =
                ImmutableList.of(
                        TIME_FORMAT,
                        TIME_TEXT_FORMAT,
                        TIME_COLOR,
                        SHOW_TIME,
                        CLEAR_ON_DISCONNECT,
                        CHAT_STACK,
                        CHAT_STACK_UPDATE,
                        MESSAGE_OWNER_REGEX,
                        FILTER_PROFANITY,
                        PROFANITY_ABOVE,
                        PROFANITY_ON_WORD_BOUNDARIES
                );
    }

    public static class ChatScreen {

        public static final String NAME = "chatscreen";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.chatscreen." + key);
        }

        public static final SaveableConfig<ConfigBoolean> PERSISTENT_TEXT =
                SaveableConfig.fromConfig(
                        "persistentText",
                        new ConfigBoolean(
                                translate("persistenttext"),
                                false,
                                translate("info.persistenttext")));

        public static final SaveableConfig<ConfigColor> COLOR =
                SaveableConfig.fromConfig(
                        "color",
                        new ConfigColor(
                                translate("color"),
                                Colors.getInstance().getColor("black").get().withAlpha(100),
                                translate("info.color")));

        public static final SaveableConfig<ConfigBoolean> MORE_TEXT =
                SaveableConfig.fromConfig(
                        "moreText",
                        new ConfigBoolean(
                                translate("moretext"), false, translate("info.moretext")));

        public static final SaveableConfig<ConfigBoolean> SHOW_CHAT_ICONS =
                SaveableConfig.fromConfig(
                        "showChatIcons",
                        new ConfigBoolean(
                                translate("showchaticons"), true, translate("info.showchaticons")));

        public static final SaveableConfig<ConfigColor> MODIFIED =
                SaveableConfig.fromConfig(
                        "modified",
                        new ConfigColor(
                                translate("modified"), new Color(15386724), translate("info.modified")));

        public static final SaveableConfig<ConfigColor> SYSTEM =
                SaveableConfig.fromConfig(
                        "system",
                        new ConfigColor(
                                translate("system"), new Color(10526880), translate("info.system")));

        public static final SaveableConfig<ConfigColor> FILTERED =
                SaveableConfig.fromConfig(
                        "filtered",
                        new ConfigColor(
                                translate("filtered"), new Color(15386724), translate("info.filtered")));

        public static final SaveableConfig<ConfigColor> NOT_SECURE =
                SaveableConfig.fromConfig(
                        "notSecure",
                        new ConfigColor(
                                translate("notsecure"), new Color(15224664), translate("info.notsecure")));


        public static final ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS =
                ImmutableList.of(PERSISTENT_TEXT, COLOR, MORE_TEXT, SHOW_CHAT_ICONS, MODIFIED, SYSTEM, FILTERED, NOT_SECURE);
    }

    public static class Hotkeys {
        public static final String NAME = "hotkeys";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.hotkeys." + key);
        }

        public static final SaveableConfig<ConfigHotkey> OPEN_CHAT = SaveableConfig.fromConfig("openChat",
                new ConfigHotkey(translate("openchat"), "", KeybindSettings.create(
                        KeybindSettings.Context.INGAME, KeyAction.PRESS, false, true, false, true
                ), translate("info.openchat")));

        public static final SaveableConfig<ConfigHotkey> TOGGLE_PERMANENT = SaveableConfig.fromConfig("togglePermanent",
                new ConfigHotkey(translate("togglepermanentfocus"), "", KeybindSettings.create(
                        KeybindSettings.Context.INGAME, KeyAction.PRESS, false, true, false, true
                ), translate("info.togglepermanentfocus")));

        public static final SaveableConfig<ConfigHotkey> OPEN_CHAT_WITH_LAST = SaveableConfig.fromConfig("openChatWithLast",
                new ConfigHotkey(translate("openchatwithlast"), "UP", KeybindSettings.create(
                        KeybindSettings.Context.INGAME, KeyAction.PRESS, true, true, false, true
                ), translate("info.openchatwithlast")));

        public static final SaveableConfig<ConfigHotkey> OPEN_CHAT_FREE_MOVEMENT = SaveableConfig.fromConfig("openChatFreeMovement",
                new ConfigHotkey(translate("openchatfreemovement"), "", KeybindSettings.create(
                        KeybindSettings.Context.INGAME, KeyAction.PRESS, true, true, false, true
                ), translate("info.openchatfreemovement")));

        public static final SaveableConfig<ConfigHotkey> OPEN_SETTINGS = SaveableConfig.fromConfig("openSettings",
                new ConfigHotkey(translate("opensettings"), "", KeybindSettings.create(
                        KeybindSettings.Context.ANY, KeyAction.PRESS, false, true, false, true
                ), translate("info.opensettings")));

        public static final ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS =
                ImmutableList.of(OPEN_SETTINGS, OPEN_CHAT, OPEN_CHAT_FREE_MOVEMENT, TOGGLE_PERMANENT, OPEN_CHAT_WITH_LAST);
    }

    public static void loadFromFile() {
        File v3 = FileUtils.getConfigDirectory().toPath().resolve(CONFIG_FILE_NAME).toFile();
        File configFile;
        if (v3.exists()
                && !FileUtils.getConfigDirectory()
                        .toPath()
                        .resolve("advancedchat")
                        .resolve(CONFIG_FILE_NAME)
                        .toFile()
                        .exists()) {
            configFile = v3;
        } else {
            configFile =
                    FileUtils.getConfigDirectory()
                            .toPath()
                            .resolve("advancedchat")
                            .resolve(CONFIG_FILE_NAME)
                            .toFile();
        }

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                readOptions(root, General.NAME, General.OPTIONS);
                readOptions(root, ChatScreen.NAME, ChatScreen.OPTIONS);
                readOptions(root, Hotkeys.NAME, Hotkeys.OPTIONS);

                int version = JsonUtils.getIntegerOrDefault(root, "configVersion", 0);
            }
        }
    }

    /**
     * Applies a JSON element into a registry
     *
     * @param element Element in key
     * @param registry Registry to apply too
     */
    public static void applyRegistry(
            JsonElement element, AbstractRegistry<?, ? extends ConfigRegistryOption<?>> registry) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        for (ConfigRegistryOption<?> option : registry.getAll()) {
            if (obj.has(option.getSaveString())) {
                option.load(obj.get(option.getSaveString()));
            }
        }
    }

    /**
     * Creates a {@link JsonObject} containing registry data
     *
     * @param registry
     * @return
     */
    public static JsonObject saveRegistry(
            AbstractRegistry<?, ? extends ConfigRegistryOption<?>> registry) {
        JsonObject object = new JsonObject();
        for (ConfigRegistryOption<?> option : registry.getAll()) {
            object.add(option.getSaveString(), option.save());
        }
        return object;
    }

    public static void saveFromFile() {
        File dir = FileUtils.getConfigDirectory().toPath().resolve("advancedchat").toFile();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            writeOptions(root, General.NAME, General.OPTIONS);
            writeOptions(root, ChatScreen.NAME, ChatScreen.OPTIONS);
            writeOptions(root, Hotkeys.NAME, Hotkeys.OPTIONS);

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    public static void readOptions(
            JsonObject root, String category, List<SaveableConfig<?>> options) {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null) {
            for (SaveableConfig<?> conf : options) {
                IConfigBase option = conf.config;
                if (obj.has(conf.key)) {
                    option.setValueFromJsonElement(obj.get(conf.key));
                }
            }
        }
    }

    // WINDOWS BAD AND MINECRAFT LIKES UTF-16
    public static JsonElement parseJsonFile(File file) {
        if (file != null && file.exists() && file.isFile() && file.canRead()) {
            String fileName = file.getAbsolutePath();

            try {
                JsonParser parser = new JsonParser();
                Charset[] sets =
                        new Charset[] {
                            StandardCharsets.UTF_8, Charset.defaultCharset(),
                        };
                // Start to enforce UTF 8. Old files may be UTF-16
                for (Charset s : sets) {
                    JsonElement element;
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(file), s);
                    try {
                        element = parser.parse(reader);
                    } catch (Exception e) {
                        reader.close();
                        MaLiLib.logger.error(
                                "Failed to parse the JSON file '{}'. Attempting different charset."
                                        + " ",
                                fileName,
                                e);
                        continue;
                    }
                    reader.close();

                    return element;
                }
            } catch (Exception e) {
                MaLiLib.logger.error("Failed to parse the JSON file '{}'", fileName, e);
            }
        }

        return null;
    }

    // WINDOWS BAD AND MINECRAFT LIKES UTF-16
    public static boolean writeJsonToFile(JsonObject root, File file) {
        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(JsonUtils.GSON.toJson(root));
            writer.close();

            return true;
        } catch (IOException e) {
            MaLiLib.logger.warn(
                    "Failed to write JSON data to file '{}'", file.getAbsolutePath(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                MaLiLib.logger.warn("Failed to close JSON file", e);
            }
        }

        return false;
    }

    public static void writeOptions(
            JsonObject root, String category, List<SaveableConfig<?>> options) {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (SaveableConfig<?> option : options) {
            obj.add(option.key, option.config.getAsJsonElement());
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveFromFile();
    }

    /** Serializable easing data */
    public enum Easing implements IConfigOptionListEntry, EasingMethod {
        LINEAR("linear", Method.LINEAR),
        SINE("sine", Method.SINE),
        QUAD("quad", Method.QUAD),
        QUART("quart", Method.QUART),
        CIRC("circ", Method.CIRC);

        public final EasingMethod ease;
        public final String configString;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.easing." + key);
        }

        Easing(String configString, EasingMethod ease) {
            this.ease = ease;
            this.configString = configString;
        }

        @Override
        public String getStringValue() {
            return configString;
        }

        @Override
        public String getDisplayName() {
            return translate(configString);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();
            if (forward) {
                id++;
            } else {
                id--;
            }
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return fromEasingString(value);
        }

        public static Easing fromEasingString(String visibility) {
            for (Easing e : Easing.values()) {
                if (e.configString.equals(visibility)) {
                    return e;
                }
            }
            return Easing.LINEAR;
        }

        @Override
        public double apply(double v) {
            return ease.apply(v);
        }
    }
}
