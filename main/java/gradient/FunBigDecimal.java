package gradient;

import java.math.BigDecimal;


@FunctionalInterface
// вроде можно убрать
public interface FunBigDecimal {
    BigDecimal f(BigDecimal x[]);
}