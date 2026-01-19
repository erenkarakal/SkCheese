package me.eren.skcheese;

import ch.njol.skript.Skript;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.skriptlang.skript.addon.SkriptAddon;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class SkCheese extends JavaPlugin {

    public static SkCheese instance;
    public static SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Started SkCheese " + getDescription().getVersion());
        Metrics metrics = new Metrics(this, 19846);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));

        String currentVersion = getDescription().getVersion();
        String latestVersion = getLatestVersion();

        if (!latestVersion.equals(currentVersion)) {
            getLogger().warning("Running an outdated version!");
            getLogger().warning("Latest: " + latestVersion);
        }

        addon = Skript.instance().registerAddon(SkCheese.class, "SkCheese");
        saveConfig();
    }

    public static boolean isSyntaxEnabled(String syntax) {
        return isSyntaxEnabled(syntax, true);
    }

    public static boolean isSyntaxEnabled(String syntax, boolean defaultValue) {
        if (!instance.getConfig().isSet("syntaxes." + syntax)) {
            instance.getConfig().set("syntaxes." + syntax, defaultValue);
            return defaultValue;
        }
        return instance.getConfig().getBoolean("syntaxes." + syntax);
    }

    private String getLatestVersion() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/eren/SkCheese"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                getLogger().info("Got code " + response.statusCode() + " while checking for updates.");
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
