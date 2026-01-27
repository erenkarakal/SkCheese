package me.eren.skcheese.elements.pairs;

import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class PairModule implements AddonModule {

    @Override
    public String name() {
        return "pairs";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("pairs");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        PairType.register();
        ExprPairValue.register(registry);
        FuncPair.register();
    }

}
