package at.boing.jsplugins;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class CoffeeScriptLoader extends AbstractScriptLoader {
    public static final Pattern FILE_PATTERN = Pattern.compile(".*\\.coffee$");

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

    private ScriptEngineFactory engineFactory;

    public CoffeeScriptLoader(Server server) {
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

            Process coffeeProcess;
            try {
                coffeeProcess = buildCoffeeProcess();
            } catch (IOException e) {
                logger.severe("Could not locate your coffee-script installation. Install coffee-script and restart the server.");
                throw new RuntimeException(e);
            }
            writeFile(file, coffeeProcess.getOutputStream());
            String generated = readInput(coffeeProcess.getInputStream());
            scriptEngine.eval(generated);

            Invocable invocable = (Invocable) scriptEngine;
            IJSPlugin plugin = invocable.getInterface(IJSPlugin.class);
            if (plugin == null) {
                throw new InvalidPluginException(String.format("Your CoffeeScript plugin is not valid. Please check for any missing / misspelled methods in %s.", file.getName()));
            }
            ScriptPlugin pluginImpl = new ScriptPlugin(plugin, this);
            scriptEngine.put("$", pluginImpl);

            return pluginImpl;
        } catch (ScriptException | IOException | ClassCastException e) {
            throw new InvalidPluginException(e);
        }
    }

    private Process buildCoffeeProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("coffee", "-scb");
        try {
            return processBuilder.start();
        } catch (IOException e) {
            processBuilder = new ProcessBuilder("coffee.cmd", "-scb");
            return processBuilder.start();
        }
    }

    private void writeFile(File file, OutputStream outputStream) throws IOException {
        ByteStreams.copy(Files.newInputStream(file.toPath()), outputStream);
        outputStream.close();
    }


    private String readInput(InputStream stream) throws IOException {
        return CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
    }
}