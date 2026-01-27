package me.eren.skcheese.elements.wrappedlists;

import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class WrappedListModule implements AddonModule {

    @Override
    public String name() {
        return "wrapped lists";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("wrapped-lists");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        WrappedListType.register();
        EffUnwrapWrappedList.register(registry);
        ExprWrappedList.register(registry);
    }

}

