package linear.methods;

import linear.table.SimTableThreads;
import linear.table.SimTable;
import nonlinear.gradtable.GradTable;

public class ArtificialBasis implements SimCommand {
    @Override
    public SimTable method(SimTable simTable){
        SimTable t = simTable.getSimTable();
        t.artificialBasis();
        return t;
    }
    @Override
    public SimTableThreads method(SimTableThreads table) throws InterruptedException {
        SimTableThreads t = table.getSimTable();
        t.artificialBasis();
        return t;
    }
}
