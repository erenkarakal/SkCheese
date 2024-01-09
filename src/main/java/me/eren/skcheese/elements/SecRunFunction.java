package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.*;

@Name("Run Function")
@Description("Executes a function using a section. Pass parameters by doing 'parameter name: value'")
@Since("1.0")
@Examples("""
        function add(1: int, 2: int) :: number:
          return {_1} + {_2}
        
        execute function add and store it in {_result}:
          1: 5   # (the order of the parameters don't matter)
          2: 10
        broadcast {_result} # 15
        """)

public class SecRunFunction extends Section {

    static {
        if (SkCheese.isSyntaxEnabled("run-function-section"))
            Skript.registerSection(SecRunFunction.class, "(execute|run) function <.+> [and store it in %-~objects%]");
    }

    private Function<?> function;
    private final Map<String, Expression<?>> paramExpression = new LinkedHashMap<>();
    private Expression<?> storeExpression;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        String functionName = parseResult.regexes.get(0).group(0);
        function = Functions.getFunction(functionName, getParser().getCurrentScript().toString());

        if (function == null) {
            Skript.error("Function \"" + functionName + "\" doesn't exist.");
            return false;
        }

        if (expressions[0] != null) {
            storeExpression = expressions[0];
            if (function.getReturnType() == null) {
                Skript.error("The output of function \"" + functionName + "\" can not be stored because it does not return anything.");
                return false;
            }
            if (!Changer.ChangerUtils.acceptsChange(storeExpression, Changer.ChangeMode.SET, function.getReturnType().getC())) {
                Skript.error(storeExpression.toString() + " can not store objects of type " + function.getReturnType().getCodeName());
                return false;
            }
        }

        Parameter<?>[] parameters = function.getSignature().getParameters();

        EntryValidator.EntryValidatorBuilder builder = EntryValidator.builder();
        for (Parameter<?> parameter : parameters) {
            Expression<?> defaultValue = parameter.getDefaultExpression();
            boolean optional = defaultValue != null;
            builder.addEntryData(createEntryData(parameter.getName(), defaultValue, optional, parameter.getType().getC()));
        }
        EntryContainer container = builder.build().validate(sectionNode);
        if (container == null) return false;

        for (Parameter<?> parameter : parameters)
            paramExpression.put(parameter.getName(), container.getOptional(parameter.getName(), Expression.class, true));

        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        int size = function.getSignature().getMaxParameters();
        Object[][] params = new Object[size][0];

        int i = 0;
        for (Parameter<?> parameter : function.getSignature().getParameters()) {
            Expression<?> expr = paramExpression.get(parameter.getName());
            if (expr != null) params[i] = expr.getAll(event);
            i++;
        }

        Object[] returnValue = function.execute(params);
        if (storeExpression != null) storeExpression.change(event, returnValue, Changer.ChangeMode.SET);
        return getNext();
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "execute function " + function.getName() + ":";
    }

    @SuppressWarnings("unchecked")
    private static <T> ExpressionEntryData<T> createEntryData(String name, Expression<?> defaultValue, boolean optional, Class<?> type) {
        return new ExpressionEntryData<>(name, (Expression<T>) defaultValue, optional, (Class<T>) type);
    }

}
