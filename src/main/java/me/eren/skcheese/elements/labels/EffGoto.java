package me.eren.skcheese.elements.labels;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.io.File;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Labels - Goto")
@Description("Jumps to a label.")
@Since("1.1")
@Example("""
        on load:
          goto "code"
          broadcast "hi" # gets skipped
        
          label code
          broadcast "hello"
        """)
public class EffGoto extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                builder(EffGoto.class)
                        .addPattern("(go|jump)[ ]to %string%")
                        .build()
        );
    }

    private Expression<String> label;
    private File scriptFile;
    private Structure structure;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        label = (Expression<String>) expressions[0];
        scriptFile = getParser().getCurrentScript().getConfig().getFile();
        structure = getParser().getCurrentStructure();
        return true;
    }

    @Override
    protected void execute(Event event) {
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "goto " + label.toString(event, debug);
    }

    @Override
    protected TriggerItem walk(Event event) {
        String labelName = label.getSingle(event);
        return LabelStorage.getLabel(scriptFile, structure, labelName);
    }

}
