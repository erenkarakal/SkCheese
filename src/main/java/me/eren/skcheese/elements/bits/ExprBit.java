package me.eren.skcheese.elements.bits;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Bits - Bit of Number")
@Description("Gets the bit at index in a number.")
@Since("1.5")
@Example("set {_first} to bit 1 of 150")
public class ExprBit extends SimpleExpression<Number> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprBit.class, Number.class)
                        .addPatterns(
                                "[the] %integer%(st|nd|rd|th) bit (in|of) %numbers%",
                                "bit %integer% (in|of) %numbers%"
                        ).build()
        );
    }

    private Expression<Integer> position;
    private Expression<Number> numbers;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        position = (Expression<Integer>) expressions[0];
        // noinspection unchecked
        numbers = (Expression<Number>) expressions[1];
        return true;
    }

    @Override
    protected @Nullable Number[] get(Event event) {
        Integer integer = position.getSingle(event);
        if (integer == null) {
            return null;
        }
        int pos = integer;
        Number[] numbers = this.numbers.getArray(event);
        Number[] bits = new Number[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            long num = numbers[i].longValue();
            num >>= (pos - 1);
            num &= 1;
            bits[i] = num;
        }
        return bits;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class, Number.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta[0] == null) {
            return;
        }
        Integer integer = position.getSingle(event);
        if (integer == null) {
            return;
        }
        int pos = integer;
        Number[] numbers = this.numbers.getArray(event);

        int x;
        if (delta[0] instanceof Boolean bool) {
            x = bool ? 1 : 0;
        } else {
            x = ((Number) delta[0]).intValue();
        }

        if (x != 1 && x != 0) {
            return;
        }
        for (int i = 0; i < numbers.length; i++) {
            long num = 1L << (pos - 1);
            if (x == 0) {
                num = ~num;
                num &= numbers[i].longValue();
            }
            else {
                num |= numbers[i].longValue();
            }
            numbers[i] = num;
        }
        this.numbers.change(event, numbers, ChangeMode.SET);
    }

    @Override
    public boolean isSingle() {
        return numbers.isSingle();
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "bit " + position.toString(event, debug) + " of " + numbers.toString(event, debug);
    }

}
