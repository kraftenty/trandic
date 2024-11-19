package org.kraftenty.config;

import android.content.Context;
import android.content.res.AssetManager;
import java.util.Properties;

public class ApiConfig {
    private static Properties properties;

    public static void init(Context context) {
        try {
            properties = new Properties();
            AssetManager assetManager = context.getAssets();
            properties.load(assetManager.open("config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getApiKey() {
        return properties.getProperty("API_KEY");
    }

    public static String getBaseUrl() {
        return properties.getProperty("BASE_URL");
    }
} 