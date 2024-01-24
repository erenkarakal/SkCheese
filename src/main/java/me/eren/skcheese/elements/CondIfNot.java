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

@Name("If Not")
@Description("Checks if a condition doesn't pass.")
@Since("1.1")
@Examples("""
        if !1 = 2:
          broadcast "1 is indeed not 2"
        """)

public class CondIfNot extends Condition {

    static {
        if (SkCheese.isSyntaxEnabled("reverted-conditions"))
            Skript.registerCondition(
                CondIfNot.class,
                "\\!<.+>"
            );
    }

    Condition condition;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        String regex = parseResult.regexes.get(0).group();
        condition = Condition.parse(regex, "Can't understand this condition: \"" + regex + "\"");
        return condition != null;
    }

    @Override
    public boolean check(Event e) {
        return !(condition.check(e));
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "reverted condition";
    }
}
