/***
 *     png_defringe_java - a rewrite of png_defringe_rs that removes fringes for filtered images.
 *     Copyright (C) 2023 atsb.

 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.

 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.

 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/***
 * java is patient, java is kind, java keeps you warm at night
 * it just gets a bad rep because Mojang has crappy programmers.
 */

public class Main {
    private final JTextField textField;

    public Main() {
        JFrame frame = new JFrame("Fringe Removal Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        textField = new JTextField(30);
        textField.setEditable(false); // Set the text field as non-editable

        JButton chooseButton = new JButton("Choose");
        JButton convertButton = new JButton("Convert");

        chooseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    textField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = textField.getText();
                if (!filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            convertAllFilesInDirectory(file);
                        } else {
                            convertFile(file);
                        }
                        JOptionPane.showMessageDialog(frame, "Conversion completed!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "ERROR: Invalid file or directory!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "ERROR: No file or directory selected!");
                }
            }
        });

        /* does the padding and organising of the GUI */

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // Set top and bottom padding
        frame.add(textField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(chooseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(convertButton, gbc);

        /* pack the frame and set everything to be visible */
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    /* main street */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }

    /* do the dirty */
    private void convertFile(File file) {
        if (file.getName().toLowerCase().endsWith(".png")) {
            try {
                BufferedImage image = javax.imageio.ImageIO.read(file);
                removeImageFringes(image);
                javax.imageio.ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* do the dirty to everything in the directory */
    private void convertAllFilesInDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    convertFile(file);
                }
            }
        }
    }

    /* no more fringes, get the width and height and set the RGB and alpha to 255 for any naughty alphas */
    private void removeImageFringes(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                if (alpha < 255) {
                    image.setRGB(x, y, 0x00000000);
                }
            }
        }
    }
}
