import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.function.Consumer;

public class Actions implements ActionListener {
    private Window window;
    private RobotLivraison robot;
    private ImagePanel imagePanel;
    private ControlPanel controlPanel;

    public Actions(Window window, RobotLivraison robot, ImagePanel imagePanel, ControlPanel controlPanel) {
        this.window = window;
        this.robot = robot;
        this.imagePanel = imagePanel;
        this.controlPanel = controlPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        switch (action) {
            case "marche_arret":
                handleMarcheArret();
                break;
            case "Effectuer Tache":
                handleEffectuerTache();
                break;
            case "Deplacer":
                handleDeplacer();
                break;
            case "Recycler":
                handleRecycler();
                break;
            case "Planter":
                handlePlanter();
                break;
            case "Recharger":
                handleRecharger();
                break;
            case "Maintenance":
                handleMaintenance();
                break;
            case "Connecter":
                handleConnecter();
                break;
            case "Déconnecter":
                handleDeconnecter();
                break;
            case "Envoyer Données":
                handleEnvoyerDonnees();
                break;
        }
    }

    private void handleMarcheArret() {
        if (robot.enMarche) {
            robot.arreter();
            controlPanel.updateRobotInfo();
        } else {
            try {
                robot.demarrer();
                controlPanel.updateRobotInfo();
            } catch (RobotException ex) {
                handleRobotException(ex);
            }
        }
        controlPanel.updateMarcheArretButtonColor(robot.enMarche);
    }

    private void handleEffectuerTache() {
        if (!robot.enMarche) {
            handleRobotException(new RobotException("Le robot doit être démarré pour effectuer une tâche"));
            return;
        }
        if (robot.getEnlivraison()) {
            openInputDialog("Coordonnées de Livraison", "Destination X", "Destination Y", (x, y) -> {
                try {
                    robot.setDestination(x + "," + y);
                    robot.effectuerTache();
                    imagePanel.updateRobotPosition();
                    controlPanel.updateRobotInfo();
                    JOptionPane.showMessageDialog(window, "Colis livré avec succès");
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

            JDialog dialog = new JDialog(window, "Chargement de colis", true);
            dialog.getContentPane().add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(window);

            boutonOui.addActionListener(evt -> {
                dialog.dispose();

                JTextField nomColisField = new JTextField(15);
                JPanel saisiePanel = new JPanel();
                saisiePanel.add(new JLabel("Nom du colis :"));
                saisiePanel.add(nomColisField);

                int result = JOptionPane.showConfirmDialog(window, saisiePanel, "Saisir le nom du colis", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String nomColis = nomColisField.getText().trim();
                    if (!nomColis.isEmpty()) {
                        try {
                            robot.chargerColis(nomColis);
                            controlPanel.updateRobotInfo();
                            openInputDialog("Coordonnées de Livraison", "Destination X", "Destination Y", (x, y) -> {
                                try {
                                    robot.setDestination(x + "," + y);
                                    robot.effectuerTache();
                                    imagePanel.updateRobotPosition();
                                    controlPanel.updateRobotInfo();
                                    JOptionPane.showMessageDialog(window, "Colis livré avec succès");
                                } catch (RobotException ex) {
                                    handleRobotException(ex);
                                }
                            });
                        } catch (RobotException ex) {
                            handleRobotException(ex);
                        }
                    } else {
                        JOptionPane.showMessageDialog(window, "Le nom du colis ne peut pas être vide.");
                    }
                }
            });

            boutonNon.addActionListener(evt -> {
                dialog.dispose();
                try {
                    robot.effectuerTache();
                    controlPanel.updateRobotInfo();
                } catch (RobotException ex) {
                    handleRobotException(ex);
                }
            });

            dialog.setVisible(true);
        }
    }

    private void handleDeplacer() {
        if (!robot.enMarche) {
            handleRobotException(new RobotException("Le robot doit être démarré pour se déplacer"));
            return;
        }
        openInputDialog("Déplacer le Robot", "X", "Y", (x, y) -> {
            try {
                robot.deplacer(x, y);
                imagePanel.updateRobotPosition();
                controlPanel.updateRobotInfo();
            } catch (RobotException ex) {
                handleRobotException(ex);
            }
        });
    }

    private void handleRecycler() {
        if (!robot.enMarche) {
            handleRobotException(new RobotException("Le robot doit être démarré pour recycler"));
            return;
        }
        try {
            boolean wastePresent = (robot.x == 300 && robot.y == 50) || (robot.x == 0 && robot.y == 400);
            if (wastePresent) {
                JOptionPane.showMessageDialog(window, "Déchet trouvé à la position actuelle (" + robot.x + ", " + robot.y + "), recyclage en cours...");
                imagePanel.removeDechet(robot.x, robot.y);
            }
            robot.recycler();
            robot.FaireLivraison(200, 300);
            imagePanel.updateRobotPosition();
            controlPanel.updateRobotInfo();
            JOptionPane.showMessageDialog(window, "Recyclage terminé : 1 graine produite", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handlePlanter() {
        if (!robot.enMarche) {
            handleRobotException(new RobotException("Le robot doit être démarré pour planter"));
            return;
        }
        try {
            if (robot.toString().contains("graines disponibles=0")) {
                JOptionPane.showMessageDialog(window, "Aucune graine disponible, veuillez recycler des déchets pour obtenir des graines", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(window, "Graine disponible, plantation en cours...");
            }
            robot.planter();
            robot.FaireLivraison(350, 500);
            imagePanel.updateRobotPosition();
            controlPanel.updateRobotInfo();
            JOptionPane.showMessageDialog(window, "Plantation terminée : offset de carbone +5", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handleRecharger() {
        openInputDialog("Recharger le Robot", "Valeur", null, (val, unused) -> {
            try {
                robot.recharger(val);
                controlPanel.updateRobotInfo();
                JOptionPane.showMessageDialog(window, "Merci d'avoir opté pour la recharge photovoltaïque. Profitons du soleil!", "Recharge Terminée", JOptionPane.INFORMATION_MESSAGE);
            } catch (RobotException ex) {
                handleRobotException(ex);
            }
        });
    }

    private void handleMaintenance() {
        try {
            if (!robot.verifierMaintenance()) {
                JOptionPane.showMessageDialog(window, "Aucune maintenance requise", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
        controlPanel.updateRobotInfo();
    }

    private void handleConnecter() {
        openStringInputDialog("Connexion au Réseau", "Nom du réseau", (reseau) -> {
            try {
                robot.connecter(reseau);
                controlPanel.updateRobotInfo();
                JOptionPane.showMessageDialog(window, "Connecté au réseau " + reseau, "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (RobotException ex) {
                handleRobotException(ex);
            }
        });
    }

    private void handleDeconnecter() {
        robot.deconnecter();
        controlPanel.updateRobotInfo();
        JOptionPane.showMessageDialog(window, "Robot déconnecté", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleEnvoyerDonnees() {
        openStringInputDialog("Envoyer des Données", "Données à envoyer", (donnees) -> {
            try {
                robot.envoyerDonnees(donnees);
                controlPanel.updateRobotInfo();
                JOptionPane.showMessageDialog(window, "Données envoyées : " + donnees, "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (RobotException ex) {
                handleRobotException(ex);
            }
        });
    }

    private void handleRobotException(RobotException ex) {
        JOptionPane.showMessageDialog(window, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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

        inputFrame.setLocationRelativeTo(window);
        inputFrame.setVisible(true);
    }

    private void openStringInputDialog(String title, String label, Consumer<String> action) {
        JFrame inputFrame = new JFrame(title);
        inputFrame.setSize(400, 150);
        inputFrame.setLayout(new GridLayout(3, 2, 5, 5));

        JTextField field = new JTextField();
        inputFrame.add(new JLabel(label + ":"));
        inputFrame.add(field);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(evt -> {
            String input = field.getText().trim();
            if (!input.isEmpty()) {
                action.accept(input);
                inputFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer une valeur valide.");
            }
        });

        inputFrame.add(new JLabel());
        inputFrame.add(okButton);

        inputFrame.setLocationRelativeTo(window);
        inputFrame.setVisible(true);
    }
}