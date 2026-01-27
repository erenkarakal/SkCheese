package me.eren.skcheese.elements.labels;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.io.File;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Labels - New Label")
@Description("Creates a label that you can jump to later on.")
@Since("1.1")
@Example("""
        on load:
          goto "code"
        
          label code
          broadcast "hi"
        """)
public class EffLabel extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                builder(EffLabel.class)
                        .addPattern("label <.+>")
                        .build()
        );
    }

    private String labelName;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, @NotNull ParseResult parseResult) {
        File scriptFile = getParser().getCurrentScript().getConfig().getFile();
        Structure structure = getParser().getCurrentStructure();
        labelName = parseResult.regexes.getFirst().group(0);

        if (LabelStorage.getLabel(scriptFile, structure, labelName) != null) {
            Skript.error("This label is already defined!");
            return false;
        }
        LabelStorage.addLabel(scriptFile, structure, labelName, this);
        return true;

    }

    @Override
    protected void execute(Event event) {
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "label " + labelName;
    }

}
