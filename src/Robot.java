import java.util.ArrayList;

public abstract class Robot {
    private String id;
    private int x,y;
    public int energie;
    private int heuresUtilisation;
    public boolean enMarche;
    private ArrayList <String> historiqueActions=new ArrayList();

    public Robot(String id, int x, int y, int energie) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = energie;
        this.heuresUtilisation = 0;
        this.enMarche = false;
        this.ajouterHistorique("Robot crée");
    }

    public void ajouterHistorique(String action) {
        this.historiqueActions.add(action);
    }

    public boolean verifierEnergie (int energieRequise){
        if (this.energie>=energieRequise) {
            return true;
        }
        else {
           return false; //ajouter l'exception EnergieInsuffisanteException
        }
    }


}
