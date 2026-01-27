package me.eren.skcheese.elements.bits;

import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class BitModule implements AddonModule {

    @Override
    public String name() {
        return "bit operations";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("bit-operations");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = addon.syntaxRegistry();
        ExprBit.register(registry);
        ExprBitwise.register(registry);
        ExprShift.register(registry);
    }

}
