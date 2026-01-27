package me.eren.skcheese.elements.labels;

import ch.njol.skript.ScriptLoader;
import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class LabelModule implements AddonModule {

    @Override
    public String name() {
        return "labels";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("labels");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        EffGoto.register(registry);
        EffLabel.register(registry);

        ScriptLoader.eventRegistry().register(ScriptLoader.ScriptPreInitEvent.class, configs ->
                configs.forEach(config -> LabelStorage.removeScriptLabels(config.getFile())));
    }

}
