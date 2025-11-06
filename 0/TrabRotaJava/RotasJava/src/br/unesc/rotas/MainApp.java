package br.unesc.rotas;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                boolean firstRun = ConfigManager.isFirstRun(); 
                
                if (!firstRun) {
                    ConfigManager.loadConfig();
                }

                new ConfigFrame(firstRun).setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro crítico na inicialização: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void startApplication() {
        if (ConfigManager.getRootFolder() == null && !ConfigManager.loadConfig()) {
             JOptionPane.showMessageDialog(null, "Falha ao carregar configurações. Não é possível iniciar o processamento.", "Erro", JOptionPane.ERROR_MESSAGE);
             return;
        }

        if (ConfigManager.isRotaAutomatica()) {
            startAutomaticProcessing();
            JOptionPane.showMessageDialog(null, "Sistema iniciado em modo AUTOMÁTICO (background).", "Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            new BuscaFrame().setVisible(true);
        }
    }
    
    private static void startAutomaticProcessing() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        
        final Runnable monitor = () -> {
            try {
                String rootPath = ConfigManager.getRootFolder();
                if (rootPath == null) return;
                
                File rootDir = new File(rootPath);
                if (rootDir.exists() && rootDir.isDirectory()) {
                    for (File file : rootDir.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".txt"))) {
                        processFile(file, true); 
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro no monitoramento automático: " + e.getMessage());
            }
        };
        
        scheduler.scheduleAtFixedRate(monitor, 0, 10, TimeUnit.SECONDS); 
    }

    public static void processFile(File file, boolean isAutomatic) {
        try {
            RouteParser parser = new RouteParser(file);
            parser.validate();
            Graph graph = parser.buildGraphFromPesos();
            
            Node startNode = graph.getNode(parser.getStartNode());
            if (startNode == null) {
                 throw new IllegalArgumentException("Nó inicial (" + parser.getStartNode() + ") não encontrado no grafo.");
            }
            Dijkstra.computePaths(startNode);
            
            FileUtil.moveFile(file, ConfigManager.getProcessedDir());
            
            if (!isAutomatic) {
                JOptionPane.showMessageDialog(null, "Arquivo " + file.getName() + " processado com SUCESSO!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (RuntimeException e) { 
            System.err.println("Erro de Processamento no arquivo " + file.getName() + ": " + e.getMessage());
            try {
                FileUtil.moveFile(file, ConfigManager.getNotProcessedDir());
            } catch (IOException ioException) {
                System.err.println("Erro FATAL ao tentar mover para pasta ERRO: " + ioException.getMessage());
            }
            if (!isAutomatic) {
                JOptionPane.showMessageDialog(null, "Erro ao processar arquivo " + file.getName() + ":\n" + e.getMessage(), "Erro de Processamento", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O no arquivo " + file.getName() + ": " + e.getMessage());
            try {
                 FileUtil.moveFile(file, ConfigManager.getNotProcessedDir());
            } catch (IOException ioException) {
                 System.err.println("Erro FATAL ao tentar mover arquivo com erro de I/O para pasta ERRO: " + ioException.getMessage());
            }
            if (!isAutomatic) {
                 JOptionPane.showMessageDialog(null, "Erro de I/O ao processar arquivo " + file.getName() + ":\n" + e.getMessage(), "Erro Crítico de Arquivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}