import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        RobotLivraison WALLE= new RobotLivraison("WALLE", 0, 0);
        try {
            WALLE.demarrer();
//            WALLE.deplacer(10, 20);
//            WALLE.consommerEnergie(20);
//            WALLE.arreter();
            Window frame = new Window(WALLE);
        } catch (RobotException e) {
            System.out.println(e.getMessage());
        }
    }

}