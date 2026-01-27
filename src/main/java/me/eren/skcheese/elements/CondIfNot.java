package me.eren.skcheese.elements;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxRegistry;

import static org.skriptlang.skript.registration.SyntaxInfo.builder;

@Name("If Not")
@Description("Checks if a condition doesn't pass.")
@Since("1.1")
@Example("""
        if !(1 = 2):
          broadcast "1 is indeed not 2"
        """)
public class CondIfNot extends Condition {

    public static void register(SyntaxRegistry registry) {
        if (SkCheese.isSyntaxEnabled("reverted-conditions")) {
            registry.register(SyntaxRegistry.CONDITION,
                    builder(CondIfNot.class)
                            .addPattern("\\!\\(<.+>\\)")
                            .build()
            );
        }
    }

    private Condition condition;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        String regex = parseResult.regexes.getFirst().group();
        condition = Condition.parse(regex, "Can't understand this condition: \"" + regex + "\"");
        return condition != null;
    }

    @Override
    public boolean check(Event event) {
        return !(condition.check(event));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "reverted condition " + condition.toString(event, debug);
    }

}
