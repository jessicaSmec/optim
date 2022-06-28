package forAll;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Fraction {
    long numenator; // мб отриц
    long denominator;


    public Fraction(long numenator, long denominator) {
        this.numenator = numenator;
        this.denominator = denominator;

        // сократить
    }

    public Fraction(){

    }




    public long getNumenator() {
        return numenator;
    }

    public void setNumenator(long numenator) {
        this.numenator = numenator;
    }

    public long getDenominator() {
        return denominator;
    }

    public void setDenominator(long denominator) {
        this.denominator = denominator;
    }

    public void reduction(){
/*
        if(this.denominator>1000000 ||  this.numenator>1000000){
            Fraction newF = overflow(this);
            this.numenator = newF.numenator;
            this.denominator = newF.denominator;
        }


 */

        long gcd = GCD(getNumenator(),getDenominator());


        if(this.denominator < 0){
            this.denominator=-denominator;
            numenator=-numenator;
        }
        setNumenator(numenator/gcd);
        setDenominator(denominator/gcd);
    }

    public long LCM(long a, long b){
        return (a*b)/GCD(a, b);
    }

    public long GCD(long a, long b){
        if (a<0){
            a=-a;
        }
        if(b<0){
            b=-b;
        }
        while(! (a==0 ||  b==0)) {
            if (a > b) {
                a = a - b; //(b+=-a???)
            } else {
                b = b - a;
            }
        }
        return  (a>0)?a:b;
    }


    public BigDecimal bigDecimalValue(Fraction f){
        return BigDecimal.valueOf(f.numenator).
                divide(BigDecimal.valueOf(f.denominator),12,  RoundingMode.HALF_EVEN);

        //System.out.println("overflow");
    }


    public BigDecimal bigDecimalValue(){
        return BigDecimal.valueOf(this.numenator).
                divide(BigDecimal.valueOf(this.denominator),12,  RoundingMode.HALF_EVEN);

    }

    public Fraction overflow(Fraction f){
        BigDecimal value = bigDecimalValue(f);
        double val = value.doubleValue();
        return Fraction.valueOf(value);
    }

    public static  Fraction valueOf(BigDecimal f){
        double val = f.doubleValue();
        return  Fraction.valueOf(val);
    }

    public Fraction[] commonDenominator(Fraction f1, Fraction f2){


        if(f1.denominator>1000000000 || f1.numenator>1000000000){
            f1=overflow(f1);

        }

        if(f2.denominator>1000000000|| f2.numenator>1000000000) {
            f2=overflow(f2);
        }



        Fraction newF1 = new Fraction();
        Fraction newF2 = new Fraction();
        long denom = (f1.getDenominator()==f2.getDenominator())?f1.getDenominator():LCM(f1.getDenominator(),f2.getDenominator());
        //long denom = LCM(f1.getDenominator(),f2.getDenominator());
        long num1= f1.getNumenator()*(denom/f1.getDenominator());
        long num2= f2.getNumenator()*(denom/f2.getDenominator());


        newF1.setNumenator(num1);
        newF2.setNumenator(num2);
        newF1.setDenominator(denom);
        newF2.setDenominator(denom);

        return new Fraction[] {newF1, newF2};
    }

    public Fraction add(Fraction fraction){
        Fraction[] fr= commonDenominator(this, fraction);
        Fraction f= new Fraction();
        f.setDenominator(fr[0].getDenominator());
        f.setNumenator(fr[0].getNumenator()+fr[1].getNumenator());
        f.reduction();
        return  f;
    }

    public Fraction subtract(Fraction fraction){
        Fraction[] fr= commonDenominator(this, fraction);
        Fraction f= new Fraction();
        f.setDenominator(fr[0].getDenominator());
        f.setNumenator(fr[0].getNumenator()-fr[1].getNumenator());
        f.reduction();
        return  f;
    }

    public Fraction multiply(Fraction fraction){


        if(this.denominator>100000000 ||  this.numenator>100000000){
            Fraction newF = overflow(this);
            this.numenator = newF.numenator;
            this.denominator = newF.denominator;
        }

        if(fraction.denominator>100000000 || fraction.numenator>100000000) {
            fraction=overflow(fraction);
        }



        Fraction f= new Fraction();
        f.setNumenator(this.getNumenator()*fraction.getNumenator());
        f.setDenominator(this.getDenominator()*fraction.getDenominator());
        f.reduction();
        return  f;
    }

    public Fraction divide(Fraction fraction, int scale, Object o){
        Fraction f= new Fraction();
        f.setNumenator(this.getNumenator()*fraction.getDenominator());
        f.setDenominator(this.getDenominator()*fraction.getNumenator());
        if(f.getNumenator()!=0){
        f.reduction();
         if(f.denominator>1000000 ||  f.numenator>1000000){
            Fraction newF = overflow(this);
            f.numenator = newF.numenator;
            f.denominator = newF.denominator;
            f.reduction();
        }}
        return  f;
    }


    public int compareTo(Fraction fraction){
        Fraction[] fractions;
        if(fraction.getNumenator()!=0) {
             fractions = commonDenominator(this, fraction);
        }
        else{
             fractions = new Fraction[2];
             fractions[0]=this;
             fractions[1]=fraction;

        }
        if(fractions[0].numenator>fractions[1].numenator)
            return 1;
        else if(fractions[0].numenator<fractions[1].numenator)
            return -1;
        else return 0;
    }

    public static Fraction valueOf(int number){
        return new Fraction(number, 1);
    }

    public static Fraction valueOf(double number){
        boolean positive = true;
        if(number<0) {
            number = -number;
            positive=false;

        }
        BigDecimal n = BigDecimal.valueOf(number)
                .setScale(11, RoundingMode.HALF_EVEN);

        BigDecimal ceil = BigDecimal.valueOf(number)
                .setScale(0, RoundingMode.HALF_UP);;


        double fr = n.subtract(ceil).doubleValue();
        long denominator=10;

        while (fr%0.1>0 ){
            fr*=10;
            denominator*=10;
            double ceilFr = BigDecimal.valueOf(fr)
                    .setScale(0, RoundingMode.HALF_UP).doubleValue();;
                    if(Math.abs(fr-ceilFr)<0.0001) break;

        }
        fr*=10;

        Fraction f= new Fraction();


        long a = ceil.multiply(BigDecimal.valueOf(denominator)).add(BigDecimal.valueOf(fr)).intValue();

        if(positive) {
            f.setNumenator(a);
        } else{
            f.setNumenator(-a);
        }
        f.setDenominator(denominator);
        f.reduction();
        return f;

    }


    public Fraction fractionPart(){
        Fraction f = new Fraction();
        f.setNumenator(numenator-(ceil()*denominator));
        f.setDenominator(denominator);
        f.reduction();
        return f;
    }


    public long ceil(){
        return numenator/denominator;
    }

    public Fraction abs(){
        // тернарный сделать
        long num;
        if (numenator<0){
            num = -numenator;
        } else {
            num=numenator;
        }
        return new Fraction(num, denominator);

    }


/*
    public Fraction multiply(Fraction fraction){
        Fraction f= new Fraction();
        f.setNumenator(this.getNumenator()*fraction.getNumenator());
        f.setDenominator(this.getDenominator()*fraction.getDenominator());
        f.reduction();
        return  f;
    }


 */
    public Fraction pow(int i){
        Fraction f = this;
        while(i>1) {

            f = f.multiply(this);
            i--;
        }
        return f;
    }



}
