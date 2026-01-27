package me.eren.skcheese.elements.wrappedlists;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Map;
import java.util.TreeMap;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("WrappedList - Unwrap List")
@Description("Unwraps a wrapped list inside a list variable.")
@Since("1.3")
@Example("unwrap {_wl} into {_var::*}")
public class EffUnwrapWrappedList extends Effect {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                builder(EffUnwrapWrappedList.class)
                        .addPattern("unwrap %wrappedlist% into %-~objects%")
                        .build()
        );
    }

    private Variable<?> variable;
    private Expression<WrappedList> wrappedList;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(expressions[1] instanceof Variable<?>)) {
            Skript.error("You can only unwrap a wrapped list into a variable.");
            return false;
        }
        variable = (Variable<?>) expressions[1];
        if (!variable.isList()) {
            Skript.error("You can only unwrap a wrapped list into a list variable.");
            return false;
        }
        // noinspection unchecked
        wrappedList = (Expression<WrappedList>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        WrappedList wrappedList = this.wrappedList.getSingle(event);
        if (wrappedList == null) {
            return;
        }
        // code from SkriptLang EffCopy
        String target = variable.getName().getSingle(event);
        if (target == null) {
            return;
        }
        target = target.substring(0, target.length() - (Variable.SEPARATOR + "*").length());
        TreeMap<String, Object> treeMap = wrappedList.treeMap();
        setVariables(event, target, treeMap, variable.isLocal());
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "unwrap " + wrappedList + " into " + variable;
    }

    // code from SkriptLang EffCopy
    private void setVariables(Event event, String targetName, Map<String, Object> source, boolean local) {
        source.forEach((key, value) -> {
            String node = targetName + (key == null ? "" : Variable.SEPARATOR + key);
            if (value instanceof Map) {
                // noinspection unchecked
                setVariables(event, node, (Map<String, Object>) value, local);
                return;
            }
            Variables.setVariable(node, value, event, local);
        });
    }

}
