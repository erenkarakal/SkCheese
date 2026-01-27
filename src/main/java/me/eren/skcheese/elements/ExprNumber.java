package me.eren.skcheese.elements;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Binary and Hexadecimal Numbers")
@Description("Allows the use of hexadecimal and binary formats to represent literal numbers.")
@Example("set {_hex} to 0xFF # 255")
@Example("set {_bin} to 0b1010 # 10")
@Since("1.0.0")
public class ExprNumber extends SimpleExpression<Number> {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("binary-and-hex-numbers"))
            registry.register(SyntaxRegistry.EXPRESSION,
                    builder(ExprNumber.class, Number.class)
                            .addPatterns(
                                    "0(x|X)<[A-Fa-f0-9]+>",
                                    "0(b|B)<[0-1]+>"
                            ).build()
            );
    }

    private String number;
    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean kleenean, ParseResult parseResult) {
        pattern = matchedPattern;
        number = parseResult.regexes.getFirst().group();
        return true;
    }

    @Override
    protected Number @NotNull [] get(@NotNull Event event) {
        if (number == null) {
            return new Number[0];
        }
        Number result = Integer.parseInt(number, pattern == 0 ? 16 : 2);
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
    public @NotNull String toString(Event event, boolean debug) {
        return (pattern == 0 ? "hexadecimal " : "binary ") + number;
    }

}
