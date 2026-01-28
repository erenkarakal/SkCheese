package me.eren.skcheese.elements.string;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.DefaultSyntaxInfos.Expression.builder;


@Name("String Builder - Last String")
@Description("Represents the last string created by the string builder, if it wasn't stored anywhere.")
@Since("1.2")
@Example("""
        new string:
          "line 1"
          "line 2"
        send last string
        """)
public class ExprLastString extends SimpleExpression<String> {

    protected static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                builder(ExprLastString.class, String.class)
                        .addPattern("last string")
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Skript.warning("This expression is deprecated and will be removed in the future. Use 'set {_x} to new string:' instead.");
        return true;
    }

    @Override
    protected String[] get(Event event) {
        return new String[] { SecNewString.lastString };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "last string";
    }

}
