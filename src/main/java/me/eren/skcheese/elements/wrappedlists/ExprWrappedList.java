package me.eren.skcheese.elements.wrappedlists;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

import java.util.TreeMap;

@Name("WrappedList - New Wrapped List")
@Description("Converts a list variable to a wrapped list.")
@Since("1.3")
@Examples("""
        set {_wl} to wrapped {_var::*}
        """)

public class ExprWrappedList extends SimpleExpression<WrappedList> {

    static {
        if (SkCheese.isSyntaxEnabled("wrapped-lists"))
            Skript.registerExpression(ExprWrappedList.class, WrappedList.class, ExpressionType.COMBINED, "wrapped %-~objects%");
    }

    private Variable<?> variable;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!(exprs[0] instanceof Variable<?>)) {
            Skript.error("The wrapped list expression can only be used with variables.");
            return false;
        }
        variable = (Variable<?>) exprs[0];
        if (!variable.isList()) {
            Skript.error("You can only wrap a list variable.");
            return false;
        }
        return true;
    }

    @Override
    protected WrappedList[] get(Event e) {
        // already checked if it's a list in init(), has to be a map now
        TreeMap<String, Object> map = (TreeMap<String, Object>) variable.getRaw(e);
        // if we are wrapping {_hippo::*}, we don't want to copy {_hippo}
        if (map != null) map.remove(null);
        return new WrappedList[]{ new WrappedList(map) };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WrappedList> getReturnType() {
        return WrappedList.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "wrapped " + variable;
    }

}
