import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RobotFrame extends JFrame implements ActionListener {

    private JPanel robotPanel;
    RobotLivraison robot;
    JButton tache, livraison, deplacer, recycler, planter, recharger;

    ImageIcon robotIcon = new ImageIcon("src/robot.png");
    ImageIcon logo = new ImageIcon("src/logo.png");

    JLabel display= new JLabel();

    JPanel image = new JPanel();

    public RobotFrame(RobotLivraison robot) {
        setTitle("Robot Control Panel");
        setLayout(new BorderLayout());
        setIconImage(logo.getImage());

        Image scaledImage = robotIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        display= new JLabel(scaledIcon);
        display.setBounds(robot.x, robot.y, 150, 150);
        image.setPreferredSize(new Dimension(1500, 1000));  // Set the preferred size of the panel
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
                display.setToolTipText("Energie restante: "+robot.energie);
            }
        });

        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        tache = new JButton("Effectuer Tache");
        livraison = new JButton("Livraison");
        deplacer = new JButton("Deplacer");
        recycler = new JButton("Recycler");
        planter = new JButton("Planter");
        recharger = new JButton("Recharger");

        // Register action listeners
        tache.addActionListener(this);
        livraison.addActionListener(this);
        deplacer.addActionListener(this);
        recycler.addActionListener(this);
        planter.addActionListener(this);
        recharger.addActionListener(this);

        buttonPanel.add(tache);
        buttonPanel.add(livraison);
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
                try {
                    robot.effectuerTache();
                } catch (RobotException ex) {
                    if (ex.getMessage().equals("Energie insuffisante")){
                        rechargeDialogue();
                    }
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Livraison":
                try {
                    robot.FaireLivraison(50, 50); // Example coordinates
                } catch (RobotException ex) {
                    if (ex.getMessage().equals("Energie insuffisante")){
                        rechargeDialogue();
                    }
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Deplacer":
                openDeplacerDialog();
                break;
            case "Recycler":
                try {
                    robot.recycler();
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Planter":
                try {
                    robot.planter();
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
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer des entiers valides.");
            } catch (RobotException ex) {
                if (ex.getMessage().equals("Energie insuffisante")){
                    rechargeDialogue();
                }
                JOptionPane.showMessageDialog(inputFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
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
        }
            catch (RobotException ex){
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
