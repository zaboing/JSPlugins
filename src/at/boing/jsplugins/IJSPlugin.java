package at.boing.jsplugins;

public interface IJSPlugin {

    void onEnable();

    void onDisable();

    String getName();

    String getVersion();
}
