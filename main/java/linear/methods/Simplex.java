package linear.methods;

import linear.table.SimTableThreads;
import linear.table.SimTable;

public class

Simplex implements SimCommand {
    @Override
     public SimTable method(SimTable simTable)  {
        SimTable t = simTable.getSimTable();
        t.simplex();
        return t;
    }
    @Override
     public SimTableThreads method(SimTableThreads table) throws InterruptedException {
        SimTableThreads t = table.getSimTable();
        t.simplex();
        return t;
    }
}


