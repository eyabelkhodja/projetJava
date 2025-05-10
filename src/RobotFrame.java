import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RobotFrame extends JFrame implements ActionListener {

    private JPanel robotPanel;
    RobotLivraison robot;
    JButton tache, deplacer, recycler, planter, recharger;

    ImageIcon robotIcon = new ImageIcon("src/robot.png");
    ImageIcon logo = new ImageIcon("src/logo.png");

    JLabel display = new JLabel();

    JPanel image = new JPanel();

    public RobotFrame(RobotLivraison robot) {
        setTitle("Robot Control Panel");
        setLayout(new BorderLayout());
        setIconImage(logo.getImage());

        Image scaledImage = robotIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        display = new JLabel(scaledIcon);
        display.setBounds(robot.x, robot.y, 150, 150);
        image.setPreferredSize(new Dimension(1500, 1000));
        image.setLayout(null);
        image.add(display);
        add(image, BorderLayout.WEST);

        this.robot = robot;
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display.setToolTipText(robot.toString());

        display.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                display.setToolTipText(robot.toString());
                // Update border based on seed availability
                if (robot.toString().contains("graines disponibles=0")) {
                    display.setBorder(null);
                } else {
                    display.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                }
            }
        });

        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        tache = new JButton("Effectuer Tache");
        deplacer = new JButton("Deplacer");
        recycler = new JButton("Recycler");
        planter = new JButton("Planter");
        recharger = new JButton("Recharger");

        tache.addActionListener(this);
        deplacer.addActionListener(this);
        recycler.addActionListener(this);
        planter.addActionListener(this);
        recharger.addActionListener(this);

        buttonPanel.add(tache);
        buttonPanel.add(deplacer);
        buttonPanel.add(recycler);
        buttonPanel.add(planter);
        buttonPanel.add(recharger);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateRobotPosition() {
        display.setBounds(robot.x, robot.y, 150, 150);
        image.revalidate();
        image.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        switch (action) {
            case "Effectuer Tache":
                if (!robot.enMarche) {
                    JOptionPane.showMessageDialog(this, "Le robot doit être démarré pour effectuer une tâche", "Erreur", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                if (robot.getEnlivraison()) {
                    openDeplacerDialogForDelivery();
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
                                    openDeplacerDialogForDelivery();
                                } catch (RobotException ex) {
                                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                                    if (ex.getMessage().contains("Énergie insuffisante")) {
                                        rechargeDialogue();
                                    }
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
                        } catch (RobotException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    dialog.setVisible(true);
                }
                break;

            case "Deplacer":
                if (!robot.enMarche) {
                    JOptionPane.showMessageDialog(this, "Le robot doit être démarré pour se déplacer", "Erreur", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                openDeplacerDialog();
                break;

            case "Recycler":
                if (!robot.enMarche) {
                    JOptionPane.showMessageDialog(this, "Le robot doit être démarré pour recycler", "Erreur", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                try {
                    boolean wastePresent = (robot.x == 0 && robot.y == 0) || (robot.x == 50 && robot.y == 50);
                    if (wastePresent) {
                        JOptionPane.showMessageDialog(this, "Déchet trouvé à la position actuelle (" + robot.x + ", " + robot.y + "), recyclage en cours...");
                    }
                    robot.recycler();
                    updateRobotPosition();
                    JOptionPane.showMessageDialog(this, "Recyclage terminé : 1 graine produite", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Planter":
                if (!robot.enMarche) {
                    JOptionPane.showMessageDialog(this, "Le robot doit être démarré pour planter", "Erreur", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                try {
                    if (robot.toString().contains("graines disponibles=0")) {
                        JOptionPane.showMessageDialog(this, "Aucune graine disponible, veuillez recycler des déchets pour obtenir des graines", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Graine disponible, plantation en cours...");
                    }
                    robot.planter();
                    updateRobotPosition();
                    JOptionPane.showMessageDialog(this, "Plantation terminée : offset de carbone +5", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Recharger":
                rechargeDialogue();
                break;

            default:
                JOptionPane.showMessageDialog(this, "Action non reconnue", "Avertissement", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }

    private void openDeplacerDialog() {
        JFrame inputFrame = new JFrame(" Déplacer le Robot ");
        inputFrame.setSize(1000, 600);
        inputFrame.setLayout(new GridLayout(3, 2));

        JLabel description = new JLabel("Veuillez donner les coordonnées X et Y pour le déplacement du robot:");

        JLabel xLabel = new JLabel("    X:");
        JTextField xField = new JTextField();

        JLabel yLabel = new JLabel("    Y:");
        JTextField yField = new JTextField();

        JButton okButton = new JButton(" OK ");

        okButton.addActionListener(event -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                robot.deplacer(x, y);
                updateRobotPosition();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer des entiers valides.");
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(inputFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                if (ex.getMessage().contains("Énergie insuffisante")) {
                    rechargeDialogue();
                }
            }
            inputFrame.dispose();
        });

        inputFrame.setLayout(new GridLayout(5, 2, 5, 5));
        inputFrame.add(description);
        inputFrame.add(new JLabel());
        inputFrame.add(xLabel);
        inputFrame.add(xField);
        inputFrame.add(yLabel);
        inputFrame.add(yField);
        inputFrame.add(new JLabel());
        inputFrame.add(okButton);

        inputFrame.setLocationRelativeTo(this);
        inputFrame.setVisible(true);
    }

    private void openDeplacerDialogForDelivery() {
        JFrame inputFrame = new JFrame("Coordonnées de Livraison");
        inputFrame.setSize(1000, 600);
        inputFrame.setLayout(new GridLayout(5, 2, 5, 5));

        JLabel description = new JLabel("Veuillez donner les coordonnées X et Y pour la livraison:");
        JLabel xLabel = new JLabel("    X:");
        JTextField xField = new JTextField();
        JLabel yLabel = new JLabel("    Y:");
        JTextField yField = new JTextField();
        JButton okButton = new JButton("OK");

        okButton.addActionListener(event -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                robot.destination = x + "," + y;
                robot.effectuerTache();
                updateRobotPosition();
                inputFrame.dispose();
                JOptionPane.showMessageDialog(this, "Colis livré avec succès");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer des entiers valides.");
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(inputFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                if (ex.getMessage().contains("Énergie insuffisante")) {
                    rechargeDialogue();
                }
            }
        });

        inputFrame.add(description);
        inputFrame.add(new JLabel());
        inputFrame.add(xLabel);
        inputFrame.add(xField);
        inputFrame.add(yLabel);
        inputFrame.add(yField);
        inputFrame.add(new JLabel());
        inputFrame.add(okButton);

        inputFrame.setLocationRelativeTo(this);
        inputFrame.setVisible(true);
    }

    private void rechargeDialogue() {
        JFrame inputFrame = new JFrame(" Recharger le Robot ");
        inputFrame.setSize(1000, 600);
        inputFrame.setLayout(new GridLayout(3, 2));

        JLabel description = new JLabel("Veuillez donner le pourcentage d'énergie que vous souhaiter ajouter:");

        JLabel xLabel = new JLabel("Valeur:");
        JTextField xField = new JTextField();

        JButton okButton = new JButton(" OK ");

        okButton.addActionListener(event -> {
            try {
                int x = Integer.parseInt(xField.getText());
                robot.recharger(x);
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer des entiers valides.");
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(inputFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputFrame.setLayout(new GridLayout(5, 2, 5, 5));
        inputFrame.add(description);
        inputFrame.add(new JLabel());
        inputFrame.add(xLabel);
        inputFrame.add(xField);
        inputFrame.add(new JLabel());
        inputFrame.add(okButton);

        inputFrame.setLocationRelativeTo(this);
        inputFrame.setVisible(true);
    }
}