package at.boing.jsplugins;

import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class AbstractScriptLoader implements PluginLoader {

    protected final Server server;
    protected final Logger logger;

    private Pattern[] filePatterns;

    public AbstractScriptLoader(Server server) {
        this.server = server;
        this.logger = server.getLogger();
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        return new PluginDescriptionFile(file.getName(), "0.0", "");
    }

    protected void setFilePatterns(Pattern... patterns) {
        this.filePatterns = patterns;
    }


    @Override
    public final Pattern[] getPluginFileFilters() {
        return filePatterns;
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");
        Validate.notNull(listener, "Listener can not be null");
        boolean useTimings = this.server.getPluginManager().useTimings();
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();

        if (!(plugin instanceof ScriptPlugin)) {
            throw new RuntimeException("You shouldn't do this.");
        }
        ScriptPlugin scriptPlugin = (ScriptPlugin) plugin;
        for (Map.Entry<Class<? extends Event>, Set<Consumer<Event>>> entry : scriptPlugin.listeners.entrySet()) {
            Class<? extends Event> eventClass = entry.getKey();
            for (Class executor = eventClass; Event.class.isAssignableFrom(executor); executor = executor.getSuperclass()) {
                @SuppressWarnings("unchecked")
                Annotation deprecated = executor.getAnnotation(Deprecated.class);
                if (deprecated != null) {
                    @SuppressWarnings("unchecked")
                    Warning warning = (Warning) executor.getAnnotation(Warning.class);
                    Warning.WarningState warningState = this.server.getWarningState();
                    if (warningState.printFor(warning)) {
                        plugin.getLogger().log(Level.WARNING, String.format("\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\"; please notify the authors %s.", new Object[]{plugin.getDescription().getFullName(), executor.getName(), "A js plugin", warning != null && warning.reason().length() != 0 ? warning.reason() : "Server performance will be affected", Arrays.toString(plugin.getDescription().getAuthors().toArray())}), warningState == Warning.WarningState.ON ? new AuthorNagException(null) : null);
                    }
                    break;
                }
            }

            Set<RegisteredListener> listeners = ret.get(eventClass);
            if (listeners == null) {
                listeners = new HashSet<>();
                ret.put(eventClass, listeners);
            }
            for (Consumer<Event> callback : entry.getValue()) {
                EventExecutor eventExecutor = (l, e) -> {
                    try {
                        if (eventClass.isAssignableFrom(e.getClass())) {
                            callback.accept(e);
                        }
                    } catch (Throwable var5) {
                        throw new EventException(var5);
                    }
                };
                if (useTimings) {
                    listeners.add(new TimedRegisteredListener(listener, eventExecutor, EventPriority.NORMAL, plugin, false));
                } else {
                    listeners.add(new RegisteredListener(listener, eventExecutor, EventPriority.NORMAL, plugin, false));
                }
            }
        }
        logger.info("Added " + ret.size() + " listeners");
        return ret;
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
