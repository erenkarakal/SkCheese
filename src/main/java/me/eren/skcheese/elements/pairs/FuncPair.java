package me.eren.skcheese.elements.pairs;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.util.Pair;
import me.eren.skcheese.SkCheese;

@Name("Pairs - New Pair")
@Description("Creates a new pair.")
@Since("1.4")
@Examples("""
        send first value of {_pair}
        """)
public class FuncPair {
    static {
        if (SkCheese.isSyntaxEnabled("pairs")) {
            PairType.register();

            Functions.registerFunction(new JavaFunction<>("pair", new Parameter[]{
                    new Parameter<>("first", DefaultClasses.OBJECT, true, null),
                    new Parameter<>("second", DefaultClasses.OBJECT, true, null)
            }, Classes.getExactClassInfo(Pair.class), true) {
                @Override
                public Pair[] execute(FunctionEvent<?> e, Object[][] params) {
                    Object first = params[0].length == 1 ? params[0][0] : null;
                    Object second = params[1].length == 1 ? params[1][0] : null;
                    return new Pair<?, ?>[]{ new Pair<>(first, second) };
                }
            });
        }
    }
}
