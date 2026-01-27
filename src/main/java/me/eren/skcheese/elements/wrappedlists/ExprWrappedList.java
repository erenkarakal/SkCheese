package me.eren.skcheese.elements.wrappedlists;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.TreeMap;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("WrappedList - New Wrapped List")
@Description("Converts a list variable to a wrapped list.")
@Since("1.3")
@Example("set {_wl} to wrapped {_var::*}")
public class ExprWrappedList extends SimpleExpression<WrappedList> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprWrappedList.class, WrappedList.class)
                        .addPattern("wrapped %-~objects%")
                        .build()
        );
    }

    private Variable<?> variable;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(expressions[0] instanceof Variable<?>)) {
            Skript.error("The wrapped list expression can only be used with variables.");
            return false;
        }
        variable = (Variable<?>) expressions[0];
        if (!variable.isList()) {
            Skript.error("You can only wrap a list variable.");
            return false;
        }
        return true;
    }

    @Override
    protected WrappedList[] get(Event e) {
        // already checked if it's a list in init(), has to be a map now
        // noinspection unchecked
        TreeMap<String, Object> map = (TreeMap<String, Object>) variable.getRaw(e);
        // if we are wrapping {_hippo::*}, we don't want to copy {_hippo}
        if (map != null) {
            map.remove(null);
            return new WrappedList[]{new WrappedList(map)};
        }
        return new WrappedList[]{};
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
    public String toString(Event event, boolean debug) {
        return "wrapped " + variable.toString(event, debug);
    }

}
