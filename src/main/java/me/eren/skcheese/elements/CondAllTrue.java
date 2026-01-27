package me.eren.skcheese.elements;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("All True?")
@Description("Checks if a list of booleans are all true.")
@Since("1.1")
@Example("""
        if {_booleans::*}?:
          broadcast "all booleans in the list are true
        """)
public class CondAllTrue extends Condition {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("cond-all-true")) {
            registry.register(SyntaxRegistry.CONDITION,
                    builder(CondAllTrue.class)
                            .addPattern("%booleans%\\?")
                            .build()
            );
        }
    }

    Expression<Boolean> b00lean;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        b00lean = (Expression<Boolean>) expressions[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        Boolean[] values = b00lean.getAll(event);
        if (values.length == 0) {
            return false; // apparently nothing is true
        }
        return Arrays.stream(values).allMatch(value -> value != null && value);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "reverted boolean " + b00lean.toString(event, debug);
    }

}
