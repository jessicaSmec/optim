package nonlinear.gradtable;

public interface GradTable {
    void sgd() throws InterruptedException;
    void momentum();
    void rmsprop();
    void adam();
    GradTable getTable();

}
