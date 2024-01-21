package me.eren.skcheese.elements.labels;

import ch.njol.skript.config.Config;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import me.eren.skcheese.SkCheese;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class ScriptLoadListener implements Listener {

    static {
        if (SkCheese.isSyntaxEnabled("labels"))
            Bukkit.getPluginManager().registerEvents(new ScriptLoadListener(), SkCheese.instance);
    }

    @EventHandler
    public void onScriptLoad(PreScriptLoadEvent e) {
        for (Config config : e.getScripts()) {
            File scriptFile = config.getFile();
            LabelStorage.removeScriptLabels(scriptFile);
        }
    }
}