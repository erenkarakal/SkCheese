package me.eren.skcheese.elements.string;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@Name("String Builder - New String Builder")
@Description("Creates a new string that is joined by a new line by default.")
@Since("1.2")
@Examples("""
        new string stored in {_string}:
          "line 1"
          "line 2"
        send {_string}
        """)
public class SecNewString extends Section {

    static {
        if (SkCheese.isSyntaxEnabled("multi-line-strings"))
            Skript.registerSection(SecNewString.class, "new string [joined with %-string%] [stored in %-~object%]");
    }

    public static String lastString;
    private Expression<String> joinExpression;
    private Expression<?> storeExpression;
    private final List<Expression<String>> expressions = new ArrayList<>();

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        joinExpression = (Expression<String>) exprs[0];
        if (exprs[1] != null) {
            storeExpression = exprs[1];
            if (!Changer.ChangerUtils.acceptsChange(storeExpression, Changer.ChangeMode.SET, String.class)) {
                Skript.error("A string can not be stored in " + storeExpression);
                return false;
            }
        }
        for (Node node : sectionNode) {
            String line = node.getKey();
            if (line != null) {
                SkriptParser parser = new SkriptParser(line, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);
                expressions.add((Expression<String>) parser.parseExpression(String.class));
            }
        }
        return true;
    }

    @Override
    protected TriggerItem walk(Event e) {
        String joinText = (joinExpression != null) ? joinExpression.getSingle(e) : null;
        joinText = (joinText != null) ? joinText : "\n";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < expressions.size(); i++) {
            Expression<String> stringExpression = expressions.get(i);
            if (stringExpression != null) {
                stringBuilder.append(stringExpression.getSingle(e));
            }
            if (i < expressions.size() - 1) {
                stringBuilder.append(joinText);
            }
        }

        if (storeExpression != null) {
            storeExpression.change(e, new Object[]{ stringBuilder.toString() }, Changer.ChangeMode.SET);
        } else {
            lastString = stringBuilder.toString();
        }

        return getNext();
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "new string";
    }
}
