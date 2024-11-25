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

    public static String getChatGptApiKey() {
        return properties.getProperty("CHATGPT_API_KEY");
    }

    public static String getChatGptBaseUrl() {
        return properties.getProperty("CHATGPT_BASE_URL");
    }

    public static String getUnsplashClientId() {
        return properties.getProperty("UNSPLASH_CLIENT_ID");
    }

    public static String getUnsplashBaseUrl() {
        return properties.getProperty("UNSPLASH_BASE_URL");
    }
} 