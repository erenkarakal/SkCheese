package me.eren.skcheese.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;

@Name("Parsed As Type")
@Description("Parse a string as a type. The type can be a non-literal unlike vanilla Skript.")
@Since("1.1")
@Example("""
        set {_c} to class info of {variable}
        set {_new.value} to {_input} parsed as type {_c}
        if {_new.value} isn't set:
          send "Please make sure the input is the same type as the old value." to player
          stop
        set {variable} to {_new.value}
        """)
public class ExprParsedAs extends SimpleExpression<Object> {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("dynamic-parsing")) {
            registry.register(SyntaxRegistry.EXPRESSION,
                    builder(ExprParsedAs.class, Object.class)
                            .addPattern("%string% parsed as type %~classinfo%")
                            .build()
            );
        }
    }

    private Expression<ClassInfo<?>> classInfo;
    private Expression<String> toParse;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        // noinspection unchecked
        toParse = (Expression<String>) expressions[0];
        // noinspection unchecked
        classInfo = (Expression<ClassInfo<?>>) expressions[1];
        return true;
    }

    @Override
    protected Object[] get(Event event) {
        ClassInfo<?> classInfo = this.classInfo.getSingle(event);
        String toParse = this.toParse.getSingle(event);
        if (classInfo == null || toParse == null) {
            return null;
        }
        if (classInfo.getC() == String.class) {
            return new Object[]{toParse}; // because parsing as string returns null
        }

        Object result = Classes.parseSimple(toParse, classInfo.getC(), ParseContext.SCRIPT);
        return new Object[]{ result };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return toParse.toString(event, debug) + " parsed as type " + classInfo.toString(event, debug);
    }

}
