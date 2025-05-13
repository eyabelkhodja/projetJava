import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private RobotLivraison robot;
    private ImageIcon logo = new ImageIcon("src/logo.png");

    public Window(RobotLivraison robot) {
        this.robot = robot;
        // Set up the main, window
        setTitle("Robot Control Panel");
        setLayout(new BorderLayout());
        setIconImage(logo.getImage());
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set up the panels
        ImagePanel imagePanel = new ImagePanel(robot);
        ControlPanel controlPanel = new ControlPanel(robot);
        Actions actions = new Actions(this, robot, imagePanel, controlPanel);

        add(imagePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        JPanel robotPanel = new JPanel();
        robotPanel.setBackground(Color.LIGHT_GRAY);
        add(robotPanel, BorderLayout.SOUTH);

        // Add action listeners to buttons
        for (JButton button : controlPanel.getButtons()) {
            button.addActionListener(actions);
        }


        setVisible(true);
        showWelcomeDialog();
    }
    private void showWelcomeDialog() {
        String message = "<html><body style='width: 400px; padding: 10px;'>" +
                "<h2>Bienvenue dans le Panneau de Contrôle du Robot</h2>" +
                "<p>Cette application vous permet de contrôler un robot de livraison et de recyclage. Voici un aperçu de l'interface :</p>" +
                "<h3>Carte (Panneau de Gauche)</h3>" +
                "<ul>" +
                "<li><b>Icône du Robot</b> : Indique la position actuelle du robot (x, y). Survolez pour voir son niveau d'énergie.</li>" +
                "<li><b>Icônes de Déchets</b> : Situées à (0, 400) et (300, 50), marquées par des icônes zebla, indiquant les déchets recyclables.</li>" +
                "<li><b>Centre de Recyclage</b> : Une étiquette à (200, 300) marquant le centre de recyclage.</li>" +
                "<li><b>Jardin</b> : Une étiquette à (350, 500) marquant l'emplacement pour planter des graines.</li>" +
                "</ul>" +
                "<h3>Panneau de Contrôle (Panneau de Droite)</h3>" +
                "<ul>" +
                "<li><b>marche_arret</b> : Démarre ou arrête le robot. Vert lorsqu'il est en marche, rouge lorsqu'il est arrêté.</li>" +
                "<li><b>Effectuer Tache</b> : Exécute une tâche (par exemple, livrer un colis ou en charger un nouveau). <i> Charger un colis consomme 5 unités d'énergie. </i></li>" +
                "<li><b>Deplacer</b> : Déplace le robot vers les coordonnées (x, y) spécifiées. <i>Consomme 15 unités d'énergie tous les 100 unités de distance parcourue.</i></li>" +
                "<li><b>Recycler</b> : Recycle les déchets à la position du robot, produisant une graine.</li>" +
                "<li><b>Planter</b> : Plante une graine pour compenser le carbone, si des graines sont disponibles.</li>" +
                "<li><b>Recharger</b> : Recharge l'énergie du robot d'un pourcentage spécifié.</li>" +
                "<li><b>Maintenance</b> : Vérifie si une maintenance est requise.</li>" +
                "<li><b>Connecter</b> : Connecte le robot à un réseau spécifié. <i> Consomme 5 unités d'énergie. </i></li>" +
                "<li><b>Déconnecter</b> : Déconnecte le robot du réseau actuel.</li>" +
                "<li><b>Envoyer Données</b> : Envoie des données via le réseau connecté. <i> Consomme 3 unités d'énergie. </i></li>" +
                "</ul>" +
                "<h3>État du Robot (Bas du Panneau de Droite)</h3>" +
                "<p>La zone de texte affiche l'état actuel du robot, y compris l'énergie, les graines disponibles, l'état de livraison et l'état de connexion.</p>" +
                "<p>Cliquez sur <b>OK</b> pour commencer à utiliser l'application !</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, message, "Bienvenue dans le Panneau de Contrôle du Robot", JOptionPane.INFORMATION_MESSAGE);
    }

}