package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skriptlang.skript.lang.structure.Structure;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Name("Label")
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
        if (SkCheese.isSyntaxEnabled("labels")) {
            Skript.registerEffect(EffLabel.class, "label <.+>");
            Skript.registerEffect(EffGoto.class, "(go|jump)[ ]to %string%");
            Bukkit.getPluginManager().registerEvents(new ScriptLoadListener(), SkCheese.instance);
        }
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

    @Name("Goto")
    @Description("Jumps to a label.")
    @Since("1.1")
    @Examples("""
        on load:
          goto "code"
        
          label code
          broadcast "hi"
        """)

    public static class EffGoto extends Effect {
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
            EffLabel label = LabelStorage.getLabel(scriptFile, structure, labelName);
            return label;
        }
    }

    public static class ScriptLoadListener implements Listener {
        @EventHandler
        public void onScriptLoad(PreScriptLoadEvent e) {
            for (Config config : e.getScripts()) {
                File scriptFile = config.getFile();
                LabelStorage.removeScriptLabels(scriptFile);
            }
        }
    }

    protected static class LabelStorage {
        private static final Map<File, Map<Structure, Map<String, EffLabel>>> labels = new HashMap<>();

        protected static void addLabel(File scriptFile, Structure structure, String labelKey, EffLabel label) {
            if (!labels.containsKey(scriptFile)) {
                labels.put(scriptFile, new HashMap<>());
            }
            Map<Structure, Map<String, EffLabel>> scriptMap = labels.get(scriptFile);
            if (!scriptMap.containsKey(structure)) {
                scriptMap.put(structure, new HashMap<>());
            }
            Map<String, EffLabel> parentMap = scriptMap.get(structure);
            parentMap.put(labelKey, label);
        }

        protected static EffLabel getLabel(File scriptFile, Structure structure, String labelKey) {
            try {
                return labels.get(scriptFile).get(structure).get(labelKey);
            } catch (NullPointerException e) {
                return null;
            }
        }

        protected static void removeScriptLabels(File scriptFile) {
            labels.remove(scriptFile);
        }
    }
}
