package me.eren.skcheese.elements.pairs;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.function.Functions;
import me.eren.skcheese.SkCheese;

import static org.skriptlang.skript.common.function.DefaultFunction.builder;

@Name("Pairs - New Pair")
@Description("Creates a new pair.")
@Since("1.4")
@Example("send first value of {_pair}")
public class FuncPair {

    protected static void register() {
        Functions.register(builder(SkCheese.addon(), "pair", Pair.class)
                .parameter("first", Object.class)
                .parameter("second", Object.class)
                .build(args -> new Pair<>(args.get("first"), args.get("second")))
        );
    }

}
