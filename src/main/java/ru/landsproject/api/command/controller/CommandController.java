package ru.landsproject.api.command.controller;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.landsproject.api.command.BasicCommand;
import ru.landsproject.api.util.interfaces.Initable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class CommandController implements Initable {

    private final Logger LOG = Logger.getLogger(CommandController.class.getName());

    public SimplePluginManager simplePluginManager;

    public SimpleCommandMap simpleCommandMap;

    public CommandController() {
    }

    @Override
    public void init() {
        this.simplePluginManager = (SimplePluginManager) Bukkit.getServer().getPluginManager();
        Field f = null;
        try {
            f = SimplePluginManager.class.getDeclaredField("commandMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert f != null;
        f.setAccessible(true);
        try {
            this.simpleCommandMap = (SimpleCommandMap)f.get(this.simplePluginManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destruct() {
        for(JavaPlugin javaPlugin : commandList.values()) {
            unRegisterAll(javaPlugin);
        }
    }

    private final ConcurrentHashMap<BasicCommand, JavaPlugin> commandList = new ConcurrentHashMap<>();

    public void registerCommands(JavaPlugin plugin, BasicCommand... commands) {
        for (BasicCommand command : commands) {
            if (command.getClass().isAnnotationPresent(ru.landsproject.api.util.interfaces.Command.class)) {
                ru.landsproject.api.util.interfaces.Command commandAnnotation = command.getClass().getAnnotation(ru.landsproject.api.util.interfaces.Command.class);
                String commandName = commandAnnotation.name();
                command.setName(commandName);

                String[] aliases = commandAnnotation.aliases();
                command.setAliases(Arrays.asList(aliases));

                command.setPlugin(plugin);
                this.simpleCommandMap.register(commandAnnotation.identifier(), command);

                commandList.put(command, plugin);
            } else {
                this.LOG.warning("ERROR TO REGISTER COMMAND! NO ANNOTATION!");
            }
        }
    }

    public void unRegisterAll(JavaPlugin plugin) {
        Field f = null;
        try {
            f = SimpleCommandMap.class.getDeclaredField("knownCommands");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert f != null;
        f.setAccessible(true);
        try {
            Map<String, Command> commandMap = (Map<String, org.bukkit.command.Command>) f.get(this.simpleCommandMap);

            Iterator<Map.Entry<String, Command>> iterator = commandMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, org.bukkit.command.Command> entry = iterator.next();
                org.bukkit.command.Command command = entry.getValue();

                for (BasicCommand basicCommand : getCommandsByPlugin(plugin)) {
                    if (basicCommand.getName().equals(command.getName())) {
                        command.unregister(this.simpleCommandMap);
                        iterator.remove();
                        break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        commandList.clear();
    }

    private List<BasicCommand> getCommandsByPlugin(JavaPlugin plugin) {
        List<BasicCommand> basicCommands = new ArrayList<>();
        for(BasicCommand basicCommand : commandList.keySet()) {
            if(Objects.equals(basicCommand.getPlugin(), plugin)) {
                basicCommands.add(basicCommand);
            }
        }
        return basicCommands;
    }
}
