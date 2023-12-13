package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Arrays;

@Name("Reverted Condition / All True?")
@Description("Reverts a condition or checks if a boolean or a list of booleans are all true.")
@Since("1.1")
public class CondImprovements extends Condition {

    static {
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
                return Arrays.stream(booleanExpression.getAll(e)).allMatch(value -> value);
        }
        return false;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "reversed condition";
    }
}
