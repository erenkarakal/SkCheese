package me.eren.skcheese.elements.function;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.common.function.FunctionReference;
import org.skriptlang.skript.common.function.FunctionReference.Argument;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Run Function (Expression Section)")
@Description("Runs a function with the specified arguments.")
@Since("1.7")
@Example("""
        local function multiply(x: number, y: number) returns number:
        	return {_x} * {_y}
        
        set {_x} to function multiply with arguments:
        	x set to 2
        	y set to 3
        
        broadcast "%{_x}%" # returns 6
        """)
public class ExprSecFunction extends SectionExpression<Object> {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("run-function-section")) {
            registry.register(SyntaxRegistry.EXPRESSION,
                    builder(ExprSecFunction.class, Object.class)
                            .addPattern("[the] function <.+> with [the] arg[ument][s]")
                            .build()
            );
        }
    }

    private FunctionReference<?> reference;
    private final List<Argument<String>> arguments = new ArrayList<>();

    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
                        @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
        assert node != null;

        if (node.isEmpty()) {
            Skript.error("A function section must contain arguments");
            return false;
        }

        String name = result.regexes.getFirst().group();
        reference = FunctionModule.parseFunctionSection(name, node, arguments);

        if (reference.signature().returnType() == null) {
            Skript.error("Function '" + name + "' doesn't return a value");
            return false;
        }

        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        if (reference == null) {
            return null;
        }

        Class<?> returnType = reference.signature().returnType();
        if (returnType == null) {
            return null;
        }

        Object result = reference.execute(event);
        if (result == null) {
            return null;
        }

        reference.function().resetReturnValue();

        if (result.getClass().isArray()) {
            return (Object[]) result;
        } else {
            return new Object[]{result};
        }
    }

    @Override
    public boolean isSingle() {
        return reference.isSingle();
    }

    @Override
    public boolean isSectionOnly() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return reference.signature().returnType() != null
                ? Utils.getComponentType(reference.signature().returnType())
                : null;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug)
                .append("run function")
                .append(reference.name());

        if (arguments.size() > 1) {
            builder.append("with arguments");
        } else {
            builder.append("with argument");
        }

        arguments.forEach(argument -> builder.append(argument.name() + ": " + argument.value() + ", "));

        return builder.toString();
    }

}
