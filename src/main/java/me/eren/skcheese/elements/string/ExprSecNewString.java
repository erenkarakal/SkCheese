package me.eren.skcheese.elements.string;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

public class ExprSecNewString extends SectionExpression<String> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprSecNewString.class, String.class)
                        .addPattern("new string [joined with %-string%]")
                        .build()
        );
    }

    private Expression<String> join;
    private final List<Expression<?>> expressions = new ArrayList<>();

    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
                        @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        // noinspection unchecked
        join = (Expression<String>) expressions[0];

        if (sectionNode == null) {
            Skript.error("Missing section");
            return false;
        }

        for (Node node : sectionNode) {
            if (!(node instanceof SimpleNode)) {
                Skript.error("Invalid line: " + node.getKey());
                continue;
            }

            String line = node.getKey();
            if (line != null) {
                line = ScriptLoader.replaceOptions(line);
                SkriptParser parser = new SkriptParser(line, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);

                Expression<?> expression = parser.parseExpression(Object.class);
                if (expression == null) {
                    Skript.error("Can't understand this expression: " + line);
                    return false;
                }
                expression = LiteralUtils.defendExpression(expression);
                if (!LiteralUtils.canInitSafely(expression)) {
                    Skript.error("Can't understand this expression: " + line);
                    return false;
                }

                this.expressions.add(expression);
            }
        }
        return true;
    }

    @Override
    protected String @Nullable [] get(Event event) {
        StringBuilder builder = new StringBuilder();

        String join;
        if (this.join == null || (join = this.join.getSingle(event)) == null) {
            join = "\n";
        }

        for (int i = 0; i < expressions.size(); i++) {
            Expression<?> expression = expressions.get(i);
            Object value = expression.getSingle(event);
            builder.append(Objects.requireNonNullElse(Classes.toString(value), "\n"));

            if (i != expressions.size() - 1) {
                builder.append(join);
            }
        }

        return new String[]{builder.toString()};
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
    public String toString(@Nullable Event event, boolean debug) {
        return "new string" + (join == null ? "" : " joined with " + join.toString(event, debug));
    }

}
