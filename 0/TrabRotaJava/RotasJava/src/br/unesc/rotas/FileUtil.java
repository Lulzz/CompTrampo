package br.unesc.rotas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    public static void moveFile(File sourceFile, String destinationDir) throws IOException {
        Path sourcePath = sourceFile.toPath();
        
        File destDir = new File(destinationDir);
        if (!destDir.exists()) {
            boolean created = destDir.mkdirs();
            if (!created) {
                throw new IOException("Falha ao criar o diretório de destino: " + destinationDir);
            }
        }
        
        Path destinationPath = new File(destDir, sourceFile.getName()).toPath();
        
        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Erro ao mover arquivo: " + sourceFile.getAbsolutePath() + " para " + destinationPath.toAbsolutePath());
            throw e;
        }
    }
}