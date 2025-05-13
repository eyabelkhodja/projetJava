import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private JButton[] buttons;
    private JTextArea robotInfo;
    private RobotLivraison robot;
    private String[] buttonNames = {
            "marche_arret", "Effectuer Tache", "Deplacer",
            "Recycler", "Planter", "Recharger", "Maintenance",
            "Connecter", "Déconnecter", "Envoyer Données"};

    public ControlPanel(RobotLivraison robot) {
        this.robot = robot;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 700));
        setupButtons();
        // Initialize bouton marche_arret color
        updateMarcheArretButtonColor(robot.enMarche);
    }

    private void setupButtons() {
        buttons = new JButton[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(Box.createVerticalStrut(10));
            add(button);
            buttons[i] = button;
        }
        //Robot info text
        robotInfo = new JTextArea(robot.toString());
        robotInfo.setEditable(false);
        robotInfo.setLineWrap(true);
        robotInfo.setWrapStyleWord(true);
        robotInfo.setBackground(getBackground());
        robotInfo.setPreferredSize(new Dimension(150, 300));
        robotInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
        robotInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(robotInfo);
    }

    public void updateRobotInfo() {
        robotInfo.setText(robot.toString());
    }

    public void updateMarcheArretButtonColor(boolean enMarche) {
        JButton button = buttons[0];
        if (enMarche) {
            button.setBackground(Color.GREEN);
            button.setForeground(Color.BLACK);
        } else {
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
        }
    }

    public JButton[] getButtons() {
        return buttons;
    }
}