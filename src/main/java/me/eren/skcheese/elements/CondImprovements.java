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

@Name("Reverted Condition / All True?")
@Description("Reverts a condition or checks if a boolean or a list of booleans are all true.")
@Since("1.1")
@Examples("""
        if !1 = 2:
          broadcast "1 is indeed not 2"
        
        if {_booleans::*}?:
          broadcast "all booleans in the list are true"
        """)

public class CondImprovements extends Condition {

    static {
        if (SkCheese.isSyntaxEnabled("condition-improvements"))
            Skript.registerCondition(
                CondImprovements.class,
                "\\!<.+>",
                "%booleans%\\?"
            );
    }

    Condition condition;
    Expression<Boolean> booleanExpression;
    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;

        switch (matchedPattern) {
            case 0: // revert condition
                String regex = parseResult.regexes.get(0).group();
                condition = Condition.parse(regex, "Can't understand this condition: \"" + regex + "\"");
                return condition != null;
            case 1: // all true?
                booleanExpression = (Expression<Boolean>) exprs[0];
                return true;
        }
        return false;
    }

    @Override
    public boolean check(Event e) {
        switch (pattern) {
            case 0: // revert condition
                return !(condition.check(e));
            case 1: // all true?
                Boolean[] values = booleanExpression.getAll(e);
                if (values.length == 0) return false; // apparently nothing is true
                return Arrays.stream(values).allMatch(value -> value != null && value);
        }
        return false;
    }

    @Override
    public String toString(Event e, boolean debug) {
        switch (pattern) {
            case 0:
                return "reverted condition";
            case 1:
                return "reverted boolean";
        }
        return "improved conditions";
    }
}
