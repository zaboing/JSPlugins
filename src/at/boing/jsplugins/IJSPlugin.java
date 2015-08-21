package at.boing.jsplugins;

import org.bukkit.event.Listener;

public interface IJSPlugin extends Listener {

    void onEnable();

    void onDisable();

    String getName();

    String getVersion();
}
