import java.time.LocalDateTime;


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
            this.heuresUtilisation+= (int) Math.ceil(distance / 10);
            this.x = x;
            this.y = y;
            this.ajouterHistorique("Robot déplacé vers (" + x + ", " + y + ") sur une distance de " + distance + " unités");
        }
    }

    public void FaireLivraison(int Destx, int Desty) throws RobotException {
        if (this.colisActuel != null && !this.enlivraison) {
            this.enlivraison = true;
            this.ajouterHistorique("Début de livraison du colis : " + this.colisActuel);
            this.deplacer(Destx, Desty);
            this.ajouterHistorique("Livraison terminée à (" + Destx + ", " + Desty + ")");
            this.colisActuel = null;
            this.enlivraison = false;
        } else if (this.enlivraison) {
            throw new RobotException("Le robot est déjà en livraison");
        } else {
            throw new RobotException("Aucun colis à livrer");
        }
    }

    public void chargerColis(String destination) throws RobotException {
        if (this.enlivraison) {
            throw new RobotException("Le robot est déjà en livraison");
        }
        if (this.colisActuel != null) {
            throw new RobotException("Le robot a déjà un colis");
        }
        if (!this.verifierEnergie(ENERGIE_CHARGEMENT)) {
            throw new RobotException("Énergie insuffisante pour charger un colis");
        }

        this.colisActuel = "1"; // Indique qu'un colis est chargé
        this.destination = destination;
        this.consommerEnergie(ENERGIE_CHARGEMENT);
        this.ajouterHistorique("Colis chargé pour la destination : " + destination);
    }

    public void recycler () throws RobotException {
        chargerColis("Centre de recyclage");
        this.ajouterHistorique("Déchet recyclé");
    }

    public void planter () throws RobotException {
        chargerColis("Jardin");
        this.ajouterHistorique("Plante plantée");
    }

    public String toString() {
        return "RobotLivraison{" +
                "colisActuel='" + colisActuel + '\'' +
                ", destination='" + destination + '\'' +
                ", enlivraison=" + enlivraison +
                ", id='" + this.id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", energie=" + energie +
                ", heuresUtilisation=" + this.heuresUtilisation +
                ", enMarche=" + enMarche +
                ", historiqueActions=" + this.historiqueActions +
                '}';
    }

}
