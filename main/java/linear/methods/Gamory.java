package linear.methods;

import linear.table.SimTable;
import linear.table.SimTableThreads;

public class Gamory implements SimCommand {
    @Override
    public SimTable method(SimTable simTable) {
        SimTable t = simTable.getSimTable();
        t.gamory();
        return t;
    }

    @Override
    public SimTableThreads method(SimTableThreads table) throws InterruptedException {
        SimTableThreads t = table.getSimTable();
        t.gamory();
        return t;
    }
}
