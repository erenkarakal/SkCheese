package me.eren.skcheese.elements.string;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.localization.Language;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("String Builder - New String Builder")
@Description("Creates a new string that is joined by a new line by default.")
@Since("1.2")
@Example("""
        new string stored in {_string}:
          "line 1"
          "line 2"
        send {_string}
        """)
public class SecNewString extends Section {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.SECTION,
                builder(SecNewString.class)
                        .addPattern("new string [joined with %-string%] [stored in %-~object%]")
                        .build()
        );
    }

    public static String lastString;
    private Expression<String> join;
    private Expression<?> store;
    private final List<Expression<String>> expressions = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
                        ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        Skript.warning("This section is deprecated and will be removed in the future. Use 'set {_x} to new string:' instead.");
        join = (Expression<String>) expressions[0];
        if (expressions[1] != null) {
            store = expressions[1];
            if (!Changer.ChangerUtils.acceptsChange(store, Changer.ChangeMode.SET, String.class)) {
                Skript.error("A string can not be stored in " + store);
                return false;
            }
        }
        for (Node node : sectionNode) {
            String line = node.getKey();
            if (line != null) {
                line = ScriptLoader.replaceOptions(line);
                SkriptParser parser = new SkriptParser(line, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);
                this.expressions.add((Expression<String>) parser.parseExpression(String.class));
            }
        }
        return true;
    }

    @Override
    protected TriggerItem walk(Event event) {
        String joinText = (join != null) ? join.getSingle(event) : null;
        joinText = (joinText != null) ? joinText : "\n";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < expressions.size(); i++) {
            Expression<String> stringExpression = expressions.get(i);
            if (stringExpression != null) {
                String string = stringExpression.getSingle(event);
                stringBuilder.append(string != null ? string : Language.get("none"));
            }
            if (i < expressions.size() - 1) {
                stringBuilder.append(joinText);
            }
        }

        if (store != null) {
            store.change(event, new Object[]{stringBuilder.toString()}, Changer.ChangeMode.SET);
        } else {
            lastString = stringBuilder.toString();
        }

        return getNext();
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "new string";
    }

}
