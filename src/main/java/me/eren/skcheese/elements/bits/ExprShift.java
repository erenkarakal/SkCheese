package me.eren.skcheese.elements.bits;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Bits - Shift")
@Description("Performs a bitwise shift operation and returns the value.")
@Since("1.5")
@Example("broadcast 10 >> 3")
public class ExprShift extends SimpleExpression<Number> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprShift.class, Number.class)
                        .addPatterns(
                                "%number% \\<\\< %number%",
                                "%number% \\>\\> %number%",
                                "%number% \\>\\>\\> %number%"
                        ).build());
    }

    private Expression<Number> first;
    private Expression<Number> second;

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed,
                        @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        first = LiteralUtils.defendExpression(expressions[0]);
        second = LiteralUtils.defendExpression(expressions[1]);
        return true;
    }

    @Override
    protected Number @NotNull [] get(@NotNull Event event) {
        Number first = this.first.getSingle(event);
        Number second = this.second != null ? this.second.getSingle(event) : null;
        if (first == null) first = 0;
        if (second == null) second = 0;
        return switch (pattern) {
            case 0 -> new Number[]{ first.intValue() << second.intValue() };
            case 1 -> new Number[]{ first.intValue() >> second.intValue() };
            case 2 -> new Number[]{ first.intValue() >>> second.intValue() };
            default -> new Number[0];
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        String first = this.first.toString(event, debug);
        String second = this.second.toString(event, debug);

        return switch (pattern) {
            case 0 -> first + " << " + second;
            case 1 -> first + " >> " + second;
            case 2 -> first + " >>> " + second;
            default -> throw new IllegalStateException();
        };
    }

}
