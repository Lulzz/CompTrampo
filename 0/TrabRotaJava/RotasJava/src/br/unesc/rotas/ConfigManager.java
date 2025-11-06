package br.unesc.rotas;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    private static final String KEY_ROOT_FOLDER = "root_folder";
    private static final String KEY_PROCESSED_DIR = "processed_dir";
    private static final String KEY_NOT_PROCESSED_DIR = "not_processed_dir";
    private static final String KEY_ROTA_AUTOMATICA = "rota_automatica";

    public static boolean isFirstRun() {
        return !new File(CONFIG_FILE).exists();
    }

    public static boolean loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return false;
        }
        try (FileReader reader = new FileReader(configFile)) {
            properties.load(reader);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao carregar configurações: " + e.getMessage());
            return false;
        }
    }

    public static void saveConfig(String rootFolder, String processedDir, String notProcessedDir, boolean rotaAutomatica) throws IOException {
        properties.setProperty(KEY_ROOT_FOLDER, rootFolder);
        properties.setProperty(KEY_PROCESSED_DIR, processedDir);
        properties.setProperty(KEY_NOT_PROCESSED_DIR, notProcessedDir);
        properties.setProperty(KEY_ROTA_AUTOMATICA, String.valueOf(rotaAutomatica));

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            properties.store(writer, "Configurações da Aplicação de Rotas");
        }

        new File(processedDir).mkdirs();
        new File(notProcessedDir).mkdirs();
    }

    public static String getRootFolder() {
        return properties.getProperty(KEY_ROOT_FOLDER);
    }

    public static String getProcessedDir() {
        return properties.getProperty(KEY_PROCESSED_DIR);
    }

    public static String getNotProcessedDir() {
        return properties.getProperty(KEY_NOT_PROCESSED_DIR);
    }

    public static boolean isRotaAutomatica() {
        return Boolean.parseBoolean(properties.getProperty(KEY_ROTA_AUTOMATICA, "false"));
    }
}