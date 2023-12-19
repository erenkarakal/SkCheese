package me.eren.skcheese.elements;

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
import org.bukkit.event.Event;

@Name("Reverted Boolean")
@Description("Returns the opposite of a boolean.")
@Since("1.1")
@Examples("""
        set {ability::%player%} to !{ability::%player%}
        send "Toggled your ability!" to player
        """)

public class ExprRevertedBoolean extends SimpleExpression<Boolean> {

    static {
        Skript.registerExpression(ExprRevertedBoolean.class, Boolean.class, ExpressionType.COMBINED, "!%boolean%");
    }

    Expression<Boolean> booleanExpression;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        booleanExpression = (Expression<Boolean>) exprs[0];
        return true;
    }

    @Override
    protected Boolean[] get(Event e) {
        boolean bool = Boolean.TRUE.equals(booleanExpression.getSingle(e));
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
    public String toString(Event e, boolean debug) {
        return "reverted boolean " + booleanExpression;
    }
}
