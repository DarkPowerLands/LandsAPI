package ru.landsproject.api.configuration;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Messager {

    public static String parseApi(String text, Player player) {
        String s2 = text;
        if (player != null && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            s2 = PlaceholderAPI.setPlaceholders(player, text);
        }

        return s2;
    }

    public static void sendTitle(Player player, String text, int fadeIn, int fadeStay, int fadeOut) {
        if (!text.contains("<br>")) {
            player.sendTitle(color(text), "", fadeIn, fadeStay, fadeOut);
        } else {
            String[] lines = text.split("<br>");
            player.sendTitle(color(lines[0]), color(lines[1]), fadeIn, fadeStay, fadeOut);
        }
    }

    public static void sendActionBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (new ComponentBuilder(color(text))).create());
    }

    public static String color(String text) {
        if (!text.contains("&")) {
            return text;
        } else {
            String[] texts = text.split(String.format("((?<=%1$s)|(?=%1$s))", "&"));
            StringBuilder finalText = new StringBuilder();

            try {
                for (int i = 0; i < texts.length; ++i) {
                    if (texts[i].equalsIgnoreCase("&")) {
                        ++i;
                        if (texts[i].charAt(0) == '#') {
                            try {
                                finalText.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                            } catch (Exception var5) {
                                finalText.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                            }
                        } else {
                            finalText.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                        }
                    } else {
                        finalText.append(texts[i]);
                    }
                }
            } catch (Exception e) {
                return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
            }

            return finalText.toString();
        }
    }

    public static String getRuTimeForm(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds cannot be negative");
        }

        if (seconds == 0) {
            return "0 секунд";
        }

        long days = seconds / (60 * 60 * 24);
        seconds %= (60 * 60 * 24);
        long hours = seconds / (60 * 60);
        seconds %= (60 * 60);
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(" ").append(getRussianWordForNumber(days, "день", "дня", "дней")).append(" ");
        }

        if (hours > 0) {
            result.append(hours).append(" ").append(getRussianWordForNumber(hours, "час", "часа", "часов")).append(" ");
        }

        if (minutes > 0) {
            result.append(minutes).append(" ").append(getRussianWordForNumber(minutes, "минута", "минуты", "минут")).append(" ");
        }

        if (seconds > 0) {
            result.append(seconds).append(" ").append(getRussianWordForNumber(seconds, "секунда", "секунды", "секунд"));
        }

        return result.toString();
    }

    private static String getRussianWordForNumber(long number, String form1, String form2, String form5) {
        number = Math.abs(number) % 100;
        long remainder = number % 10;
        if (number > 10 && number < 20) {
            return form5;
        }
        if (remainder > 1 && remainder < 5) {
            return form2;
        }
        if (remainder == 1) {
            return form1;
        }
        return form5;
    }
}
