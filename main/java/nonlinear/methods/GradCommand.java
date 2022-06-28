package nonlinear.methods;

import linear.table.SimTableThreads;
import nonlinear.gradtable.GradTable;
import nonlinear.gradtable.GradTableThreads;

public interface GradCommand {
    public  GradTable method(GradTable table) throws InterruptedException;
    public GradTableThreads method(GradTableThreads table) throws InterruptedException;

}
