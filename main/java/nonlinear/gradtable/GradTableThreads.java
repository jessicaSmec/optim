package nonlinear.gradtable;

public interface GradTableThreads {
    void sgd() throws InterruptedException ;
    void momentum() throws InterruptedException ;
    void rmsprop() throws InterruptedException ;
    void adam() throws InterruptedException ;
            GradTableThreads getTable();
}
