public abstract class RobotConnecte extends Robot implements Connectable{
    Boolean connecte;
    String reseauConnecte;
    public RobotConnecte(String id, int x, int y, int energie) {
        super(id, x, y, energie);
        this.connecte= false;
        this.reseauConnecte= null;
    }
    public void connecter(String reseau) {
        if (this.connecte == false && this.verifierEnergie(5)) {
            this.connecte = true;
            this.reseauConnecte = reseau;
            this.consommerEnergie(5);
            this.ajouterHistorique("Robot connecté au réseau " + reseau);
        } else {
            this.ajouterHistorique("Robot déjà connecté au réseau " + this.reseauConnecte);
        }
    }
    public void deconnecter() {
        if (this.connecte == true) {
            this.connecte = false;
            this.reseauConnecte = null;
            this.ajouterHistorique("Robot déconnecté");
        } else {
            this.ajouterHistorique("Robot déjà déconnecté");
        }
    }
    public void envoyerDonnees(String donnees) {
        if (this.connecte == true) {
            this.consommerEnergie(3);
            this.ajouterHistorique("Données envoyées : " + donnees);
        } else {
            this.ajouterHistorique("Robot non connecté, données non envoyées");
        }
    }
}

