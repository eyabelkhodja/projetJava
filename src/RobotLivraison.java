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
    public boolean getEnlivraison() {
        return this.enlivraison;
    }
    public void setEnlivraison(boolean enlivraison) {
        this.enlivraison = enlivraison;
    }
    public String getColisActuel() {
        return colisActuel;
    }
    public void setColisActuel(String colisActuel) {
        this.colisActuel = colisActuel;
    }

    public void effectuerTache() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }
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
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }
        double distance = Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));

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

    public void FaireLivraison(int Destx, int Desty) throws RobotException, EnergieInsuffisanteException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }

        if (this.colisActuel != null) {
            if (!this.verifierEnergie(ENERGIE_LIVRAISON)) {
                throw new RobotException("Énergie insuffisante pour la livraison");
            }
            this.ajouterHistorique("Début de livraison du colis : " + this.colisActuel);
            this.deplacer(Destx, Desty);
            this.ajouterHistorique("Livraison terminée à (" + Destx + ", " + Desty + ")");
            this.colisActuel = null;
            this.energie-=RobotLivraison.ENERGIE_LIVRAISON;
            this.enlivraison = false;
        } else if (this.enlivraison) {
            throw new RobotException("Le robot est déjà en livraison");
        } else {
            throw new RobotException("Aucun colis à livrer");
        }
    }

//    public void chargerColis(String destination) throws RobotException {
//        if (!this.enMarche) {
//            throw new RobotException("Le robot est éteint");
//        }
//        if (this.verifierMaintenance()) {
//            throw new RobotException("Maintenance requise");
//        }
//
//        if (this.enlivraison) {
//            throw new RobotException("Le robot est déjà en livraison");
//        }
//        if (this.colisActuel != null) {
//            throw new RobotException("Le robot a déjà un colis");
//        }
//        if (!this.verifierEnergie(ENERGIE_CHARGEMENT)) {
//            throw new RobotException("Énergie insuffisante pour charger un colis");
//        }
//
//        this.colisActuel = "1";
//        this.destination = destination;
//        this.consommerEnergie(ENERGIE_CHARGEMENT);
//        this.enlivraison = true;
//        this.ajouterHistorique("Colis chargé pour la destination : " + destination);
//    }
public void chargerColis(String nomColis) throws RobotException {
    if (!this.enMarche) {
        throw new RobotException("Le robot est éteint");
    }
    if (this.verifierMaintenance()) {
        throw new RobotException("Maintenance requise");
    }

    if (this.enlivraison) {
        throw new RobotException("Le robot est déjà en livraison");
    }
    if (this.colisActuel != null) {
        throw new RobotException("Le robot a déjà un colis");
    }
    if (!this.verifierEnergie(ENERGIE_CHARGEMENT)) {
        throw new RobotException("Énergie insuffisante pour charger un colis");
    }

    this.colisActuel = nomColis;
    this.consommerEnergie(ENERGIE_CHARGEMENT);
    this.enlivraison = true;
    this.ajouterHistorique("Colis chargé pour la destination : " + destination);
}

    public void recycler () throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }
        chargerColis("Centre de recyclage");
        this.ajouterHistorique("Déchet recyclé");
    }

    public void planter () throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }

        chargerColis("Jardin");
        this.ajouterHistorique("Plante plantée");
    }


    public String toString() {
        return "RobotLivraison:" + "\n" +
                "id='" + this.id + "\n" +
                "x=" + x +
                ", y=" + y + "\n" +
                "energie=" + energie + "\n" +
                "heuresUtilisation=" + this.heuresUtilisation + "\n" +
                "enMarche=" + (enMarche?"oui":"non") + "\n" +
                "colisActuel='" + colisActuel + "\n" +
                "destination='" + destination + "\n" +
                "enlivraison=" + (enlivraison?"oui":"non") +
                '}';
    }

}
