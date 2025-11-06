package br.unesc.rotas;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ConfigFrame extends JFrame {
    
    private static final long serialVersionUID = 1L; 
    
    private final JTextField pastaField;
    private final JTextField sucessoField;
    private final JTextField erroField;
    private final JCheckBox rotaAutomaticaCheckBox;
    private final JButton salvarButton;
    
    private final boolean isFirstRun;

    public ConfigFrame(boolean isFirstRun) {
        super("Configuração");
        this.isFirstRun = isFirstRun;
        
        if (isFirstRun) { 
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        } else { 
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        }
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Pasta:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; pastaField = new JTextField(25); add(pastaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Sucesso:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; sucessoField = new JTextField(25); add(sucessoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Erro:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; erroField = new JTextField(25); add(erroField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        rotaAutomaticaCheckBox = new JCheckBox("Rota automática"); add(rotaAutomaticaCheckBox, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        salvarButton = new JButton("SALVAR"); add(salvarButton, gbc);
        
        loadValues();
        salvarButton.addActionListener(e -> saveConfig());
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void loadValues() {
        boolean loaded = ConfigManager.loadConfig(); 
        
        if (loaded && !isFirstRun) {
            pastaField.setText(ConfigManager.getRootFolder());
            sucessoField.setText(ConfigManager.getProcessedDir());
            erroField.setText(ConfigManager.getNotProcessedDir());
            rotaAutomaticaCheckBox.setSelected(ConfigManager.isRotaAutomatica());
        } else {
            pastaField.setText("C:\\Teste");
            sucessoField.setText("C:\\Teste\\Processado");
            erroField.setText("C:\\Teste\\NaoProcessado");
            rotaAutomaticaCheckBox.setSelected(false);
        }
    }

    private void saveConfig() {
        try {
            ConfigManager.saveConfig(
                pastaField.getText().trim(),
                sucessoField.getText().trim(),
                erroField.getText().trim(),
                rotaAutomaticaCheckBox.isSelected()
            );
            
            JOptionPane.showMessageDialog(this, "Configurações salvas com sucesso. Os diretórios foram criados.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
            MainApp.startApplication();
            
        } catch (IOException e) {
             JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo de configuração e criar diretórios:\n" + e.getMessage(), "Erro de I/O", JOptionPane.ERROR_MESSAGE);
        }
    }
}