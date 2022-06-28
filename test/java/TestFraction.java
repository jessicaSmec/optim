import forAll.Fraction;
import org.junit.jupiter.api.Test;
import linear.table.SimTableFraction;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class TestFraction {

    @Test
    public void testOperation() {
        Fraction f1= new Fraction(1, 3);
        Fraction f2= new Fraction(2, 3);
        Fraction f3 = f2.divide(f1, 5 , RoundingMode.CEILING);

        //assertEquals(f2.compareTo(f1), 1);



    }

    @Test
    public void Simplex(){
        double[][] tab = {{1, -2, 1},{-2, 1, 2},{2, 1, 6}, {-3, -1, 0}};

        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for(int i=0; i< arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }


        String [] base = {"u1", "u2", "u3"};
        String [] free = {"x1", "x2"};
        SimTableFraction table = new SimTableFraction(arr, base, free);

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
        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for(int i=0; i< arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }


        SimTableFraction table = new SimTableFraction(arr, base, free);
        table.setGoal(goal);

        table.artificialBasis();// f and b не те
         //assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(0), BigDecimal.valueOf(1), BigDecimal.valueOf(7)}, table.table[0]);

    }
    @Test
    public  void dualSimplex() { // правильно работает
        double[][] tab = {{-3,-1, -2, -6},{-1, -1, -1, -4},{1, -3, 1, -4}, {1, -8, -3, 0}};
        String [] base = {"u1", "u2", "u3"};
        String [] free = {"x1", "x2", "x3"};
        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for(int i=0; i< arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }


        SimTableFraction table = new SimTableFraction(arr, base, free);
        table.dualSimplex();

    }


    @Test
    public  void Gamory() { // правильно работает
        double[][] tab = {{10, 30, 4500},{25, 25, 6250},{41, 90, 14100}, {90, 50, 18000}, {-100, -250, 0}};
        String [] base = {"u1", "u2", "u3", "u4"};
        String [] free = {"x1", "x2", "x3"};
        Fraction[][] arr = new Fraction[tab.length][tab[0].length];

        for(int i=0; i< arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = Fraction.valueOf(tab[i][j]);
            }
        }

        SimTableFraction table = new SimTableFraction(arr, base, free);
        table.gamory();

    }

}
