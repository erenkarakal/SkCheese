package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.command.EffectCommandEvent;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

@Name("Evaluate")
@Description("Runs Skript code from a string as if you used effect commands. This shouldn't be used in production.")
@Since("1.5")
@Examples("eval \"broadcast 2+2\"")

public class EffEval extends Effect {

    static {
        if (SkCheese.isSyntaxEnabled("eval"))
            Skript.registerEffect(EffEval.class, "eval[uate] %strings% [as %-commandsender%]");
    }

    private Expression<String> linesExpression;
    private Expression<CommandSender> senderExpression;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        linesExpression = (Expression<String>) exprs[0];
        senderExpression = (Expression<CommandSender>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        CommandSender sender = this.senderExpression != null ? this.senderExpression.getSingle(event) : Bukkit.getConsoleSender();
        for (String line : linesExpression.getArray(event)) {
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
    public String toString(Event e, boolean d) {
        return "evaluate '" + linesExpression + "' as " + senderExpression;
    }

}
