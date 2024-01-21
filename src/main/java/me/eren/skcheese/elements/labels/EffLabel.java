package me.eren.skcheese.elements.labels;

import ch.njol.skript.Skript;
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
import org.skriptlang.skript.lang.structure.Structure;

import java.io.File;

@Name("Labels - New Label")
@Description("Creates a label that you can jump to later on.")
@Since("1.1")
@Examples("""
        on load:
          goto "code"
        
          label code
          broadcast "hi"
        """)

public class EffLabel extends Effect {

    static {
        if (SkCheese.isSyntaxEnabled("labels"))
            Skript.registerEffect(EffLabel.class, "label <.+>");
    }

    private String labelName;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        File scriptFile = getParser().getCurrentScript().getConfig().getFile();
        Structure structure = getParser().getCurrentStructure();
        labelName = parseResult.regexes.get(0).group(0);

        if (LabelStorage.getLabel(scriptFile, structure, labelName) != null) {
            Skript.error("This label is already defined!");
            return false;
        }
        LabelStorage.addLabel(scriptFile, structure, labelName, this);
        return true;

    }

    @Override
    protected void execute(Event e) {}

    @Override
    public String toString(Event e, boolean debug) {
        return "label " + labelName;
    }
}
