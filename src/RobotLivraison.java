public class RobotLivraison extends RobotConnecte {
    private String colisActuel;
    private String destination;
    private Boolean enlivraison;
    public static final int ENERGIE_LIVRAISON= 15;
    public static final int ENERGIE_CHARGEMENT= 5;

    public RobotLivraison (String id, int x, int y) {
        super(id, x, y);
        this.colisActuel= null;
        this.destination= null;
        this.enlivraison= false;
    }

    public void effectuerTache() throws RobotException {
        if (this.enlivraison == false) {
            if (this.colisActuel != null && this.destination != null) {
                this.ajouterHistorique("Robot en livraison de " + this.colisActuel + " vers " + this.destination);
                this.consommerEnergie(ENERGIE_LIVRAISON);
                this.enlivraison = true;
                this.ajouterHistorique("Colis livré : " + this.colisActuel);
                this.colisActuel = null;
                this.destination = null;
                this.enlivraison = false;
            } else {
                throw new RobotException("Colis ou destination non spécifiés");
            }
        } else {
            throw new RobotException("Robot déjà en livraison");
        }
    }

    public void deplacer(int x, int y) throws RobotException{
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));

        if (distance > 100) {
            throw new RobotException("Distance supérieure à 100 unités");
        }

        if (this.verifierEnergie((int) Math.ceil(distance * 0.3)) && !this.verifierMaintenance()) {
            this.consommerEnergie((int) Math.ceil(distance * 0.3));
            this.setHeuresUtilisation(this.getHeuresUtilisation()+ (int) Math.ceil(distance / 10));
            this.x = x;
            this.y = y;
            this.ajouterHistorique("Robot déplacé vers (" + x + ", " + y + ") sur une distance de " + distance + " unités");
        }
    }

}
