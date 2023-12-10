package me.eren.skcheese.utils;

import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.skriptlang.skript.lang.script.Script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnlockedTrigger extends Trigger {

    private List<TriggerItem> items;

    public UnlockedTrigger(Script script, String name, SkriptEvent event, List<TriggerItem> items) {
        super(script, name, event, items);
        this.items = new ArrayList<>();
        this.items.addAll(items);
    }

    @Override
    public TriggerItem walk(@NotNull Event event) {
        return super.walk(event);
    }

    @Override
    public void setTriggerItems(@NotNull List<TriggerItem> items) {
        if (this.items == null) this.items = new ArrayList<>();
        super.setTriggerItems(items);
        this.items.clear();
        this.items.addAll(items);
    }

    public @Unmodifiable List<TriggerItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public @Nullable TriggerItem getFirst() {
        return first;
    }

    public @Nullable TriggerItem getLast() {
        return last;
    }

}
