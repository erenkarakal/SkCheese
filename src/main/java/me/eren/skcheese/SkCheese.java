package me.eren.skcheese;

import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public final class SkCheese extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Started SkCheese " + getDescription().getVersion());
        new Metrics(this, 19846);
        this.saveDefaultConfig();
        for (String key : this.getConfig().getConfigurationSection("syntaxes").getKeys(false)) {
            if (this.getConfig().getBoolean("syntaxes." + key)) {
                registerClass(key);
            }
        }
    }

    private void registerClass(String className) {
        try {
            Class<?> c = Class.forName("me.eren.skcheese.elements." + className);
            c.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            getLogger().log(Level.SEVERE, "Failed to load the addon classes", exception);
        }
    }
}
