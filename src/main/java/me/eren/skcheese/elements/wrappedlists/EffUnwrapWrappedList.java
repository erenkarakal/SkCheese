package me.eren.skcheese.elements.wrappedlists;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

import java.util.Map;
import java.util.TreeMap;

@Name("WrappedList - Unwrap List")
@Description("Unwraps a wrapped list inside a list variable.")
@Since("1.3")
@Examples("""
        unwrap {_wl} into {_var::*}
        """)

public class EffUnwrapWrappedList extends Effect {

    static {
        if (SkCheese.isSyntaxEnabled("wrapped-lists"))
            Skript.registerEffect(EffUnwrapWrappedList.class, "unwrap %wrappedlist% into %-~objects%");
    }

    private Variable<?> variable;
    private Expression<WrappedList> wrappedListExpr;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!(exprs[1] instanceof Variable<?>)) {
            Skript.error("You can only unwrap a wrapped list into a variable.");
            return false;
        }
        variable = (Variable<?>) exprs[1];
        if (!variable.isList()) {
            Skript.error("You can only unwrap a wrapped list into a list variable.");
            return false;
        }
        wrappedListExpr = (Expression<WrappedList>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        WrappedList wrappedList = wrappedListExpr.getSingle(e);
        if (wrappedList == null) return;
        // code from SkriptLang EffCopy
        String target = variable.getName().getSingle(e);
        target = target.substring(0, target.length() - (Variable.SEPARATOR + "*").length());
        TreeMap<String, Object> treeMap = wrappedList.treeMap();
        setVariables(e, target, treeMap, variable.isLocal());
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "unwrap " + wrappedListExpr + " into " + variable;
    }

    // code from SkriptLang EffCopy
    @SuppressWarnings("unchecked")
    private void setVariables(Event event, String targetName, Map<String, Object> source, boolean local) {
        source.forEach((key, value) -> {
            String node = targetName + (key == null ? "" : Variable.SEPARATOR + key);
            if (value instanceof Map) {
                setVariables(event, node, (Map<String, Object>) value, local);
                return;
            }
            Variables.setVariable(node, value, event, local);
        });
    }
}
