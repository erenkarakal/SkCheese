package me.eren.skcheese.elements;

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
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Tree of List Variable")
@Description("Returns all indices of a list variable. In vanilla Skript, if you set \"{_var::a::b}\" and loop {_var::*} " +
        "nothing would be looped but this expression recursively gets all indices.")
@Since("1.1")
@Example("""
        set {_var::1} to "a"
        set {_var::1::3} to "c"
        set {_var::2} to "b"
        
        loop tree of {_var::*}:
          send loop-branch       # 1, 2, 1::3
          send {_var::%loop-branch%}  # "a", "b", "c"
        """)
public class ExprTree extends SimpleExpression<String> {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("variable-tree")) {
            registry.register(SyntaxRegistry.EXPRESSION,
                    builder(ExprTree.class, String.class)
                            .addPattern("tree of %~objects%")
                            .build()
            );
        }
    }

    private Variable<?> variable;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(expressions[0] instanceof Variable<?>)) {
            Skript.error("The tree expression can only be used with variables.");
            return false;
        }
        variable = (Variable<?>) expressions[0];
        if (!variable.isList()) {
            Skript.error("You can only get the tree of a list variable.");
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String[] get(Event event) {
        return getAllKeys((TreeMap<Object, Object>) variable.getRaw(event));
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "tree of " + variable.toString(event, debug);
    }

    @Override
    public boolean isLoopOf(String input) {
        return input.equals("branch");
    }

    public String[] getAllKeys(TreeMap<Object, Object> map) {
        ArrayList<String> keysList = getAllKeysHelper(map, "");
        return keysList.toArray(new String[0]);
    }

    public ArrayList<String> getAllKeysHelper(TreeMap<Object, Object> map, String prefix) {
        ArrayList<String> keys = new ArrayList<>();

        for (Entry<Object, Object> entry : map.entrySet()) {
            String currentKey = entry.getKey() == null ? "" : entry.getKey().toString();
            String keyPath = prefix.isEmpty() ? currentKey : prefix +
                    (currentKey.isEmpty() ? "" : Variable.SEPARATOR + currentKey);

            if (entry.getValue() instanceof TreeMap) {
                // noinspection unchecked
                keys.addAll(getAllKeysHelper((TreeMap<Object, Object>) entry.getValue(), keyPath));
            } else if (!keyPath.isEmpty()) {
                keys.add(keyPath);
            }
        }

        return keys;
    }

}
