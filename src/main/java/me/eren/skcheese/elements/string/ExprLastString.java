package me.eren.skcheese.elements.string;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

@Name("String Builder - Last String")
@Description("Represents the last string created by the string builder, if it wasn't stored anywhere.")
@Since("1.2")
@Examples("""
        new string:
          "line 1"
          "line 2"
        send last string
        """)
public class ExprLastString extends SimpleExpression<String> {

    static {
        if (SkCheese.isSyntaxEnabled("multi-line-strings"))
            Skript.registerExpression(ExprLastString.class, String.class, ExpressionType.SIMPLE, "last string");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected String[] get(Event e) {
        return new String[] { SecNewString.lastString };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "last string";
    }
}
