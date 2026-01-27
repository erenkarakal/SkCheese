package me.eren.skcheese.elements;

import ch.njol.skript.command.EffectCommandEvent;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Evaluate")
@Description("Runs Skript code from a string as if you used effect commands. This shouldn't be used in production.")
@Since("1.5")
@Example("eval \"broadcast 2+2\"")
public class EffEval extends Effect {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("eval")) {
            registry.register(SyntaxRegistry.EFFECT,
                    builder(EffEval.class)
                            .addPattern("eval[uate] %strings% [as %-commandsender%]")
                            .build()
            );
        }
    }

    private Expression<String> code;
    private Expression<CommandSender> sender;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        code = (Expression<String>) expressions[0];
        // noinspection unchecked
        sender = (Expression<CommandSender>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        CommandSender sender = this.sender != null ? this.sender.getSingle(event) : Bukkit.getConsoleSender();
        for (String line : code.getArray(event)) {
            ParserInstance parserInstance = ParserInstance.get();
            parserInstance.setCurrentEvent("effect command", EffectCommandEvent.class);
            Effect effect = Effect.parse(line, null);
            parserInstance.deleteCurrentEvent();
            if (effect != null) {
                TriggerItem.walk(effect, new EffectCommandEvent(sender, line));
            }
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "evaluate '" + code.toString(event, debug) + "' as " + sender.toString(event, debug);
    }

}
