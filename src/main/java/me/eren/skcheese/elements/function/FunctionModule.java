package me.eren.skcheese.elements.function;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import me.eren.skcheese.SkCheese;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.common.function.FunctionReference;
import org.skriptlang.skript.common.function.FunctionReference.Argument;
import org.skriptlang.skript.common.function.FunctionReference.ArgumentType;
import org.skriptlang.skript.common.function.FunctionReferenceParser;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionModule implements AddonModule {

    @Override
    public String name() {
        return "run function section";
    }

    @Override
    public boolean canLoad(SkriptAddon addon) {
        return SkCheese.isSyntaxEnabled("run-function-section");
    }

    @Override
    public void load(SkriptAddon addon) {
        SyntaxRegistry registry = SkCheese.addon().syntaxRegistry();
        ExprSecFunction.register(registry);
        SecRunFunction.register(registry);
    }

    /**
     * The pattern for a valid function name.
     * Functions must start with a letter or underscore and can only contain letters, numbers, and underscores.
     */
    private final static Pattern FUNCTION_NAME_PATTERN = Pattern.compile(Functions.functionNamePattern);

    /**
     * The pattern for an argument that can be passed in the children of this section.
     */
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("(?:(?:the )?argument )?(?<name>%s) set to (?<value>.+)"
            .formatted(FUNCTION_NAME_PATTERN.toString()));

    protected static FunctionReference<?> parseFunctionSection(String name, SectionNode sectionNode, List<Argument<String>> arguments) {
        FunctionReference<?> reference;

        for (Node child : sectionNode) {
            if (!(child instanceof SimpleNode) || child.getKey() == null) {
                Skript.error("Invalid argument " + child.getKey());
                return null;
            }

            Matcher matcher = ARGUMENT_PATTERN.matcher(child.getKey());
            if (!matcher.matches()) {
                Skript.error("Invalid argument " + child.getKey());
                return null;
            }

            arguments.add(new Argument<>(ArgumentType.NAMED, matcher.group("name"), matcher.group("value")));
        }

        if (!FUNCTION_NAME_PATTERN.matcher(name).matches()) {
            Skript.error("Function '" + name + "' doesn't exist");
            return null;
        }

        FunctionReferenceParser parser = new FunctionReferenceParser(ParseContext.DEFAULT, SkriptParser.PARSE_EXPRESSIONS);
        // noinspection unchecked
        Argument<String>[] array = (Argument<String>[]) arguments.toArray(new Argument[0]);
        try (ParseLogHandler log = SkriptLogger.startParseLogHandler()) {
            reference = parser.parseFunctionReference(name, array, log);
        }

        if (reference == null || arguments.isEmpty()) {
            doesNotExist(name, arguments);
            return null;
        }

        return reference;
    }

    /**
     * Prints the error for when a function does not exist.
     *
     * @param name The function name.
     */
    protected static void doesNotExist(String name, List<Argument<String>> arguments) {
        StringJoiner joiner = new StringJoiner(", ");

        for (Argument<String> argument : arguments) {
            SkriptParser parser = new SkriptParser(argument.value(), SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);

            Expression<?> expression = LiteralUtils.defendExpression(parser.parseExpression(Object.class));
            if (!LiteralUtils.canInitSafely(expression)) {
                joiner.add(argument.name() + ": ?");
                continue;
            }

            Noun className = Classes.getSuperClassInfo(expression.getReturnType()).getName();
            if (expression.isSingle()) {
                joiner.add(argument.name() + ": " + className.getSingular());
            } else {
                joiner.add(argument.name() + ": " + className.getPlural());
            }
        }

        Skript.error("Function " + name + "(" + joiner + ") doesn't exist.");
    }

}
