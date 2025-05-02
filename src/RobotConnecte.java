public abstract class RobotConnecte extends Robot implements Connectable{
    Boolean connecte;

    public RobotConnecte(String id, int x, int y, int energie) {
        super(id, x, y, energie);
    }

}
