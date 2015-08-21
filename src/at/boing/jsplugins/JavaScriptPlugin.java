package at.boing.jsplugins;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class JavaScriptPlugin extends PluginBase {

    JavaScriptLoader loader;
    private IJSPlugin plugin;
    private PluginDescriptionFile descriptionFile;
    private File dataFolder;
    private FileConfiguration config;
    private boolean isEnabled;

    public JavaScriptPlugin(IJSPlugin plugin, JavaScriptLoader loader) {
        this.plugin = plugin;
        this.descriptionFile = new PluginDescriptionFile(plugin.getName(), plugin.getVersion(), plugin.getName());
        this.dataFolder = new File(new File("jsplugins", plugin.getName()), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.loader = loader;
    }


    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return descriptionFile;
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @Override
    public InputStream getResource(String s) {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(String s, boolean b) {

    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        try {
            config.load(new File(getDataFolder(), "default_config.yml"));
        } catch (IOException e) {
            getLogger().info("Did not load default configuration: " + e.getLocalizedMessage());
        } catch (InvalidConfigurationException e) {
            getLogger().info("Invalid default configuration: " + e.getLocalizedMessage());
        }
    }

    @Override
    public PluginLoader getPluginLoader() {
        return loader;
    }

    @Override
    public Server getServer() {
        return loader.server;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void onDisable() {
        isEnabled = false;
        plugin.onDisable();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        plugin.onEnable();
        isEnabled = true;
    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean b) {

    }

    @Override
    public EbeanServer getDatabase() {
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return getServer().getLogger();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
