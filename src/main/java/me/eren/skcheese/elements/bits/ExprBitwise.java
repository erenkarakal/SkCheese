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

@Name("Bits - Bitwise Operation")
@Description("Performs bitwise operations on numbers and returns their value.")
@Since("1.5")
@Example("broadcast 5 & 3")
public class ExprBitwise extends SimpleExpression<Number> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprBitwise.class, Number.class)
                        .addPatterns(
                                "%number% \\& %number%",
                                "%number% \\| %number%",
                                "%number% \\^\\^ %number%",
                                "\\~%number%"
                        ).build()
        );
    }

    private Expression<Number> first;
    private Expression<Number> second;

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed,
                        @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        first = LiteralUtils.defendExpression(expressions[0]);
        if (pattern != 3) {
            second = LiteralUtils.defendExpression(expressions[1]);
        }
        return true;
    }

    @Override
    protected Number @NotNull [] get(@NotNull Event event) {
        Number first = this.first.getSingle(event);
        Number second = this.second != null ? this.second.getSingle(event) : null;
        if (first == null) first = 0;
        if (second == null) second = 0;
        return switch (pattern) {
            case 0 -> new Number[]{ first.intValue() & second.intValue() };
            case 1 -> new Number[]{ first.intValue() | second.intValue() };
            case 2 -> new Number[]{ first.intValue() ^ second.intValue() };
            case 3 -> new Number[]{ ~first.intValue() };
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
        return switch (pattern) {
            case 0 -> "bitwise AND";
            case 1 -> "bitwise inclusive OR";
            case 2 -> "bitwise exclusive OR";
            case 3 -> "Unary bitwise complement";
            default -> "bitwise operator";
        };
    }

}
