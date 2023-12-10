package me.eren.skcheese;

import ch.njol.skript.Skript;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class SkCheese extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Started SkCheese " + getDescription().getVersion());
        new Metrics(this, 19846);
        try {
            Skript.registerAddon(this).loadClasses("me.eren.skcheese", "elements");
        } catch (IOException exception) {
            getLogger().log(Level.SEVERE, "Failed to load the addon classes", exception);
        }
    }
}
