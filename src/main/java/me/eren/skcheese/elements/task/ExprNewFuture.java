package me.eren.skcheese.elements.task;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static ch.njol.skript.util.Timespan.TimePeriod.TICK;
import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Futures - New Future")
@Description("Creates a new future that can be waited for.")
@Since("1.2")
@Example("""
        set {_future} to new future with 5 second timeout
        """)
public class ExprNewFuture extends SimpleExpression<Future> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprNewFuture.class, Future.class)
                        .addPattern("[new] future with %timespan% timeout")
                        .build()
        );
    }

    private Expression<Timespan> timeout;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        timeout = (Expression<Timespan>) expressions[0];
        return true;
    }

    @Override
    protected Future[] get(Event event) {
        Timespan timespan = timeout.getSingle(event);
        if (timespan != null) {
            return new Future[]{new Future(timespan.getAs(TICK))};
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
    public String toString(Event event, boolean debug) {
        return "new future";
    }

}
