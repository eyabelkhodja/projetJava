import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window extends JFrame implements ActionListener {

    private JPanel robotPanel;
    private JPanel imagePanel;
    private JLabel display;
    private JLabel zeblaDisplay1; // To display the zebla icon
    private JLabel zeblaDisplay2; // To display the zebla icon

    private RobotLivraison robot;

    private JButton[] buttons;
    private String[] buttonNames = {
            "marche_arret", "Effectuer Tache", "Deplacer",
            "Recycler", "Planter", "Recharger", "Maintenance"};

    private ImageIcon robotIcon = new ImageIcon("src/robot.png");
    private ImageIcon zebla = new ImageIcon("src/zebla.png"); // Corrected path
    private ImageIcon logo = new ImageIcon("src/logo.png");
    private JTextArea robotInfo;

    public Window(RobotLivraison robot) {
        this.robot = robot;

        setTitle("Robot Control Panel");
        setLayout(new BorderLayout());
        setIconImage(logo.getImage());
        setSize(950, 750); // Increased size to accommodate larger imagePanel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupLayout();

        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.SOUTH); // Adjusted to SOUTH


        setVisible(true);
    }

    private void setupLayout() {
        imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(700, 700));
        imagePanel.setLayout(null);
        imagePanel.setBackground(Color.WHITE); // Ensure panel is visible

        // Robot icon
        Image scaledRobotImage;
        try {
            scaledRobotImage = robotIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error loading robot icon: " + e.getMessage());
            scaledRobotImage = null;
        }
        ImageIcon scaledRobotIcon = new ImageIcon(scaledRobotImage);

        display = new JLabel(scaledRobotIcon);
        display.setBounds(robot.x, robot.y, 80, 80);
        display.setToolTipText("Energie restante: " + robot.energie);

        // Zebla icons (replacing waste markers)
        Image scaledZeblaImage;
        try {
            scaledZeblaImage = zebla.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error loading zebla icon: " + e.getMessage());
            scaledZeblaImage = null;
        }
        ImageIcon scaledZeblaIcon = new ImageIcon(scaledZeblaImage);

        zeblaDisplay1 = new JLabel(scaledZeblaIcon);
        zeblaDisplay1.setBounds(0, 400, 100, 100);
        zeblaDisplay1.setToolTipText("Déchet à recycler");

        zeblaDisplay2 = new JLabel(scaledZeblaIcon);
        zeblaDisplay2.setBounds(300, 50, 100, 100);
        zeblaDisplay2.setToolTipText("Déchet à recycler");

        // Add components to imagePanel
        imagePanel.add(zeblaDisplay1);
        imagePanel.add(zeblaDisplay2);
        imagePanel.add(display);

        // Add the "Centre de Recyclage" label (last to ensure it’s on top)
        JLabel recyclingLabel = new JLabel("<html><center><b>Centre de Recyclage</b></center></html>");
        recyclingLabel.setBounds(300, 300, 200, 40);
        recyclingLabel.setForeground(new Color(34, 139, 34)); // Green color
        recyclingLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Bold and larger font
        imagePanel.add(recyclingLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(250, 700));

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

        getContentPane().add(imagePanel, BorderLayout.CENTER); // Changed to CENTER
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        // Force repaint to ensure label is rendered
        imagePanel.revalidate();
        imagePanel.repaint();
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
            boolean wastePresent = (robot.x == 300 && robot.y == 50) || (robot.x == 400 && robot.y == 0);
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
}