import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        RobotLivraison WALLE= new RobotLivraison("WALLE", 0, 0);
        try {
            WALLE.demarrer();
            Window frame = new Window(WALLE);
        } catch (RobotException e) {
            System.out.println(e.getMessage());
        }
    }

}