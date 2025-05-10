import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window extends JFrame implements ActionListener {

    private JPanel robotPanel;
    private JPanel imagePanel;
    private JLabel display;
    private JLabel[] wasteMarkers; // To mark waste locations

    private RobotLivraison robot;

    private JButton[] buttons;
    private String[] buttonNames = {
            "marche_arret", "Effectuer Tache", "Deplacer",
            "Recycler", "Planter", "Recharger", "Maintenance"};

    private ImageIcon robotIcon = new ImageIcon("src/robot.png");
    private ImageIcon logo = new ImageIcon("src/logo.png");
    private ImageIcon wasteIcon = new ImageIcon("src/waste.png"); // Assume a waste.png icon exists
    private JTextArea robotInfo;

    public Window(RobotLivraison robot) {
        this.robot = robot;

        setTitle("Robot Control Panel");
        setLayout(new BorderLayout());
        setIconImage(logo.getImage());
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupLayout();

        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void setupLayout() {
        imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(500, 500));
        imagePanel.setLayout(null);
        imagePanel.setBackground(Color.WHITE);

        Image scaledImage = robotIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        display = new JLabel(scaledIcon);
        display.setBounds(robot.x, robot.y, 80, 80);
        display.setToolTipText("Energie restante: " + robot.energie);

        // Add waste markers at (0, 0) and (50, 50)
        wasteMarkers = new JLabel[2];
        Image scaledWasteImage = wasteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledWasteIcon = new ImageIcon(scaledWasteImage);
        wasteMarkers[0] = new JLabel(scaledWasteIcon);
        wasteMarkers[0].setBounds(0, 0, 20, 20);
        wasteMarkers[0].setToolTipText("Déchet à recycler");
        wasteMarkers[1] = new JLabel(scaledWasteIcon);
        wasteMarkers[1].setBounds(50, 50, 20, 20);
        wasteMarkers[1].setToolTipText("Déchet à recycler");

        imagePanel.add(wasteMarkers[0]);
        imagePanel.add(wasteMarkers[1]);
        imagePanel.add(display);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(250, 500));

        buttons = new JButton[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            button.addActionListener(this);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(button);
            buttons[i] = button;
        }
        updateMarcheArretButtonColor();

        robotInfo = new JTextArea(robot.toString());
        robotInfo.setEditable(false);
        robotInfo.setLineWrap(true);
        robotInfo.setWrapStyleWord(true);
        robotInfo.setBackground(buttonPanel.getBackground());
        robotInfo.setPreferredSize(new Dimension(150, 300));
        robotInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
        robotInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(robotInfo);

        getContentPane().add(imagePanel, BorderLayout.WEST);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
    }

    private void updateRobotPosition() {
        display.setBounds(robot.x, robot.y, 80, 80);
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void updateRobotInfo() {
        if (robotInfo != null) {
            robotInfo.setText(robot.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        switch (action) {
            case "marche_arret":
                if (robot.enMarche) {
                    robot.arreter();
                    updateRobotInfo();
                } else {
                    try {
                        robot.demarrer();
                        updateRobotInfo();
                    } catch (RobotException ex) {
                        handleRobotException(ex);
                    }
                }
                updateMarcheArretButtonColor();
                break;

            case "Effectuer Tache":
                if (!robot.enMarche) {
                    handleRobotException(new RobotException("Le robot doit être démarré pour effectuer une tâche"));
                    break;
                }
                if (robot.getEnlivraison()) {
                    openInputDialog("Coordonnées de Livraison", "Destination X", "Destination Y", (x, y) -> {
                        try {
                            robot.destination = x + "," + y;
                            robot.effectuerTache();
                            updateRobotPosition();
                            updateRobotInfo();
                            JOptionPane.showMessageDialog(this, "Colis livré avec succès");
                        } catch (RobotException ex) {
                            handleRobotException(ex);
                        }
                    });
                } else {
                    JButton boutonOui = new JButton("Oui");
                    JButton boutonNon = new JButton("Non");

                    JPanel panel = new JPanel();
                    panel.add(new JLabel("Voulez-vous charger un nouveau colis ?"));
                    panel.add(boutonOui);
                    panel.add(boutonNon);

                    JDialog dialog = new JDialog(this, "Chargement de colis", true);
                    dialog.getContentPane().add(panel);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);

                    boutonOui.addActionListener(evt -> {
                        dialog.dispose();

                        JTextField nomColisField = new JTextField(15);
                        JPanel saisiePanel = new JPanel();
                        saisiePanel.add(new JLabel("Nom du colis :"));
                        saisiePanel.add(nomColisField);

                        int result = JOptionPane.showConfirmDialog(this, saisiePanel, "Saisir le nom du colis", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            String nomColis = nomColisField.getText().trim();
                            if (!nomColis.isEmpty()) {
                                try {
                                    robot.chargerColis(nomColis);
                                    updateRobotInfo();
                                    openInputDialog("Coordonnées de Livraison", "Destination X", "Destination Y", (x, y) -> {
                                        try {
                                            robot.destination = x + "," + y;
                                            robot.effectuerTache();
                                            updateRobotPosition();
                                            updateRobotInfo();
                                            JOptionPane.showMessageDialog(this, "Colis livré avec succès");
                                        } catch (RobotException ex) {
                                            handleRobotException(ex);
                                        }
                                    });
                                } catch (RobotException ex) {
                                    handleRobotException(ex);
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "Le nom du colis ne peut pas être vide.");
                            }
                        }
                    });

                    boutonNon.addActionListener(evt -> {
                        dialog.dispose();
                        try {
                            robot.effectuerTache();
                            updateRobotInfo();
                        } catch (RobotException ex) {
                            handleRobotException(ex);
                        }
                    });

                    dialog.setVisible(true);
                }
                break;

            case "Deplacer":
                if (!robot.enMarche) {
                    handleRobotException(new RobotException("Le robot doit être démarré pour se déplacer"));
                    break;
                }
                openInputDialog("Déplacer le Robot", "X", "Y", (x, y) -> {
                    try {
                        robot.deplacer(x, y);
                        updateRobotPosition();
                        updateRobotInfo();
                    } catch (RobotException ex) {
                        handleRobotException(ex);
                    }
                });
                break;

            case "Recycler":
                if (!robot.enMarche) {
                    handleRobotException(new RobotException("Le robot doit être démarré pour recycler"));
                    break;
                }
                handleRecycler();
                break;

            case "Planter":
                if (!robot.enMarche) {
                    handleRobotException(new RobotException("Le robot doit être démarré pour planter"));
                    break;
                }
                handlePlanter();
                break;

            case "Recharger":
                openInputDialog("Recharger le Robot", "Valeur", null, (val, unused) -> {
                    try {
                        robot.recharger(val);
                        updateRobotInfo();
                    } catch (RobotException ex) {
                        handleRobotException(ex);
                    }
                });
                break;

            case "Maintenance":
                try {
                    if (!robot.verifierMaintenance()) {
                        JOptionPane.showMessageDialog(this, "Aucune maintenance requise", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                updateRobotInfo();
                break;
        }
    }

    private void updateMarcheArretButtonColor() {
        JButton button = buttons[0];
        if (robot.enMarche) {
            button.setBackground(Color.GREEN);
            button.setForeground(Color.BLACK);
        } else {
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
        }
    }

    private void handleRecycler() {
        try {
            // Check if waste is present at the current location
            boolean wastePresent = (robot.x == 0 && robot.y == 0) || (robot.x == 50 && robot.y == 50);
            if (wastePresent) {
                JOptionPane.showMessageDialog(this, "Déchet trouvé à la position actuelle (" + robot.x + ", " + robot.y + "), recyclage en cours...");
            }
            robot.recycler();
            updateRobotPosition();
            updateRobotInfo();
            JOptionPane.showMessageDialog(this, "Recyclage terminé : 1 graine produite", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handlePlanter() {
        try {
            // Check if seeds are available
            if (robot.toString().contains("graines disponibles=0")) {
                JOptionPane.showMessageDialog(this, "Aucune graine disponible, veuillez recycler des déchets pour obtenir des graines", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Graine disponible, plantation en cours...");
            }
            robot.planter();
            updateRobotPosition();
            updateRobotInfo();
            JOptionPane.showMessageDialog(this, "Plantation terminée : offset de carbone +5", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handleRobotException(RobotException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    interface InputAction {
        void apply(int val1, int val2);
    }

    private void openInputDialog(String title, String label1, String label2, InputAction action) {
        JFrame inputFrame = new JFrame(title);
        inputFrame.setSize(400, 200);
        inputFrame.setLayout(new GridLayout(4, 2, 5, 5));

        JTextField field1 = new JTextField();
        JTextField field2 = new JTextField();

        inputFrame.add(new JLabel(label1 + ":"));
        inputFrame.add(field1);

        if (label2 != null) {
            inputFrame.add(new JLabel(label2 + ":"));
            inputFrame.add(field2);
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(evt -> {
            try {
                int val1 = Integer.parseInt(field1.getText());
                int val2 = label2 != null ? Integer.parseInt(field2.getText()) : 0;
                action.apply(val1, val2);
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer des entiers valides.");
            }
        });

        inputFrame.add(new JLabel());
        inputFrame.add(okButton);

        inputFrame.setLocationRelativeTo(this);
        inputFrame.setVisible(true);
    }
}