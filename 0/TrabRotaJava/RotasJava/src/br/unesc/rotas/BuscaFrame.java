package br.unesc.rotas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BuscaFrame extends JFrame {
    
    private static final long serialVersionUID = 1L; 

    private JTextField buscaField;
    private JButton buscarButton;
    private JTextField codigoOrigemField;
    private JTextField cidadeOrigemField;
    private JTextField codigoDestinoField;
    private JTextField cidadeDestinoField;
    private JTextField kmField;
    private JButton addButton; 
    private JButton salvarButton;
    private JButton processarButton;
    private JTable tabelaRotas;
    private DefaultTableModel tableModel;

    public BuscaFrame() {
        super("Dijsktra - Busca por melhor caminho");
        initUI();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Código Origem", "Cidade Origem", "Código Destino", "Cidade Destino", "Distância"}; 
        tableModel = new DefaultTableModel(columnNames, 0);
        tabelaRotas = new JTable(tableModel);
        add(new JScrollPane(tabelaRotas), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("SALVAR");
        processarButton = new JButton("PROCESSAR");
        bottomPanel.add(salvarButton);
        bottomPanel.add(processarButton);
        add(bottomPanel, BorderLayout.SOUTH);

        buscarButton.addActionListener(this::buscarArquivo);
        addButton.addActionListener(this::adicionarRotaManual);
        processarButton.addActionListener(this::processarArquivo);
        salvarButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade SALVAR: Não implementada neste escopo.", "Info", JOptionPane.INFORMATION_MESSAGE));
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panel.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3; buscaField = new JTextField(30); panel.add(buscaField, gbc);
        gbc.gridx = 4; gbc.weightx = 0; gbc.gridwidth = 1; buscarButton = new JButton("Buscar"); panel.add(buscarButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; codigoOrigemField = new JTextField(5); panel.add(codigoOrigemField, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Cidade:"), gbc);
        gbc.gridx = 3; cidadeOrigemField = new JTextField(15); panel.add(cidadeOrigemField, gbc);
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("(ORIGEM)"), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; codigoDestinoField = new JTextField(5); panel.add(codigoDestinoField, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Cidade:"), gbc);
        gbc.gridx = 3; cidadeDestinoField = new JTextField(15); panel.add(cidadeDestinoField, gbc);
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("(DESTINO)"), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("KM:"), gbc);
        gbc.gridx = 1; kmField = new JTextField(5); panel.add(kmField, gbc);
        gbc.gridx = 4; gbc.anchor = GridBagConstraints.EAST; addButton = new JButton("+"); panel.add(addButton, gbc);
        
        return panel;
    }

    private void buscarArquivo(ActionEvent e) {
        String rootPath = ConfigManager.getRootFolder();
        File initialDir = null;
        
        if (rootPath != null && !rootPath.isEmpty()) {
            File dir = new File(rootPath);
            if (dir.isDirectory()) {
                 initialDir = dir;
            }
        }
        
        JFileChooser fileChooser = new JFileChooser(initialDir); 
        fileChooser.setDialogTitle("Selecione o Arquivo rotaNN.txt");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            buscaField.setText(selectedFile.getAbsolutePath());
            
            try {
                RouteParser parser = new RouteParser(selectedFile); 
                parser.validate(); 
                
                List<Object[]> routes = parser.getRoutesDataForTable();
                
                tableModel.setRowCount(0);
                
                for (Object[] route : routes) {
                    tableModel.addRow(route);
                }
                
                JOptionPane.showMessageDialog(this, "Rotas pré-definidas do arquivo carregadas com sucesso!", "Carregamento", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro de I/O ao ler o arquivo:\n" + ex.getMessage(), "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
                tableModel.setRowCount(0);
            } catch (RuntimeException ex) {
                 JOptionPane.showMessageDialog(this, "Erro de formato ou validação do arquivo de rota:\n" + ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                 tableModel.setRowCount(0);
            }
        }
    }

    private void adicionarRotaManual(ActionEvent e) {
        try {
            String codOrigem = codigoOrigemField.getText().trim();
            String cidOrigem = cidadeOrigemField.getText().trim();
            String codDestino = codigoDestinoField.getText().trim();
            String cidDestino = cidadeDestinoField.getText().trim();
            
            String kmText = kmField.getText().trim();
            if (kmText.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "O campo KM deve ser preenchido.", "Erro de Input", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            int km = Integer.parseInt(kmText);

            if (codOrigem.isEmpty() || codDestino.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Os códigos de Origem e Destino são obrigatórios.", "Erro de Input", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            tableModel.addRow(new Object[]{codOrigem, cidOrigem, codDestino, cidDestino, km});

            codigoOrigemField.setText(""); cidadeOrigemField.setText("");
            codigoDestinoField.setText(""); cidadeDestinoField.setText("");
            kmField.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "O campo KM deve ser um número inteiro válido.", "Erro de Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processarArquivo(ActionEvent e) {
        String filePath = buscaField.getText();

        if (filePath == null || filePath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione ou digite o caminho de um arquivo rotaNN.txt para processar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File fileToProcess = new File(filePath);

        if (!fileToProcess.exists()) {
             JOptionPane.showMessageDialog(this, "Arquivo não encontrado no caminho especificado.", "Erro", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        MainApp.processFile(fileToProcess, false);
        
        buscaField.setText(""); 
        tableModel.setRowCount(0);
    }
}