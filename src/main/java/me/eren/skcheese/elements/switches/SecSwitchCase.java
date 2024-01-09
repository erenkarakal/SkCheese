package me.eren.skcheese.elements.switches;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import me.eren.skcheese.utils.SkriptUtil;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.Arrays;
import java.util.List;

public class SecSwitchCase extends Section {

    private Trigger trigger;
    private @Nullable Expression<?> expression;
    private boolean isDefault = false;


    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {

        if (!getParser().isCurrentSection(SecSwitch.class)) {
            Skript.error("Case sections must be inside a switch section");
            return false;
        }

        if (matchedPattern == 0) expression = LiteralUtils.defendExpression(expressions[0]);
        if (matchedPattern == 2) isDefault = true;

        trigger = SkriptUtil.loadCode(
                getParser(),
                new SectionSkriptEvent("case", this),
                "case",
                null,
                new SimpleEvent(),
                sectionNode,
                getParser().getCurrentSections(),
                getParser().getCurrentEvents()
        );

        trigger.setNext(null);

        return matchedPattern != 0 || LiteralUtils.canInitSafely(expression);
    }

    public boolean matches(Object input, Event event) {
        if (isDefault) return true;
        if (expression == null) return input == null;
        return Arrays.stream(expression.getAll(event)).anyMatch(obj -> Comparators.compare(input, obj) == Relation.EQUAL);
    }

    public Trigger trigger() {
        return trigger;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event event) {
        return null;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        if (isDefault) return "default case";
        if (expression == null) return "case none";
        return "case " + expression.toString(event, debug);
    }

}