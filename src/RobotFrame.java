import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RobotFrame extends JFrame {

    private JPanel robotPanel;

    public RobotFrame(RobotLivraison robot) {
        setTitle("Robot Control Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton tache = new JButton("Effectuer Tache");
        JButton livraison = new JButton("Livraison");
        JButton deplacer = new JButton("Deplacer");

        tache.addActionListener(e -> {
            try {
                robot.effectuerTache();
            } catch (RobotException ex) {
                ex.printStackTrace();
            }
        });

        livraison.addActionListener(e -> {
            try {
                robot.FaireLivraison(50, 50);
            } catch (RobotException ex) {
                ex.printStackTrace();
            }
        });

        deplacer.addActionListener(e -> {
            try {
                robot.deplacer(10, 20);
            } catch (RobotException ex) {
                ex.printStackTrace();
            }
        });


        buttonPanel.add(tache);
        buttonPanel.add(livraison);
        buttonPanel.add(deplacer);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

}
