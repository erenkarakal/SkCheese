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
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Inverted Boolean")
@Description("Returns the opposite of a boolean.")
@Since("1.1")
@Example("""
        set {ability::%player%} to !{ability::%player%}
        send "Toggled your ability!" to player
        """)

public class InvertedBoolean extends SimpleExpression<Boolean> {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("inverted-booleans")) {
            registry.register(SyntaxRegistry.EXPRESSION,
                    builder(InvertedBoolean.class, Boolean.class)
                            .addPattern("!%boolean%")
                            .build()
            );
        }
    }

    Expression<Boolean> b00lean;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        b00lean = (Expression<Boolean>) expressions[0];
        return true;
    }

    @Override
    protected Boolean[] get(Event event) {
        boolean bool = Boolean.TRUE.equals(b00lean.getSingle(event));
        return new Boolean[]{ !bool };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "inverted boolean " + b00lean.toString(event, debug);
    }

}
