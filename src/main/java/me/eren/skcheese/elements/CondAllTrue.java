package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

import java.util.Arrays;

@Name("All True?")
@Description("Checks if a list of booleans are all true.")
@Since("1.1")
@Examples("""
        if {_booleans::*}?:
          broadcast "all booleans in the list are true
        """)

public class CondAllTrue extends Condition {

    static {
        if (SkCheese.isSyntaxEnabled("cond-all-true"))
            Skript.registerCondition(
                    CondAllTrue.class,
                    "%booleans%\\?"
            );
    }

    Expression<Boolean> booleanExpression;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        booleanExpression = (Expression<Boolean>) exprs[0];
        return true;
    }

    @Override
    public boolean check(Event e) {
        Boolean[] values = booleanExpression.getAll(e);
        if (values.length == 0) return false; // apparently nothing is true
        return Arrays.stream(values).allMatch(value -> value != null && value);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "reverted boolean";
    }
}
