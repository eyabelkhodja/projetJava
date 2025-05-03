import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window extends JFrame implements ActionListener {

    private JPanel robotPanel;
    private JPanel imagePanel;
    private JLabel display;

    private RobotLivraison robot;

    private JButton marche_arret,tache, livraison, deplacer, recycler, planter, recharger;

    private ImageIcon robotIcon = new ImageIcon("src/robot.png");
    private ImageIcon logo = new ImageIcon("src/logo.png");

    public Window(RobotLivraison robot) {
        this.robot = robot;

        setTitle("Robot Control Panel");
        setLayout(new BorderLayout());
        setIconImage(logo.getImage());
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupRobotImage();
        setupButtons();

        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void setupRobotImage() {
        imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(1500, 1000));
        imagePanel.setLayout(null);

        Image scaledImage = robotIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        display = new JLabel(scaledIcon);
        display.setBounds(robot.x, robot.y, 150, 150);
        display.setToolTipText("Energie restante: "+robot.energie);

        display.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                display.setToolTipText("Energie restante: "+robot.energie);
            }
        });

        imagePanel.add(display);
        add(imagePanel, BorderLayout.WEST);
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        tache = new JButton("Effectuer Tache");
        marche_arret = new JButton("Marche/Arret");
        updateMarcheArretButtonColor();
        livraison = new JButton("Livraison");
        deplacer = new JButton("Deplacer");
        recycler = new JButton("Recycler");
        planter = new JButton("Planter");
        recharger = new JButton("Recharger");

        tache.addActionListener(this);
        marche_arret.addActionListener(this);
        livraison.addActionListener(this);
        deplacer.addActionListener(this);
        recycler.addActionListener(this);
        planter.addActionListener(this);
        recharger.addActionListener(this);

        buttonPanel.add(marche_arret);
        buttonPanel.add(tache);
        buttonPanel.add(livraison);
        buttonPanel.add(deplacer);
        buttonPanel.add(recycler);
        buttonPanel.add(planter);
        buttonPanel.add(recharger);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateRobotPosition() {
        display.setBounds(robot.x, robot.y, 150, 150);
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        switch (action) {
            case "Marche/Arret":
                robot.marchearret();
                updateMarcheArretButtonColor();
                break;
            case "Effectuer Tache":
                handleEffectuerTache();
                break;
            case "Livraison":
                handleLivraison();
                break;
            case "Deplacer":
                openInputDialog("Déplacer le Robot", "X", "Y", (x, y) -> {
                    try {
                        robot.deplacer(x, y);
                        updateRobotPosition();
                    } catch (RobotException ex) {
                        handleRobotException(ex);
                    }
                });
                break;
            case "Recycler":
                handleRecycler();
                break;
            case "Planter":
                handlePlanter();
                break;
            case "Recharger":
                openInputDialog("Recharger le Robot", "Valeur", null, (val, unused) -> {
                    try {
                        robot.recharger(val);
                    } catch (RobotException ex) {
                        handleRobotException(ex);
                    }
                });
                break;
        }
    }
    private void updateMarcheArretButtonColor() {
        if (robot.enMarche) {
            marche_arret.setBackground(Color.GREEN);
            marche_arret.setForeground(Color.BLACK);
        } else {
            marche_arret.setBackground(Color.RED);
            marche_arret.setForeground(Color.WHITE);
        }
    }
    private void handleEffectuerTache() {
        try {
            robot.effectuerTache();
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handleLivraison() {
        try {
            robot.FaireLivraison(50, 50);
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handleRecycler() {
        try {
            robot.recycler();
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handlePlanter() {
        try {
            robot.planter();
        } catch (RobotException ex) {
            handleRobotException(ex);
        }
    }

    private void handleRobotException(RobotException ex) {
        if ("Energie insuffisante".equals(ex.getMessage())) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Interface fonctionnelle pour appliquer des actions sur les entrées
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

