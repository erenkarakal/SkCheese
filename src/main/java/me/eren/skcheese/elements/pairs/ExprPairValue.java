package me.eren.skcheese.elements.pairs;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

@Name("Pairs - Value of Pair")
@Description("Gets the first/second value of a pair.")
@Since("1.4")
@Examples("""
        set {_pair} to pair(1, "hello")
        """)
public class ExprPairValue extends SimpleExpression<Object> {

    static {
        if (SkCheese.isSyntaxEnabled("pairs"))
            Skript.registerExpression(ExprPairValue.class, Object.class, ExpressionType.COMBINED, "(:first|second) value of %pair%");
    }

    private boolean isFirst;
    private Expression<Pair<?,?>> pairExpr;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        isFirst = parseResult.hasTag("first");
        pairExpr = (Expression<Pair<?, ?>>) exprs[0];
        return true;
    }

    @Override
    protected Object[] get(Event e) {
        Pair<?,?> pair = pairExpr.getSingle(e);
        if (pair == null) return null;
        Object value = isFirst ? pair.getFirst() : pair.getSecond();
        if (value == null) return null;
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
    public String toString(Event e, boolean debug) {
        return (isFirst ? "first" : "second") + "value of " + pairExpr;
    }
}
