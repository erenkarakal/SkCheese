package me.eren.skcheese.elements.pairs;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Pairs - Value of Pair")
@Description("Gets the first/second value of a pair.")
@Since("1.4")
@Example("set {_pair} to pair(1, \"hello\")")
public class ExprPairValue extends SimpleExpression<Object> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprPairValue.class, Object.class)
                        .addPattern("(:first|second) value of %pair%")
                        .build()
        );
    }

    private boolean isFirst;
    private Expression<Pair<?, ?>> pair;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        isFirst = parseResult.hasTag("first");
        // noinspection unchecked
        pair = (Expression<Pair<?, ?>>) expressions[0];
        return true;
    }

    @Override
    protected Object[] get(Event event) {
        Pair<?, ?> pair = this.pair.getSingle(event);
        if (pair == null) {
            return null;
        }
        Object value = isFirst ? pair.first() : pair.second();
        if (value == null) {
            return null;
        }
        return new Object[]{ value };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Pair.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return (isFirst ? "first" : "second") + "value of " + pair.toString(event, debug);
    }

}
