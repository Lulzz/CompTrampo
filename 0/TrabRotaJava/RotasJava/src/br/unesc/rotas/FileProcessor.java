package br.unesc.rotas;

import java.io.File;
import java.io.IOException;

public class FileProcessor {

    public void processFile(File file) {
        final boolean isAutomatic = true; 
        
        try {
            RouteParser parser = new RouteParser(file);
            parser.validate();
            Graph graph = parser.buildGraphFromPesos();

            Node startNode = graph.getNode(parser.getStartNode());
            if (startNode == null) {
                 throw new IllegalArgumentException("Nó inicial (" + parser.getStartNode() + ") não encontrado no grafo.");
            }
            
            // CORREÇÃO: Usando computePaths
            Dijkstra.computePaths(startNode); 

            FileUtil.moveFile(file, ConfigManager.getProcessedDir());
            
            if (isAutomatic) {
                System.out.println("Arquivo " + file.getName() + " processado com SUCESSO!");
            }

        } catch (RuntimeException e) {
            System.err.println("Erro de Processamento no arquivo " + file.getName() + ": " + e.getMessage());
            try {
                FileUtil.moveFile(file, ConfigManager.getNotProcessedDir());
            } catch (IOException ioException) {
                System.err.println("Erro FATAL ao tentar mover para pasta ERRO: " + ioException.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O no arquivo " + file.getName() + ": " + e.getMessage());
            try {
                 FileUtil.moveFile(file, ConfigManager.getNotProcessedDir());
            } catch (IOException ioException) {
                 System.err.println("Erro FATAL ao tentar mover arquivo com erro de I/O para pasta ERRO: " + ioException.getMessage());
            }
        }
    }
}