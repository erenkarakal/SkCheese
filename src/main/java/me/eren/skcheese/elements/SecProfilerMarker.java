package me.eren.skcheese.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import me.eren.skcheese.SkCheese;
import me.eren.skcheese.utils.reflection.Marker;
import me.eren.skcheese.utils.reflection.MarkerFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

public class SecProfilerMarker extends Section {

	public static void register(SyntaxRegistry registry) {
		if (SkCheese.isSyntaxEnabled("profiler-markers", false)) {
			registry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecProfilerMarker.class)
					.addPattern("profiler marker <.+>")
					.build()
			);
		}
	}

	private Marker markerDelegate;
	private String markerName;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
						ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		String rawName = parseResult.regexes.getFirst().group();
		markerName = rawName.replaceAll("[^a-zA-Z0-9]", "_");

		try {
			this.markerDelegate = MarkerFactory.generate(markerName);
		} catch (Throwable e) {
			Skript.error("Failed to generate a marker. Check the console for more info.");
			e.printStackTrace();
			return false;
		}

		loadCode(sectionNode);
		if (getParser().getHasDelayBefore().isTrue()) {
			Skript.error("A profiler marker cannot contain delayed syntaxes.");
			return false;
		}

		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		if (last != null) {
			last.setNext(null);
		}
		markerDelegate.run(() -> TriggerItem.walk(walk(event, true), event));
		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "profiler marker " + markerName;
	}

}
