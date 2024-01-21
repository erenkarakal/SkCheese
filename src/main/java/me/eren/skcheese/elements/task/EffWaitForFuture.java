package me.eren.skcheese.elements.task;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Name("Futures - Wait for Future")
@Description("Waits for a future until it is completed or it timeouts.")
@Since("1.2")
@Examples("""
        wait for {_future} and store the result in {_result}
        """)
public class EffWaitForFuture extends Effect {

    static {
        if (SkCheese.isSyntaxEnabled("futures"))
            Skript.registerEffect(EffWaitForFuture.class, "wait for [future] %future% and store the result in %-~object%");
    }

    private Expression<Timespan> storeExpr;
    private Expression<Future> futureExpr;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        futureExpr = (Expression<Future>) exprs[0];
        storeExpr = (Expression<Timespan>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event e) {}

    @Override
    protected TriggerItem walk(Event e) {
        Future future = futureExpr.getSingle(e);
        TriggerItem next = getNext();
        if (next == null || future == null) return null;
        Object localVars = Variables.removeLocals(e);

        CompletableFuture<?> completableFuture = future.completableFuture;
        completableFuture.completeOnTimeout(null, future.timeout, TimeUnit.MILLISECONDS); // I don't want TimeoutException

        completableFuture.thenAcceptAsync(returnValue -> {
            if (localVars != null)
                Variables.setLocalVariables(e, localVars);

            storeExpr.change(e, new Object[]{ returnValue }, Changer.ChangeMode.SET);
            TriggerItem.walk(next, e);
            Variables.removeLocals(e);
        }).exceptionally(ex -> null);

        return null;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "wait for future " + futureExpr + " and store it in " + storeExpr;
    }
}
