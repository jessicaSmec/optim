package linear.methods;

import linear.table.SimTable;
import linear.table.SimTableThreads;

public interface SimCommand {

    SimTable method(SimTable table);
    public SimTableThreads method(SimTableThreads table) throws InterruptedException;


}
