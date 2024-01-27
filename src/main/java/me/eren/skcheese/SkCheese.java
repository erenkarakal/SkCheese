package me.eren.skcheese;

import ch.njol.skript.Skript;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class SkCheese extends JavaPlugin {

    public static SkCheese instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Started SkCheese " + getDescription().getVersion());
        new Metrics(this, 19846);

        try {
            Skript.registerAddon(this).loadClasses("me.eren.skcheese", "elements");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the addon.", e);
        }
        saveConfig();
    }

    public static boolean isSyntaxEnabled(String syntax) {
        return isSyntaxEnabled(syntax, true);
    }

    public static boolean isSyntaxEnabled(String syntax, boolean defaultValue) {
        if (!instance.getConfig().isSet("syntaxes." + syntax)) {
            instance.getConfig().set("syntaxes." + syntax, defaultValue);
            return defaultValue;
        }
        return instance.getConfig().getBoolean("syntaxes." + syntax);
    }
}
