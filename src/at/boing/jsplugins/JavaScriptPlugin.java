package at.boing.jsplugins;

import com.avaje.ebean.EbeanServer;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaScriptPlugin extends PluginBase {

    protected final Map<Class<? extends Event>, Set<Consumer<Event>>> listeners = new HashMap<>();
    JavaScriptLoader loader;
    private IJSPlugin plugin;
    private PluginDescriptionFile descriptionFile;
    private File dataFolder;
    private File configFile;
    private FileConfiguration config;
    private boolean isEnabled;

    public JavaScriptPlugin(IJSPlugin plugin, JavaScriptLoader loader) {
        this.loader = loader;
        this.plugin = plugin;
        this.descriptionFile = new PluginDescriptionFile(plugin.getName(), plugin.getVersion(), plugin.getName());
        this.dataFolder = new File("jsplugins", plugin.getName());
        if (!dataFolder.exists()) {
            if (dataFolder.mkdirs()) {
                getLogger().info("Created directory " + dataFolder.getPath());
            }
        }
        this.configFile = new File(getDataFolder(), "config.yml");
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
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    @Override
    public void saveDefaultConfig() {
        throw new NotImplementedException();
    }

    @Override
    public void saveResource(String s, boolean b) {
        throw new NotImplementedException();
    }

    @SuppressWarnings("unused") // Suppress unused warnings: This method is being used in JavaScript plugins
    public String loadObject(String name) {
        try {
            return new String(Files.readAllBytes(getDataFolder().toPath().resolve(name)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load object " + name, e);
            return "{}";
        }
    }

    @SuppressWarnings("unused") // Suppress unused warnings: This method is being used in JavaScript plugins
    public boolean saveObject(String name, String jsonRepresentation) {
        try {
            Files.write(getDataFolder().toPath().resolve(name), jsonRepresentation.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save object " + name, e);
            return false;
        }
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
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
        listeners.clear();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        isEnabled = true;
        plugin.onEnable();
        getServer().getPluginManager().registerEvents(plugin, this);
    }

    @SuppressWarnings("unused") // Suppress unused warnings: This method is being used in JavaScript plugins
    public void on(String eventName, Consumer<Event> callback) {
        Class<? extends Event> eventClass = findEventClass(eventName);
        if (eventClass == null) {
            getLogger().warning("Couldn't find event type " + eventName);
        } else {
            Set<Consumer<Event>> callbacks = listeners.get(eventClass);
            if (callbacks == null) {
                callbacks = new HashSet<>();
                listeners.put(eventClass, callbacks);
            }
            callbacks.add(callback);
        }
    }

    private Class<? extends Event> findEventClass(String name) {
        for (Package pack : Package.getPackages()) {
            if (pack.getName().startsWith(Event.class.getPackage().getName())) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Event> found = (Class<? extends Event>) Class.forName(pack.getName() + "." + name);
                    return found;
                } catch (ClassNotFoundException e) {
                    // SILENTLY IGNORE AND CONTINUE SEARCHING
                }
            }
        }
        return null;
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
