package me.eren.skcheese.elements.labels;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.structure.Structure;

import java.io.File;

@Name("Labels - Goto")
@Description("Jumps to a label.")
@Since("1.1")
@Examples("""
        on load:
          goto "code"
        
          label code
          broadcast "hi"
        """)
public class EffGoto extends Effect {

    static {
        if (SkCheese.isSyntaxEnabled("labels"))
            Skript.registerEffect(EffGoto.class, "(go|jump)[ ]to %string%");
    }

    private Expression<String> labelExpr;
    private File scriptFile;
    private Structure structure;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        labelExpr = (Expression<String>) exprs[0];
        scriptFile = getParser().getCurrentScript().getConfig().getFile();
        structure = getParser().getCurrentStructure();
        return true;
    }

    @Override
    protected void execute(Event e) {}

    @Override
    public String toString(Event e, boolean debug) {
        return "goto " + labelExpr;
    }

    @Override
    protected TriggerItem walk(final Event e) {
        String labelName = labelExpr.getSingle(e);
        return LabelStorage.getLabel(scriptFile, structure, labelName);
    }
}