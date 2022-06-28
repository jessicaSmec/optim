package nonlinear.gradtable;

import gradient.Df;
import gradient.DfBigDecimal;
import gradient.FunBigDecimal;
import gradient.FunDouble;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GradTableBigDecimal implements GradTable {

    private FunBigDecimal f;
    private DfBigDecimal[] dx;
    private BigDecimal[]x;
    private BigDecimal[]nextX;
    private Map<String,BigDecimal> params= new HashMap<>();
    private Map<String,BigDecimal[]> arrParams = new HashMap<>();

    public GradTableBigDecimal(){}

    public GradTableBigDecimal(FunBigDecimal f,  BigDecimal[]x,DfBigDecimal[] dx, Map<String,BigDecimal> params, Map<String,BigDecimal[]> arrParams ){

        setAll(f, x, dx, params, arrParams );
    }

    private void setAll(FunBigDecimal f,  BigDecimal[]x,DfBigDecimal[] dx, Map<String,BigDecimal> params, Map<String,BigDecimal[]> arrParams){
        this.f=f;
        this.x=new BigDecimal[x.length];
        this.params = params;
        this.arrParams = arrParams;
        if (dx != null) {
            this.dx = dx; // тоже копировать?
        } else {
            dx = new DfBigDecimal[x.length];
            for(int i=0; i < x.length; i++){
                int finalI = i;
                dx[i] = (w)-> df(finalI);
            }
        }

        nextX = new BigDecimal[x.length];
        this.x = new BigDecimal[x.length];


        for(int l=0; l< x.length; l++){
            String value = String.valueOf(x[l]);
            nextX[l] = new BigDecimal(value);
            this.x[l] = new BigDecimal(value);
        }
    }

    public BigDecimal df(int i){
        BigDecimal eps = BigDecimal.valueOf(0.0001); // отдельно инициализировать?
        BigDecimal[] _x = new BigDecimal[x.length];
        System.arraycopy(x, 0, _x, 0, x.length);
        _x[i]= _x[i].add(eps);
        return ((f.f(_x).subtract(f.f(x))).divide(eps, RoundingMode.CEILING));
    }

    public void sgd() {

        int iter = params.get("iter").intValue();
        boolean check = false;
        int length = x.length;
        BigDecimal lr = params.get("lr");
        BigDecimal stop = params.get("stop") != null ? params.get("stop") : null;

        for (int i = 0; i < iter; i++) {

            for (int j = 0; j < length; j++) {

                nextX[j] = nextX[j].subtract(lr.multiply(dx[j].df(x)));
            }
            if (stop != null) {
                for (int k = 0; i < length; i++) {
                    check = nextX[k].subtract(x[k]).abs().compareTo(stop) == -1;
                }
            }

            if (check) break;

            for (int l = 0; l < length; l++) {
                x[l] = new BigDecimal(String.valueOf(nextX[l]));
            }

        }

    }

    public BigDecimal[] getX(){
        return this.x;
    }


    public GradTableBigDecimal getTable(){
        return new GradTableBigDecimal(f, x,dx, params, arrParams);
    }




    public void rmsprop() {
        int length = x.length;
        BigDecimal stop =  params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        BigDecimal eps = params.get("eps");;
        BigDecimal lr = params.get("lr");
        int iter = params.get("iter").intValue();
        BigDecimal decayRate = params.get("decayRate");
        BigDecimal[] cache = new BigDecimal[length];

        Arrays.fill(cache, new BigDecimal("0"));
        BigDecimal one = BigDecimal.valueOf(1);

        for (int i = 0; i < iter; i++) {

            for (int j = 0; j < length; j++) {
                cache[j] = decayRate.multiply(cache[j]).add((one.subtract(decayRate)).multiply(dx[j].df(x).pow(2)));
                BigDecimal sqrt = cache[j].add(eps).sqrt(MathContext.DECIMAL128);
                nextX[j] = nextX[j].subtract(lr.multiply(dx[j].df(x)).divide(sqrt, 32, RoundingMode.HALF_UP));

                if (stop != null) {
                    if ((j > 0 && check) || j == 0) {
                        check = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                    }
                }

                if (check) break;

            }
            for(int l=0; l<length; l++){
                this.x[l] = new BigDecimal(String.valueOf(nextX[l]));
            }
        }

    }

    public void momentum() {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.
        BigDecimal stop =  params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        BigDecimal lr = params.get("lr");
        int iter = params.get("iter").intValue();
        BigDecimal mu = params.get("mu");
        BigDecimal[] v = arrParams.get("v");
        int length = x.length;

        for (int i = 0; i < iter; i++) {
            for (int j = 0; j < length; j++) {
                v[j]= v[j].multiply(mu).subtract(lr.multiply(dx[j].df(x)));
                nextX[j] = nextX[j].add(v[j]);

                if(stop !=null) {
                    if ((j > 0 && check) || j == 0){
                        check= (nextX[j].subtract(x[j]).abs().compareTo(stop) == -1);
                    }
                }
            }
            if (check) break;
            for(int l=0; l<length; l++){
                x[l] = new BigDecimal(String.valueOf(nextX[l]));
            }
        }

    }

    public void adam() {
        BigDecimal stop = params.get("stop") != null ? params.get("stop") : null;
        boolean check = false;
        int t = 0;
        int length = x.length;
        BigDecimal[] m = this.arrParams.get("m");
        ;
        BigDecimal[] v = this.arrParams.get("v");
        BigDecimal[] mt = new BigDecimal[x.length];
        BigDecimal[] vt = new BigDecimal[x.length];
        int iter = params.get("iter").intValue();
        BigDecimal lr = params.get("lr");
        BigDecimal beta1 = params.get("beta1");
        BigDecimal beta2 = params.get("beta2");
        BigDecimal eps = params.get("eps");



        BigDecimal one = BigDecimal.valueOf(1);
        for (int i = 0; i < iter; i++) {
            t++;
            for (int j = 0; j < x.length; j++) {
                m[j] = beta1.multiply(m[j]).add(one.subtract(beta1).multiply(dx[j].df(x)));
                v[j] = beta2.multiply(v[j]).add(one.subtract(beta2).multiply(dx[j].df(x).pow(2)));
                mt[j] = m[j].divide(one.subtract(beta1.pow(t)), 32, RoundingMode.HALF_UP);
                vt[j] = v[j].divide(one.subtract(beta2.pow(t)), 32, RoundingMode.HALF_UP);
                BigDecimal sqrt = vt[j].add(eps).sqrt(new MathContext(32));
                nextX[j] = nextX[j].subtract(mt[j].multiply(lr).divide(sqrt, 32, RoundingMode.HALF_UP));
                if (stop != null) {
                    if ((j > 0 && check) || j == 0) {
                        check = (nextX[j].subtract(x[j]).abs().compareTo(stop) == -1);
                    }
                }


            }
            if (check) break;
            for (int l = 0; l < length; l++) {
                x[l] = new BigDecimal(String.valueOf(nextX[l]));
            }

        }
    }


}
