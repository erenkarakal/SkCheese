package me.eren.skcheese.elements.switches;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import me.eren.skcheese.utils.SkriptUtil;
import me.eren.skcheese.utils.UnlockedTrigger;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Name("Switch Case")
@Description("The switch case executes one statement from multiple ones. " +
        "Thus, it is like an if-else-if ladder statement. " +
        "The switch statement is used to test the equality of a variable against several values specified in the tests cases.")
@Since("1.0")
@Examples("""
        switch {_var}:
          case 1:
            broadcast "1"
          case 2:
            broadcast "2"
          default:
            broadcast "neither"
        """)

public class SecSwitch extends Section {

    static {
        if (SkCheese.isSyntaxEnabled("switch-cases"))
            Skript.registerSection(SecSwitch.class, "switch %~object%");
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

        TriggerItem.walk(found.trigger(), event);

        return getNext();
    }


    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "switch " + input.toString(event, debug);
    }
}