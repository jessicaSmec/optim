package nonlinear.gradtable;

import gradient.Df;
import gradient.FunDouble;
import gradient.FunDouble;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GradTableDouble implements GradTable{
/*
    private Df[] dx;
    private FunDouble fd;
    private double[]x;
    private double[]nextX;
    private Map<String,Double> params= new HashMap<>();

    private Map<String,double[]> arrParams = new HashMap<>();
    GradTableDouble(){

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

    public GradTableDouble getTable(){
        return new GradTableDouble(fd, x,dx, params, arrParams);

    }




    public  GradTableDouble(FunDouble fd, double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        this.fd=fd;
        setAll(x, dx, params, arrParams);
    }
    public  GradTableDouble(double []x, Df[] dx, Map<String,Double> params){
        setAll(x, dx, params, null);
    }
    public  GradTableDouble(double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
    }



    public  GradTableDouble(double []x, Df[] dx, FunDouble db, Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
        setfd(fd);
    }

    public void setfd(FunDouble fd){
        this.fd= fd;
    }
    public void setAll(double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        this.x=new double[x.length];
        this.nextX=new double[x.length];
        this.dx=dx;
        this.params=params;
        this.arrParams=arrParams;
        for(int i =0; i< x.length;i++){
            this.x[i]=x[i];
            this.nextX[i]=x[i];
        }

    }


    public double[] getX(){

        return x;
    }

    public void sgd() {


        int length = x.length;
        int iter = (params.get("iter")).intValue();
        Double lr = params.get("lr");

        Double stop = params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;

        for (int i = 0; i < iter; i++) {


            for (int j = 0; j < length; j++) {

                    nextX[j] -= lr * this.dx[j].df(this.x);

                if (stop != null) {
                    if ((j > 0 && check) || j == 0) {
                        check = Math.abs(nextX[j] - x[j]) < stop;
                    }
                }
            }
            if (check) break;
            for(int l =0; l< length;l++){
                this.x[l]=nextX[l];
            }
        }

    }

    public void momentum() {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.
        Double stop =  params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        double lr = params.get("lr");
        int iter = params.get("iter").intValue();
        double mu = params.get("mu");
        double[] v = arrParams.get("v");
        int length = x.length;
        System.arraycopy(this.x, 0, nextX, 0, x.length);
        for (int i = 0; i < iter; i++) {
            for (int j = 0; j < length; j++) {
                v[j] = v[j]*mu-lr*dx[j].df(x);
                nextX[j] = nextX[j]+v[j];

                if(stop !=null) {
                    if ((j > 0 && check) || j == 0){
                        check= Math.abs(nextX[j] - x[j]) < stop;;
                    }
                }
            }
            if (check) break;
            System.arraycopy(nextX, 0, x, 0, x.length);
        }

    }



    public void rmsprop() {
        Double stop = params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        double eps = params.get("eps");;
        double lr = params.get("lr");
        int iter = params.get("iter").intValue();
        double decay_rate = params.get("decay_rate");


        double[] cache = new double[x.length];

        for (int i = 0; i < cache.length; i++) {
            cache[i] = 0;
        }


        for (int i = 0; i < iter; i++) {


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
        }

    }


    public void adam() {
        Double stop = params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        double t = 0;
        double[] m = new double[x.length];
        double[] v =new double[x.length];
        double[] mt = new double[x.length];
        double[] vt = new double[x.length];
        double iter = params.get("iter");
        double lr = params.get("lr");
        double beta1 = params.get("beta1");
        double beta2 = params.get("beta2");
        double epsilon = 1e-8;



        for (int i = 0; i < m.length; i++) {
            m[i] = 0;
            v[i] = 0;
        }


        for (int i = 0; i < (int)iter; i++) {

            t+=1;
            for (int j = 0; j < x.length; j++) {
                double dw = dx[j].df(x);
                m[j] = beta1 * m[j] + (1 - beta1) * dw;

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
    }


 */



    private Df[] dx;
    private FunDouble fd;
    private double[]x;
    private double[]nextX;
    private Map<String,Double> params= new HashMap<>();

    private Map<String,double[]> arrParams = new HashMap<>();
    GradTableDouble(){

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

    public  GradTableDouble(double []x, Df[] dx, Map<String,Double> params){
        setAll(x, dx, params, null);
    }
    public  GradTableDouble(double []x, Df[] dx, Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
    }



    public  GradTableDouble(double []x, Df[] dx, FunDouble db, Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
        setfd(fd);
    }



    public  GradTableDouble(FunDouble fd, double []x, Df[] dx,  Map<String,Double> params, Map<String,double[]> arrParams){
        setAll(x, dx, params, arrParams);
        setfd(fd);
    }

    public GradTableDouble getTable(){
        return new GradTableDouble(fd, x,dx, params, arrParams);

    }
    public double[] getX(){

        return x;
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


    public void sgd() {


        double iter = (params.get("iter")).intValue();
        double lr = params.get("lr");
        double nextX[] = new double[this.x.length];
        // nextX[0] = x[0];
        System.arraycopy(this.x, 0, nextX, 0, x.length);
        for (int i = 0; i < iter; i++) {


            for (int j = 0; j < x.length; j++) {
                if(dx!=null){
                    nextX[j] -= lr * this.dx[j].df( this.x);}
                else {
                    double s = this.df(j);
                    nextX[j] -= lr * s ;

                }
                //nextX[j] = this.x[j];
            }
            //this.x= nextX;
            //this.x[0] = nextX[0];
            //this.x[1] = nextX[1];
            System.arraycopy(nextX, 0, this.x, 0, x.length);
        }

    }

    public void momentum() {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.

        double lr = params.get("lr");
        int iter = params.get("iter").intValue();
        double mu = params.get("mu");
        double[] v = arrParams.get("v");

        for (int i = 0; i < iter; i++) {

            for (int j = 0; j < x.length; j++) {
                v[j] = mu * v[j] - lr * dx[j].df(x);
                nextX[j] += v[j];

            }

            System.arraycopy(nextX, 0, x, 0, x.length);
        }

    }



    public void rmsprop() {
    /*
    Uses the RMSProp update rule, which uses a moving average of squared
    gradient values to set adaptive per-parameter learning rates.
    config format:
    - learning_rate: Scalar learning rate.
    - decay_rate: Scalar between 0 and 1 giving the decay rate for the squared
      gradient cache.
    - epsilon: Small scalar used for smoothing to avoid dividing by zero.
    - cache: Moving average of second moments of gradients.
    */

        // config.setdefault("learning_rate", 1e-2)
        //config.setdefault("decay_rate", 0.99)
        //config.setdefault("epsilon", 1e-8)
        //config.setdefault("cache", np.zeros_like(w))

        //next_w = None
        double eps = params.get("eps");;
        double lr = params.get("lr");
        int iter = params.get("iter").intValue();
        //double mu = params.get("mu");
        //double v = params.get("v");
        double decay_rate = params.get("decay_rate");

        double nextX[] = new double[this.x.length];
        System.arraycopy(x, 0, nextX, 0, x.length);
        //cache = array x (0)
        double[] cache = new double[x.length];

        for (int i = 0; i < cache.length; i++) {
            cache[i] = 0;
        }


        for (int i = 0; i < iter; i++) {
            double[] dx_ = new double[x.length];
            for (int k = 0; k < x.length; k++) {
                dx_[k] = this.dx[k].df(x);

            }
            for (int j = 0; j < x.length; j++) {
                cache[j] = decay_rate * cache[j] + (1 - decay_rate) * dx_[j] * dx_[j];
                nextX[j] -= lr * dx_[j] / (Math.sqrt(cache[j]) + eps);
            }
            System.arraycopy(nextX, 0, x, 0, x.length);
        }


    }


    public void adam() {

        double t = 0;
        double[] m = new double[x.length];
        double[] v =new double[x.length];
        double[] mt = new double[x.length];
        double[] vt = new double[x.length];
        double iter = params.get("iter");
        double lr = params.get("lr");
        double beta1 = params.get("beta1");
        double beta2 = params.get("beta2");
        double epsilon = 1e-8;
        double nextX[] = new double[this.x.length];

        System.arraycopy(x, 0, nextX, 0, x.length);
        for (int i = 0; i < m.length; i++) {
            m[i] = 0;
            v[i] = 0;
        }



        for (int i = 0; i < (int)iter; i++) {
            double[] dww = new double[x.length];
            for (int k = 0; k < x.length; k++) {
                dww[k] = dx[k].df(x);

            }

            for (int j = 0; j < x.length; j++) {
                double dw = dww[j];
                m[j] = beta1 * m[j] + (1 - beta1) * dw;
                t += 1;
                v[j] = beta2 * v[j] + (1 - beta2) * dw * dw;
                mt[j] = m[j] / (1 - Math.pow(beta1, t));
                vt[j] = v[j] / (1 - Math.pow(beta2, t));
                nextX[j] -= mt[j] * lr / (Math.sqrt(vt[j]) + epsilon);

            }
            System.arraycopy(nextX, 0, x, 0, x.length);
        }

    }



}
