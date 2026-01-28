package me.eren.skcheese.elements.string;

import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class StringModule implements AddonModule {

    @Override
    public String name() {
        return "multi line strings";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("multi-line-strings");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        ExprLastString.register(registry);
        SecNewString.register(registry);
        ExprSecNewString.register(registry);
    }

}
