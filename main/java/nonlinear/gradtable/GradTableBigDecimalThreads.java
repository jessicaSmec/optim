package nonlinear.gradtable;

import gradient.Df;
import gradient.DfBigDecimal;
import gradient.FunBigDecimal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GradTableBigDecimalThreads implements GradTableThreads {

    private ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private AtomicInteger atomicInteger = new AtomicInteger();
    private FunBigDecimal f;
    private DfBigDecimal[] dx;
    private BigDecimal[]x;
    private Map<String,BigDecimal> params= new HashMap<>();
    private Map<String,BigDecimal[]> arrParams = new HashMap<>();
    private BigDecimal[] nextX;



    public GradTableBigDecimalThreads(){}
    public GradTableBigDecimalThreads(FunBigDecimal f,  BigDecimal[]x,DfBigDecimal[] dx, Map<String,BigDecimal> params, Map<String,BigDecimal[]> arrParams ){

        setAll(f, x, dx, params, arrParams );
    }
    private void setAll(FunBigDecimal f,  BigDecimal[]x,DfBigDecimal[] dx, Map<String,BigDecimal> params, Map<String,BigDecimal[]> arrParams){
        this.f=f;
        //this.x=x;
        //this.x=new BigDecimal[x.length];
        //System.arraycopy(x, 0, this.x, 0, x.length); //?? исправить??
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


    public BigDecimal[] getX(){
        return this.x;
    }


    public GradTableBigDecimalThreads getTable(){
        return new GradTableBigDecimalThreads(f, x,dx, params, arrParams);
    }

    public BigDecimal df(int i){
        BigDecimal eps = BigDecimal.valueOf(0.0001); // отдельно инициализировать?
        BigDecimal[] _x = new BigDecimal[x.length];
        System.arraycopy(x, 0, _x, 0, x.length);
        _x[i]= _x[i].add(eps);
        return ((f.f(_x).subtract(f.f(x))).divide(eps, RoundingMode.CEILING));
    }

    public void sgd() throws InterruptedException {

        int iter = params.get("iter").intValue();
        final boolean[] check = {false};
        int length = x.length;
        BigDecimal lr = params.get("lr");
        BigDecimal stop =  params.get("stop") != null ? params.get("stop") : null;

        for (int i = 0; i < iter; i++) {

                atomicInteger.set(0);
                List<Callable<Object>> mc = new ArrayList<>();
                while (atomicInteger.get()<length){
                    int j=atomicInteger.getAndIncrement();
                    mc.add(
                            new Callable<Object>() {
                                public Object call() throws Exception {
                                    nextX[j] = nextX[j].subtract(lr.multiply(dx[j].df(x)));
                                    if(stop !=null) {
                                        if ((j > 0 && check[0]) || j == 0) {
                                            check[0] = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                                        }
                                    }
                                    return null;
                                }
                            });

                }

                threadPool.invokeAll(mc);

            atomicInteger.set(0);
            List<Callable<Object>> mc1 = new ArrayList<>();
            while (atomicInteger.get()<length){
                int j=atomicInteger.getAndIncrement();
                mc1.add(
                        new Callable<Object>() {
                            public Object call() throws Exception {
                                // sgdFor(j, lr);
                                x[j] = new BigDecimal(String.valueOf(nextX[j]));
                                return null;
                            }
                        });

            }



            if(check[0]) break;
            threadPool.invokeAll(mc1);



        }

    }

    public void momentum() throws InterruptedException {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.
        final boolean[] check = {false};
        BigDecimal lr = params.get("lr");
        int iter = params.get("iter").intValue();
        BigDecimal mu = params.get("mu");
        BigDecimal[] v = arrParams.get("v");
        BigDecimal stop = params.get("stop");
        int length = x.length;
        for(int l=0; l<length; l++){
            nextX[l] = new BigDecimal(String.valueOf(nextX[l]));
        }


        for (int i = 0; i < iter; i++) {

                atomicInteger.set(0);
                List<Callable<Object>> mc = new ArrayList<>();
                while (atomicInteger.get()<length){
                    int j=atomicInteger.getAndIncrement();
                    mc.add(
                            new Callable<Object>() {
                                public Object call() throws Exception {
                                    v[j]= v[j].multiply(mu).subtract(lr.multiply(df(j)));
                                    nextX[j] = nextX[j].add(v[j]);
                                    if(stop !=null) {
                                        if ((j > 0 && check[0]) || j == 0) {
                                            check[0] = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                                        }
                                    }
                                    return null;
                                }
                            });

                }

                threadPool.invokeAll(mc);

            atomicInteger.set(0);
            List<Callable<Object>> mc1 = new ArrayList<>();

            while (atomicInteger.get()<length){
                int j=atomicInteger.getAndIncrement();
                mc1.add(
                        new Callable<Object>() {
                            public Object call() throws Exception {
                                x[j] = new BigDecimal(String.valueOf(nextX[j]));
                                return null;
                            }
                        });
            }

            threadPool.invokeAll(mc1);
        }
    }

    public void rmsprop() throws InterruptedException {
        int length = x.length;
        BigDecimal stop =  params.get("stop") != null ? params.get("stop") : null;
        final boolean[] check = {false};
        BigDecimal eps = params.get("eps");;
        BigDecimal lr = params.get("lr");
        int iter = params.get("iter").intValue();
        BigDecimal decayRate = params.get("decayRate");
        BigDecimal nextX[] = new BigDecimal[length];

        for(int l=0; l<length; l++){
            nextX[l] = new BigDecimal(String.valueOf(x[l]));
        }

        BigDecimal[] cache = new BigDecimal[length];
        BigDecimal zero = BigDecimal.valueOf(0);
        Arrays.fill(cache, BigDecimal.valueOf(0));
        BigDecimal one = BigDecimal.valueOf(1);
        for (int i = 0; i < iter; i++) {
                atomicInteger.set(0);
                List<Callable<Object>> mc = new ArrayList<>();
                while (atomicInteger.get()<length){
                    int j=atomicInteger.getAndIncrement();
                    mc.add(
                            new Callable<Object>() {
                                public Object call() throws Exception {
                                    cache[j] = decayRate.multiply(cache[j]).add((one.subtract(decayRate)).multiply(dx[j].df(x).pow(2)));
                                    BigDecimal sqrt = BigDecimal.valueOf(Math.sqrt(cache[j].add(eps).doubleValue()));
                                    nextX[j] = nextX[j].subtract( lr.multiply(dx[j].df(x)).divide(sqrt,32, RoundingMode.HALF_UP));
                                    if(stop !=null) {
                                        if ((j > 0 && check[0]) || j == 0) {
                                            check[0] = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                                        }
                                    }
                                    return null;
                                }
                            });
                }

                threadPool.invokeAll(mc);

            atomicInteger.set(0);
            List<Callable<Object>> mc1 = new ArrayList<>();
            while (atomicInteger.get()<length){
                int j=atomicInteger.getAndIncrement();
                mc1.add(
                        new Callable<Object>() {
                            public Object call() throws Exception {
                                x[j] = new BigDecimal(String.valueOf(nextX[j]));
                                return null;
                            }
                        });
            }

            threadPool.invokeAll(mc1);
            if(check[0]) break;
        }
    }

    public void adam() throws InterruptedException {

        int t = 0;
        int length= x.length;
        final Boolean[] check = {false};
        BigDecimal[] m = this.arrParams.get("m");;
        BigDecimal[] v = this.arrParams.get("v");
        BigDecimal[] mt = new BigDecimal[x.length];

        BigDecimal[] vt = new BigDecimal[x.length];
        int iter = params.get("iter").intValue();
        BigDecimal lr = params.get("lr");
        BigDecimal beta1 = params.get("beta1");
        BigDecimal stop = params.get("stop");
        BigDecimal beta2 = params.get("beta2");
        BigDecimal eps = params.get("eps");;

        for(int l=0; l<length; l++){
            nextX[l] = new BigDecimal(String.valueOf(nextX[l]));
        }

        BigDecimal one = BigDecimal.valueOf(1);
        for (int i = 0; i < iter; i++) {

            atomicInteger.set(0);
            List<Callable<Object>> mc = new ArrayList<>();
            t++;
            while (atomicInteger.get()<length){
                int j=atomicInteger.getAndIncrement();
                int finalT = t;
                mc.add(
                        new Callable<Object>() {
                            public Object call() throws Exception {
                                m[j] = beta1.multiply(m[j]).add(one.subtract(beta1).multiply(dx[j].df(x)));

                                v[j] = beta2.multiply(v[j]).add(one.subtract(beta2).multiply(dx[j].df(x).pow(2)));
                                mt[j] = m[j].divide(one.subtract(beta1.pow(finalT)), 32, RoundingMode.HALF_UP);
                                vt[j] = v[j].divide(one.subtract(beta2.pow(finalT)), 32, RoundingMode.HALF_UP);
                                BigDecimal sqrt = vt[j].add(eps).sqrt(MathContext.DECIMAL32);
                                nextX[j]= nextX[j].subtract(mt[j].multiply(lr).divide(sqrt, 32, RoundingMode.HALF_UP));
                                if(stop !=null) {
                                    if ((j > 0 && check[0]) || j == 0) {
                                        check[0] = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                                    }
                                }
                                return null;
                            }
                        });

            }

            threadPool.invokeAll(mc);

            atomicInteger.set(0);
            List<Callable<Object>> mc1 = new ArrayList<>();
            while (atomicInteger.get()<length){
                int j=atomicInteger.getAndIncrement();
                mc1.add(
                        new Callable<Object>() {
                            public Object call() throws Exception {
                                // sgdFor(j, lr);
                                x[j] = new BigDecimal(String.valueOf(nextX[j]));
                                return null;
                            }
                        });

            }



            if(check[0]) break;
            threadPool.invokeAll(mc1);
        }

    }









}
