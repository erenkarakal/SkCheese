package me.eren.skcheese;

import ch.njol.skript.Skript;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class SkCheese extends JavaPlugin {

    public static SkCheese instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Started SkCheese " + getDescription().getVersion());
        new Metrics(this, 19846);

        String currentVersion = getDescription().getVersion();
        String latestVersion = getLatestVersion();

        if (!latestVersion.equals(currentVersion)) {
            getLogger().warning("Running an outdated version!");
            getLogger().warning("Latest: " + latestVersion);
        }

        try {
            Skript.registerAddon(this).loadClasses("me.eren.skcheese", "elements");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the addon.", e);
        }
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
        try {
            URL url = new URI("https://api.github.com/repos/erenkarakal/SkCheese/releases/latest").toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000); // ms

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                this.getLogger().info("Got code " + con.getResponseCode() + " while checking for updates.");
                return getDescription().getVersion();
            }

            String response = new String(con.getInputStream().readAllBytes());
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response, JsonObject.class);
            if (json.has("tag_name")) return json.get("tag_name").getAsString();

        } catch (URISyntaxException | IOException exception) {
            this.getLogger().info("Unable to check for updates: " + exception.getMessage());
        }

        return getDescription().getVersion();
    }
}
