package nonlinear.gradtable;

import forAll.Fraction;
import gradient.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GradTableFraction implements GradTable{


    private FunFraction f;
    private DfFraction[] dx;
    private Fraction[]x;

    private Fraction[]nextX;

    private Map<String,Fraction> params= new HashMap<>();
    private Map<String,Fraction[]> arrParams = new HashMap<>();



    public GradTableFraction(){}

    public GradTableFraction(FunFraction f,  Fraction[]x,DfFraction[] dx, Map<String,Fraction> params, Map<String,Fraction[]> arrParams ){

        setAll(f, x, dx, params, arrParams );
    }

    private void setAll(FunFraction f, Fraction[]x, DfFraction[] dx, Map<String,Fraction> params, Map<String,Fraction[]> arrParams){
        this.f=f;
        //this.x=x;
        this.x=new Fraction[x.length];
        //System.arraycopy(x, 0, this.x, 0, x.length);
        this.params = params;
        this.arrParams = arrParams;
        if (dx != null) {
            this.dx = dx; // тоже копировать?
        } else {
            dx = new DfFraction[x.length];
            for(int i=0; i < x.length; i++){
                int finalI = i;
                dx[i] = (w)-> df(finalI);
            }
        }

        this.nextX = new Fraction[x.length];
        this.x = new Fraction[x.length];


        for(int l=0; l< x.length; l++){
           // String value = String.valueOf(x[l]);
            this.nextX[l] = new Fraction(x[l].getNumenator(), x[l].getDenominator());
            this.x[l] = new Fraction(x[l].getNumenator(), x[l].getDenominator());
        }
    }


    public GradTableFraction getTable(){
        return new GradTableFraction(f, x,dx, params, arrParams);
    }
    public Fraction df(int i){
        Fraction eps = Fraction.valueOf(0.0001); // отдельно инициализировать?
        Fraction[] _x = new Fraction[x.length];
        System.arraycopy(x, 0, _x, 0, x.length);
        _x[i]= _x[i].add(eps);
        //return ((f.f(_x).subtract(f.f(x))).divide(eps, 5, RoundingMode.CEILING));
        Fraction f1 = f.f(_x);
        Fraction f2 = f.f(x);
        Fraction ff = f1.subtract(f2);
        Fraction res = ff.divide(eps, 8 , RoundingMode.CEILING);
        return res;

    }

    public Fraction[] getX() {
        return x;
    }

    public void sgd() {
        int iter = (int) params.get("iter").getNumenator();
        boolean check =false;
        int length = x.length;
        Fraction lr = params.get("lr");
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;


        for (int i = 0; i < iter; i++) {


            for (int j = 0; j < length; j++) {

                nextX[j] = nextX[j].subtract(lr.multiply(dx[j].df(x)));
            }
            if(stop !=null) {
                for (int k = 0; i < length; i++) {
                    check= nextX[k].subtract(x[k]).abs().compareTo(stop) == -1;
                }
            }

            if(check) break;

            for(int l=0; l<length; l++){
                x[l] = new Fraction(nextX[l].getNumenator(), nextX[l].getDenominator());
            }

        }

    }




    public void momentum() {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.
        boolean check =false;
        Fraction lr = params.get("lr");
        int iter = (int) params.get("iter").ceil();
        Fraction mu = params.get("mu");
        Fraction v[] = arrParams.get("v");
        int length = x.length;
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;

        for (int i = 0; i < iter; i++) {

            for (int j = 0; j < length; j++) {
                /*
                System.out.println("-----");
                System.out.println(i);
                System.out.println(j);


                 */
                Fraction for_mu = dx[j].df(x);
                Fraction for_v = lr.multiply(for_mu);
                v[j] = v[j].multiply(mu).subtract(for_v);

                this.nextX[j] = nextX[j].add(v[j]);
                if (stop != null) {
                    for (int k = 0; i < length; i++) {
                        check = nextX[k].subtract(x[k]).abs().compareTo(stop) == -1;
                    }
                }
            }
                if(check) break;
            for(int l=0; l<length; l++){
                x[l] = new Fraction(nextX[l].getNumenator(), nextX[l].getDenominator());
            }
        }
    }



    public static BigDecimal sqrt(BigDecimal value) {

        BigDecimal x = BigDecimal.valueOf(Math.sqrt(value.setScale(4, RoundingMode.HALF_UP).doubleValue()));
        return x.add(BigDecimal.valueOf(value.subtract(x.multiply(x)).doubleValue() / (x.doubleValue() * 2.0)));
    }


    public void adam() {
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        int t = 0;
        Fraction[] m = this.arrParams.get("m");;
        Fraction[] v = this.arrParams.get("v");
        Fraction[] mt = new Fraction[x.length];
        Fraction[] vt = new Fraction[x.length];
        int iter = (int) params.get("iter").ceil();
        Fraction lr = params.get("lr");
        Fraction beta1 = params.get("beta1");
        Fraction beta2 = params.get("beta2");
        Fraction eps = params.get("eps");;
        int length = x.length;

        Fraction one = Fraction.valueOf(1);
        for (int i = 0; i < iter; i++) {

t++;
            for (int j = 0; j < length; j++) {




                Fraction dw = dx[j].df(x);

                m[j] = beta1.multiply(m[j]).add(one.subtract(beta1).multiply(dw));

                v[j] = beta2.multiply(v[j]).add(one.subtract(beta2).multiply(dw.pow(2)));
                mt[j] = m[j].divide(one.subtract(beta1.pow(t)), 32,  RoundingMode.HALF_UP);
                vt[j] = v[j].divide(one.subtract(beta2.pow(t)), 32, RoundingMode.HALF_UP);

                Fraction sqrt = Fraction.valueOf(Math.sqrt(vt[j].add(eps).bigDecimalValue().doubleValue()));
                nextX[j]= nextX[j].subtract(mt[j].multiply(lr).divide(sqrt, 32, RoundingMode.HALF_UP));

                if (stop != null) {
                    for (int k = 0; i < length; i++) {
                        check = nextX[k].subtract(x[k]).abs().compareTo(stop) == -1;
                    }
                }

            }
            if(check) break;
            for(int l=0; l<length; l++){
                x[l] = new Fraction(nextX[l].getNumenator(), nextX[l].getDenominator());
            }
        }

    }




    public void rmsprop() {
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        int length = x.length;

        Fraction eps = params.get("eps");;
        Fraction lr = params.get("lr");
        int iter = (int) params.get("iter").getNumenator();
        Fraction decayRate = params.get("decayRate");
        Fraction[] cache = new Fraction[length];
        Arrays.fill(cache, Fraction.valueOf(0));
        Fraction one = Fraction.valueOf(1);
        for (int i = 0; i < iter; i++) {


            for (int j = 0; j < length; j++) {
                cache[j] = decayRate.multiply(cache[j]).add((one.subtract(decayRate)).multiply(dx[j].df(x).pow(2)));

                Fraction sqrt = Fraction.valueOf(Math.sqrt(cache[j].add(eps).bigDecimalValue().doubleValue()));

                nextX[j] = nextX[j].subtract( lr.multiply(dx[j].df(x)).divide(sqrt,10,RoundingMode.HALF_UP) );

                if(stop !=null) {
                    if ((j > 0 && check) || j == 0) {
                        check = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                    }
                }

            }


            if(check) break;


            for(int l=0; l<length; l++){
                x[l] = new Fraction(nextX[l].getNumenator(), nextX[l].getDenominator());
            }
        }



    }




}
