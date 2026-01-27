package me.eren.skcheese.elements.switches;

import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class SwitchModule implements AddonModule {

    @Override
    public String name() {
        return "switch cases";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("switch-cases");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        SecSwitch.register(registry);
        SecSwitchCase.register(registry);
    }

}
