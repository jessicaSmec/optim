package nonlinear.methods;

import linear.table.SimTableThreads;
import nonlinear.gradtable.GradTable;
import nonlinear.gradtable.GradTableThreads;

public class Sgd implements GradCommand{
    @Override
    public GradTable method(GradTable table) throws InterruptedException {
        GradTable t = table.getTable();
        t.sgd();
        return t;
    }

    @Override
    public GradTableThreads method(GradTableThreads table) throws InterruptedException {
        GradTableThreads t = table.getTable();
        t.sgd();
        return t;
    }


}
