public class RobotLivraison extends RobotConnecte {
    private String colisActuel;
    private String destination;
    private Boolean enlivraison;
    public static final int ENERGIE_LIVRAISON= 15;
    public static final int ENERGIE_CHARGEMENT= 5;

    public RobotLivraison (String id, int x, int y) {
        super(id, x, y, 100);
        this.colisActuel= null;
        this.destination= null;
        this.enlivraison= false;
    }

}
