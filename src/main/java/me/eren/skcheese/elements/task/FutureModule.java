package me.eren.skcheese.elements.task;

import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class FutureModule implements AddonModule {

    @Override
    public String name() {
        return "futures";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("futures");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        FutureType.register();
        EffCompleteFuture.register(registry);
        EffWaitForFuture.register(registry);
        ExprNewFuture.register(registry);
    }

}
