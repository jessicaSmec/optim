import forAll.Fraction;
import linear.methods.Simplex;
import linear.table.SimTableBigDecimalThreads;
import linear.table.SimTableFractionThreads;
import org.junit.jupiter.api.Test;
import linear.table.SimTableBigDecimal;

//import table.TableBigDecimalThreads;
import linear.table.SimTableFraction;
//import table.TableFractionThreads;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestThreads {



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
    public void Simplex_size3on4_threads() throws InterruptedException {

        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];


        for (int it = 0; it<iter; it++){

        double[][] tab = {{1, -2, 1}, {-2, 1, 2}, {2, 1, 6}, {-3, -1, 0}};

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }


        String[] base = {"u1", "u2", "u3"};
        String[] free = {"x1", "x2"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;
            ress[it] = res;
        }

        long sum = 0;
        for(int i=0; i<iter;i++){
            ress[i]/=1000;
            sum+=ress[i];
        }

        sum/=iter;

        System.out.printf("size 3*4 fract potoks: %d \n", sum);
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
        //int size = map.size();
        for(Long data : map.keySet()){
            sum+= (double)data*map.get(data)/size;
        }
        return sum;
    }


    public  double Mpow2(Map<Long, Long> map, int size){
        double sum =0;
        //  int size = map.size();
        for(Long data : map.keySet()){
            sum+= Math.pow(data, 2)*map.get(data)/size;
        }
        return sum;
    }




    @Test
    public void Simplex3Frac() throws InterruptedException {

        int iter = 150;
        long [] ress = new long[iter];


        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){

            double[][] tab = {{1, -2, 1}, {-2, 1, 2}, {2, 1, 6}, {-3, -1, 0}};

            Fraction[][] arr = new Fraction[tab.length][tab[0].length];

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = Fraction.valueOf(tab[i][j]);
                }
            }


            String[] base = {"u1", "u2", "u3"};
            String[] free = {"x1", "x2"};
            SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end - start;
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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


    @Test
    public void Simplex7Fract() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];


        List<Long>[] results = new List [1];



        for (int it = 0; it<iter; it++){
        double[][] tab = {{1, -2, 1, 2, 1, 2, 7},
                {-2, 1, 2, 3, 4, 1, 10},
                {1, 2, -2, 4, 1, 2, 12},
                {-2, 1, 2, 1, 2, 1, 9},
                {2, -1, -2, 3, 4, 2, 10},
                {2, 1, 6, 3, 1, 2, 15},
                {-3, -1, 0, 2, 1, 1, 0}};

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }


        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7"};
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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



    @Test
    public void Simplex7() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];


        List<Long>[] results = new List [1];



        for (int it = 0; it<iter; it++){
            double[][] tab = {{1, -2, 1, 2, 1, 2, 7},
                    {-2, 1, 2, 3, 4, 1, 10},
                    {1, 2, -2, 4, 1, 2, 12},
                    {-2, 1, 2, 1, 2, 1, 9},
                    {2, -1, -2, 3, 4, 2, 10},
                    {2, 1, 6, 3, 1, 2, 15},
                    {-3, -1, 0, 2, 1, 1, 0}};



            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }



            String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7"};
            String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7"};
            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);

            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end - start;
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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





    @Test
    public void Simplex7FractThreads() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];
        for (int it = 0; it<iter; it++){

        double[][] tab = {{1, -2, 1, 2, 1, 2, 7},
                {-2, 1, 2, 3, 4, 1, 10},
                {1, 2, -2, 4, 1, 2, 12},
                {-2, 1, 2, 1, 2, 1, 9},
                {2, -1, -2, 3, 4, 2, 10},
                {2, 1, 6, 3, 1, 2, 15},
                {-3, -1, 0, 2, 1, 1, 0}};

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }


        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7"};
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;
            ress[it] = (long) Math.ceil((double)res / 1000);;
    }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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



    @Test
    public void testSimplex11() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];


        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {10, 5, 3, 6, 8, 9, 10, 2, 1, 0, 80},
                {5, 6, 8, 0, 0, 4, 13, 0, 5, 0, 100},
                {0, 7, 0, 7, 0, 5, 0, 9, 9, 9, 130},
                {2, 0, 2, 6, 6, 0, 5, 0, 2, 2, 80},
                {15, 0, 2, 9, 9, 0, 7, 1, 0, 0, 100},
                {1, 10, 0, 0, 2, 7, 0, 3, 0, 10, 85},
                {0, 0, 4, 0, 10, 0, 7, 9, 1, 0, 100},
                {5, 0, 5, 0, 0, 1, 0, 7, 0, 8, 100},
                {7, 0, 7, 4, 0, 5, 0, 6, 0, 2, 120},
                {0, 5, 0, 7, 0, 5, 5, 2, 2, 2, 150},
                {-5, -1, -3, -7, -1, -2, -3, -4, -5, -2, 0}
        };

        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10"};
        SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);


        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
        boolean a = true;
        //int index = 0;

        for (int index = 0; index < values.length; index++) {
            if (Math.abs(new BigDecimal(values[index]).subtract(table.getTable()[indexs[index][0]][indexs[index][1]]).doubleValue()) > 0.1) {
                a = false;
            }
        }
        assertTrue(a);
           // ress[it] = res;
            ress[it] = (long) Math.ceil((double)res / 10000);
        }


        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();


        Map<Long, Long> map = histogramm(arr, false);
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


    @Test
    public void testSimplex16and16() throws InterruptedException {

        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){

        double[][] tab = {
                {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 770},
                {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 750},
                {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 500},
                {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 1000},
                {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 1000},
                {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 1500},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 800},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 500},
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 30},
                {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2000},
                {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 600},
                {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 2500},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 550},
                {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 1000},
                {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1000},
                {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, 0}
        };

        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15"};
        SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);


        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
        boolean a = true;
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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


    @Test
    public void testSimplex18() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];


        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 770},
                {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 750},
                {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 500},
                {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 1000},
                {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1000},
                {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 1500},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 800},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 500},
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 30},
                {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 2000},
                {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 600},
                {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 2500},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 550},
                {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 1000},
                {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1000},
                {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 600},
                {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 500},
                {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 3000},
                {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, 0}
        };

        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u17"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17"};
        SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);


        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
        boolean a = true;
        //int index = 0;
            ress[it] = (long) Math.ceil((double)res / 10000);;
        }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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


    @Test
    public void testSimplex19() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 1, 770},
                {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0, 750},
                {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 500},
                {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 0, 1000},
                {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 1000},
                {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 1500},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 800},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 500},
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 30},
                {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2000},
                {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0, 600},
                {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 1, 2500},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 550},
                {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 1000},
                {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 1000},
                {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 4, 600},
                {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 1, 500},
                {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 3000},
                {2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 5000},
                {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, -5, 0}
        };

        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u18", "u19"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19"};
        SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);


        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            ress[it] = (long) Math.ceil((double)res / 10000);;
        }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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


    @Test
    public void testSimplex23() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){

        double[][] tab = {
                {4, 1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 1, 3, 2, 1, 770},
                {2, 10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0, 1, 1, 0, 750},
                {0, 0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 0, 1, 2, 500},
                {5, 4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 0, 4, 3, 0, 1000},
                {1, 0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 3, 3, 3, 1000},
                {2, 0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 1, 2, 1, 1500},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 0, 0, 0, 800},
                {5, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 1, 0, 1, 500},
                {1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 30},
                {4, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2, 20, 4, 2000},
                {3, 0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0, 3, 1, 2, 600},
                {2, 0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 1, 2, 1, 4, 2500},
                {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 1, 1, 1, 550},
                {5, 2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 5, 6, 7, 1000},
                {6, 10, 12, 2, 2, 40, 2, 0, 2, 0, 2, 50, 2, 0, 2, 0, 35, 7, 0, 2, 20, 4, 2000},
                {4, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 2, 4, 1, 1000},
                {2, 1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 4, 3, 2, 1, 600},
                {5, 1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 1, 3, 4, 5, 500},
                {3, 2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 15, 3, 2, 3000},
                {2, 2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 8, 3, 5, 5000},
                {0, 5, 2, 2, 7, 0, 2, 6, 2, 2, 2, 3, 2, 2, 5, 0, 3, 7, 0, 2, 10, 4, 2100},
                {3, 1, 1, 2, 1, 0, 2, 0, 2, 0, 2, 3, 2, 2, 5, 0, 0, 2, 0, 2, 20, 4, 2100},
                {4, 1, 2, 2, 1, 0, 2, 1, 2, 1, 2, 1, 2, 2, 5, 0, 3, 1, 0, 2, 1, 4, 8900},
                {-5, -7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -2, -3, -3, -4, -5, -10, -7, -5, 0}
        };

        BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = BigDecimal.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u18", "u19", "u20", "u21", "u22"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19", "x20", "x21", "x22"};

        SimTableBigDecimalThreads table = new SimTableBigDecimalThreads(arr, base, free);

            Simplex simplex = new Simplex();

        long start = System.nanoTime();
        simplex.method(table);
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            ress[it] = (long) Math.ceil((double)res / 1000);;
        }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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




    @Test
    public void testSimplex26() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){

            double[][] tab = {
                    {4, 1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 1, 3, 2, 1, 4, 2, 1, 770},
                    {2, 10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0, 1, 1, 0, 2, 2, 2, 750},
                    {0, 0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 0, 1, 2, 10, 1, 2, 500},
                    {5, 4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 0, 4, 3, 0, 4, 1, 1, 1000},
                    {1, 0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 3, 3, 3, 0, 10, 2, 1000},
                    {2, 0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 1, 2, 1, 3, 4, 5,  1500},
                    {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 0, 0, 0, 20, 15, 14, 800},
                    {5, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 1, 0, 1, 2, 4, 5, 500},
                    {1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 10, 11, 12,  30},
                    {4, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2, 20, 4, 1, 3, 5,  2000},
                    {3, 0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0, 3, 1, 2, 4, 4, 4,  600},
                    {2, 0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 1, 2, 1, 4, 2, 6, 10, 2500},
                    {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 1, 1, 1, 4, 5, 2,  550},
                    {5, 2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 5, 6, 7, 5, 5, 7, 1000},
                    {6, 10, 12, 2, 2, 40, 2, 0, 2, 0, 2, 50, 2, 0, 2, 0, 35, 7, 0, 2, 20, 4, 0, 1, 1, 2000},
                    {4, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 2, 4, 1, 5, 5, 2, 1000},
                    {2, 1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 4, 3, 2, 1, 8, 7, 6,  600},
                    {5, 1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 1, 3, 4, 5, 4, 5, 6,  500},
                    {3, 2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 15, 3, 2, 2, 2, 2, 3000},
                    {2, 2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 8, 3, 5,1, 5, 1,  5000},
                    {0, 5, 2, 2, 7, 0, 2, 6, 2, 2, 2, 3, 2, 2, 5, 0, 3, 7, 0, 2, 10, 4, 3, 10, 11, 2100},
                    {3, 1, 1, 2, 1, 0, 2, 0, 2, 0, 2, 3, 2, 2, 5, 0, 0, 2, 0, 2, 20, 4, 6, 7, 8,  2100},
                    {4, 1, 2, 2, 1, 0, 2, 9, 2, 1, 2, 1, 2, 2, 5, 0, 3, 1, 0, 2, 1, 4, 1, 8, 5,  8900},
                    {4, 1, 2, 2, 1, 0, 2, 1, 2, 1, 2, 1, 2, 4, 5, 0, 3, 1, 0, 2, 1, 4, 1, 9, 6,  1900},
                    {4, 1, 2, 5, 1, 0, 2, 1, 2, 1, 2, 1, 2, 18, 5, 0, 3, 1, 0, 2, 1, 4, 10, 9, 5,  4900},
                    {-5, -7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -2, -3, -3, -4, -5, -2, -6, -2, -10, -7, -5, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u18", "u19", "u20", "u21", "u22"}; //исправить
            String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19", "x20", "x21", "x22"};

            SimTableBigDecimalThreads table = new SimTableBigDecimalThreads(arr, base, free);

            long start = System.nanoTime();

            table.simplex();
            long end = System.nanoTime();
            long res = end - start;


            int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
            double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            ress[it] = (long) Math.ceil((double)res / 10000);;
        }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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


    @Test
    public void testSimplex32() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];
        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){

            double[][] tab = {
                    {4, 1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 1, 3, 2, 1, 4, 2, 1, 4, 5, 1, 6, 1, 1,  770},
                    {2, 10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0, 1, 1, 0, 2, 2, 2,1, 2, 3, 4, 5, 6,   750},
                    {0, 0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 0, 1, 2, 10, 1, 2, 6, 5, 4, 3, 2, 1, 500},
                    {5, 4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 0, 4, 3, 0, 4, 1, 1, 4, 5, 5,  1000},
                    {1, 0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 3, 3, 3, 0, 10, 2, 1, 2, 3, 4, 5, 6,  1000},
                    {2, 0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 1, 2, 1, 3, 4, 5, 5, 5, 5, 5, 5, 5,  1500},
                    {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 0, 0, 0, 20, 15, 14, 2, 5, 2, 5, 2, 5, 800},
                    {5, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 1, 0, 1, 2, 4, 5, 1, 0, 1, 0, 1, 0,  500},
                    {1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 10, 11, 12, 4, 5, 6, 4, 5, 6, 30},
                    {4, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2, 20, 4, 1, 3, 5, 7, 7, 6, 6, 5, 4, 2000},
                    {3, 0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0, 3, 1, 2, 4, 4, 4, 0, 8, 0, 11, 0, 1, 600},
                    {2, 0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 1, 2, 1, 4, 2, 6, 10, 11, 12, 5, 4,  4, 3, 2500},
                    {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 1, 1, 1, 4, 5, 2, 1, 3, 5, 7, 1, 3,  550},
                    {5, 2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 5, 6, 7, 5, 5, 7, 2, 2, 2, 2, 2, 2, 1000},
                    {6, 10, 12, 2, 2, 40, 2, 0, 2, 0, 2, 50, 2, 0, 2, 0, 35, 7, 0, 2, 20, 4, 0, 1, 1, 1, 3, 5, 7, 1, 3, 2000},
                    {4, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 2, 4, 1, 5, 5, 2, 1, 6, 5, 6, 1, 4,1000},
                    {2, 1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 4, 3, 2, 1, 8, 7, 6, 1, 3, 1, 7, 1, 8, 600},
                    {5, 1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 1, 3, 4, 5, 4, 5, 6,  1, 3, 5, 7, 1, 1,500},
                    {3, 2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 15, 3, 2, 2, 2, 2,10, 3, 5, 7, 1, 3, 3000},
                    {2, 2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 8, 3, 5,1, 5, 1, 1, 3, 5, 7, 1, 30, 5000},
                    {0, 5, 2, 2, 7, 0, 2, 6, 2, 2, 2, 3, 2, 2, 5, 0, 3, 7, 0, 2, 10, 4, 3, 10, 11, 1, 3, 15, 7, 1, 3,2100},
                    {3, 1, 1, 2, 1, 0, 2, 0, 2, 0, 2, 3, 2, 2, 5, 0, 0, 2, 0, 2, 20, 4, 6, 7, 8,  1, 3, 5, 7, 17, 3,2100},
                    {4, 1, 2, 2, 1, 0, 2, 9, 2, 1, 2, 1, 2, 2, 5, 0, 3, 1, 0, 2, 1, 4, 1, 8, 5, 1, 3, 5, 7, 1, 13,8900},
                    {4, 1, 2, 2, 1, 0, 2, 1, 2, 1, 2, 1, 2, 4, 5, 0, 3, 1, 0, 2, 1, 4, 1, 9, 6,  1, 3, 5, 7, 8, 3, 1900},
                    {4, 1, 2, 5, 1, 0, 2, 1, 2, 1, 2, 1, 2, 18, 5, 0, 3, 1, 0, 2, 1, 4, 10, 9, 5, 1, 0, 5, 17, 1, 3, 4900},
                    {3, 1, 1, 2, 1, 0, 2, 0, 2, 0, 2, 3, 2, 2, 5, 0, 0, 2, 0, 2, 20, 4, 6, 7, 8,  1, 3, 5, 7, 17, 3,2100},
                    {3, 1, 1, 2, 1, 0, 2, 1, 2, 0, 2, 0, 2, 2, 5, 0, 4, 2, 0, 2, 20, 4, 6, 7, 8,  1, 3, 5, 7, 17, 3,2100},
                    {3, 1, 1, 2, 1, 0, 2, 0, 0, 0, 2, 3, 0, 2, 5, 0, 1, 2, 0, 2, 12, 4, 6, 7, 8,  18, 30, 5, 7, 1, 3,3100},
                    {3, 1, 1, 2, 1, 0, 0, 0, 2, 0, 2, 3, 2, 2, 5, 5, 20, 2, 0, 2, 10, 4, 6, 7, 8,  1, 13, 5, 7, 11, 3,5100},
                    {0, 1, 1, 1, 4, 1, 1, 1, 10, 1, 1, 1, 1, 0, 1, 1, 2, 1, 3, 0, 3, 0, 20, 15, 14, 2, 5, 4, 5, 2, 5, 800},

                    {-5, -7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -2, -3, -3, -4, -5, -2, -6, -2, -10, -5, -14, -7, -15, -10, -12, -7, -5, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u18", "u19", "u20", "u21", "u22"}; //исправить
            String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19", "x20", "x21", "x22"};

            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);

            long start = System.nanoTime();

            table.simplex();
            long end = System.nanoTime();
            long res = end - start;


            int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
            double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            ress[it] = (long) Math.ceil((double)res / 10000);;
        }

        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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










    @Test
    public void SimplexFract16()throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];


        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 770},
                {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 750},
                {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 500},
                {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 1000},
                {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 1000},
                {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 1500},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 800},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 500},
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 30},
                {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2000},
                {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 600},
                {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 2500},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 550},
                {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 1000},
                {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1000},
                {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, 0}
        };

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

// сначала здесь начинать

        // потом здесь
        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
        boolean a = true;
        //int index = 0;
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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




    @Test
    public void SimplexFract18() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];

        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 770},
                {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 750},
                {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 500},
                {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 1000},
                {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1000},
                {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 1500},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 800},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 500},
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 30},
                {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 2000},
                {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 600},
                {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 2500},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 550},
                {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 1000},
                {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1000},
                {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 600},
                {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 500},
                {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 3000},
                {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, 0}
        };

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u17"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);


        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
        boolean a = true;
        //int index = 0;
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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




    @Test
    public void  SimplexFract20() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];

        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 1, 770},
                {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0, 750},
                {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 500},
                {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 0, 1000},
                {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 1000},
                {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 1500},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 800},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 500},
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 30},
                {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2000},
                {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0, 600},
                {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 1, 2500},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 550},
                {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 1000},
                {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 1000},
                {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 4, 600},
                {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 1, 500},
                {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 3000},
                {2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 5000},
                {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, -5, 0}
        };

        Fraction[][] arr = new  Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] =  Fraction.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u18", "u19"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);


        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;

        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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






    @Test
    public void SimplexFract23() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];

        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {4, 1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4, 5, 1, 3, 2, 1, 770},
                {2, 10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0, 1, 1, 0, 750},
                {0, 0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 0, 1, 2, 500},
                {5, 4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0, 5, 0, 0, 4, 3, 0, 1000},
                {1, 0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 3, 3, 3, 1000},
                {2, 0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 1, 2, 1, 1500},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 0, 0, 0, 800},
                {5, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 1, 0, 1, 500},
                {1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 30},
                {4, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2, 20, 4, 2000},
                {3, 0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0, 3, 1, 2, 600},
                {2, 0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4, 1, 2, 1, 4, 2500},
                {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 1, 1, 1, 550},
                {5, 2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 5, 6, 7, 1000},
                {6, 10, 12, 2, 2, 40, 2, 0, 2, 0, 2, 50, 2, 0, 2, 0, 35, 7, 0, 2, 20, 4, 2000},
                {4, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 2, 4, 1, 1000},
                {2, 1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1, 0, 4, 3, 2, 1, 600},
                {5, 1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2, 1, 1, 3, 4, 5, 500},
                {3, 2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 15, 3, 2, 3000},
                {2, 2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 8, 3, 5, 5000},
                {0, 5, 2, 2, 7, 0, 2, 6, 2, 2, 2, 3, 2, 2, 5, 0, 3, 7, 0, 2, 10, 4, 2100},
                {3, 1, 1, 2, 1, 0, 2, 0, 2, 0, 2, 3, 2, 2, 5, 0, 0, 2, 0, 2, 20, 4, 2100},
                {4, 1, 2, 2, 1, 0, 2, 1, 2, 1, 2, 1, 2, 2, 5, 0, 3, 1, 0, 2, 1, 4, 8900},
                {-5, -7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -2, -3, -3, -4, -5, -10, -7, -5, 0}
        };
        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17", "u18", "u19", "u20", "u21", "u22"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19", "x20", "x21", "x22"};

        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

        long start = System.nanoTime();

        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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




    @Test
    public void SimplexFract11() throws InterruptedException {
        int iter = 150;
        long [] ress = new long[iter];

        List<Long>[] results = new List [1];

        for (int it = 0; it<iter; it++){
        double[][] tab = {
                {10, 5, 3, 6, 8, 9, 10, 2, 1, 0, 80},
                {5, 6, 8, 0, 0, 4, 13, 0, 5, 0, 100},
                {0, 7, 0, 7, 0, 5, 0, 9, 9, 9, 130},
                {2, 0, 2, 6, 6, 0, 5, 0, 2, 2, 80},
                {15, 0, 2, 9, 9, 0, 7, 1, 0, 0, 100},
                {1, 10, 0, 0, 2, 7, 0, 3, 0, 10, 85},
                {0, 0, 4, 0, 10, 0, 7, 9, 1, 0, 100},
                {5, 0, 5, 0, 0, 1, 0, 7, 0, 8, 100},
                {7, 0, 7, 4, 0, 5, 0, 6, 0, 2, 120},
                {0, 5, 0, 7, 0, 5, 5, 2, 2, 2, 150},
                {-5, -1, -3, -7, -1, -2, -3, -4, -5, -2, 0}
        };

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }
        String[] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10"}; //исправить
        String[] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10"};
        SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);

// сначала здесь начинать

        // потом здесь
        long start = System.nanoTime();
        table.simplex();
        long end = System.nanoTime();
        long res = end - start;


        int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
        double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
        boolean a = true;
        //int index = 0;

        for (int index = 0; index < values.length; index++) {
           // if (Math.abs(new forAll.Fraction(values[index]).subtract(table.table[indexs[index][0]][indexs[index][1]]).doubleValue()) > 0.01) {
             //   a = false;
            }


            ress[it] = (long) Math.ceil((double)res / 10000);;

        }



        Arrays.sort(ress);
        results[0]= release(ress);

        histogramm(ress, false);

        long[] arr = new long[results[0].size()];

        for(int c=0;c<results[0].size();c++){
            arr[c] = results[0].get(c);
        }
        LongSummaryStatistics stats =
                LongStream.of(arr).summaryStatistics();



        Map<Long, Long> map = histogramm(arr, false);
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
//        assertTrue(a);









}
