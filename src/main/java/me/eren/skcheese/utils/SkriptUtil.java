package me.eren.skcheese.utils;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.List;

public final class SkriptUtil {

    private SkriptUtil() {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public static UnlockedTrigger loadCode(ParserInstance parser,
                                           Structure structure,
                                           String name,
                                           @Nullable Runnable afterLoading,
                                           SkriptEvent skriptEvent,
                                           SectionNode sectionNode,
                                           List<TriggerSection> sections,
                                           Class<? extends Event>... events) {
        // replicates Section#loadCode method
        String previousName = parser.getCurrentEventName();
        Class<? extends Event>[] previousEvents = parser.getCurrentEvents();
        Structure previousStructure = parser.getCurrentStructure();
        List<TriggerSection> previousSections = parser.getCurrentSections();
        Kleenean previousDelay = parser.getHasDelayBefore();

        parser.setCurrentEvent(name, events);
        parser.setCurrentStructure(structure);
        parser.setCurrentSections(sections);
        parser.setHasDelayBefore(Kleenean.FALSE);
        List<TriggerItem> triggerItems = ScriptLoader.loadItems(sectionNode);
        if (afterLoading != null)
            afterLoading.run();

        //noinspection ConstantConditions - We are resetting it to what it was
        parser.setCurrentEvent(previousName, previousEvents);
        parser.setCurrentStructure(previousStructure);
        parser.setCurrentSections(previousSections);
        parser.setHasDelayBefore(previousDelay);
        return new UnlockedTrigger(parser.getCurrentScript(), name, skriptEvent, triggerItems);
    }

}
