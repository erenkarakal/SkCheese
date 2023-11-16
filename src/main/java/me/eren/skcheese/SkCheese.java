package me.eren.skcheese;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.TriggerSection;
import me.eren.skcheese.elements.SecSwitch;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SkCheese extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Started SkCheese " + getDescription().getVersion());
        new Metrics(this, 19846);
        try {
            Skript.registerAddon(this)
                    .loadClasses("me.eren.skcheese", "elements");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
