import forAll.Fraction;
import linear.methods.Simplex;
import linear.table.*;
import org.junit.jupiter.api.Test;
//import table.TableBigDecimalThreads;
//import table.TableFractionThreads;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.math.RoundingMode.CEILING;
import static org.junit.jupiter.api.Assertions.*;

public  class TestTable {


        @Test
        public void BigDecimal(){

            BigDecimal bd1 = new BigDecimal("7.5");
            BigDecimal bd2 = new BigDecimal("1.55555555");

            bd1.setScale(3);
            String bb = bd1.toString();
            BigDecimal sub = bd1.subtract(bd2); //add
            BigDecimal div = bd1.divide(bd2, 7, CEILING);// multiply
            //BigInteger mod(BigInteger other): возвращает остаток от целочисленного деления двух чисел
            // String aa = bd.toString();
            ;
            // assertEquals(8.6, res);
            int a = bd1.compareTo(bd2); // 1
            a = bd2.compareTo(bd1);//-1
            a = bd2.compareTo(BigDecimal.valueOf(1.55555555));//0
        }

        @Test
        public void Simplex(){
            double[][] tab = {{1, -2, 1},{-2, 1, 2},{2, 1, 6}, {-3, -1, 0}};

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }


            String [] base = {"u1", "u2", "u3"};
            String [] free = {"x1", "x2"};
            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);

            table.simplex();
        }


        @Test
        public void ArtificialBasis(){
            double[][] tab = {{1, -1, 0, 4}, {1, 1, -1, 2}, {-1, 1, 0, 3}, {-1, -1, 1, -2}};
            Map<String, Integer> goal = new HashMap<>();
            goal.put("x1", -2);
            goal.put("x2", 1);
            String[] base = {"u1", "w1", "u3"};
            String[] free = {"x1", "x2", "u2"};
            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }

            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);
            table.setGoal(goal);
            table.artificialBasis();// f and b не те

        }




    @Test
    public void ArtificialBasisDouble() throws InterruptedException {
        double[][] arr = {{1, -1, 0, 4}, {1, 1, -1, 2}, {-1, 1, 0, 3}, {-1, -1, 1, -2}};
        Map<String, Integer> goal = new HashMap<>();
        goal.put("x1", -2);
        goal.put("x2", 1);
        String[] base = {"u1", "w1", "u3"};
        String[] free = {"x1", "x2", "u2"};
        SimTableDoubleThreads table = new SimTableDoubleThreads(arr, base, free);
        table.setGoal(goal);
        table.artificialBasis();// f and b не те
        assertArrayEquals(new double[]{0.0, 1.0, 7.0}, table.getTable()[0]);
    }

        @Test
        public  void dualSimplex() { // правильно работает
            double[][] tab = {{-3,-1, -2, -6},{-1, -1, -1, -4},{1, -3, 1, -4}, {1, -8, -3, 0}};
            String [] base = {"u1", "u2", "u3"};
            String [] free = {"x1", "x2", "x3"};
            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }


            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);
            table.dualSimplex();

        }


    @Test
    public  void dualSimplexDouble() { // правильно работает
        double[][] arr = {{-3,-1, -2, -6},{-1, -1, -1, -4},{1, -3, 1, -4}, {1, -8, -3, 0}};
        String [] base = {"u1", "u2", "u3"};
        String [] free = {"x1", "x2", "x3"};
      //  BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

        SimTableDouble table = new SimTableDouble(arr, base, free);
        table.dualSimplex();

    }



    @Test
        public void Simplex2() throws InterruptedException {
            double[][] tab = {{1, -2, 1},{-2, 1, 2},{2, 1, 6}, {-3, -1, 0}};

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }


            String [] base = {"u1", "u2", "u3"};
            String [] free = {"x1", "x2"};
            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);

            table.simplex();

           // table.threadPool.shutdown();
        }




        @Test
        public void testSimplex21() throws InterruptedException {
            double[][] tab = {
                    {10, 5, 3, 6, 8, 9, 10, 2, 1, 0, 80},
                    {5, 6, 8, 0, 0, 4, 13, 0, 5, 0, 100},
                    {0, 7, 0, 7, 0, 5, 0 , 9 , 9, 9, 130},
                    {2, 0, 2, 6, 6, 0 , 5, 0 ,2 , 2, 80},
                    {15, 0, 2, 9, 9, 0, 7, 1, 0, 0, 100},
                    {1, 10, 0, 0, 2, 7,0, 3, 0, 10, 85},
                    {0, 0, 4, 0, 10, 0, 7, 9 , 1, 0, 100},
                    {5, 0 , 5, 0, 0, 1, 0, 7, 0, 8, 100},
                    {7, 0, 7, 4, 0 ,5 , 0 , 6 , 0, 2, 120},
                    {0, 5, 0, 7, 0, 5, 5, 2, 2, 2, 150},
                    {-5, -1, -3, -7, -1, -2, -3,-4, -5, -2, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10"};
            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);

// сначала здесь начинать

            // потом здесь
            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            boolean a = true;
            //int index = 0;

            for(int index=0 ; index<values.length; index++) {
                if (Math.abs(new BigDecimal(values[index]).subtract(table.getTable()[indexs[index][0]][indexs[index][1]]).doubleValue()) > 0.1) {
                    a = false;
                }
            }
            assertTrue(a);



        }

        @Test
        public void testSimplex11(){
            double[][] tab = {
                    {10, 5, 3, 6, 8, 9, 10, 2, 1, 0, 80},
                    {5, 6, 8, 0, 0, 4, 13, 0, 5, 0, 100},
                    {0, 7, 0, 7, 0, 5, 0 , 9 , 9, 9, 130},
                    {2, 0, 2, 6, 6, 0 , 5, 0 ,2 , 2, 80},
                    {15, 0, 2, 9, 9, 0, 7, 1, 0, 0, 100},
                    {1, 10, 0, 0, 2, 7,0, 3, 0, 10, 85},
                    {0, 0, 4, 0, 10, 0, 7, 9 , 1, 0, 100},
                    {5, 0 , 5, 0, 0, 1, 0, 7, 0, 8, 100},
                    {7, 0, 7, 4, 0 ,5 , 0 , 6 , 0, 2, 120},
                    {0, 5, 0, 7, 0, 5, 5, 2, 2, 2, 150},
                    {-5, -1, -3, -7, -1, -2, -3,-4, -5, -2, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10"};
            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);


            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            boolean a = true;
            //int index = 0;

            for(int index=0 ; index<values.length; index++) {
                if (Math.abs(new BigDecimal(values[index]).subtract(table.getTable()[indexs[index][0]][indexs[index][1]]).doubleValue()) > 0.01) {
                    a = false;
                }
            }
            assertTrue(a);



        }


        @Test
        public void testSimplex12() throws InterruptedException {
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
            SimTableBigDecimalThreads table = new SimTableBigDecimalThreads(arr, base, free);

// сначала здесь начинать

            // потом здесь
            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end - start;
            System.out.println(res);

            int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
            double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            boolean a = true;
            //int index = 0;
        }


        @Test
        public void testSimplex22() throws InterruptedException {
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

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15"};
            SimTableBigDecimalThreads table = new SimTableBigDecimalThreads(arr, base, free);


            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            boolean a = true;


        }

        @Test
        public void testSimplex23() throws InterruptedException {
            double[][] tab = {
                    {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4,5, 770},
                    {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 750},
                    {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 500},
                    {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0,5, 0, 1000},
                    {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4,1000},
                    {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 1500},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1,  800},
                    {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 500},
                    {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,  30},
                    {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7,  2000},
                    {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5,  600},
                    {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4,  2500},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 550},
                    {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8,  1000},
                    {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1000},
                    {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1,0,  600},
                    {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2,1,  500},
                    {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20,  3000},
                    {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17" ,"u18"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17"};
            SimTableBigDecimalThreads table = new SimTableBigDecimalThreads(arr, base, free);


            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};

        }

        @Test
        public void testSimplex24() throws InterruptedException {
            double[][] tab = {
                    {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4,5, 1, 770},
                    {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0,  750},
                    {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 500},
                    {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0,5, 0, 0, 1000},
                    {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 1000},
                    {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0,1500},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 800},
                    {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 500},
                    {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 30},
                    {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2000},
                    {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0,  600},
                    {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4,1,  2500},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2,550},
                    {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 1000},
                    {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 1000},
                    {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1,0,4,  600},
                    {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2,1, 1, 500},
                    {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 3000},
                    {2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 5000},
                    {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, -5, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17" ,"u18", "u19"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19"};
            SimTableBigDecimalThreads table = new SimTableBigDecimalThreads(arr, base, free);


            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};

        }

        @Test
        public void testSimplexFraction24() throws InterruptedException {
            double[][] tab = {
                    {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4,5, 1, 770},
                    {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0,  750},
                    {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 500},
                    {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0,5, 0, 0, 1000},
                    {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 1000},
                    {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0,1500},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 800},
                    {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 500},
                    {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 30},
                    {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2000},
                    {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0,  600},
                    {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4,1,  2500},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2,550},
                    {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 1000},
                    {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 1000},
                    {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1,0,4,  600},
                    {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2,1, 1, 500},
                    {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 3000},
                    {2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 5000},
                    {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, -5, 0}
            };

            Fraction[][] arr = new Fraction[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = Fraction.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17" ,"u18", "u19"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19"};
            SimTableFractionThreads table = new SimTableFractionThreads(arr, base, free);


            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};

        }

        @Test
        public void testSimplex14() throws InterruptedException {
            double[][] tab = {
                    {1, 5, 6, 2, 4, 1, 4, 6, 3, 2, 1, 3, 5, 2, 1, 4,5, 1, 770},
                    {10, 0, 1, 5, 0, 3, 0, 3, 0, 2, 0, 1, 0, 3, 0, 0, 0, 0,  750},
                    {0, 4, 0, 2, 0, 2, 2, 5, 9, 0, 4, 0, 7, 0, 4, 2, 1, 2, 500},
                    {4, 5, 32, 3, 11, 24, 2, 11, 24, 2, 11, 15, 4, 3, 2, 1, 0, 0,5, 0, 0, 1000},
                    {0, 0, 5, 4, 4, 43, 0, 0, 4, 6, 4, 0, 3, 0, 2, 0, 4, 1, 1000},
                    {0, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0,1500},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 3, 800},
                    {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 4, 0, 0, 500},
                    {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 30},
                    {0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 7, 0, 2000},
                    {0, 0, 0, 5, 0, 5, 0, 0, 5, 5, 0, 0, 0, 5, 0, 10, 5, 0,  600},
                    {0, 0, 0, 1, 0, 0, 3, 0, 4, 4, 1, 1, 1, 1, 1, 0, 4,1,  2500},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2,550},
                    {2, 1, 2, 0, 0, 2, 0, 0, 0, 2, 1, 0, 1, 0, 0, 10, 8, 4, 1000},
                    {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 7, 1000},
                    {1, 5, 6, 7, 2, 6, 7, 1, 1, 2, 3, 5, 2, 1, 3, 1,0,4,  600},
                    {1, 5, 1, 0, 0, 0, 0, 5, 1, 4, 2, 2, 0, 0, 0, 2,1, 1, 500},
                    {2, 5, 4, 0, 0, 9, 1, 3, 5, 2, 5, 6, 1, 1, 0, 4, 20, 1, 3000},
                    {2, 5, 4, 4, 1, 9, 1, 3, 5, 2, 5, 6, 3, 1, 3, 4, 10, 1, 5000},
                    {-7, 14, -2, -5, -1, -4, -10, -15, -3, -6, -3, -5, -8, -1, -3, -3, -4, -5, 0}
            };

            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for(int i=0; i< arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String [] base = {"u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9", "u10", "u11", "u12", "u13", "u14", "u15", "u16", "u17" ,"u18", "u19"}; //исправить
            String [] free = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19"};
            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);


            long start = System.nanoTime();
            table.simplex();
            long end = System.nanoTime();
            long res = end-start;
            System.out.println(res);

            int [] [] indexs = new int[][] {{0,0}, {2,0}, {0,1}, {0,4}, {10,0}, {10,2},{10,6}, {10,9}, {10, 10}};
            double [] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};

        }


        @Test
        public void testSimplex13() {
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
            System.out.println(res);

            int[][] indexs = new int[][]{{0, 0}, {2, 0}, {0, 1}, {0, 4}, {10, 0}, {10, 2}, {10, 6}, {10, 9}, {10, 10}};
            double[] values = new double[]{0.26, -0.29, 1.57, 0.15, 0.39, 0.35, 4.52, 1.91, 120.1619};
            boolean a = true;
            //int index = 0;

        }

        @Test
        public void testSimplexException() {
            double [][] tab = new double[][]{
                    {-1, 1, 1},
                    {1, -1, 1},
                    {1, -2, 2},
                    {-6, 4, 3}
            };
            BigDecimal[][] arr = new BigDecimal[tab.length][tab[0].length];

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = BigDecimal.valueOf(tab[i][j]);
                }
            }
            String[] base = {"x3", "x4", "x5"};
            String[] free = {"x1", "x2"};

            SimTableBigDecimal table = new SimTableBigDecimal(arr, base, free);
            Simplex sim = new Simplex();
            assertThrows(RuntimeException.class, () -> {
               sim.method(table);
            });


        }


    }


