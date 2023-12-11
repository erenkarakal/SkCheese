package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import me.eren.skcheese.utils.SkriptUtil;
import me.eren.skcheese.utils.UnlockedTrigger;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.*;

@Name("Switch Case")
@Description("The switch case executes one statement from multiple ones. " +
        "Thus, it is like an if-else-if ladder statement. " +
        "The switch statement is used to test the equality of a variable against several values specified in the test cases.")
@Since("1.0")

public class SecSwitch extends Section {

    static {
        Skript.registerSection(SecSwitch.class, "switch %~object%");
        Skript.registerSection(SecSwitchCase.class,
                "case %objects%",
                "case (none|not set)",
                "default");
    }

    private final List<SecSwitchCase> cases = new LinkedList<>();
    private SecSwitchCase defaultCase;
    private Expression<?> input;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {

        input = LiteralUtils.defendExpression(expressions[0]);

        List<TriggerSection> sections = new ArrayList<>(getParser().getCurrentSections());
        sections.add(this);
        UnlockedTrigger trigger = SkriptUtil.loadCode(
                getParser(),
                new SectionSkriptEvent("switch", this),
                "switch",
                null,
                new SimpleEvent(),
                sectionNode,
                sections,
                getParser().getCurrentEvents()
        );

        if (trigger.getItems().size() < 2) {
            Skript.error("Switch sections must contain at least two conditions");
            return false;
        }

        for (TriggerItem item : trigger.getItems()) {
            if (!(item instanceof SecSwitchCase switchCase)) {
                Skript.error("Switch sections may only contain case sections");
                return false;
            }
            if (switchCase.isDefault()) {
                if (defaultCase != null) {
                    Skript.error("Switch sections may only have one default case section");
                    return false;
                }
                defaultCase = switchCase;
                continue;
            }

            cases.add(switchCase);
        }

        if (defaultCase == null) {
            Skript.error("Switch sections need to have a default case section");
            return false;
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event event) {
        Object value = input.getSingle(event);
        SecSwitchCase found = null;

        for (SecSwitchCase switchCase : cases) {
            if (!switchCase.matches(value, event)) continue;
            found = switchCase;
            break;
        }

        if (found == null) found = defaultCase;

        found.trigger().execute(event);

        return getNext();
    }


    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "switch " + input.toString(event, debug);
    }

    public static class SecSwitchCase extends Section {

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

}