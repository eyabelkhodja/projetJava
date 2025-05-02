import java.util.ArrayList;

public abstract class Robot {
    private String id;
    public int x,y;
    public int energie;
    private int heuresUtilisation;
    public boolean enMarche;
    private ArrayList <String> historiqueActions=new ArrayList();

    public Robot(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = 100;
        this.heuresUtilisation = 0;
        this.enMarche = false;
        this.ajouterHistorique("Robot crée");
    }

    public void ajouterHistorique(String action) {
        this.historiqueActions.add(action);
    }

    public boolean verifierEnergie (int energieRequise) throws EnergieInsuffisanteException {
        if (this.energie>=energieRequise) {
            return true;
        }
        else {
            throw new EnergieInsuffisanteException("Energie insuffisante");
        }
    }

    public boolean verifierMaintenance() throws MaintenanceRequiseException {
        if (this.heuresUtilisation >= 100) {
            throw new MaintenanceRequiseException("Maintenance requise");
        } else {
            return false;
        }
    }

    public void demarrer() throws RobotException {
        if (this.enMarche == false) {
            if (this.energie > 10) {
                this.enMarche = true;
                this.ajouterHistorique("Robot demaré");
            } else {
                throw new RobotException ("Energie insuffisante pour démarrer le robot");
            }
        }
    }

    public void arreter(){
        if (this.enMarche == true) {
            this.enMarche = false;
            this.ajouterHistorique("Robot arreté");
        }
    }

    public void consommerEnergie(int quantite) {
        if (this.energie >= quantite) {
            this.energie -= quantite;
            this.ajouterHistorique("Energie consommée: " + quantite);
        } else {
            this.ajouterHistorique("Energie insuffisante pour la consommation");
        }
    }

    public void recharger(int quantite){
        if (this.energie + quantite <= 100) {
            this.energie += quantite;
            this.ajouterHistorique("Energie rechargée: " + quantite);
        } else {
            this.ajouterHistorique("Energie maximale atteinte");
        }
    }

    public abstract void deplacer(int x, int y) throws RobotException;
    public abstract void effectuerTache() throws RobotException;

    public String getHistorique(){
        String historique = "";
        for (String action : this.historiqueActions) {
            historique += action + "\n";
        }
        return historique;
    }

    public String toString() {
        return "Robot{" +
                "id='" + id + '\'' +
                ", Position :(" + x + y + ")" +
                ", Energie=" + energie +
                ", heuresUtilisation=" + heuresUtilisation;
    }

    public int getHeuresUtilisation (){
        return this.heuresUtilisation;
    }

    public void setHeuresUtilisation(int heuresUtilisation) {
        this.heuresUtilisation = heuresUtilisation;
    }
}
