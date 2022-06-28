package nonlinear.gradtable;

import gradient.Df;
import gradient.FunDouble;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GradTableDoubleThreads implements GradTableThreads{

    private Df[] dx;
    private FunDouble fd;
    private double[]x;
    private double[]nextX;
    private Map<String,Double> params= new HashMap<>();
    private ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private AtomicInteger atomicInteger = new AtomicInteger();
    private Map<String,double[]> arrParams = new HashMap<>();
    GradTableDoubleThreads(){

    }


    public double df(int i){
        double[] _x = new double[x.length];
        System.arraycopy(x, 0, _x, 0, x.length);
        _x[i]= _x[i]+0.0001;
        double f1 = fd.f(_x);
        double f2 = fd.f(x);
        double ff = f1-f2;
        double res = ff / 0.0001;
        return res;

    }


    public  GradTableDoubleThreads(FunDouble fd, double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        this.fd=fd;
        setAll(x, dx, params, arrParams);
    }

    public  GradTableDoubleThreads(double []x, Df[] dx, Map<String,Double> params){
        setAll(x, dx, params, null);
    }
    public  GradTableDoubleThreads(double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
    }



    public  GradTableDoubleThreads(double []x, Df[] dx, FunDouble db, Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
        setfd(fd);
    }

    public void setfd(FunDouble fd){
        this.fd= fd;
    }
    public void setAll(double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        this.x=new double[x.length];
        this.nextX=new double[x.length];
        System.arraycopy(x, 0, this.x, 0, x.length);
        this.dx=dx;
        this.params=params;
        this.arrParams=arrParams;
        System.arraycopy(nextX, 0, this.x, 0, x.length);
    }


    public double[] getX(){
        return this.x;
    }
    public GradTableDoubleThreads getTable(){
        return new GradTableDoubleThreads(fd, x,dx, params, arrParams);
    }

    public void sgd() throws InterruptedException {


        int length = x.length;
        int iter = (params.get("iter")).intValue();
        Double lr = params.get("lr");
        Double stop = params.get("stop") != null ? params.get("stop") : null;
        final boolean[] check = {false};
        for (int i = 0; i < iter; i++) {


            atomicInteger.set(0);
            List<Callable<Object>> mc = new ArrayList<>();
            while (atomicInteger.get()<length) {
                int j = atomicInteger.getAndIncrement();
                mc.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        nextX[j] -= lr * dx[j].df(x);
                        if (stop != null) {
                            if ((j > 0 && check[0]) || j == 0) {
                                check[0] = Math.abs(nextX[j] - x[j]) < stop;
                            }}
                        return null;
                    }
                });
            }
            threadPool.invokeAll(mc);

            if (check[0]) break;
            System.arraycopy(nextX, 0, this.x, 0, length);
        }

    }

    public void momentum() throws InterruptedException {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.
        Double stop = params.get("stop") != null ? params.get("stop") : null;
        final boolean[] check = {false};
        Double lr = params.get("lr");
        int iter = params.get("iter").intValue();
        Double mu = params.get("mu");
        double[] v = arrParams.get("v");
        int length = x.length;
        System.arraycopy(this.x, 0, nextX, 0, x.length);

        for (int i = 0; i < iter; i++) {
            atomicInteger.set(0);
            List<Callable<Object>> mc = new ArrayList<>();
            while (atomicInteger.get() < length) {
                int j = atomicInteger.getAndIncrement();
                mc.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        v[j] = v[j] * mu - lr * dx[j].df(x);
                        nextX[j] = nextX[j] + (v[j]);
                        if (stop != null) {
                            if ((j > 0 && check[0]) || j == 0) {
                                check[0] = Math.abs(nextX[j] - x[j]) < stop;
                            }
                        }
                        return null;
                    }
                });
            }
            threadPool.invokeAll(mc);

            if (check[0]) break;
            System.arraycopy(nextX, 0, this.x, 0, length);


        }
    }



    public void rmsprop() throws InterruptedException {
        Double stop = params.get("stop") != null ? params.get("stop") : null;
        final boolean[] check = {false};
        Double eps = params.get("eps");;
        Double lr = params.get("lr");
        int iter = params.get("iter").intValue();
        Double decay_rate = params.get("decay_rate");

        Double[] cache = new Double[x.length];
        int length = x.length;
        for (int i = 0; i < cache.length; i++) {
            cache[i] = 0.0;
        }


        for (int i = 0; i < iter; i++) {
/*

            for (int j = 0; j < x.length; j++) {
                cache[j] = decay_rate * cache[j] + (1 - decay_rate) * dx[j].df(x) * dx[j].df(x);
                nextX[j] -= lr * dx[j].df(x) / (Math.sqrt(cache[j]) + eps);
                if (stop != null) {
                    if ((j > 0 && check) || j == 0) {
                        check = Math.abs(nextX[j] - x[j]) < stop;
                    }
                }
            }
            if (check) break;
            System.arraycopy(nextX, 0, x, 0, x.length);

 */

            atomicInteger.set(0);
            List<Callable<Object>> mc = new ArrayList<>();
            while (atomicInteger.get()<length) {
                int j = atomicInteger.getAndIncrement();
                mc.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        cache[j] = decay_rate * cache[j] + (1 - decay_rate) * dx[j].df(x) * dx[j].df(x);
                        nextX[j] -= lr * dx[j].df(x) / (Math.sqrt(cache[j]) + eps);
                        if (stop != null) {
                            if ((j > 0 && check[0]) || j == 0) {
                                check[0] = Math.abs(nextX[j] - x[j]) < stop;
                            }}
                        return null;
                    }
                });
            }
            threadPool.invokeAll(mc);

            if (check[0]) break;
            System.arraycopy(nextX, 0, this.x, 0, length);
        }



    }


    public void adam() throws InterruptedException {
        int length = x.length;
        Double stop = params.get("stop") != null ? params.get("stop") : null;
        final boolean[] check = {false};
        final double[] t = {0};
        Double[] m = new Double[x.length];
        Double[] v =new Double[x.length];
        Double[] mt = new Double[x.length];
        Double[] vt = new Double[x.length];
        int iter = params.get("iter").intValue();
        Double lr = params.get("lr");
        Double beta1 = params.get("beta1");
        Double beta2 = params.get("beta2");
        Double epsilon = 1e-8;

        for (int i = 0; i < m.length; i++) {
            m[i] = 0.0;
            v[i] = 0.0;
        }


        for (int i = 0; i < (int)iter; i++) {

/*
            for (int j = 0; j < x.length; j++) {
                double dw = dx[j].df(x);
                m[j] = beta1 * m[j] + (1 - beta1) * dw;
                t += 1;
                v[j] = beta2 * v[j] + (1 - beta2) * dw * dw;
                mt[j] = m[j] / (1 - Math.pow(beta1, t));
                vt[j] = v[j] / (1 - Math.pow(beta2, t));
                nextX[j] -= mt[j] * lr / (Math.sqrt(vt[j]) + epsilon);
                if (stop != null) {
                    if ((j > 0 && check) || j == 0) {
                        check = Math.abs(nextX[j] - x[j]) < stop;
                    }
                }

            }
            if (check) break;
            System.arraycopy(nextX, 0, x, 0, x.length);
        }

 */
            t[0] +=1;
            atomicInteger.set(0);
            List<Callable<Object>> mc = new ArrayList<>();
            while (atomicInteger.get()<length) {
                int j = atomicInteger.getAndIncrement();
                mc.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        double dw = dx[j].df(x);
                        m[j] = beta1 * m[j] + (1 - beta1) * dw;
                        v[j] = beta2 * v[j] + (1 - beta2) * dw * dw;
                        mt[j] = m[j] / (1 - Math.pow(beta1, t[0]));
                        vt[j] = v[j] / (1 - Math.pow(beta2, t[0]));
                        nextX[j] -= mt[j] * lr / (Math.sqrt(vt[j]) + epsilon);
                        if (stop != null) {
                            if ((j > 0 && check[0]) || j == 0) {
                                check[0] = Math.abs(nextX[j] - x[j]) < stop;
                            }}
                        return null;
                    }
                });
            }
            threadPool.invokeAll(mc);

            if (check[0]) break;
            System.arraycopy(nextX, 0, this.x, 0, length);
        }


    }
}
