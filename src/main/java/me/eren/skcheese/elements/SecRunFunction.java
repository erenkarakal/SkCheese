package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.*;

@Name("Run Function")
@Description("Executes a function using a section. Pass parameters by doing 'parameter name: value'")
@Since("1.0")

public class SecRunFunction extends Section {

    static {
        Skript.registerSection(SecRunFunction.class, "(execute|run) function <.+> [and store it in %-~objects%]");
    }

    private Function<?> func;
    private final Map<String, Expression<?>> map = new HashMap<>();
    private Expression<?> expression;

    //@SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        func = Functions.getFunction(parseResult.regexes.get(0).group(0), getParser().getCurrentScript().toString());
        if (func == null) {
            Skript.error("Function \"" + parseResult.regexes.get(0).group(0) + "\" doesn't exist.");
            return false;
        }
        EntryValidator.EntryValidatorBuilder builder = EntryValidator.builder();
        for (Parameter<?> parameter : func.getSignature().getParameters()) {
            Expression<?> defaultValue = parameter.getDefaultExpression();
            boolean optional = defaultValue != null;
            builder.addEntryData(new ExpressionEntryData<>(parameter.getName(), defaultValue, optional, (Class) parameter.getType().getC()));
        }
        EntryContainer container = builder.build().validate(sectionNode);
        if (container == null) return false;
        for (Parameter<?> parameter : func.getSignature().getParameters()) {
            map.put(parameter.getName(), container.getOptional(parameter.getName(), Expression.class, true));
        }
        expression = parseResult.exprs[0];
        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event e) {
        // put the parameters in correct order!
        int size = func.getSignature().getMaxParameters();
        Object[][] params = new Object[size][1];
        int i = 0;
        for (Parameter<?> parameter : func.getSignature().getParameters()) {
            Expression<?> expr = map.get(parameter.getName());
            if (expr != null)
                params[i] = expr.getAll(e);
            i++;
        }
        Object[] returnValue = func.execute(params);
        if (expression != null)
            expression.change(e, returnValue, Changer.ChangeMode.SET);
        return getNext();
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "execute function " + func.getName() + ":";
    }
}
