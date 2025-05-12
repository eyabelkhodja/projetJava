import java.time.LocalDateTime;

public class RobotLivraison extends RobotConnecte {
    private String colisActuel;
    public String destination;
    private Boolean enlivraison;
    public static final int ENERGIE_LIVRAISON = 15;
    public static final int ENERGIE_CHARGEMENT = 5;

    // New variables for resource management and environmental impact
    private int wasteCollected; // Tracks waste collected for recycling
    private int recycledResources; // Seeds produced from recycling
    private int carbonOffset; // Environmental benefit from planting

    public RobotLivraison(String id, int x, int y) {
        super(id, x, y);
        this.colisActuel = null;
        this.destination = null;
        this.enlivraison = false;
        this.wasteCollected = 0;
        this.recycledResources = 0;
        this.carbonOffset = 0;
    }

    public boolean getEnlivraison() {
        return this.enlivraison;
    }

    public void effectuerTache() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot doit être démarré pour effectuer une tâche");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }
        if (this.enlivraison) {
            if (this.destination != null && this.colisActuel != null) {
                int destX = Integer.parseInt(this.destination.split(",")[0]);
                int destY = Integer.parseInt(this.destination.split(",")[1]);
                this.FaireLivraison(destX, destY);
            } else {
                throw new RobotException("Destination ou colis non spécifiés");
            }
        } else {
            if (this.colisActuel == null) {
                ajouterHistorique("En attente de colis");
            } else if (this.destination == null) {
                throw new RobotException("Destination non spécifiée");
            } else {
                int destX = Integer.parseInt(this.destination.split(",")[0]);
                int destY = Integer.parseInt(this.destination.split(",")[1]);
                this.FaireLivraison(destX, destY);
            }
        }
    }

    public void deplacer(int x, int y) throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }

        // Calculate total straight-line distance
        double totalDistance = Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
        ajouterHistorique("Début du déplacement vers (" + x + ", " + y + ") sur une distance totale de " + String.format("%.2f", totalDistance) + " unités");

        // Calculate energy required: 15 energy per 100 distance units
        int totalEnergyRequired = (int) Math.ceil(totalDistance / 100.0) * 15;
        StringBuilder energyDetails = new StringBuilder("Détails du trajet :\n");
        energyDetails.append("Distance totale = ").append(String.format("%.2f", totalDistance))
                .append(" unités, Énergie requise = ").append(totalEnergyRequired).append(" unités\n")
                .append("Énergie disponible : ").append(this.energie).append(" unités");

        // Verify sufficient energy
        if (!this.verifierEnergie(totalEnergyRequired)) {
            throw new RobotException("Énergie insuffisante pour ce déplacement\n" + energyDetails.toString());
        }

        // Simulate segmented movement for logging, but apply energy in one step
        int segments = (int) Math.ceil(totalDistance / 100.0); // Segments for logging only
        double segmentDx = (x - this.x) / (double) segments;
        double segmentDy = (y - this.y) / (double) segments;
        int currentX = this.x;
        int currentY = this.y;

        for (int i = 0; i < segments; i++) {
            if (this.verifierMaintenance()) {
                throw new RobotException("Maintenance requise pendant le déplacement");
            }

            int nextX, nextY;
            if (i == segments - 1) {
                nextX = x;
                nextY = y;
            } else {
                nextX = (int) (currentX + segmentDx);
                nextY = (int) (currentY + segmentDy);
            }

            double segmentDist = Math.sqrt(Math.pow(currentX - nextX, 2) + Math.pow(currentY - nextY, 2));
            this.ajouterHistorique("Segment " + (i + 1) + "/" + segments + " : déplacé vers (" + nextX + ", " + nextY + ") sur " + String.format("%.2f", segmentDist) + " unités");

            currentX = nextX;
            currentY = nextY;
        }

        // Apply energy consumption and update position in one step
        this.consommerEnergie(totalEnergyRequired);
        this.heuresUtilisation += (int) Math.ceil(totalDistance / 10);
        this.x = x;
        this.y = y;

        this.ajouterHistorique("Déplacement terminé vers (" + x + ", " + y + ") avec " + totalEnergyRequired + " unités d'énergie consommées");
    }

    public void FaireLivraison(int Destx, int Desty) throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }

        if (this.colisActuel == null) {
            throw new RobotException("Aucun colis à livrer");
        }
        if (!this.verifierEnergie(ENERGIE_LIVRAISON)) {
            throw new RobotException("Énergie insuffisante pour la livraison");
        }

        this.ajouterHistorique("Début de livraison du colis : " + this.colisActuel);
        this.deplacer(Destx, Desty);
        this.destination = Destx + "," + Desty;
        this.ajouterHistorique("Livraison terminée à (" + Destx + ", " + Desty + ")");
        this.colisActuel = null;
        this.enlivraison = false;
    }

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
        this.enlivraison = true;
        this.consommerEnergie(ENERGIE_CHARGEMENT);
        this.ajouterHistorique("Colis chargé : " + nomColis);
    }

    public void recycler() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }

        // Simulate checking for waste at the current location
        // For simplicity, assume waste is present if the robot is at (0, 0) or (50, 50)
        boolean wastePresent = (this.x == 0 && this.y == 400) || (this.x == 300 && this.y == 50);
        if (!wastePresent) {
            throw new RobotException("Aucun déchet à recycler à la position actuelle (" + this.x + ", " + this.y + ")");
        }

        // Collect waste and deliver to recycling center
        this.wasteCollected += 10; // Collect 10 units of waste
        chargerColis("Déchet pour recyclage");
//        this.FaireLivraison(300, 300); // Deliver to recycling center at (100, 100)
        // Convert waste to resources (seeds)
        this.recycledResources += this.wasteCollected / 10; // 1 seed per 10 units of waste
        this.ajouterHistorique("Déchet recyclé : " + this.wasteCollected + " unités de déchet transformées en " + (this.wasteCollected / 10) + " graines");
        this.wasteCollected = 0; // Reset collected waste after recycling
    }

    public void planter() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le robot est éteint");
        }
        if (this.verifierMaintenance()) {
            throw new RobotException("Maintenance requise");
        }

        // Check if the robot has seeds to plant
        if (this.recycledResources <= 0) {
            throw new RobotException("Aucune graine disponible pour planter (recyclez des déchets pour obtenir des graines)");
        }

        // Consume a seed and plant at the garden location
        this.recycledResources -= 1; // Consume 1 seed
        chargerColis("Graine pour planter");// Deliver to garden at (200, 200)

        // Increase environmental impact
        this.carbonOffset += 5; // Each plant offsets 5 units of carbon
        this.ajouterHistorique("Plante plantée : impact environnemental amélioré, offset de carbone +5 (total : " + this.carbonOffset + ")");
    }

    @Override
    public String toString() {
        return "RobotLivraison:\n" +
                "id='" + this.id + "\n" +
                "x=" + x + ", y=" + y + "\n" +
                "energie=" + energie + "\n" +
                "heuresUtilisation=" + this.heuresUtilisation + "\n" +
                "connecté=" + (this.connecte ? "oui" : "non") + "\n" +
                "réseau=" + this.reseauConnecte + "\n" +
                "enMarche=" + (enMarche ? "oui" : "non") + "\n" +
                "colisActuel='" + colisActuel + "\n" +
                "destination='" + destination + "\n" +
                "enlivraison=" + (enlivraison ? "oui" : "non") + "\n" +
                "déchets collectés=" + wasteCollected + "\n" +
                "graines disponibles=" + recycledResources + "\n" +
                "offset de carbone=" + carbonOffset;
    }
}