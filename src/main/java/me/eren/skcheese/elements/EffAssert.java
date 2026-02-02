package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.log.runtime.ErrorSource;
import org.skriptlang.skript.log.runtime.RuntimeErrorProducer;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Assert")
@Description("Checks if a condition is true, otherwise throws an error or a warning with a custom message and stops the code.")
@Example("assert {_x} is 1 with error \"{_x} should've been 1\"")
@Example("assert {_var} > 5000 with warning \"{_var} should've been more than 5000\"")
@Since("")
public class EffAssert extends Effect implements RuntimeErrorProducer {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("assert")) {
            registry.register(SyntaxRegistry.EFFECT,
                    builder(EffAssert.class)
                            .addPattern("assert <.+> with (:error|warning) [message] %string%")
                            .build()
            );
        }
    }

    private Node node;
    private boolean isError;
    private Condition condition;
    private Expression<String> message;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        node = getNode();
        isError = parseResult.hasTag("error");

        String unparsedCondition = parseResult.regexes.getFirst().group();
        condition = Condition.parse(unparsedCondition, null);
        if (condition == null) {
            Skript.error("Can't understand this condition: " + unparsedCondition);
            return false;
        }

        // noinspection unchecked
        message = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (!condition.check(event)) {
            String message = this.message.getSingle(event);
            if (isError) {
                error(message);
            } else {
                warning(message);
            }
            return null;
        }
        return getNext();
    }

    @Override
    public @NotNull ErrorSource getErrorSource() {
        return ErrorSource.fromNodeAndElement(node, this);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return new SyntaxStringBuilder(event, debug)
                .append("assert")
                .append(condition)
                .append("with")
                .append(isError ? "error" : "warning")
                .append(message)
                .toString();
    }

}
