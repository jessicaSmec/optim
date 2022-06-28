package nonlinear.methods;

import nonlinear.gradtable.GradTable;
import nonlinear.gradtable.GradTableThreads;

public class Rmsprop implements  GradCommand{
    @Override
    public GradTable method(GradTable table) {
        GradTable t = table.getTable();
        t.rmsprop();
        return t;
    }

    @Override
    public GradTableThreads method(GradTableThreads table) throws InterruptedException {
        GradTableThreads t = table.getTable();
        t.rmsprop();
        return t;
    }
}
