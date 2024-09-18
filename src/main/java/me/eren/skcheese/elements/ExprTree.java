package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

import java.util.*;

@Name("Tree of List Variable")
@Description("Returns all indices of a list variable. In vanilla Skript, if you set \"{_var::a::b}\" and loop {_var::*} " +
        "nothing would be looped but this expression recursively gets all indices.")
@Since("1.1")
@Examples("""
        set {_var::1} to "a"
        set {_var::1::3} to "c"
        set {_var::2} to "b"
        
        loop tree of {_var::*}:
          send loop-branch       # 1, 2, 1::3
          send {_var::%loop-branch%}  # "a", "b", "c"
        """)

public class ExprTree extends SimpleExpression<String> {

    static {
        if (SkCheese.isSyntaxEnabled("variable-tree"))
            Skript.registerExpression(ExprTree.class, String.class, ExpressionType.COMBINED, "tree of %~objects%");
    }

    private Variable<?> variable;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!(exprs[0] instanceof Variable<?>)) {
            Skript.error("The tree expression can only be used with variables.");
            return false;
        }
        variable = (Variable<?>) exprs[0];
        if (!variable.isList()) {
            Skript.error("You can only get the tree of a list variable.");
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String[] get(Event e) {
        return getAllKeys((TreeMap<Object, Object>) variable.getRaw(e));
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
    public String toString(Event e, boolean debug) {
        return "tree of " + variable;
    }

    @Override
    public boolean isLoopOf(String s) {
        return s.equals("branch");
    }

    public String[] getAllKeys(TreeMap<Object, Object> map) {
        ArrayList<String> keysList = getAllKeysHelper(map, "");
        return keysList.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getAllKeysHelper(TreeMap<Object, Object> map, String prefix) {
        ArrayList<String> keys = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            String currentKey = entry.getKey() == null ? "" : entry.getKey().toString();
            String keyPath = prefix.isEmpty() ? currentKey : prefix +
                    (currentKey.isEmpty() ? "" : Variable.SEPARATOR + currentKey);

            if (entry.getValue() instanceof TreeMap) {
                keys.addAll(getAllKeysHelper((TreeMap<Object, Object>) entry.getValue(), keyPath));
            } else if (!keyPath.isEmpty()) {
                keys.add(keyPath);
            }
        }

        return keys;
    }
}
