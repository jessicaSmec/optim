package linear.table;

public interface SimTableThreads {
    void artificialBasis() throws InterruptedException;
    void simplex() throws InterruptedException;
    void dualSimplex() throws InterruptedException;
    void gamory() throws InterruptedException;
    SimTableThreads getSimTable() throws InterruptedException;
}
