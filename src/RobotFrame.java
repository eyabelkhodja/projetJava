import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RobotFrame extends JFrame implements ActionListener {

    private JPanel robotPanel;
    RobotLivraison robot;
    JButton tache, livraison, deplacer;

    public RobotFrame(RobotLivraison robot) {
        setTitle("Robot Control Panel");
        this.robot = robot;
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        tache = new JButton("Effectuer Tache");
        livraison = new JButton("Livraison");
        deplacer = new JButton("Deplacer");

        // Register action listeners
        tache.addActionListener(this);
        livraison.addActionListener(this);
        deplacer.addActionListener(this);

        buttonPanel.add(tache);
        buttonPanel.add(livraison);
        buttonPanel.add(deplacer);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        switch (action) {
            case "Effectuer Tache":
                try {
                    robot.effectuerTache();
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Livraison":
                try {
                    robot.FaireLivraison(50, 50); // Example coordinates
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Deplacer":
                openDeplacerDialog(); // Now we use your method correctly
                break;

            default:
                JOptionPane.showMessageDialog(this, "Action non reconnue", "Avertissement", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }

    private void openDeplacerDialog() {
        JFrame inputFrame = new JFrame("Déplacer le Robot");
        inputFrame.setSize(300, 150);
        inputFrame.setLayout(new GridLayout(3, 2));

        JLabel xLabel = new JLabel("X:");
        JTextField xField = new JTextField();

        JLabel yLabel = new JLabel("Y:");
        JTextField yField = new JTextField();

        JButton okButton = new JButton("OK");

        okButton.addActionListener(event -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                robot.deplacer(x, y);
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Veuillez entrer des entiers valides.");
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(inputFrame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputFrame.add(xLabel);
        inputFrame.add(xField);
        inputFrame.add(yLabel);
        inputFrame.add(yField);
        inputFrame.add(new JLabel()); // empty cell
        inputFrame.add(okButton);

        inputFrame.setLocationRelativeTo(this);
        inputFrame.setVisible(true);
    }
}
