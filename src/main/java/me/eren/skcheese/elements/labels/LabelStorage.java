package me.eren.skcheese.elements.labels;

import org.skriptlang.skript.lang.structure.Structure;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LabelStorage {
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