package gradient;


import java.math.BigDecimal;

@FunctionalInterface


public interface Df {
    double df(double []f );
    //BigDecimal df(BigDecimal f, double []ff );

   // double df(double f[]);
}


