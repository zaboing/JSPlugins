package at.boing.jsplugins;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class JavaScriptLoader extends AbstractScriptLoader {
    public static final Pattern FILE_PATTERN = Pattern.compile(".*\\.js$");

    /**
     * This string is appended before every single JavaScript plugin.
     * There is no native way in Nashorn to set the directory for load calls; therefore, we have to use this workaround and overload the default load()
     * The default user.dir directory will be set in the engine arguments.
     */
    private static final String PREAMBLE = "var oldLoad = load;\n" +
            "\n" +
            "load = function(path) {\n" +
            "\toldLoad(\"jsplugins/\" + path);\n" +
            "}\n";

    private NashornScriptEngineFactory engineFactory;

    public JavaScriptLoader(Server server) {
        super(server);
        super.setFilePatterns(FILE_PATTERN);
        engineFactory = new NashornScriptEngineFactory();
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        try {
            logger.info("Loading " + file.getAbsolutePath());
            ScriptEngine scriptEngine = engineFactory.getScriptEngine();
            scriptEngine.eval(PREAMBLE);
            scriptEngine.eval(Files.toString(file, Charsets.UTF_8));

            Invocable invocable = (Invocable) scriptEngine;
            IJSPlugin plugin = invocable.getInterface(IJSPlugin.class);
            if (plugin == null) {
                throw new InvalidPluginException(String.format("Your JavaScript plugin is not valid. Please check for any missing / misspelled methods in %s.", file.getName()));
            }
            ScriptPlugin pluginImpl = new ScriptPlugin(plugin, this);
            scriptEngine.put("$", pluginImpl);

            return pluginImpl;
        } catch (ScriptException | IOException | ClassCastException e) {
            throw new InvalidPluginException(e);
        }
    }
}
