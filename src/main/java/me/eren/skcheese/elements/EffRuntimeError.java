package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer;

@Name("Runtime Error / Warning")
@Description("Throws a runtime error or warning.")
@Since("1.6")
@Examples("""
        throw runtime warning "This is bad..."
        throw runtime error "This is really really bad..."
        """)

public class EffRuntimeError extends Effect implements SyntaxRuntimeErrorProducer {

    static {
        if (SkCheese.isSyntaxEnabled("runtime-error-effect"))
            Skript.registerEffect(EffRuntimeError.class, "throw runtime (:warning|error) %string%");
    }

    private boolean isWarning;
    private Expression<String> messageExpr;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        isWarning = parseResult.hasTag("warning");
        messageExpr = (Expression<String>) expressions[0];
        node = getParser().getNode();
        return true;
    }

    @Override
    protected void execute(Event event) {
        String message = messageExpr.getSingle(event);

        if (isWarning)
            warning(message);
        else
            error(message);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "throw runtime error " + messageExpr;
    }

}
