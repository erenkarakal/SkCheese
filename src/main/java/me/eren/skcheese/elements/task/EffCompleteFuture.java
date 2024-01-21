package me.eren.skcheese.elements.task;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

@Name("Futures - Complete Future")
@Description("Completes a future with the given value.")
@Since("1.2")
@Examples("""
        complete future {_future} with {_value}
        """)
public class EffCompleteFuture extends Effect {

    static {
        if (SkCheese.isSyntaxEnabled("futures"))
            Skript.registerEffect(EffCompleteFuture.class, "complete [future] %future% with %objects%");
    }

    Expression<Future> futureExpr;
    Expression<?> completeExpr;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        futureExpr = (Expression<Future>) exprs[0];
        completeExpr = LiteralUtils.defendExpression(exprs[1]);
        return true;
    }

    @Override
    protected void execute(Event e) {
        Future future = futureExpr.getSingle(e);
        Object value = completeExpr.getSingle(e);
        if (future == null) return;
        future.completableFuture.complete(value);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "complete future " + futureExpr + " with " + completeExpr;
    }
}
