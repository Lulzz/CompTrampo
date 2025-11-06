package br.unesc.rotas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteParser {
    
    private final File file;
    private final List<String> conexoes = new ArrayList<>();
    private final List<String> pesos = new ArrayList<>();
    
    private int totalNodes;
    private int sumWeightsHeader;
    private int numConexoesTrailer;
    private int numPesosTrailer;
    private int sumWeightsTrailer;
    private String startNode; 

    public RouteParser(File file) throws IOException {
        this.file = file; 
        parseFile();
    }

    private void parseFile() throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        if (lines.isEmpty()) throw new IOException("Arquivo vazio.");
        
        parseHeader(lines.get(0));

        for (int i = 1; i < lines.size() - 1; i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            
            if (line.startsWith("01")) { 
                conexoes.add(line); 
            } else if (line.startsWith("02")) { 
                pesos.add(line.replaceAll("^\\$|\\$$", ""));
            } else {
                 throw new IllegalArgumentException("Registro desconhecido ou inválido: " + line); 
            }
        }
        
        if (lines.size() >= 2) {
             parseTrailer(lines.get(lines.size() - 1));
        } else {
             throw new IllegalArgumentException("Arquivo inválido: Falta o TRAILER.");
        }
    }

    private void parseHeader(String headerLine) { 
        if (!headerLine.startsWith("00")) {
            throw new IllegalArgumentException("Header inválido. Deve começar com '00'.");
        }
        try {
            totalNodes = Integer.parseInt(headerLine.substring(2, 4).trim()); 
            sumWeightsHeader = Integer.parseInt(headerLine.substring(4).trim()); 
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Header inválido (Formato 00NNSP). Verifique NN e SP.");
        }
    }

    private void parseTrailer(String trailerLine) { 
        Pattern pattern = Pattern.compile("09RC=(\\d+);RP=(\\d+);P=(\\d+)$"); 
        Matcher matcher = pattern.matcher(trailerLine);
        
        if (matcher.find()) {
            try {
                numConexoesTrailer = Integer.parseInt(matcher.group(1));
                numPesosTrailer = Integer.parseInt(matcher.group(2));
                sumWeightsTrailer = Integer.parseInt(matcher.group(3));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Trailer inválido (RC, RP ou P não são números).");
            }
        } else {
            throw new IllegalArgumentException("Trailer inválido: Formato incorreto (esperado 09RC=NN;RP=NN;P=NN).");
        }
    }
    
    private String[] extractNodes(String connectionLine) {
        String nodesPart = connectionLine.substring(2).trim(); 
        String[] nodes = nodesPart.split("[\\s=]+"); 
        
        if (nodes.length != 2 || nodes[0].isEmpty() || nodes[1].isEmpty()) {
            throw new IllegalArgumentException("Linha de conexão (01) inválida (esperado 01NO ND ou 01NO=ND): " + connectionLine);
        }
        return nodes;
    }


    public Graph buildGraphFromPesos() { 
        Graph graph = new Graph();
        
        for (String con : conexoes) { 
            String[] nodes = extractNodes(con);
            graph.addNode(nodes[0]); 
            graph.addNode(nodes[1]);
            
            if (startNode == null) {
                startNode = nodes[0]; 
            }
        }

        for (String pesoLine : pesos) {
            Pattern pattern = Pattern.compile("^02([A-Za-z0-9]+);([A-Za-z0-9]+)=(\\d+)$");
            Matcher matcher = pattern.matcher(pesoLine);
            
            if (matcher.find()) {
                String sourceName = matcher.group(1).trim();
                String destName = matcher.group(2).trim();
                int weight;
                try {
                    weight = Integer.parseInt(matcher.group(3));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Peso inválido na linha: " + pesoLine);
                }
                
                graph.addEdge(sourceName, destName, weight); 
            } else {
                throw new IllegalArgumentException("Linha de peso inválida (formato 02NO;ND=P): " + pesoLine);
            }
        }
        
        return graph;
    }

    public void validate() {
        long distinctNodes = conexoes.stream()
            .flatMap(s -> {
                try {
                    return Arrays.stream(extractNodes(s)); 
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Erro ao extrair nós para validação: " + e.getMessage());
                }
            })
            .distinct()            
            .count();
            
        if (totalNodes != distinctNodes) {
             throw new IllegalArgumentException("Validação NN falhou. Header NN=" + totalNodes + " vs. Encontrados=" + distinctNodes); 
        }
        
        if (numConexoesTrailer != conexoes.size()) {
            throw new IllegalArgumentException("Validação RC falhou. Trailer RC=" + numConexoesTrailer + " vs. Encontrados=" + conexoes.size());
        }

        if (numPesosTrailer != pesos.size()) {
            throw new IllegalArgumentException("Validação RP falhou. Trailer RP=" + numPesosTrailer + " vs. Encontrados=" + pesos.size());
        }

        int calculatedSumWeights = pesos.stream()
            .mapToInt(s -> {
                Pattern p = Pattern.compile("=(\\d+)$");
                Matcher m = p.matcher(s);
                return m.find() ? Integer.parseInt(m.group(1)) : 0;
            })
            .sum();

        if (sumWeightsHeader != calculatedSumWeights) {
            throw new IllegalArgumentException("Validação SP falhou. SP=" + sumWeightsHeader + " vs. Calculado=" + calculatedSumWeights);
        }
        
        if (sumWeightsTrailer != calculatedSumWeights) {
            throw new IllegalArgumentException("Validação P falhou. P=" + sumWeightsTrailer + " vs. Calculado=" + calculatedSumWeights);
        }
    }
    
    public String getStartNode() {
        return startNode;
    }

    public List<Object[]> getRoutesDataForTable() {
        List<Object[]> routeData = new ArrayList<>();
        
        for (String pesoLine : pesos) {
            Pattern pattern = Pattern.compile("^02([A-Za-z0-9]+);([A-Za-z0-9]+)=(\\d+)$");
            Matcher matcher = pattern.matcher(pesoLine);
            
            if (matcher.find()) {
                String sourceCode = matcher.group(1).trim();
                String destCode = matcher.group(2).trim();
                int weight = Integer.parseInt(matcher.group(3));
                
                routeData.add(new Object[]{
                    sourceCode, 
                    sourceCode, 
                    destCode, 
                    destCode,   
                    weight      
                });
            }
        }
        return routeData;
    }
}