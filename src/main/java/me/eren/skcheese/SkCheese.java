package me.eren.skcheese;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SyntaxElement;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.eren.skcheese.elements.bits.BitModule;
import me.eren.skcheese.elements.labels.LabelModule;
import me.eren.skcheese.elements.pairs.PairModule;
import me.eren.skcheese.elements.string.StringModule;
import me.eren.skcheese.elements.switches.SwitchModule;
import me.eren.skcheese.elements.task.FutureModule;
import me.eren.skcheese.elements.wrappedlists.WrappedListModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.skriptlang.skript.util.ClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public final class SkCheese extends JavaPlugin {

    private static SkCheese instance;
    private static SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;

        Metrics metrics = new Metrics(this, 19846);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));

        String currentVersion = getDescription().getVersion();
        String latestVersion = getLatestVersion();

        if (!latestVersion.equals(currentVersion)) {
            getLogger().warning("Running an outdated version!");
            getLogger().warning("Latest: " + latestVersion);
        }

        addon = Skript.instance().registerAddon(SkCheese.class, "SkCheese");
        addon.loadModules(
                new BitModule(),
                new LabelModule(),
                new PairModule(),
                new StringModule(),
                new SwitchModule(),
                new FutureModule(),
                new WrappedListModule()
        );

        loadAddon(addon);

        saveConfig();
    }

    public static SkCheese instance() {
        return instance;
    }

    public static SkriptAddon addon() {
        return addon;
    }

    /**
     * Loads the syntaxes
     */
    public void loadAddon(SkriptAddon addon) {
        ClassLoader.builder()
                .basePackage("me.eren.skcheese.elements")
                .deep(false)
                .initialize(true)
                .forEachClass(clazz -> {
                    if (SyntaxElement.class.isAssignableFrom(clazz)) {
                        try {
                            clazz.getMethod("register", SyntaxRegistry.class).invoke(null, addon.syntaxRegistry());
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            getLogger().severe("Failed to load syntax class: " + e);
                        }
                    }
                })
                .build()
                .loadClasses(SkCheese.class, getFile());
    }

    /**
     * Gets whether a feature is enabled from the config. Defaults to true if it isn't
     *
     * @param syntax The name of the syntax
     * @return Whether the syntax should be registered
     */
    public static boolean isSyntaxEnabled(String syntax) {
        return isSyntaxEnabled(syntax, true);
    }

    /**
     * Gets whether a feature is enabled from the config.
     * @param syntax The name of the syntax
     * @param defaultValue Whether the syntax should be enabled by default if it doesn't exist in the config
     * @return Whether the syntax should be registered
     */
    public static boolean isSyntaxEnabled(String syntax, boolean defaultValue) {
        if (!instance.getConfig().isSet("syntaxes." + syntax)) {
            instance.getConfig().set("syntaxes." + syntax, defaultValue);
            return defaultValue;
        }
        return instance.getConfig().getBoolean("syntaxes." + syntax);
    }

    /**
     * @return The latest version of SkCheese, or the current version if it fails.
     */
    private String getLatestVersion() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/erenkarakal/SkCheese/releases/latest"))
                    .timeout(Duration.of(3, SECONDS))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                getLogger().info("Got code " + response.statusCode() + " while checking for updates: " + response.body());
                return getDescription().getVersion();
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            if (json.has("tag_name")) {
                return json.get("tag_name").getAsString();
            }

        } catch (InterruptedException | IOException exception) {
            this.getLogger().info("Unable to check for updates: " + exception.getMessage());
        }

        return getDescription().getVersion();
    }

}
