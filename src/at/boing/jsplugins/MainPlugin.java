package at.boing.jsplugins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MainPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerInterface(JavaScriptLoader.class);
    }

    @Override
    public void onDisable() {
        getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("load".equals(command.getName()) && args.length == 1) {
            try {
                Plugin plugin = getServer().getPluginManager().loadPlugin(new File("jsplugins", args[0]));
                if (plugin == null) {
                    return false;
                }
                getServer().getPluginManager().enablePlugin(plugin);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return super.onCommand(sender, command, label, args);
    }
}
