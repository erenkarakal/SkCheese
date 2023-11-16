package me.eren.skcheese.elements;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.google.common.collect.Iterables;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Switch Case")
@Description("The switch case executes one statement from multiple ones. " +
        "Thus, it is like an if-else-if ladder statement. " +
        "The switch statement is used to test the equality of a variable against several values specified in the test cases.")
@Since("1.0")

public class SecSwitch extends Section {

    static {
        Skript.registerSection(SecSwitch.class, "switch %~object%");
        Skript.registerSection(SecSwitchCase.class, "case %*object%");
    }

    Map<Object, SecSwitchCase> cases = new HashMap<>();
    Expression<?> input;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {

        input = LiteralUtils.defendExpression(expressions[0]);

        ParserInstance parser = getParser();
        int nonEmptyNodeCount = Iterables.size(sectionNode);
        if (nonEmptyNodeCount < 2) {
            Skript.error("Switch sections must contain at least two conditions");
            return false;
        }

        List<TriggerSection> sections = parser.getCurrentSections();
        sections.add(this);
        parser.setCurrentSections(sections);
        Kleenean hasDelayAfter = isDelayed;
        for (Node childNode : sectionNode) {
            if (!(childNode instanceof SectionNode)) {
                Skript.error("Switch sections may only contain case sections");
                return false;
            }
            String childKey = childNode.getKey();
            if (childKey != null) {
                childKey = ScriptLoader.replaceOptions(childKey);

                parser.setNode(childNode);
                parser.setHasDelayBefore(isDelayed);
                Section section = Section.parse(childKey, "Can't understand this condition: '" + childKey + "'", (SectionNode) childNode, new ArrayList<>());
                // if this condition was invalid, don't bother parsing the rest
                if (!(section instanceof SecSwitchCase)) {
                    Skript.error("Switch sections may only contain case sections");
                    return false;
                }
                cases.put(((SecSwitchCase) section).getValue(), (SecSwitchCase) section);
                if (!parser.getHasDelayBefore().isTrue()) {
                    hasDelayAfter = parser.getHasDelayBefore();
                }
                parser.setHasDelayBefore(isDelayed);
            }
        }
        parser.setNode(sectionNode);
        parser.setHasDelayBefore(hasDelayAfter);
        return LiteralUtils.canInitSafely(input);
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event event) {
        Object input = this.input.getSingle(event);
        if (input == null) {
            return null;
        }
        Section toRun = cases.get(input);
        setTriggerItems(toRun == null ? new ArrayList<>() : Collections.singletonList(toRun));
        return walk(event, true);
    }


    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "switch " + input.toString(event, debug);
    }

    public static class SecSwitchCase extends Section {
        private Literal<?> value;

        @Override
        public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {

            if (!getParser().isCurrentSection(SecSwitch.class)) {
                Skript.error("Case sections must be inside a switch section");
                return false;
            }

            value = (Literal<?>) LiteralUtils.defendExpression(expressions[0]);
            loadOptionalCode(sectionNode);
            return LiteralUtils.canInitSafely(value);
        }

        @Override
        protected @Nullable TriggerItem walk(@NotNull Event event) {
            return walk(event, true);
        }

        public Object getValue() {
            return value.getSingle();
        }

        @Override
        public @NotNull String toString(@Nullable Event event, boolean debug) {
            return "case " + value.toString(event, debug);
        }
    }
}