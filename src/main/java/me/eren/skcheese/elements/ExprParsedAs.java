package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

@Name("Parsed As Type")
@Description("Parse a string as a type. The type can be a non-literal unlike vanilla Skript.")
@Since("1.1")
@Examples("""
        set {_c} to class info of {variable}
        set {_new.value} to {_input} parsed as type {_c}
        if {_new.value} isn't set:
          send "Please make sure the input is the same type as the old value." to player
          stop
        set {variable} to {_new.value}
        """)

public class ExprParsedAs extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(
                ExprParsedAs.class, Object.class, ExpressionType.COMBINED,
                "%string% parsed as type %~classinfo%"
        );
    }

    private Expression<ClassInfo<?>> classInfoExpr;
    private Expression<String> toParseExpr;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        classInfoExpr = (Expression<ClassInfo<?>>) exprs[1];
        toParseExpr = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Object[] get(Event e) {
        ClassInfo<?> classInfo = classInfoExpr.getSingle(e);
        String toParse = toParseExpr.getSingle(e);
        if (classInfo == null || toParse == null) return null;
        if (classInfo.getC() == String.class) return new Object[]{ toParse }; // because parsing as string returns null

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
    public String toString(Event e, boolean debug) {
        return toParseExpr + " parsed as type " + classInfoExpr;
    }
}
