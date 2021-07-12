package net.timme7893.gangs.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;

public class MessageManager {

    private final Map<Integer, Pattern> compiledPlaceholders = new HashMap<>();
    private final FileConfiguration cfg;
    private static String nmsver;

    public MessageManager() {
        this.cfg = Archive.get("messages").getFileConfiguration();
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
    }

    public Collection<String> getMessage(String id, Object... args) {
        ConfigurationSection section = this.cfg.getConfigurationSection(id);
        if (section == null) {
            return Collections.singleton("Missing lang key: " + id);
        }

        if (section.contains("messages")) {
            List<String> langMessages = section.getStringList("messages");

            Collection<String> messages = new ArrayList<>(langMessages.size());
            for (String message : langMessages) {
                messages.add(this.format(message, args));
            }
            return messages;
        }

        if (section.contains("message")) {
            String message = section.getString("message");
            return Collections.singleton(this.format(message, args));
        }

        return Collections.emptyList();
    }

    public void sendMessage(String id, CommandSender target, Object... args) {
        String prefix = this.isPrefixEnabled() ? this.getMessage("message-prefix").iterator().next() : "";
        for (String line : this.getMessage(id, args)) {
            target.sendMessage(prefix + line);
        }
    }

    public void sendMessage(String id, Gang gang, Object... args) {
        for (String line : this.getMessage(id, args)) {
            for (GMember member : gang.getGangMemberList()) {
                if (member.isOnline()) {
                    member.getOnlinePlayer().sendMessage(gang.getGangName() + " &f> " + line);
                }
            }
        }
    }

    public boolean isPrefixEnabled() {
        return this.cfg.getBoolean("message-prefix.enabled", true);
    }

    public void sendActionBar(String id, Player player, Object... args) {
        Collection<String> messages = this.getMessage(id, args);
        for (String message : messages) {
            sendActionBar(player,message);
        }
    }

    private String format(String fmt, Object... args) {
        String forwardBrace = "\\{", backwardsBrace = "\\}";
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String argString = arg == null ? "null" : arg.toString();
            fmt = fmt.replaceAll(forwardBrace + i + backwardsBrace, argString);
        }

        fmt = fmt.replace("%arrow%", "►");

        return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', fmt));
    }


    public static void sendActionBar(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            try {
                Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
                Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                Object chatMessageType = null;
                for (Object obj : chatMessageTypes) {
                    if (obj.toString().equals("GAME_INFO")) {
                        chatMessageType = obj;
                    }
                }
                Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
            } catch (ClassNotFoundException cnfe) {
                Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
            }

            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getDataFile() {
        return Archive.get("messages");
    }

}
