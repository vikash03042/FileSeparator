/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.fileseparator;

/**
 *
 * @author VIKASH
 */
 import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileSeparator extends JFrame {

    private final JLabel statusLabel;
    private final JButton selectFolderButton;

    public FileSeparator() {
        setTitle("File Separator");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center the window

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));

        JLabel heading = new JLabel("📂 File Type Separator");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 120));

        statusLabel = new JLabel("Choose a folder to begin.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(80, 80, 80));

        selectFolderButton = new JButton("Select Folder");
        selectFolderButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectFolderButton.setFocusPainted(false);
        selectFolderButton.setBackground(new Color(100, 149, 237));
        selectFolderButton.setForeground(Color.WHITE);
        selectFolderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectFolderButton.addActionListener(new SelectFolderAction());

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(heading);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(selectFolderButton);

        add(panel);
    }

    private class SelectFolderAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = chooser.showOpenDialog(FileSeparator.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                statusLabel.setText("Arranging files...");

                // Start background task
                new FileOrganizerWorker(selectedDir).execute();
            }
        }
    }

    private class FileOrganizerWorker extends SwingWorker<Void, Void> {
        private final File directory;

        public FileOrganizerWorker(File directory) {
            this.directory = directory;
        }

        @Override
        protected Void doInBackground() {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String extension = getFileExtension(file);
                        if (!extension.isEmpty()) {
                            File typeDir = new File(directory, extension.toUpperCase());
                            if (!typeDir.exists()) {
                                typeDir.mkdir();
                            }
                            try {
                                Files.move(file.toPath(), new File(typeDir, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void done() {
            statusLabel.setText("✅ Completed Successfully!");
        }

        private String getFileExtension(File file) {
            String name = file.getName();
            int lastDot = name.lastIndexOf('.');
            return (lastDot > 0) ? name.substring(lastDot + 1) : "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileSeparator().setVisible(true);
        });
    }
}

