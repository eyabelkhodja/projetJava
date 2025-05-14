import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private JLabel display;
    private JLabel dechetDisplay1;
    private JLabel dechetDisplay2;
    private JLabel planteDisplay;
    private RobotLivraison robot;

    private ImageIcon robotIcon = new ImageIcon("src/robot.png");
    private ImageIcon dechet = new ImageIcon("src/zebla.png");
    private ImageIcon plante = new ImageIcon("src/plante.png");

    public ImagePanel(RobotLivraison robot) {
        this.robot = robot;

        setPreferredSize(new Dimension(700, 700));
        setLayout(null);
        setBackground(Color.WHITE);
        setupComponents();
    }

    private void setupComponents() {
        // Robot icon
        Image scaledRobotImage = robotIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledRobotIcon = new ImageIcon(scaledRobotImage);

        display = new JLabel(scaledRobotIcon);
        display.setBounds(robot.x, robot.y, 80, 80);
        display.setToolTipText("Energie restante: " + robot.energie);

        // Dechet icons
        Image scaledDechetImage = dechet.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledDechetIcon = new ImageIcon(scaledDechetImage);

        dechetDisplay1 = new JLabel(scaledDechetIcon);
        dechetDisplay1.setBounds(0, 400, 100, 100);
        dechetDisplay1.setToolTipText("Déchet à recycler");

        dechetDisplay2 = new JLabel(scaledDechetIcon);
        dechetDisplay2.setBounds(300, 50, 100, 100);
        dechetDisplay2.setToolTipText("Déchet à recycler");

        // Plante icon
        Image scaledPlanteImage = plante.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledPlanteIcon  = new ImageIcon(scaledPlanteImage);

        planteDisplay = new JLabel(scaledPlanteIcon);
        planteDisplay.setBounds(400, 500, 100, 100);
        planteDisplay.setToolTipText("Plante à arroser");
        planteDisplay.setVisible(false); // Initially hidden

        // Add components
        add(dechetDisplay1);
        add(dechetDisplay2);
        add(planteDisplay);
        add(display);

        // Add labels
        JLabel recyclingLabel = new JLabel("<html><center><b>Centre de Recyclage</b></center></html>");
        recyclingLabel.setBounds(200, 300, 200, 40);
        recyclingLabel.setForeground(new Color(34, 139, 34));
        recyclingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(recyclingLabel);

        JLabel gardenLabel = new JLabel("<html><center><b>Jardin</b></center></html>");
        gardenLabel.setBounds(350, 500, 200, 40);
        gardenLabel.setForeground(new Color(34, 139, 34));
        gardenLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(gardenLabel);
    }

    public void updateRobotPosition() {
        display.setBounds(robot.x, robot.y, 80, 80);
        revalidate();
        repaint();
    }

    public void removeDechet(int x, int y) {
        if (x == 300 && y == 50) {
            remove(dechetDisplay2);
        } else if (x == 0 && y == 400) {
            remove(dechetDisplay1);
        }
        revalidate();
        repaint();
    }

    public void showPlante() {
        planteDisplay.setVisible(true);
        revalidate();
        repaint();
    }
}
