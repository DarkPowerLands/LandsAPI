package ru.landsproject.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class BasicCommand extends Command implements PluginIdentifiableCommand {
    protected CommandSender sender;
    private JavaPlugin plugin;

    protected BasicCommand() {
        super("");
    }

    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        this.sender = sender;
        List<String> complete = tabPressComplete(sender, alias, args);
        if (complete == null) {
            complete = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                complete.add(player.getName());
        }
        return complete;
    }

    @NotNull
    public Command setAliases(@NotNull List<String> aliases) {
        return super.setAliases(aliases);
    }

    public boolean setName(@NotNull String name) {
        return super.setName(name);
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] arguments) {
        this.sender = sender;
        run(sender, commandLabel, arguments);
        return true;
    }

    protected void sendMessage(String... messages) {
        Arrays.<String>stream(messages)
                .forEach(message -> this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    protected void sendMessage(CommandSender sender, String... messages) {
        Arrays.<String>stream(messages)
                .forEach(message -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    protected boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract List<String> tabPressComplete(@NotNull CommandSender paramCommandSender, @NotNull String paramString, @NotNull String[] paramArrayOfString);

    public abstract void run(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString);
}
