package at.boing.jsplugins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPlugin extends JavaPlugin {

    private static final String[] lvlOneCommands = {"help", "list", "load", "unload", "reload"};
    private final Map<String, JavaScriptPlugin> loadedPlugins = new HashMap<>();

    @Override
    public void onEnable() {
        loadedPlugins.clear();
        getServer().getPluginManager().registerInterface(JavaScriptLoader.class);
    }

    @Override
    public void onDisable() {
        getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ("js".equals(command.getName())) {
            if (args.length == 0) {
                showUsage(sender);
                return true;
            } else {
                if ("help".equalsIgnoreCase(args[0])) {
                    showUsage(sender);
                    return true;
                } else if ("list".equalsIgnoreCase(args[0])) {
                    listLoadedScripts(sender);
                    return true;
                } else if ("load".equalsIgnoreCase(args[0])) {
                    if (args.length == 1) {
                        sender.sendMessage("Invalid command: please list one or more scripts to load");
                    } else {
                        for (int i = 1; i < args.length; i++) {
                            loadPlugin(sender, args[i]);
                        }
                    }
                } else if ("unload".equalsIgnoreCase(args[0])) {
                    if (args.length == 1) {
                        sender.sendMessage("Invalid command: please list one or more scripts to unload");
                    } else {
                        for (int i = 1; i < args.length; i++) {
                            unloadPlugin(sender, args[i]);
                        }
                    }
                } else if ("reload".equalsIgnoreCase(args[0])) {
                    if (args.length == 1) {
                        for (String path : loadedPlugins.keySet()) {
                            reloadPluginFullPath(sender, path);
                        }
                    } else {
                        for (int i = 1; i < args.length; i++) {
                            reloadPlugin(sender, args[i]);
                        }
                    }
                } else {
                    showUsage(sender);
                }
                return true;
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> suggestions = new ArrayList<>();

        if (strings.length == 1) {
            for (String lvlOneCommand : lvlOneCommands) {
                if (strings[0].isEmpty() || lvlOneCommand.toLowerCase().startsWith(strings[0].toLowerCase())) {
                    suggestions.add(lvlOneCommand);
                }
            }
        } else if ("load".equalsIgnoreCase(strings[0])) {
            File[] contents = new File("jsplugins").listFiles();
            if (contents != null) {
                for (File file : contents) {
                    try {
                        if (file.isFile() && !loadedPlugins.keySet().contains(file.getCanonicalPath())) {
                            if (strings[1].isEmpty() || file.getName().toLowerCase().startsWith(strings[1].toLowerCase())) {
                                suggestions.add(file.getName());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if ("unload".equalsIgnoreCase(strings[0]) || "reload".equalsIgnoreCase(strings[0])) {
            for (String canonicalPath : loadedPlugins.keySet()) {
                String fileName = new File(canonicalPath).getName();
                if (strings[1].isEmpty() || fileName.toLowerCase().startsWith(strings[1].toLowerCase())) {
                    suggestions.add(fileName);
                }
            }
        }

        return suggestions;
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage("Example usage:");
        sender.sendMessage("Load specific script: /js load file.js");
        sender.sendMessage("Reload all scripts: /js reload");
        sender.sendMessage("List loaded scripts: /js list");
    }

    private void listLoadedScripts(CommandSender sender) {
        sender.sendMessage("Currently loaded:");
        for (Map.Entry<String, JavaScriptPlugin> entry : loadedPlugins.entrySet()) {
            sender.sendMessage("From file " + new File(entry.getKey()).getName() + ": " + entry.getValue().getName() + " v" + entry.getValue().getDescription().getVersion());
        }
    }

    private void loadPlugin(CommandSender sender, String fileName) {
        try {
            File pluginFile = new File("jsplugins", fileName);
            if (loadedPlugins.containsKey(pluginFile.getCanonicalPath())) {
                sender.sendMessage("Plugin " + fileName + " is already loaded. You might want to reload instead.");
                return;
            }
            try {
                Plugin plugin = getServer().getPluginManager().loadPlugin(pluginFile);
                getServer().getPluginManager().enablePlugin(plugin);
                loadedPlugins.put(pluginFile.getCanonicalPath(), (JavaScriptPlugin) plugin);
            } catch (InvalidPluginException | InvalidDescriptionException e) {
                sender.sendMessage("Ran into some trouble loading " + fileName + ": " + e.getMessage());
                getLogger().info("Could not load " + fileName + ": " + e);
            }
        } catch (IOException e) {
            sender.sendMessage("Unexpected exception loading " + fileName + ": " + e);
            e.printStackTrace();
        }
    }

    private void reloadPlugin(CommandSender sender, String fileName) {
        unloadPlugin(sender, fileName);
        loadPlugin(sender, fileName);
    }

    private void reloadPluginFullPath(CommandSender sender, String fullPath) {
        reloadPlugin(sender, new File(fullPath).getName());
    }

    private void unloadPlugin(CommandSender sender, String fileName) {
        try {
            File pluginFile = new File("jsplugins", fileName);
            if (loadedPlugins.containsKey(pluginFile.getCanonicalPath())) {
                Plugin plugin = loadedPlugins.get(pluginFile.getCanonicalPath());
                getServer().getPluginManager().disablePlugin(plugin);
                loadedPlugins.remove(pluginFile.getCanonicalPath());
                sender.sendMessage("Unloaded " + fileName);
            } else {
                sender.sendMessage("Plugin " + fileName + " is not currently loaded.");
            }
        } catch (IOException e) {
            sender.sendMessage("Unexpected exception unloading " + fileName + ": " + e);
            e.printStackTrace();
        }
    }
}
