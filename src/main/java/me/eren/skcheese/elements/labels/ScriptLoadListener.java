package me.eren.skcheese.elements.labels;

import ch.njol.skript.ScriptLoader;
import me.eren.skcheese.SkCheese;

public class ScriptLoadListener {

    static {
        if (SkCheese.isSyntaxEnabled("labels")) {
            ScriptLoader.eventRegistry().register(ScriptLoader.ScriptPreInitEvent.class, configs ->
                    configs.forEach(config -> LabelStorage.removeScriptLabels(config.getFile())));
        }
    }

}