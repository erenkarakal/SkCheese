package me.eren.skcheese.elements.task;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Futures - Wait for Future")
@Description("Waits for a future until it is completed or it timeouts.")
@Since("1.2")
@Example("wait for {_future} and store the result in {_result}")
public class EffWaitForFuture extends Effect {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                builder(EffWaitForFuture.class)
                        .addPattern("wait for [future] %future% and store the result in %-~object%")
                        .build()
        );
    }

    private Expression<Future> future;
    private Expression<Timespan> store;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        future = (Expression<Future>) expressions[0];
        // noinspection unchecked
        store = (Expression<Timespan>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
    }

    @Override
    protected TriggerItem walk(Event event) {
        Future future = this.future.getSingle(event);
        TriggerItem next = getNext();
        if (next == null || future == null) {
            return null;
        }
        Object localVars = Variables.removeLocals(event);

        boolean isPrimaryThread = Bukkit.isPrimaryThread();
        CompletableFuture<?> completableFuture = future.completableFuture();
        completableFuture.completeOnTimeout(null, future.timeout(), TimeUnit.MILLISECONDS); // don't want TimeoutException

        completableFuture.thenAcceptAsync(returnValue -> {
            if (localVars != null)
                Variables.setLocalVariables(event, localVars);

            store.change(event, new Object[]{returnValue}, Changer.ChangeMode.SET);

            if (isPrimaryThread) {
                Bukkit.getScheduler().runTask(SkCheese.instance(), () -> {
                    TriggerItem.walk(next, event);
                    Variables.removeLocals(event);
                });
            } else {
                TriggerItem.walk(next, event);
                Variables.removeLocals(event);
            }
        }).exceptionally(ex -> null);

        return null;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "wait for future " + future.toString(event, debug) + " and store it in " + store.toString(event, debug);
    }

}
