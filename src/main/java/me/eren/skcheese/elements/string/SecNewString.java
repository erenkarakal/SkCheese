package me.eren.skcheese.elements.string;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class SecNewString extends Section {

    static {
        if (SkCheese.isSyntaxEnabled("multi-line-strings")) {
            Skript.registerSection(SecNewString.class, "new string [stored in %-~object%]");
            Skript.registerExpression(ExprLastString.class, String.class, ExpressionType.SIMPLE, "last string");
        }
    }

    public static String lastString;
    private Expression<?> storeExpression;
    private final List<Expression<String>> expressions = new ArrayList<>();

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (exprs[0] != null) {
            storeExpression = exprs[0];
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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < expressions.size(); i++) {
            String string = expressions.get(i).getSingle(e);
            stringBuilder.append(string);
            if (i < expressions.size() - 1) {
                stringBuilder.append("\n");
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
