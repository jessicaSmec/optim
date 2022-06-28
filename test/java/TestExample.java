import forAll.Fraction;
import gradient.*;
import linear.methods.*;
import linear.table.SimTableBigDecimal;
import linear.table.SimTableDouble;
import linear.table.SimTableDoubleThreads;
import linear.table.SimTableFraction;
import nonlinear.gradtable.*;
import nonlinear.methods.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestExample {


    @Test
    public void testGradDouble() throws InterruptedException {
        // f = x^3+2y^2-3x-4y
        //df/dx =  2*x^2-3  df/dy =4y-4
        //https://matica.org.ua/metodichki-i-knigi-po-matematike/metody-optimizatcii-nekrasova-m-g/6-1-metod-gradientnogo-spuska


        GradCommand sgd = new Sgd();
        GradCommand momentum = new Momentum();
        GradCommand rmsprop  = new Rmsprop();
        GradCommand adam  = new Adam();
        // инициализируем градиент dx

        Df[] dx = new Df[2];
        dx[0]=(f)->3*f[0]*f[0]-3;
        dx[1]=(f)->4*f[0]-4;
        double[]x = new double[]{-0.5, -1};
        // инициализируем изначальную функцию
        FunDouble fd = new FunDouble() {
            @Override
            public double f(double[] x) {
                return x[0]*x[0]*x[0]+2*x[1]*x[1]-3*x[0]-4*x[1];
            }
        };

        // инициализируем параметры
        Map<String,Double> params = new HashMap<>();
        params.put("lr", 0.1);
        params.put("iter", 10.0);
        params.put("epsilon", 0.01);
        params.put("eps", 0.00000001);

        //sgd
        GradTableDouble grad1 = new GradTableDouble(x, dx, params);
       // GradTableDoubleThreads grad1 = new GradTableDoubleThreads(x, dx, params); // для многопоточного выполнения
        GradTableDouble res1 = (GradTableDouble) sgd.method(grad1);
        //GradTableDoubleThreads res1 = (GradTableDoubleThreads) sgd.method(grad1);
        double[] act1= res1.getX();
        params.put("mu", 0.3);
        Map<String,double[]> arrParams = new HashMap<>();
        double[] m = new double[x.length];
        double[] v = new double[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = 0;
            v[i] = 0;
        }
        arrParams.put("m", m);
        arrParams.put("v", v);
        params.put("iter", 10.0);

        //momentum
        GradTableDouble grad2 = new GradTableDouble(x, dx, params, arrParams);
      //  GradTableDoubleThreads grad2 = new GradTableDoubleThreads(x, dx, params, arrParams);

        GradTableDouble res2 = (GradTableDouble) momentum.method(grad2);
        //GradTableDoubleThreads res2 = (GradTableDoubleThreads) momentum.method(grad2);
        double[] act2= res2.getX();
        params.put("decay_rate", 0.99);


        // rmsprop
        GradTableDouble grad3 = new GradTableDouble(x, dx, params);
        //GradTableDoubleThreads grad3 = new GradTableDoubleThreads(x, dx, params);
        rmsprop.method(grad3);
        GradTableDouble  res3= (GradTableDouble) rmsprop.method(grad3);
       // GradTableDoubleThreads  res3= (GradTableDoubleThreads) rmsprop.method(grad1);
        double []act3= res3.getX();

        params.put("beta1", 0.2);
        params.put("beta2", 0.3);
        params.put("iter", 20.0);

         GradTableDouble grad4 = new GradTableDouble(x, dx, params, arrParams);
        //GradTableDoubleThreads grad4 = new GradTableDoubleThreads(x, dx, params, arrParams);

        GradTableDouble  res4= (GradTableDouble) adam.method(grad4);
        //GradTableDoubleThreads  res4= (GradTableDoubleThreads) adam.method(grad4);
        double []act4= res4.getX();

    }


    @Test
    public void testGradBD() throws InterruptedException {
        // f = x^3+2y^2-3x-4y
        //df/dx =  2*x^2-3  df/dy =4y-4
        //https://matica.org.ua/metodichki-i-knigi-po-matematike/metody-optimizatcii-nekrasova-m-g/6-1-metod-gradientnogo-spuska

        GradCommand sgd = new Sgd();

        DfBigDecimal[] dx = new DfBigDecimal[2];
        dx[0]=(f)-> BigDecimal.valueOf(3).multiply(f[0].pow(2)).subtract(BigDecimal.valueOf(3));
        dx[1]=(f)->BigDecimal.valueOf(4).multiply(f[1]).subtract(BigDecimal.valueOf(4));
        BigDecimal[]x = new BigDecimal[]{new BigDecimal(-0.5),new BigDecimal(-1)};


        FunBigDecimal fd = new FunBigDecimal() {
            @Override
            public BigDecimal f(BigDecimal[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));
            }
        };
        Map<String, BigDecimal> params = new HashMap<>();
        params.put("lr", new BigDecimal(0.1));
        params.put("stop", new BigDecimal(0.01)); // параметр остановки(что при такой разницы уже останавливаем алгоритм)
        params.put("iter", new BigDecimal(10.0));
        params.put("epsilon", new BigDecimal(0.01));
        params.put("decayRate", new BigDecimal("0.99"));
        params.put("beta1", new BigDecimal("0.2"));
        params.put("beta2", new BigDecimal("0.3"));
        params.put("eps", new BigDecimal("0.00000001"));
        params.put("decayRate", new BigDecimal("0.99"));
        params.put("mu", new BigDecimal("0.5"));

        Map<String, BigDecimal[]> arrParams = new HashMap<>();
        BigDecimal[] m = new BigDecimal[2];
        BigDecimal[] v = new BigDecimal[2];
        for (int z = 0; z < 2; z++) {
            m[z] = BigDecimal.valueOf(0);
            v[z] = BigDecimal.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);

        GradTableBigDecimal grad1 = new GradTableBigDecimal(fd, x, dx, params, arrParams);
        GradTableBigDecimal res = (GradTableBigDecimal) sgd.method(grad1);

        //аналогично для других методов

    }



    @Test
    public void testGradFract() throws InterruptedException {
        // f = x^3+2y^2-3x-4y
        //df/dx =  2*x^2-3  df/dy =4y-4
        //https://matica.org.ua/metodichki-i-knigi-po-matematike/metody-optimizatcii-nekrasova-m-g/6-1-metod-gradientnogo-spuska

        GradCommand sgd = new Sgd();

        DfFraction[] dx = new DfFraction[2];
        dx[0]=(f)-> Fraction.valueOf(3).multiply(f[0].pow(2)).subtract(Fraction.valueOf(3));
        dx[1]=(f)->Fraction.valueOf(4).multiply(f[1]).subtract(Fraction.valueOf(4));
        Fraction[]x = new Fraction[]{Fraction.valueOf(-0.5),Fraction.valueOf(-1)};


        FunFraction fd = new FunFraction() {
            @Override
            public Fraction f(Fraction[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(Fraction.valueOf(2)).subtract(x[0].multiply(Fraction.valueOf(3))).subtract(x[1].multiply(Fraction.valueOf(4))));
            }
        };
        Map<String, Fraction> params = new HashMap<>();
        params.put("lr", Fraction.valueOf(0.1));
        params.put("stop", Fraction.valueOf(0.01)); // параметр остановки(что при такой разницы уже останавливаем алгоритм)
        params.put("iter", Fraction.valueOf(10.0));
        params.put("epsilon", Fraction.valueOf(0.01));
        params.put("decayRate", Fraction.valueOf(0.99));
        params.put("beta1", Fraction.valueOf(0.2));
        params.put("beta2", Fraction.valueOf(0.3));
        params.put("eps", Fraction.valueOf(0.00000001));
        params.put("decayRate", Fraction.valueOf(0.99));
        params.put("mu", Fraction.valueOf(0.5));

        Map<String, Fraction[]> arrParams = new HashMap<>();
        Fraction[] m = new Fraction[2];
        Fraction[] v = new Fraction[2];
        for (int z = 0; z < 2; z++) {
            m[z] = Fraction.valueOf(0);
            v[z] = Fraction.valueOf(0);
        }
        arrParams.put("m", m);
        arrParams.put("v", v);

        GradTableFraction fr1 = new GradTableFraction(fd, x, dx, params, arrParams);
        GradTableFraction res = (GradTableFraction) sgd.method(fr1);

        //аналогично для других методов


    }



    @Test
    public  void testSimAllSimplex(){

        SimCommand sim = new Simplex();

        double[][] tabDouble = {{1, -2, 1},{-2, 1, 2},{2, 1, 6}, {-3, -1, 0}};

        BigDecimal[][] tabBD = new BigDecimal[tabDouble.length][tabDouble[0].length];
        Fraction[][] tabFract = new Fraction[tabDouble.length][tabDouble[0].length];

        for(int i=0; i< tabDouble .length; i++) {
            for (int j = 0; j < tabDouble [0].length; j++) {
                tabBD[i][j] = BigDecimal.valueOf(tabDouble[i][j]);
                tabFract[i][j] = Fraction.valueOf(tabDouble[i][j]);
            }
        }

        String [] base = {"u1", "u2", "u3"};
        String [] free = {"x1", "x2"};

        SimTableDouble tableDouble = new SimTableDouble(tabDouble, base, free);
        SimTableBigDecimal tableBigDecimal = new SimTableBigDecimal(tabBD, base, free);
        SimTableFraction tableFraction = new SimTableFraction(tabFract, base, free);

        SimTableDouble resD= (SimTableDouble) sim.method(tableDouble);
        SimTableBigDecimal resBD = (SimTableBigDecimal) sim.method(tableBigDecimal);
        SimTableFraction resFract = (SimTableFraction) sim.method(tableFraction);
    }


    @Test
    public void testArtificialBasisAll(){
        SimCommand art = new ArtificialBasis();
        double[][] tab = {{1, -1, 0, 4}, {1, 1, -1, 2}, {-1, 1, 0, 3}, {-1, -1, 1, -2}};
        Map<String, Integer> goal = new HashMap<>();
        goal.put("x1", -2);
        goal.put("x2", 1);
        String[] base = {"u1", "w1", "u3"};
        String[] free = {"x1", "x2", "u2"};
        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];
        Fraction[][] arrfr = new Fraction[tab.length][tab[0].length];
        for(int i=0; i< arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                arrfr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }

        SimTableDouble tableD = new SimTableDouble(tab, base, free);
        SimTableBigDecimal tableBD = new SimTableBigDecimal(arr,  base, free);
        SimTableFraction tableFr = new SimTableFraction(arrfr, base, free);
        tableD.setGoal(goal);
        tableBD.setGoal(goal);
        tableFr.setGoal(goal);
        SimTableDouble resD= (SimTableDouble) art.method(tableD);
        SimTableBigDecimal resBD= (SimTableBigDecimal) art.method(tableBD);
        SimTableFraction resFr= (SimTableFraction) art.method(tableFr);
    }


    @Test
    public  void testdualSimplexAll() {
        SimCommand dual = new DualSimplex();
        double[][] tab = {{-3,-1, -2, -6},{-1, -1, -1, -4},{1, -3, 1, -4}, {1, -8, -3, 0}};
        String [] base = {"u1", "u2", "u3"};
        String [] free = {"x1", "x2", "x3"};
        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];
        Fraction[][] arrfr = new Fraction[tab.length][tab[0].length];
        for(int i=0; i< arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                arrfr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }

        SimTableDouble tableD = new SimTableDouble(tab, base, free);
        SimTableBigDecimal tableBD = new SimTableBigDecimal(arr,  base, free);
        SimTableFraction tableFr = new SimTableFraction(arrfr, base, free);


        SimTableDouble resD= (SimTableDouble) dual.method(tableD);
        SimTableBigDecimal resBD= (SimTableBigDecimal) dual.method(tableBD);
        SimTableFraction resFr= (SimTableFraction) dual.method(tableFr);

    }


    @Test
    public  void testSimAllGamory(){

       // SimCommand sim = new Simplex();
        //SimCommand dual = new DualSimplex();
        //SimCommand art = new ArtificialBasis();
        SimCommand gam = new Gamory();

        double[][] tabDouble = {{1, 4, 14},{2, 3, 12},{-2, -3, 0}};

        BigDecimal[][] tabBD = new BigDecimal[tabDouble.length][tabDouble[0].length];
        Fraction[][] tabFract = new Fraction[tabDouble.length][tabDouble[0].length];

        for(int i=0; i< tabDouble .length; i++) {
            for (int j = 0; j < tabDouble [0].length; j++) {
                tabBD[i][j] = BigDecimal.valueOf(tabDouble[i][j]);
                tabFract[i][j] = Fraction.valueOf(tabDouble[i][j]);
            }
        }

        String [] base = {"u1", "u2", "u3"};
        String [] free = {"x1", "x2"};

        SimTableDouble tableDouble = new SimTableDouble(tabDouble, base, free);
        SimTableBigDecimal tableBigDecimal = new SimTableBigDecimal(tabBD, base, free);
        SimTableFraction tableFraction = new SimTableFraction(tabFract, base, free);

        SimTableDouble resD= (SimTableDouble) gam.method(tableDouble);
        SimTableBigDecimal resBD = (SimTableBigDecimal) gam.method(tableBigDecimal);
        SimTableFraction resFract = (SimTableFraction) gam.method(tableFraction);

        //остальные методы вызываются аналогично
    }

    @Test
    public void testGradresearch () throws InterruptedException {


        GradCommand sgd = new Sgd();
        GradCommand momentum = new Momentum();
        GradCommand rmsprop = new Rmsprop();
        GradCommand adam = new Adam();


        int i = 7;// количество переменных
        // чтобы проверить другое количество переменных здесь меняем число

        DfBigDecimal[] dx = new DfBigDecimal[i];
        dx[0] = (f) -> BigDecimal.valueOf(3).multiply(f[0].pow(2)).subtract(BigDecimal.valueOf(3));
        dx[1] = (f) -> BigDecimal.valueOf(4).multiply(f[1]).subtract(BigDecimal.valueOf(4));
        dx[2] = (f) -> BigDecimal.valueOf(2).multiply(f[2]).subtract(BigDecimal.valueOf(4));
        dx[3] = (f) -> BigDecimal.valueOf(2).multiply(f[3].pow(2)).subtract(BigDecimal.valueOf(4));
        dx[4] = (f) -> BigDecimal.valueOf(3).multiply(f[4].pow(2)).subtract(BigDecimal.valueOf(5));
        dx[5] = (f) -> BigDecimal.valueOf(7).multiply(f[5]).subtract(BigDecimal.valueOf(5));
        dx[6] = (f) -> BigDecimal.valueOf(7).multiply(f[6]);
       // dx[7] = (f) -> BigDecimal.valueOf(10).multiply(f[7].pow(2)).add(f[7].pow(2));
       // dx[8] = (f) -> BigDecimal.valueOf(4).multiply(f[8].pow(2)).add(f[8].pow(2));;
     //   dx[9] = (f) -> BigDecimal.valueOf(20).multiply(f[9].pow(2));
        //dx[10] = (f) -> BigDecimal.valueOf(4).multiply(f[10].pow(2)).add(f[10].pow(2).add(BigDecimal.valueOf(8)));
        //dx[11] = (f) -> BigDecimal.valueOf(4.9).multiply(f[11].pow(2)).add(f[11].pow(2).add(BigDecimal.valueOf(8)));
        //dx[12] = (f) -> BigDecimal.valueOf(4.2).multiply(f[12].pow(2)).add(f[12].pow(2).add(BigDecimal.valueOf(0.128)));


        FunBigDecimal fd = new FunBigDecimal() {
            @Override
            public BigDecimal f(BigDecimal[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));
            }
        };


        int iter = 150;
        List<Long> results = new ArrayList<> ();

        // стандартная инициализация данных
        long[] ress = new long[iter];
        for (int j = 0; j < iter; j++) {
            DfBigDecimal[] dx1 = new DfBigDecimal[i];

            dx1[0] = (f) -> new BigDecimal("3").multiply(f[0].pow(2)).subtract(new BigDecimal("3"));
            dx1[1] = (f) -> new BigDecimal("4").multiply(f[1]).subtract(new BigDecimal("4"));
            BigDecimal[] x = new BigDecimal[i];
            x[0] = new BigDecimal("-0.5");
            x[1] = new BigDecimal("-1");

            for (int l = 2; l < i; l++) {
                x[l] = new BigDecimal("-0.5");
                dx1[l] = dx[l];
            }


            Map<String, BigDecimal> params = new HashMap<>();
            params.put("lr", new BigDecimal("0.1"));
            params.put("stop", null);
            params.put("iter", new BigDecimal("10.0"));
            params.put("decayRate", new BigDecimal("0.99"));
            params.put("beta1", new BigDecimal("0.2"));
            params.put("beta2", new BigDecimal("0.3"));
            params.put("eps", new BigDecimal("0.00000001"));
            Map<String, BigDecimal[]> arrParams = new HashMap<>();
            BigDecimal[] m = new BigDecimal[i];
            BigDecimal[] v = new BigDecimal[i];
            for (int z = 0; z < i; z++) {
                m[z] = BigDecimal.valueOf(0);
                v[z] = BigDecimal.valueOf(0);
            }
            arrParams.put("m", m);
            arrParams.put("v", v);
            params.put("mu", BigDecimal.valueOf(0.5));
            params.put("decayRate", new BigDecimal("0.99"));
            params.put("eps", new BigDecimal("0.0000000001"));


            GradTableBigDecimal grad1 = new GradTableBigDecimal(fd, x, dx1, params, arrParams);
            //GradTableBigDecimalThreads grad1 = new GradTableBigDecimalThreads(fd, x, dx1, params, arrParams);

            // замер времени
            long start = System.nanoTime();
            rmsprop.method(grad1); //чтобы проверить другие методы здесь меняем
            long end = System.nanoTime();
            long res = end - start;
            ress[j] = (long) Math.ceil((double)res / 100000); //здесь настраиваем нужную точность
            // при делении на 1 будут показаны наносекунды, на 1000 - микросекунды и.т.д.
        }


        Arrays.sort(ress);
        results= release(ress); // удаляем выбросы

        histogramm(ress, false); // true - вывести на экран распределение

        long[] arr = new long[results.size()];

        for(int c=0;c<results.size();c++){
            arr[c] = results.get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();

        Map <Long, Long>map = histogramm(arr, false); // распределение без выбросов
        double m = M(map, arr.length); // математическое ожидание
        double mPow2= Mpow2(map, arr.length); // математическое ожидание от квадрата переменных
        double m2= Math.pow(m, 2); // математическое ожидание в квадрате
        double d = mPow2- m2; //дисперсия
        double s = Math.sqrt(d); // среднеквадратичное отклонение

        // для вывода результатов

        System.out.println("average:");
        System.out.println(m);
        System.out.println("d:");
        System.out.println(d);
        System.out.println("s:");
        System.out.println(s);

    }


    @Test
    public void testGradresearchFr () throws InterruptedException {


        GradCommand sgd = new Sgd();
        GradCommand momentum = new Momentum();
        GradCommand rmsprop = new Rmsprop();
        GradCommand adam = new Adam();


        int i = 3;// количество переменных
        // чтобы проверить другое количество переменных здесь меняем число

        DfFraction[] dx = new DfFraction[i];
        dx[0] = (f) -> Fraction.valueOf(3).multiply(f[0].pow(2)).subtract(Fraction.valueOf(3));
        dx[1] = (f) -> Fraction.valueOf(4).multiply(f[1]).subtract(Fraction.valueOf(4));
        dx[2] = (f) -> Fraction.valueOf(2).multiply(f[2]).subtract(Fraction.valueOf(4));
      //  dx[3] = (f) -> Fraction.valueOf(2).multiply(f[3].pow(2)).subtract(Fraction.valueOf(4));
        // dx[4] = (f) -> Fraction.valueOf(3).multiply(f[4].pow(2)).subtract(Fraction.valueOf(5));
       // dx[5] = (f) -> Fraction.valueOf(7).multiply(f[5]).subtract(Fraction.valueOf(5));
       // dx[6] = (f) -> Fraction.valueOf(7).multiply(f[6]);
       // dx[7] = (f) -> Fraction.valueOf(10).multiply(f[7].pow(2)).add(f[7].pow(2));
       // dx[8] = (f) -> Fraction.valueOf(4).multiply(f[8].pow(2)).add(f[8].pow(2));;
       // dx[9] = (f) -> Fraction.valueOf(20).multiply(f[9].pow(2));
       // dx[10] = (f) -> Fraction.valueOf(4).multiply(f[10].pow(2)).add(f[10].pow(2).add(Fraction.valueOf(8)));
      //  dx[11] = (f) -> Fraction.valueOf(4.9).multiply(f[11].pow(2)).add(f[11].pow(2).add(Fraction.valueOf(8)));
       // dx[12] = (f) -> Fraction.valueOf(4.2).multiply(f[12].pow(2)).add(f[12].pow(2).add(Fraction.valueOf(0.128)));


        FunFraction fd = new FunFraction() {
            @Override
            public Fraction f(Fraction[] x) {
                return x[0].pow(3).add(x[1].pow(2).multiply(Fraction.valueOf(2)).subtract(x[0].multiply(Fraction.valueOf(3))).subtract(x[1].multiply(Fraction.valueOf(4))));
            }
        };


        int iter = 3;
        List<Long> results = new ArrayList<> ();

        // стандартная инициализация данных
        long[] ress = new long[iter];
        for (int j = 0; j < iter; j++) {
            DfFraction[] dx1 = new DfFraction[i];

            dx1[0] = (f) -> Fraction.valueOf(3).multiply(f[0].pow(2)).subtract(Fraction.valueOf(3));
            dx1[1] = (f) -> Fraction.valueOf(4).multiply(f[1]).subtract(Fraction.valueOf(4));
            Fraction[] x = new Fraction[i];
            x[0] = Fraction.valueOf(-0.5);
            x[1] = Fraction.valueOf(-1);

            for (int l = 2; l < i; l++) {
                x[l] = Fraction.valueOf(-0.5);
                dx1[l] = dx[l];
            }


            Map<String, Fraction> params = new HashMap<>();
            params.put("lr", Fraction.valueOf(0.1));
            params.put("stop", null);
            params.put("iter", Fraction.valueOf(5.0));
            /*
            params.put("decayRate", new BigDecimal("0.99"));
            params.put("beta1", new BigDecimal("0.2"));
            params.put("beta2", new BigDecimal("0.3"));
            params.put("eps", new BigDecimal("0.00000001"));
            Map<String, BigDecimal[]> arrParams = new HashMap<>();
            BigDecimal[] m = new BigDecimal[i];
            BigDecimal[] v = new BigDecimal[i];
            for (int z = 0; z < i; z++) {
                m[z] = BigDecimal.valueOf(0);
                v[z] = BigDecimal.valueOf(0);
            }
            arrParams.put("m", m);
            arrParams.put("v", v);
            params.put("mu", BigDecimal.valueOf(0.5));
            params.put("decayRate", new BigDecimal("0.99"));
            params.put("eps", new BigDecimal("0.001"));


             */

           // GradTableFraction grad1 = new GradTableFraction(fd, x, dx1, params, null);
            GradTableFractionThreads grad1 = new GradTableFractionThreads(fd, x, dx1, params, null);

            // замер времени
            long start = System.nanoTime();
            sgd.method(grad1); //чтобы проверить другие методы здесь меняем
            long end = System.nanoTime();
            long res = end - start;
            ress[j] = (long) Math.ceil((double)res / 100000000); //здесь настраиваем нужную точность
            // при делении на 1 будут показаны наносекунды, на 1000 - микросекунды и.т.д.
        }


        Arrays.sort(ress);
        results= release(ress); // удаляем выбросы

        histogramm(ress, false); // true - вывести на экран распределение

        long[] arr = new long[results.size()];

        for(int c=0;c<results.size();c++){
            arr[c] = results.get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();

        Map <Long, Long>map = histogramm(arr, false); // распределение без выбросов
        double m = M(map, arr.length); // математическое ожидание
        double mPow2= Mpow2(map, arr.length); // математическое ожидание от квадрата переменных
        double m2= Math.pow(m, 2); // математическое ожидание в квадрате
        double d = mPow2- m2; //дисперсия
        double s = Math.sqrt(d); // среднеквадратичное отклонение

        // для вывода результатов

        System.out.println("average:");
        System.out.println(m);
        System.out.println("d:");
        System.out.println(d);
        System.out.println("s:");
        System.out.println(s);

    }



    double s(long []arr,double average ){
        double sum=0;

        for (int i =0; i<arr.length; i++){
            sum+=Math.abs(Math.pow(arr[i]-average, 2));
        }
        sum/=(arr.length-1);
        return Math.sqrt(sum);
    }

    List release(long[] arr){

        boolean norm = false;
        List<Long> list = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }

        while (!norm) {
            norm = true;

            int size = list.size();
            double check = 1 / (2 * (double) size);

            long[] array = new long[size];

            for (int i = 0; i < size; i++) {
                array[i] = list.get(i);
            }

            double av = LongStream.of((long[]) array).summaryStatistics().getAverage();
            double s = s(array, av);
            double resMax = Math.abs(list.get(size - 1) - av) / s;
            double resMin = Math.abs(list.get(0) - av) / s;

            if (1 - ErrorFunction.erf(resMax) < check) {
                list.remove(size - 1);
                norm = false;
            }

            if (1 - ErrorFunction.erf(resMin) < check) {
                list.remove(0);
                norm = false;
            }
        }
        ;
        return  list;
    }

    public Map<Long, Long> histogramm(long[] ress, boolean print){
        Map histogram1 = LongStream.of(ress)
                .boxed()
                .collect(Collectors.groupingBy(
                        e -> e,
                        Collectors.counting()
                ));

        Map<Long, Long> histogram = new TreeMap<Long, Long>(histogram1);
        if(print) {
            System.out.println("histogramm");
            for (Object data : histogram.keySet()) {
                System.out.println(data);
            }

            System.out.println("freg:");
            for (Object data : histogram.keySet()) {
                System.out.println(histogram.get(data));
            }
        }
        return histogram;
    }


    public  double M(Map<Long, Long> map, int size){
        double sum =0;
        for(Long data : map.keySet()){
            sum+= (double)data*map.get(data)/size;
        }
        return sum;
    }


    public  double Mpow2(Map<Long, Long> map, int size){
        double sum =0;
        for(Long data : map.keySet()){
            sum+= Math.pow(data, 2)*map.get(data)/size;
        }
        return sum;
    }


    @Test
    public void test_Data () {
        // сравнение точности
        double n1D = 10.0;
        double n2D=0.0825;
        double resD = n1D*n2D;

        BigDecimal n1BD = new BigDecimal("10");
        BigDecimal n2BD = new BigDecimal("0.0825");
        BigDecimal resBD = n1BD.multiply(n2BD);

        System.out.println(resD);
        System.out.println(resBD);

    }




}
