package me.eren.skcheese.elements.function;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.common.function.FunctionReference;
import org.skriptlang.skript.common.function.FunctionReference.Argument;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("Run Function")
@Description("Executes a function using a section. Pass parameters by doing 'parameter name as value'")
@Since("1.0 (1.7 Breaking Syntax Change)")
@Examples("""
        function add(1: int, 2: int) :: number:
          return {_1} + {_2}
        
        execute function add and store it in {_result}:
          1 as 5   # (the order of the parameters don't matter)
          2 as 10
        broadcast {_result} # 15
        """)
public class SecRunFunction extends Section {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.SECTION,
                builder(SecRunFunction.class)
                        .addPattern("(execute|run) function <.+> [with arg[ument]s]")
                        .build()
        );
    }

    private FunctionReference<?> reference;
    private final List<Argument<String>> arguments = new ArrayList<>();

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed,
                        @NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        String name = parseResult.regexes.getFirst().group();
        reference = FunctionModule.parseFunctionSection(name, sectionNode, arguments);
        return reference != null;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        if (reference == null) {
            return walk(event, false);
        }

        reference.execute(event);
        reference.function().resetReturnValue();
        return walk(event, false);
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "execute function " + reference.name() + ":";
    }

}

