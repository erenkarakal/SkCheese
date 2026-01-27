package me.eren.skcheese.elements.task;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Futures - Complete Future")
@Description("Completes a future with the given value.")
@Since("1.2")
@Example("complete future {_future} with {_value}")
public class EffCompleteFuture extends Effect {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                builder(EffCompleteFuture.class)
                        .addPattern("complete [future] %future% with %objects%")
                        .build()
        );
    }

    Expression<Future> future;
    Expression<?> completeValue;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        future = (Expression<Future>) expressions[0];
        completeValue = LiteralUtils.defendExpression(expressions[1]);
        return true;
    }

    @Override
    protected void execute(Event event) {
        Future future = this.future.getSingle(event);
        Object completeValue = this.completeValue.getSingle(event);
        if (future == null) {
            return;
        }
        future.completableFuture().complete(completeValue);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "complete future " + future.toString(event, debug) + " with " + completeValue.toString(event, debug);
    }

}
