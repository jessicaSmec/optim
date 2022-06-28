



import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.*;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.util.FastMath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SimplexOptimizerNelderMeadTest<LinearProblem> {

    @Test
    public void testBoundsUnsupported() {
        //Моя версия для max cx : Ax <= b, x >= 0. Может быть, это не "привет, мир", но я надеюсь, что это поможет:

        double[][] tab = {{1, -2, 1}, {-2, 1, 2}, {2, 1, 6}, {-3, -1, 0}};
        double[] c = {3, 1};
        double[] b = {1, 2, 6};
        double[][] A = {{1, -2}, {-2, 1}, {2, 1}};
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, 0);
        Collection<LinearConstraint> constraints = new
                ArrayList<LinearConstraint>();
        for(int i=0; i<A.length; i++) {
            double[] Av = new double[A[i].length];
            for(int j=0; j<A[i].length; j++) {
                Av[j] = A[i][j];
            }
            constraints.add(new LinearConstraint(Av, Relationship.LEQ, b[i]));
        }

        SimplexSolver solver = new SimplexSolver();
        PointValuePair optSolution = solver.optimize(new MaxIter(100), f, new
                        LinearConstraintSet(constraints),
                GoalType.MAXIMIZE, new
                        NonNegativeConstraint(true));


        double[] solution;
        solution = optSolution.getPoint();


    }

@Test
void grad(){
        /*
    final NonLinearConjugateGradientOptimizer.Formula form = NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES;
    final ConvergenceChecker<PointValuePair> checker = new SimpleValueChecker(1e-4, 1e-8);
    final GradientMultivariateOptimizer optim;
    optim = new NonLinearConjugateGradientOptimizer(form, checker);

    final OptimizationData[] optimData = new OptimizationData[4];
    optimData[0] = new org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction(f);
    MultivariateVectorFunction df;
    optimData[1] = new ObjectiveFunctionGradient(df);
    optimData[2] = new MaxEval(1000);
    double[] init;
    optimData[3] = new InitialGuess(init);

    final PointValuePair pair = optim.optimize(optimData);
    final double[] optimum = pair.getPoint();
    final double value = pair.getValue();\

         */
    
}



    @Test
    public void testMinimize1() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final FourExtrema fourExtrema = new FourExtrema();

        final PointValuePair optimum
                = optimizer.optimize(new MaxEval(100),
                new ObjectiveFunction(fourExtrema),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { -3, 0 }),
                new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        assertEquals(fourExtrema.xM, optimum.getPoint()[0], 2e-7);
        assertEquals(fourExtrema.yP, optimum.getPoint()[1], 2e-5);
        assertEquals(fourExtrema.valueXmYp, optimum.getValue(), 6e-12);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMinimize2() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final FourExtrema fourExtrema = new FourExtrema();

        final PointValuePair optimum
                = optimizer.optimize(new MaxEval(100),
                new ObjectiveFunction(fourExtrema),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 1, 0 }),
                new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        assertEquals(fourExtrema.xP, optimum.getPoint()[0], 5e-6);
        assertEquals(fourExtrema.yM, optimum.getPoint()[1], 6e-6);
        assertEquals(fourExtrema.valueXpYm, optimum.getValue(), 1e-11);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMaximize1() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final FourExtrema fourExtrema = new FourExtrema();

        final PointValuePair optimum
                = optimizer.optimize(new MaxEval(100),
                new ObjectiveFunction(fourExtrema),
                GoalType.MAXIMIZE,
                new InitialGuess(new double[] { -3, 0 }),
                new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        assertEquals(fourExtrema.xM, optimum.getPoint()[0], 1e-5);
        assertEquals(fourExtrema.yM, optimum.getPoint()[1], 3e-6);
        assertEquals(fourExtrema.valueXmYm, optimum.getValue(), 3e-12);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testMaximize2() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        final FourExtrema fourExtrema = new FourExtrema();

        final PointValuePair optimum
                = optimizer.optimize(new MaxEval(100),
                new ObjectiveFunction(fourExtrema),
                GoalType.MAXIMIZE,
                new InitialGuess(new double[] { 1, 0 }),
                new NelderMeadSimplex(new double[] { 0.2, 0.2 }));
        assertEquals(fourExtrema.xP, optimum.getPoint()[0], 4e-6);
        assertEquals(fourExtrema.yP, optimum.getPoint()[1], 5e-6);
        assertEquals(fourExtrema.valueXpYp, optimum.getValue(), 7e-12);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 90);

        // Check that the number of iterations is updated (MATH-949).
        assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testRosenbrock() {

        Rosenbrock rosenbrock = new Rosenbrock();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum
                = optimizer.optimize(new MaxEval(100),
                new ObjectiveFunction(rosenbrock),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { -1.2, 1 }),
                new NelderMeadSimplex(new double[][] {
                        { -1.2,  1 },
                        { 0.9, 1.2 },
                        {  3.5, -2.3 } }));

        assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
        assertTrue(optimizer.getEvaluations() > 40);
        assertTrue(optimizer.getEvaluations() < 50);
        assertTrue(optimum.getValue() < 8e-4);
    }

    @Test
    public void testPowell() {
        Powell powell = new Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(200),
                        new ObjectiveFunction(powell),
                        GoalType.MINIMIZE,
                        new InitialGuess(new double[] { 3, -1, 0, 1 }),
                        new NelderMeadSimplex(4));
        assertEquals(powell.getCount(), optimizer.getEvaluations());
        assertTrue(optimizer.getEvaluations() > 110);
        assertTrue(optimizer.getEvaluations() < 130);
        assertTrue(optimum.getValue() < 2e-3);
    }

    @Test
    public void testLeastSquares1() {
        final RealMatrix factors
                = new Array2DRowRealMatrix(new double[][] {
                { 1, 0 },
                { 0, 1 }
        }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorFunction() {
            public double[] value(double[] variables) {
                return factors.operate(variables);
            }
        }, new double[] { 2.0, -3.0 });
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(200),
                        new ObjectiveFunction(ls),
                        GoalType.MINIMIZE,
                        new InitialGuess(new double[] { 10, 10 }),
                        new NelderMeadSimplex(2));
        assertEquals( 2, optimum.getPointRef()[0], 3e-5);
        assertEquals(-3, optimum.getPointRef()[1], 4e-4);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 80);
        assertTrue(optimum.getValue() < 1.0e-6);
    }

    @Test
    public void testLeastSquares2() {
        final RealMatrix factors
                = new Array2DRowRealMatrix(new double[][] {
                { 1, 0 },
                { 0, 1 }
        }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorFunction() {
            public double[] value(double[] variables) {
                return factors.operate(variables);
            }
        }, new double[] { 2, -3 }, new double[] { 10, 0.1 });
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(200),
                        new ObjectiveFunction(ls),
                        GoalType.MINIMIZE,
                        new InitialGuess(new double[] { 10, 10 }),
                        new NelderMeadSimplex(2));
        assertEquals( 2, optimum.getPointRef()[0], 5e-5);
        assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 80);
        assertTrue(optimum.getValue() < 1e-6);
    }

    @Test
    public void testLeastSquares3() {
        final RealMatrix factors =
                new Array2DRowRealMatrix(new double[][] {
                        { 1, 0 },
                        { 0, 1 }
                }, false);
        LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorFunction() {
            public double[] value(double[] variables) {
                return factors.operate(variables);
            }
        }, new double[] { 2, -3 }, new Array2DRowRealMatrix(new double [][] {
                { 1, 1.2 }, { 1.2, 2 }
        }));
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-6);
        PointValuePair optimum
                = optimizer.optimize(new MaxEval(200),
                new ObjectiveFunction(ls),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 10, 10 }),
                new NelderMeadSimplex(2));
        assertEquals( 2, optimum.getPointRef()[0], 2e-3);
        assertEquals(-3, optimum.getPointRef()[1], 8e-4);
        assertTrue(optimizer.getEvaluations() > 60);
        assertTrue(optimizer.getEvaluations() < 80);
        assertTrue(optimum.getValue() < 1e-6);
    }
/*
    @Test
    public void testMaxIterations() {
        Powell powell = new Powell();
        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
        optimizer.optimize(new MaxEval(20),
                new ObjectiveFunction(powell),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 3, -1, 0, 1 }),
                new NelderMeadSimplex(4));
    }


 */
    private static class FourExtrema implements MultivariateFunction {
        // The following function has 4 local extrema.
        final double xM = -3.841947088256863675365;
        final double yM = -1.391745200270734924416;
        final double xP =  0.2286682237349059125691;
        final double yP = -yM;
        final double valueXmYm = 0.2373295333134216789769; // Local maximum.
        final double valueXmYp = -valueXmYm; // Local minimum.
        final double valueXpYm = -0.7290400707055187115322; // Global minimum.
        final double valueXpYp = -valueXpYm; // Global maximum.

        public double value(double[] variables) {
            final double x = variables[0];
            final double y = variables[1];
            return (x == 0 || y == 0) ? 0 :
                    FastMath.atan(x) * FastMath.atan(x + 2) * FastMath.atan(y) * FastMath.atan(y) / (x * y);
        }
    }

    private static class Rosenbrock implements MultivariateFunction {
        private int count;

        public Rosenbrock() {
            count = 0;
        }

        public double value(double[] x) {
            ++count;
            double a = x[1] - x[0] * x[0];
            double b = 1.0 - x[0];
            return 100 * a * a + b * b;
        }

        public int getCount() {
            return count;
        }
    }

    private static class Powell implements MultivariateFunction {
        private int count;

        public Powell() {
            count = 0;
        }

        public double value(double[] x) {
            ++count;
            double a = x[0] + 10 * x[1];
            double b = x[2] - x[3];
            double c = x[1] - 2 * x[2];
            double d = x[0] - x[3];
            return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
        }

        public int getCount() {
            return count;
        }
    }
}
