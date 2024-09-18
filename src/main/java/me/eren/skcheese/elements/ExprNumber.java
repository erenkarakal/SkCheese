package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprNumber extends SimpleExpression<Number> {

    private String regex;
    private int pattern;

    static {
        if (SkCheese.isSyntaxEnabled("binary-and-hex-numbers"))
            Skript.registerExpression(
                ExprNumber.class,
                Number.class,
                ExpressionType.SIMPLE,
                "0(x|X)<[A-Fa-f0-9]+>",
                "0(b|B)<[0-1]+>"
        );
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        pattern = i;
        regex = parseResult.regexes.get(0).group();
        return true;
    }

    @Override
    protected Number @NotNull [] get(@NotNull Event event) {
        if (regex == null) return new Number[0];
        Number result = Integer.parseInt(regex, pattern == 0 ? 16 : 2);
        return new Number[]{ result };
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
    public @NotNull String toString(Event event, boolean b) {
        return (pattern == 0 ? "hexadecimal " : "binary ") + regex;
    }


}