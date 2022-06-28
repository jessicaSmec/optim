import forAll.Fraction;
import gradient.*;
import gradient.FunDouble;
import nonlinear.gradtable.GradTableBigDecimal;
import nonlinear.gradtable.GradTableDouble;
import nonlinear.gradtable.GradTableFraction;
import nonlinear.gradtable.GradTableFractionThreads;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGradTable {


   double fTest1(double x []){ return x[0]*x[0]*x[0] - x[0];}


  /*

    public double f(double x1, double x2){
        return x1*x1*x1+2*x2*x2-3*x1-4*x2;
    }
    public double f1(double[] x){
        return x[0]*x[0]*x[0]+2*x[1]*x[1]-3*x[0]-4*x[1];
    }
    @Test
    public void test2_forAll() {
        // f = x^3+2y^2-3x-4y
        //df/dx =  2*x^2-3  df/dy =4y-4
        //https://matica.org.ua/metodichki-i-knigi-po-matematike/metody-optimizatcii-nekrasova-m-g/6-1-metod-gradientnogo-spuska


        Df[] dx = new Df[2];
        dx[0]=(f)->3*f[0]*f[0]-3;
        dx[1]=(f)->4*f[0]-4;
        double[]x = new double[]{-0.5, -1};
        FunDouble fd = new FunDouble() {
            @Override
            public double f(Double[] x) {
                double part11 = Math.pow(x[0], 3);
                double part1 = part11+2*Math.pow(x[1],2);
                double part2 =3*x[0]+4*x[1];
                double res = part1-part2;
                //return x[0]*x[0]*x[0]+2*x[1]*x[1]-3*x[0]-4*x[1];
                return (Double)res;
            }
        };
        Map<String,Double> params = new HashMap<>();
        params.put("lr", 0.1);
       // params.put("iter", 10.0);
        params.put("iter", 10.0);
        params.put("epsilon", 0.01);
        GradTableDouble grad1 = new GradTableDouble(x, dx, params);
        grad1.setfd(fd);
        grad1.sgd();
        Double[] act1 = grad1.getX();
        params.put("mu", 0.5);
        params.put("v", 0.01);
        GradTableDouble grad2 = new GradTableDouble(x, dx, params);
        grad2.momentum();
        Double []act2= grad2.getX();
        params.put("decay_rate", 0.99);
        params.put("eps", 1e-7);
       // GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        grad3.setfd(fd);
        double a = grad3.df(1);
        System.out.println(a);
        // если изменить массу то доскочит до другого минимума
        grad3.rmsprop();;
        double []act3= grad3.getX();;
        //params.put("beta1", 0.99);
        //params.put("beta2", 0.999);
        //params.put("iter", 2000.0);
        params.put("beta1", 0.2);
        params.put("beta2", 0.3);
        params.put("iter", 20.0);
        Map<String,double[]> arrParams = new HashMap<>();
        // if не подали то сделать самой
        double[] m = new double[x.length];
        double[] v = new double[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = 0;
            v[i] = 0;
        }
        arrParams.put("m", m);
        arrParams.put("v", v);

        GradTableDouble grad4 = new GradTableDouble(x, dx, params, arrParams);
        grad4.setfd(fd);
        grad4.adam();
        double []act4= grad4.getX();;


        System.out.println(f(act1[0], act1[1]));
        assertTrue(Math.abs((-4) - f(act1[0], act1[1])) < 0.1);
        assertTrue(Math.abs((-4) - f(act2[0], act2[1])) < 0.1);
        assertTrue(Math.abs((-4) - f(act3[0], act3[1])) < 0.1);
       assertTrue(Math.abs((-4) - f(act4[0], act4[1])) < 0.1);

    }



    @Test
    public void test_rsmpop_BD() {

        Df[] dx = new Df[2];
        dx[0]=(f)->3*f[0]-3;
        dx[1]=(f)->4*f[0]-4;
        BigDecimal[]x = new BigDecimal[]{new BigDecimal(-0.5),new BigDecimal(-1)};



        FunBigDecimal fd = new FunBigDecimal() {
            @Override
            public BigDecimal f(BigDecimal[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));
                // return 0.0001;
            }
        };


        Map<String, BigDecimal> params = new HashMap<>();
        params.put("lr", new BigDecimal(0.1));
        //params.put("stop", new BigDecimal(0.01));
        params.put("iter", new BigDecimal(10.0));
        params.put("epsilon", new BigDecimal(0.01));


        params.put("decayRate", BigDecimal.valueOf(0.99));
        params.put("eps", BigDecimal.valueOf(1e-8));
        GradTableBigDecimal grad3 = new GradTableBigDecimal(fd, x, null, params, null);
        //GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        // если изменить массу то доскочит до другого минимума
        //BigDecimal[] act3= grad3.rmsprop();;
        System.out.println(grad3.df(1).doubleValue());

        //System.out.println(fd.f(act3).doubleValue());


    }




    @Test
    public void test_adam_BD() {

        DfBigDecimal[] dx = new DfBigDecimal[2];
        // dx[0]=(f, ff)->3*f*f-3;
        dx[0]=(f)->BigDecimal.valueOf(3).multiply(f[0].pow(2)).subtract(BigDecimal.valueOf(3));
        //dx[1]=(f)->4*f-4;
        dx[1]=(f)->BigDecimal.valueOf(4).multiply(f[1]).subtract(BigDecimal.valueOf(4));
        BigDecimal[]x = new BigDecimal[]{new BigDecimal(-0.5),new BigDecimal(-1)};



        FunBigDecimal fd = new FunBigDecimal() {
            @Override
            public BigDecimal f(BigDecimal[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));
                // return 0.0001;
            }
        };


        Map<String, BigDecimal> params = new HashMap<>();
        params.put("lr", new BigDecimal(0.1));
      //  params.put("stop", new BigDecimal(0.01));
        params.put("iter", new BigDecimal(5.0));
        params.put("epsilon", new BigDecimal(0.01));


        params.put("decayRate", BigDecimal.valueOf(0.99));
        params.put("eps", BigDecimal.valueOf(1e-8));
        params.put("beta1", BigDecimal.valueOf(0.2));
        params.put("beta2", BigDecimal.valueOf(0.3));

        Map<String,BigDecimal[]> arrParams = new HashMap<>();
        // if не подали то сделать самой
        BigDecimal[] m = new BigDecimal[x.length];
        BigDecimal[] v = new BigDecimal[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = BigDecimal.valueOf(0);
            v[i] = BigDecimal.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);


        GradTableBigDecimal grad3 = new GradTableBigDecimal(fd, x, dx, params, arrParams);
        //GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        // если изменить массу то доскочит до другого минимума
        grad3.sgd();
        BigDecimal[] act3= grad3.getX();;
        System.out.println(fd.f(act3).doubleValue());


    }



    public BigDecimal fun(BigDecimal[] x){
       return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));

    }

    @Test
    public void test3_forAll_BD() {
        // f = x^3+2y^2-3x-4y
        //df/dx =  2*x^2-3  df/dy =4y-4
        //https://matica.org.ua/metodichki-i-knigi-po-matematike/metody-optimizatcii-nekrasova-m-g/6-1-metod-gradientnogo-spuska


        DfBigDecimal[] dx = new DfBigDecimal[2];
        // dx[0]=(f, ff)->3*f*f-3;
        dx[0]=(f)->BigDecimal.valueOf(3).multiply(f[0].pow(2)).subtract(BigDecimal.valueOf(3));
        //dx[1]=(f)->4*f-4;
        dx[1]=(f)->BigDecimal.valueOf(4).multiply(f[1]).subtract(BigDecimal.valueOf(4));
        BigDecimal[]x = new BigDecimal[]{new BigDecimal(-0.5),new BigDecimal(-1)};


        FunBigDecimal fd = new FunBigDecimal() {
            @Override
            public BigDecimal f(BigDecimal[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));
                // return 0.0001;
            }
        };
        Map<String, BigDecimal> params = new HashMap<>();
        params.put("lr", new BigDecimal(0.1));
        params.put("stop", new BigDecimal(0.01));
        params.put("iter", new BigDecimal(10.0));
        params.put("epsilon", new BigDecimal(0.01));
        GradTableBigDecimal grad1 = new GradTableBigDecimal(fd, x, dx, params, null);
        //BigDecimal[] act1 = grad1.sgd();
        params.put("mu", BigDecimal.valueOf(0.5));
        params.put("v", BigDecimal.valueOf(0.01));
        GradTableBigDecimal grad2 = new GradTableBigDecimal(fd, x, dx, params, null);
       // GradTableDouble grad2 = new GradTableDouble(x, dx, params);
        //BigDecimal []act2= grad2.momentum();
        params.put("decayRate", BigDecimal.valueOf(0.99));
        params.put("eps", BigDecimal.valueOf(1e-8));
        params.put("iter", new BigDecimal(15.0));
        GradTableBigDecimal grad3 = new GradTableBigDecimal(fd, x, dx, params, null);
        //GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        // если изменить массу то доскочит до другого минимума
        grad3.rmsprop();
        BigDecimal[] act3= grad3.getX();;

        params.put("beta1", BigDecimal.valueOf(0.2));
        params.put("beta2", BigDecimal.valueOf(0.3));

        Map<String,BigDecimal[]> arrParams = new HashMap<>();
        // if не подали то сделать самой
         BigDecimal[] m = new BigDecimal[x.length];
        BigDecimal[] v = new BigDecimal[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = BigDecimal.valueOf(0);
            v[i] = BigDecimal.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);

        GradTableBigDecimal grad4 = new GradTableBigDecimal(fd, x, dx, params, arrParams);

       // GradTableDouble grad4 = new GradTableDouble(x, dx, params, arrParams);

        //BigDecimal[] act4= grad4.adam();;

       // System.out.println(f(act1[0], act1[1]));
      //  BigDecimal a = new BigDecimal(-4).subtract(fd.f(act1)).abs();
        //System.out.println(a.doubleValue());
       //assertTrue((new BigDecimal(-4).subtract(fd.f(act1)).abs()).compareTo(BigDecimal.valueOf(0.1)) == -1);
      //  assertTrue((new BigDecimal(-4).subtract(fd.f(act2)).abs()).compareTo(BigDecimal.valueOf(0.1)) == -1);
       // assertTrue((new BigDecimal(-4).subtract(fd.f(act3)).abs()).compareTo(BigDecimal.valueOf(0.1)) == -1);
        System.out.println(fd.f(act3).doubleValue());
       // System.out.println(fd.f(act4).doubleValue());
        //(Math.abs((-4) - f(act2[0], act2[1])) < 0.1);
       // assertTrue(Math.abs((-4) - f(act3[0], act3[1])) < 0.1);
        //assertTrue(Math.abs((-4) - f(act4[0], act4[1])) < 0.1);

    }




    @Test
    public void test_Fraction() throws InterruptedException {



        DfFraction[] dx = new DfFraction[2];
        dx[0]=(f)->Fraction.valueOf(3).multiply(f[0].pow(2)).subtract(Fraction.valueOf(3));
        dx[1]=(f)->Fraction.valueOf(4).multiply(f[1]).subtract(Fraction.valueOf(4));

        Df[] dx1 = new Df[2];
        dx1[0]=(f)->3*f[0]*f[0]-3;
        dx1[1]=(f)->4*f[1]-4;
        //Fraction[]x = new Fraction[]{Fraction.valueOf(-0.5),Fraction.valueOf(-1)};
        Fraction[]x = new Fraction[]{Fraction.valueOf(-0.5),Fraction.valueOf(-1)};




        FunFraction fd = new FunFraction() {
            @Override
            public Fraction f(Fraction[] x) {
                //x[0]*x[0]*x[0]+2*x[1]*x[1]-3*x[0]-4*x[1]
                //Fraction x0_pow3 = x[0].pow(3);
                //Fraction sum = Fraction.valueOf(2).multiply(x[1].pow(2));
                //Fraction part1 = x0_pow3.add(sum);
                //Fraction part2 = Fraction.valueOf(3).multiply(x[0]).add(Fraction.valueOf(4).multiply(x[1]));
                //Fraction res = part1.subtract(part2);

                //double part1 = Math.pow(x[0], 3)+2*Math.pow(x[1],2);
                Fraction part11 = x[0].pow(3);
                Fraction part1 = part11.add(Fraction.valueOf(2).multiply(x[1].pow(2)));
                //double part2 =3*x[0]+4*x[1];
                Fraction part2 = Fraction.valueOf(3).multiply(x[0]).add(Fraction.valueOf(4).multiply(x[1]));
                Fraction res = part1.subtract(part2);
                return res;


        }};




        Map<String, Fraction> params = new HashMap<>();
        params.put("lr", Fraction.valueOf(0.1));
       // params.put("stop", Fraction.valueOf(0.01));
        params.put("iter", Fraction.valueOf(4.0));
        params.put("epsilon", Fraction.valueOf(0.01));


        params.put("decayRate", Fraction.valueOf(0.99));
        //params.put("eps", Fraction.valueOf(1e-8));
        params.put("beta1", Fraction.valueOf(0.2));
        //params.put("beta2", Fraction.valueOf(0.3));

        Map<String,Fraction[]> arrParams = new HashMap<>();
        // if не подали то сделать самой
        Fraction[] m = new Fraction[x.length];
        Fraction[] v = new Fraction[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = Fraction.valueOf(0);
            v[i] = Fraction.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);


        GradTableFraction grad3 = new GradTableFraction(fd, x, dx, params, arrParams);
        //Fraction fr =  grad3.df(1);
        Fraction fr1 =  Fraction.valueOf(2).pow(3);
        //GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        // если изменить массу то доскочит до другого минимума
        Fraction drad = dx[1].df(x);
        grad3.sgd();
        Fraction[] act3= grad3.getX();;
       // System.out.println(fd.f(act3).doubleValue());

        Fraction res = fd.f(act3);
        System.out.println((double)res.getNumenator()/res.getDenominator());
      //  assertTrue((Fraction.valueOf(-4).subtract( res).abs().compareTo(Fraction.valueOf(0.1)) == -1));


    }


    @Test
    public void test_Fraction_momentum() {



        DfFraction[] dx = new DfFraction[2];
        dx[0]=(f)->Fraction.valueOf(3).multiply(f[0].pow(2)).subtract(Fraction.valueOf(3));
        dx[1]=(f)->Fraction.valueOf(4).multiply(f[1]).subtract(Fraction.valueOf(4));
        Fraction[]x = new Fraction[]{Fraction.valueOf(-0.5),Fraction.valueOf(-1)};
        FunFraction fd = new FunFraction() {
            @Override
            public Fraction f(Fraction[] x) {

                Fraction part11 = x[0].pow(3);
                Fraction part1 = part11.add(Fraction.valueOf(2).multiply(x[1].pow(2)));
                Fraction part2 = Fraction.valueOf(3).multiply(x[0]).add(Fraction.valueOf(4).multiply(x[1]));
                Fraction res = part1.subtract(part2);
                return res;
            }};




        Map<String, Fraction> params = new HashMap<>();
        params.put("lr", Fraction.valueOf(0.1));
        params.put("stop", Fraction.valueOf(0.01));
        params.put("iter", Fraction.valueOf(10.0));
        params.put("epsilon", Fraction.valueOf(0.01));


        params.put("decayRate", Fraction.valueOf(0.99));
        //params.put("eps", Fraction.valueOf(1e-8));
        params.put("beta1", Fraction.valueOf(0.2));
        //params.put("beta2", Fraction.valueOf(0.3));

        Map<String,Fraction[]> arrParams = new HashMap<>();
        // if не подали то сделать самой
        Fraction[] m = new Fraction[x.length];
        Fraction[] v = new Fraction[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = Fraction.valueOf(0);
            v[i] = Fraction.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);
        params.put("mu", Fraction.valueOf(0.5));
        params.put("v", Fraction.valueOf(0.01));

        GradTableFraction grad = new GradTableFraction(fd, x, dx, params, arrParams);

        // если изменить массу то доскочит до другого минимума
        Fraction drad = dx[1].df(x);
        grad.momentum();
        Fraction[] act3= grad.getX();;
        // System.out.println(fd.f(act3).doubleValue());

        Fraction res = fd.f(act3);
        assertTrue((Fraction.valueOf(-4).subtract( res).abs().compareTo(Fraction.valueOf(1)) == -1));


    }


    @Test
    public void test_Fraction_adam() {


        DfFraction[] dx = new DfFraction[2];
        dx[0] = (f) -> Fraction.valueOf(3).multiply(f[0].pow(2)).subtract(Fraction.valueOf(3));
        dx[1] = (f) -> Fraction.valueOf(4).multiply(f[1]).subtract(Fraction.valueOf(4));
        Fraction[] x = new Fraction[]{Fraction.valueOf(-0.5), Fraction.valueOf(-1)};
        FunFraction fd = new FunFraction() {
            @Override
            public Fraction f(Fraction[] x) {

                Fraction part11 = x[0].pow(3);
                Fraction part1 = part11.add(Fraction.valueOf(2).multiply(x[1].pow(2)));
                Fraction part2 = Fraction.valueOf(3).multiply(x[0]).add(Fraction.valueOf(4).multiply(x[1]));
                Fraction res = part1.subtract(part2);
                return res;
            }
        };


        Map<String, Fraction> params = new HashMap<>();
        params.put("lr", Fraction.valueOf(0.1));
        // params.put("stop", Fraction.valueOf(0.01));
        params.put("iter", Fraction.valueOf(13.0));
        params.put("epsilon", Fraction.valueOf(0.01));


        params.put("decayRate", Fraction.valueOf(0.99));
        params.put("eps", Fraction.valueOf(0.0001));
        params.put("beta1", Fraction.valueOf(0.2));
        params.put("beta2", Fraction.valueOf(0.3));

        Map<String, Fraction[]> arrParams = new HashMap<>();
        // if не подали то сделать самой
        Fraction[] m = new Fraction[x.length];
        Fraction[] v = new Fraction[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = Fraction.valueOf(0);
            v[i] = Fraction.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);
        params.put("mu", Fraction.valueOf(0.5));
        params.put("v", Fraction.valueOf(0.01));

        GradTableFraction grad = new GradTableFraction(fd, x, dx, params, arrParams);

        // если изменить массу то доскочит до другого минимума
        Fraction drad = dx[1].df(x);
        grad.sgd();
        Fraction[] act3 = grad.getX();
        ;
        // System.out.println(fd.f(act3).doubleValue());

        Fraction res = fd.f(act3);
        assertTrue((Fraction.valueOf(-4).subtract(res).abs().compareTo(Fraction.valueOf(1)) == -1));
    }




   */
}
