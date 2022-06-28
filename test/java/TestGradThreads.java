import forAll.Fraction;
import nonlinear.gradtable.GradTableDouble;
import nonlinear.gradtable.GradTableFraction;
import gradient.*;
import nonlinear.gradtable.GradTableBigDecimal;
import nonlinear.gradtable.GradTableBigDecimalThreads;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGradThreads {


    @Test
    public void test_threads_sgd() throws InterruptedException {
        // f = x^3+2y^2-3x-4y
        //df/dx =  2*x^2-3  df/dy =4y-4
        //https://matica.org.ua/metodichki-i-knigi-po-matematike/metody-optimizatcii-nekrasova-m-g/6-1-metod-gradientnogo-spuska

        int n = 4;
        DfBigDecimal[] dx = new DfBigDecimal[n];
       // dx[0]=(f, ff)->3*f*f-3;
        dx[0]=(f)->BigDecimal.valueOf(3).multiply(f[0].pow(2)).subtract(BigDecimal.valueOf(3));
        //dx[1]=(f)->4*f-4;
        dx[1]=(f)->BigDecimal.valueOf(4).multiply(f[1]).subtract(BigDecimal.valueOf(4));
        dx[2]=(f)->BigDecimal.valueOf(2).multiply(f[2]).subtract(BigDecimal.valueOf(4));
        // dx[3]=(f)->BigDecimal.valueOf(2).multiply(f[3].pow(2)).subtract(BigDecimal.valueOf(4));
       // dx[4]=(f)->BigDecimal.valueOf(3).multiply(f[4].pow(2)).subtract(BigDecimal.valueOf(5));
       // dx[5]=(f)->BigDecimal.valueOf(7).multiply(f[5]).subtract(BigDecimal.valueOf(5));
       // dx[6]=(f)->BigDecimal.valueOf(7).multiply(f[6]);
      //  dx[7]=(f)->BigDecimal.valueOf(7).multiply(f[7].pow(2));

        BigDecimal[]x = new BigDecimal[n];
        x[0] = new BigDecimal(-0.5);
        x[1] = new BigDecimal(-1);

        for(int i =2; i<n; i++){
            x[i] = new BigDecimal(-0.5);
        }


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
        params.put("iter", new BigDecimal(10.0));
        params.put("epsilon", new BigDecimal(0.01));
        GradTableBigDecimalThreads grad1 = new GradTableBigDecimalThreads(fd, x, dx, params, null);

        long start = System.nanoTime();
         grad1.sgd();
        BigDecimal[] act1 = grad1.getX();
        long end = System.nanoTime();
        long res = end - start;
        System.out.println(res);


        System.out.println(fd.f(act1).doubleValue());

        assertTrue((new BigDecimal(-4).subtract(fd.f(act1)).abs()).compareTo(BigDecimal.valueOf(0.1)) == -1);
        //assertTrue(Math.abs((-4) - f(act4[0], act4[1])) < 0.1);

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

        @Test
        public void test_BigDecimal () throws InterruptedException {

            int n = 13;// количество переменных
            DfBigDecimal[] dx = new DfBigDecimal[n];
            dx[0] = (f) -> BigDecimal.valueOf(3).multiply(f[0].pow(2)).subtract(BigDecimal.valueOf(3));
            dx[1] = (f) -> BigDecimal.valueOf(4).multiply(f[1]).subtract(BigDecimal.valueOf(4));
            dx[2] = (f) -> BigDecimal.valueOf(2).multiply(f[2]).subtract(BigDecimal.valueOf(4));
            dx[3] = (f) -> BigDecimal.valueOf(2).multiply(f[3].pow(2)).subtract(BigDecimal.valueOf(4));
            dx[4] = (f) -> BigDecimal.valueOf(3).multiply(f[4].pow(2)).subtract(BigDecimal.valueOf(5));
            dx[5] = (f) -> BigDecimal.valueOf(7).multiply(f[5]).subtract(BigDecimal.valueOf(5));
            dx[6] = (f) -> BigDecimal.valueOf(7).multiply(f[6]);
            dx[7] = (f) -> BigDecimal.valueOf(10).multiply(f[7].pow(2)).add(f[7].pow(2));
            dx[8] = (f) -> BigDecimal.valueOf(4).multiply(f[8].pow(2)).add(f[8].pow(2));;
            dx[9] = (f) -> BigDecimal.valueOf(20).multiply(f[9].pow(2));
            dx[10] = (f) -> BigDecimal.valueOf(4).multiply(f[10].pow(2)).add(f[10].pow(2).add(BigDecimal.valueOf(8)));
            dx[11] = (f) -> BigDecimal.valueOf(4.9).multiply(f[11].pow(2)).add(f[11].pow(2).add(BigDecimal.valueOf(8)));
            dx[12] = (f) -> BigDecimal.valueOf(4.2).multiply(f[12].pow(2)).add(f[12].pow(2).add(BigDecimal.valueOf(0.128)));
            FunBigDecimal fd = new FunBigDecimal() {
                @Override
                public BigDecimal f(BigDecimal[] x) {
                    return x[0].pow(3).add(x[1].pow(2).multiply(BigDecimal.valueOf(2)).subtract(x[0].multiply(BigDecimal.valueOf(3))).subtract(x[1].multiply(BigDecimal.valueOf(4))));
                    // return 0.0001;
                }
            };




            int nn = 150;
            List<Long>[] results = new List [12];
            int i =8; // чтобы проверить другое количество переменных здесь меняем число

                long[] ress = new long[nn];
                for (int j = 0; j < nn; j++) {
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

                    GradTableBigDecimal grad1 = new GradTableBigDecimal(fd, x, dx1, params, arrParams);
                    params.put("decayRate", new BigDecimal("0.99"));
                    params.put("eps", new BigDecimal("0.0000000001"));
                    long start = System.nanoTime();
                    grad1.sgd(); //чтобы проверить другие методы здесь меняем
                    long end = System.nanoTime();
                    long res = end - start;
                    BigDecimal[] act1 = grad1.getX();
                    ress[j] = (long) Math.ceil((double)res / 10000000);


                }

                System.out.println("-----");
                System.out.println("iter");
                System.out.println(i);
                Arrays.sort(ress);
                results[i-2]= release(ress);

                histogramm(ress, false);

                long[] arr = new long[results[i-2].size()];

                for(int c=0;c<results[i-2].size();c++){
                    arr[c] = results[i-2].get(c);
                }
                LongSummaryStatistics stats =
                        LongStream.of(arr).summaryStatistics();

                Map <Long, Long>map = histogramm(arr, false);
                double m = M(map, arr.length);
                double mPow2= Mpow2(map, arr.length);
                double m2= Math.pow(m, 2);
                double d = mPow2- m2;
                double s = Math.sqrt(d);

                System.out.println("average:");
                System.out.println(m);
                System.out.println("d:");
                System.out.println(d);
                System.out.println("s:");
                System.out.println(s);


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


}
