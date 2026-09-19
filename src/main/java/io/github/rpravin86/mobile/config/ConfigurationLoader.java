package io.github.rpravin86.mobile.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

public final class ConfigurationLoader {

    private static final Path DEFAULT_CONFIG_DIRECTORY = Path.of("config");

    private final Properties properties;

    private ConfigurationLoader(Properties properties) {
        this.properties = properties;
    }

    public static FrameworkConfig load() {
        Properties common = read(DEFAULT_CONFIG_DIRECTORY.resolve("common.properties"));
        String platformName = resolve("framework.platform", common, "android");
        MobilePlatform platform = MobilePlatform.from(platformName);

        Properties combined = new Properties();
        combined.putAll(common);
        combined.putAll(read(DEFAULT_CONFIG_DIRECTORY.resolve(platform.name().toLowerCase(Locale.ROOT) + ".properties")));
        return new ConfigurationLoader(combined).build();
    }

    public static FrameworkConfig load(Path configFile) {
        return new ConfigurationLoader(read(configFile)).build();
    }

    public static String resolve(String key, String defaultValue) {
        return resolve(key, read(DEFAULT_CONFIG_DIRECTORY.resolve("common.properties")), defaultValue);
    }

    private FrameworkConfig build() {
        MobilePlatform platform = MobilePlatform.from(required("framework.platform"));
        ServerMode serverMode = ServerMode.from(value("appium.server.mode", "remote"));

        return new FrameworkConfig(
                platform,
                serverMode,
                URI.create(value("appium.server.url", "http://127.0.0.1:4723")),
                value("appium.server.host", "127.0.0.1"),
                integer("appium.server.port", 4723),
                required("device.name"),
                required("device.udid"),
                optionalPath("app.path"),
                value("android.app.package", ""),
                value("android.app.activity", ""),
                bool("android.auto.grant.permissions", true),
                value("ios.bundle.id", ""),
                value("ios.xcode.org.id", ""),
                value("ios.xcode.signing.id", "iPhone Developer"),
                value("ios.updated.wda.bundle.id", ""),
                Duration.ofSeconds(integer("appium.new.command.timeout.seconds", 120)),
                Duration.ofSeconds(integer("interaction.wait.seconds", 15)),
                Duration.ofMillis(integer("interaction.poll.millis", 250)),
                bool("appium.no.reset", false),
                bool("appium.full.reset", false)
        );
    }

    private String required(String key) {
        String result = value(key, "");
        if (result.isBlank()) {
            throw new IllegalArgumentException("Missing required configuration: " + key);
        }
        return result;
    }

    private String value(String key, String defaultValue) {
        return resolve(key, properties, defaultValue);
    }

    private int integer(String key, int defaultValue) {
        String rawValue = value(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(key + " must be an integer, but was '" + rawValue + "'", exception);
        }
    }

    private boolean bool(String key, boolean defaultValue) {
        String rawValue = value(key, Boolean.toString(defaultValue));
        if (!rawValue.equalsIgnoreCase("true") && !rawValue.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(key + " must be true or false, but was '" + rawValue + "'");
        }
        return Boolean.parseBoolean(rawValue);
    }

    private Path optionalPath(String key) {
        String rawValue = value(key, "");
        return rawValue.isBlank() ? null : Path.of(rawValue).toAbsolutePath().normalize();
    }

    private static String resolve(String key, Properties properties, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String environmentValue = System.getenv(toEnvironmentKey(key));
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        return properties.getProperty(key, defaultValue).trim();
    }

    private static String toEnvironmentKey(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static Properties read(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Configuration file does not exist: " + path.toAbsolutePath());
        }

        Properties result = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            result.load(input);
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read configuration file: " + path.toAbsolutePath(), exception);
        }
    }
}
