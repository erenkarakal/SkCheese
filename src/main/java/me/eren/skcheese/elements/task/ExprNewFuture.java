package me.eren.skcheese.elements.task;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

@Name("Futures - New Feature")
@Description("Creates a new future that can be waited for.")
@Since("1.2")
@Examples("""
        set {_future} to new future with 5 second timeout
        """)

public class ExprNewFuture extends SimpleExpression<Future> {

    static {
        if (SkCheese.isSyntaxEnabled("futures"))
            Skript.registerExpression(ExprNewFuture.class, Future.class, ExpressionType.COMBINED, "[new] future with %timespan% timeout");
    }

    private Expression<Timespan> timeoutExpr;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        timeoutExpr = (Expression<Timespan>) exprs[0];
        return true;
    }

    @Override
    protected Future[] get(Event e) {
        Timespan timespan = timeoutExpr.getSingle(e);
        if (timespan != null) {
            return new Future[]{ new Future(timespan.getMilliSeconds()) };
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Future> getReturnType() {
        return Future.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "new future";
    }
}
