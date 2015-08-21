package at.boing.jsplugins;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class JavaScriptLoader implements PluginLoader {
    public static final String JS_PATTERN = ".*\\.js$";
    Server server;
    private ScriptEngine scriptEngine;
    private Logger logger;

    public JavaScriptLoader(Server server) {
        this.server = server;
        logger = server.getLogger();
        ScriptEngineManager engineManager = new ScriptEngineManager();
        scriptEngine = engineManager.getEngineByName("nashorn");
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        try {
            logger.info("Loading " + file.getAbsolutePath());

            scriptEngine.put("logger", logger);
            scriptEngine.eval(Files.toString(file, Charsets.UTF_8));

            Invocable invocable = (Invocable) scriptEngine;
            IJSPlugin plugin = invocable.getInterface(IJSPlugin.class);
            return new JavaScriptPlugin(plugin, this);
        } catch (ScriptException | IOException | ClassCastException e) {
            throw new InvalidPluginException(e);
        }
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        return new PluginDescriptionFile(file.getName(), "0.0", "");
    }


    @Override
    public Pattern[] getPluginFileFilters() {
        return new Pattern[]{Pattern.compile(JS_PATTERN)};
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        return null;
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        logger.log(Level.INFO, "[" + plugin.getName() + "] Enabling " + plugin.getName() + " v" + plugin.getDescription().getVersion());
        plugin.onEnable();
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        logger.log(Level.INFO, "[" + plugin.getName() + "] Disabling " + plugin.getName() + " v" + plugin.getDescription().getVersion());
        plugin.onDisable();
    }
}
