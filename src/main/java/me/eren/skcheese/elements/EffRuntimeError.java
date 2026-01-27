package me.eren.skcheese.elements;

import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Runtime Error / Warning")
@Description("Throws a runtime error or warning.")
@Since("1.6")
@Example("""
        throw runtime warning "This is bad..."
        throw runtime error "This is really really bad..."
        """)
public class EffRuntimeError extends Effect implements SyntaxRuntimeErrorProducer {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("runtime-error-effect")) {
            registry.register(SyntaxRegistry.EFFECT,
                    SyntaxInfo.builder(EffRuntimeError.class)
                            .addPattern("throw runtime (:warning|error) %string%")
                            .build()
            );
        }
    }

    private boolean isWarning;
    private Expression<String> message;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        isWarning = parseResult.hasTag("warning");
        // noinspection unchecked
        message = (Expression<String>) expressions[0];
        node = getParser().getNode();
        return true;
    }

    @Override
    protected void execute(Event event) {
        String message = this.message.getSingle(event);

        if (isWarning) {
            warning(message);
        } else {
            error(message);
        }
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "throw runtime error " + message.toString(event, debug);
    }

}
