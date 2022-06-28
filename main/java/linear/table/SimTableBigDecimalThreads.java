package linear.table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SimTableBigDecimalThreads implements SimTableThreads {

    private Map<String,Integer> goal;
    private BigDecimal[][] table;
    private  BigDecimal[][] tableColumn;
    private  BigDecimal[][] koef ;
    private  BigDecimal[][] koefColumn;
    private  BigDecimal f[];
    private  BigDecimal b[];
    private  String [] base;
    private  String [] free;
    private BigDecimal[][] arr;

    private  BigDecimal mainElem;
    private int mainRow;
    private  int mainColumn;

    private ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private AtomicInteger atomicInteger = new AtomicInteger();


    public SimTableBigDecimalThreads(BigDecimal[][] table, String[] base, String[] free) throws InterruptedException {
        List<Callable<Object>> mc = new ArrayList<>();

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setTable(table);

                        return null;
                    }
                });

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setBase(base);

                        return null;
                    }
                });

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setFree(free);

                        return null;
                    }
                });

        threadPool.invokeAll(mc);

    }





    public SimTableBigDecimalThreads getSimTable() throws InterruptedException {
        SimTableBigDecimalThreads tab = new SimTableBigDecimalThreads(table,base, free);
        tab.setGoal(goal);
        return tab;
    }



    public void setGoal(Map<String,Integer> goal){
        this.goal = goal;
    }

    public BigDecimal[][] getTable() {
        return table;
    }


    /* Табличные действия */
    public void setTableColumn(BigDecimal[][] tableColumn) {
        this.tableColumn = new BigDecimal [tableColumn[0].length][tableColumn.length];

        for(int i=0; i< tableColumn[0].length; i++){
            for(int j=0; j < tableColumn.length; j++){
                this.tableColumn[i][j]=tableColumn[j][i];
            }
        }
    }


    public void setKoefColumn() {
        this.koefColumn = new BigDecimal [table[0].length-1][table.length-1];

        for(int i=0; i< koefColumn.length; i++){
            for(int j=0; j < koefColumn[0].length; j++){
                this.koefColumn[i][j]=table[j][i];
            }
        }
    }

    public void setF(BigDecimal[] f) {
        this.f = new BigDecimal[f.length-1];
        if (f.length - 1 >= 0) System.arraycopy(f, 0, this.f, 0, f.length - 1);

    }

    public void setB(BigDecimal[][] table) {
        this.b= new BigDecimal[table.length-1];
        for(int i=0; i<table.length-1; i++){
            this.b[i]=table[i][table[0].length-1];
        }
    }

    public void setKoef(BigDecimal[][] table) {
        this.koef = new BigDecimal[table.length - 1][table[0].length - 1]; // if ???
        for (int i = 0; i < table.length - 1; i++) {
            System.arraycopy(table[i], 0, koef[i], 0, table[0].length - 1);
        }
    }

    public void setAll(BigDecimal [][] table) throws InterruptedException {
        List<Callable<Object>> mc = new ArrayList<>();

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setKoef(table);

                        return null;
                    }
                });

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setKoefColumn();
                        return null;
                    }
                });

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setF(table[table.length-1]);
                        return null;
                    }
                });
        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setB(table);
                        return null;
                    }
                });

        mc.add(
                new Callable<Object>() {
                    public Object call() throws Exception {
                        setTableColumn(table);
                        return null;
                    }
                });
        threadPool.invokeAll(mc);

    }
    public void setTable(BigDecimal[][] table) throws InterruptedException {
        this.table = table;
        setAll(table);
    }

/*
    public void setBase(String[] base) {
        this.base = base;
    }


    public void setFree(String[] free) {
        this.free = free;
    }


 */


    public void setBase(String[] base) {
        // this.base = base;

        this.base = new String[base.length];
        for(int i=0; i< base.length;i++){
            this.base[i] = String.copyValueOf(base[i].toCharArray());
        }
    }


    public void setFree(String[] free) {
        //this.free = free;
        this.free = new String[free.length];
        for(int i=0; i< free.length;i++){
            this.free[i] = String.copyValueOf(free[i].toCharArray());
        }


    }

    // Методы!


    // MainElem
    public void mainElem() { // главный элемент возращать
        min(this.f);
        minPositive(this.b, this.koefColumn[this.mainColumn]);

    }

    public boolean min(BigDecimal[] arr) { // колонку
        BigDecimal min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if(min.compareTo(arr[i]) > 0) {
                min = arr[i];
                index = i;
            }
        }

        mainColumn = index;
        if(min.compareTo(BigDecimal.valueOf(0)) < 0){
            return true;}
        else return false;
    }

    // надо ли B передавать в параметры?

    public void minPositive(BigDecimal[] b, BigDecimal[] arr){ // строку
        BigDecimal elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if(

            arr[i].compareTo(BigDecimal.valueOf(0)) >0 &&(
                    b[index].divide(elem, 32,RoundingMode.HALF_UP)
                            .compareTo(b[i].divide(arr[i], 32,RoundingMode.HALF_UP)) >0
                            || elem.compareTo(BigDecimal.valueOf(0))<0)

            ) {
                elem = arr[i];
                index = i;
            }
        }

        mainElem = elem;
        mainRow = index;
    }


    // changeTable


    public  void changeName(){
        String a = this.base[this.mainRow];
        this.base[this.mainRow] =
                this.free[this.mainColumn];
        this.free[this.mainColumn] = a;

    }

    void forChange(int i){

        for(int j =0; j<table[0].length; j++){
            if(i==this.mainRow) {
                changeMainRow(i, j); //  6 ненужных инициализаций  и одна ненужная ссылка
            } else {

                if (j == this.mainColumn) {
                    changeMainColumn(i, j);

                } else {
                    changeOthers(i, j);
                } }}}

    void changeTable() throws InterruptedException {
        arr = new BigDecimal[this.table.length][this.table[0].length];
        for(int i =0; i<this.table.length;i++){
            for(int j =0; j<this.table[0].length;j++){
                arr[i][j] = this.table[i][j];
            } }


        atomicInteger.set(0);
        int size = table.length;
        List<Callable<Object>> mc = new ArrayList<>();
        while (atomicInteger.get()<size){
            int i=atomicInteger.getAndIncrement();
            mc.add(
                    new Callable<Object>() {
                        public Object call() throws Exception {
                            forChange(i);
                            return null;
                        }
                    });

        }


        threadPool.invokeAll(mc);


        changeName();


        this.table[this.mainRow][this.mainColumn] = this.table[this.mainRow][this.mainColumn].
                divide(mainElem, 5, RoundingMode.CEILING);
        this.setAll(this.table);
    }


    void changeMainRow(int i, int j){
        this.table[this.mainRow][j] = this.table[this.mainRow][j].divide(mainElem,  32,RoundingMode.HALF_UP);

    }

    void changeMainColumn(int i, int j){
        this.table[i][this.mainColumn] = this.table[i][this.mainColumn].divide(mainElem, 32,RoundingMode.HALF_UP);
        this.table[i][this.mainColumn] = this.table[i][this.mainColumn].multiply(BigDecimal.valueOf(-1));

    }

    void changeOthers (int i, int j){
        this.table[i][j] =
                this.mainElem.multiply(this.table[i][j]).subtract(
                                this.arr[this.mainRow][j].multiply(arr[i][this.mainColumn])).
                                divide(this.mainElem, 32,RoundingMode.HALF_UP);

    }

    // Result
    public void simplex() throws InterruptedException {
        while (min(this.f)) {
            this.mainElem();
            this.changeTable();
        }
    }


    /* ArtificialBasis*/


    public void newSim() throws InterruptedException {

        int length =0;
        for(String e: this.free){
            if (e.charAt(0) == 'w')
                length++;
        }
        String [] free = this.free;
        int size= this.table.length;

        int size0= this.table[0].length;
        size0--;
        BigDecimal[][] table = new BigDecimal[size][size0];
        size--;
        int k=0;

        for (int i=0; i < size; i++){
            for(int j=0; j< size0; j++){
                if(free[j].charAt(0) != 'w'){
                    table[i][k] = this.table[i][j];
                    k++;
                }
                table[i][size0-1] = this.table[i][size];
            }
            k=0;
        }

        letters();
        this.setTable(table);
        this.table[size] = f(size,size0);
        this.setAll(this.table);
    }
    void letters(){
        int lengthFree =0;
        for(String e: this.free){
            if (e.charAt(0) != 'w')
                lengthFree++;
        }

        int lengthBase =0;
        for(String e: this.base){
            if (e.charAt(0) != 'w')
                lengthBase++;
        }

        String[] free= new String[lengthFree];
        int i =0;
        for(String e: this.free) {
            if (e.charAt(0) != 'w') {
                free[i] = e;
                i++;
            }
        }

        i=0;
        String[] base = new String[lengthBase];
        for(String e: this.base){
            if (e.charAt(0) != 'w') {
                base[i] = e;
                i++;
            }
        }
        this.setBase(base);
        this.setFree(free);
    }

    BigDecimal[] f(int size, int size0){ // изменить все

        BigDecimal[]f = new BigDecimal[size];
        BigDecimal a = new BigDecimal(0);
        for(int i =0; i<size; i++) { // goal create
            if(i<(size-1) && goal.containsKey(this.free[i])) {
                a= a.subtract(BigDecimal.valueOf(goal.get(this.free[i])));

            }
            for(int j=0; j<size0; j++){
                if(j<(size0-1) && goal.containsKey(this.base[j]))
                    a= a.add(tableColumn[i][j].multiply(BigDecimal.valueOf(goal.get(this.base[j]))));
            }
            f[i]=a;

            a = BigDecimal.valueOf(0); // создать отдельно 0ую переменную
        }
        return f;
    }


    public void artificialBasis() throws InterruptedException {
        simplex();
        newSim();
        simplex();
    }


    /* DualSimplex*/

    /* MainElem*/
    public void mainElem1() {
        min1(this.b);
        minPositive1(this.f, this.koef[mainRow]);

    }

    public boolean min1(BigDecimal[] arr) {
        BigDecimal min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (
                    min.compareTo(arr[i])>0
            ) {
                min = arr[i];
                index = i;
            }
        }

        mainRow= index;
        return  min.compareTo(BigDecimal.valueOf(0))<0;
    }

    public void minPositive1(BigDecimal[] b, BigDecimal[] arr){
        BigDecimal elem = new BigDecimal(arr[0].toString());
        if(elem.compareTo(BigDecimal.ZERO)==0){
            elem=new BigDecimal("-0.0000001");
        }
        int index = 0;
        for(int i = 0; i < arr.length; i++){

            BigDecimal arr_i=new BigDecimal(arr[i].toString());
            BigDecimal b_i=new BigDecimal(b[i].toString());
            if(arr_i.compareTo(BigDecimal.ZERO)==0){
                arr_i=new BigDecimal("-0.0000001");
            }

            if(b_i.compareTo(BigDecimal.ZERO)==0){
                b_i=new BigDecimal("-0.0000001");
            }

            if(arr[index].compareTo(BigDecimal.ZERO)>0 ||
                    (arr[i].compareTo(BigDecimal.ZERO)<0 &&
                            b[index].divide(elem, 32, RoundingMode.HALF_UP).compareTo(b_i.divide(arr_i, 32, RoundingMode.HALF_UP))>0
                    )
            ){

                elem = new BigDecimal(arr_i.toString());
                index = i;
            }
        }

        // mainElem = elem;
        mainElem = new BigDecimal(arr[index].toString());
        mainColumn = index;
    }




    public void dualSimplex() throws InterruptedException {
        while (min1(b)) { // f on b
            this.mainElem1();
            changeTable();
        }
    }


    public void gamory() throws InterruptedException {
        simplex();
        negative(table.length - 1);

        while(max(b)!=-1){
            // добавляем новое ограничение
            // преобразуем

            restrictions(); // новое ограничение
            dualSimplex();
        };
       // negative(table.length - 1);
    }


    void negative(int index){ // сделать по индексу

        for(int i=0; i<table[0].length; i++){
            table[index][i]= table[index][i].multiply(BigDecimal.valueOf(-1));
        }

    }

    int max(BigDecimal[] arr){
        // если нет дроби то -1 возращаем
        BigDecimal max = BigDecimal.valueOf(0);
        int index = -1;
        BigDecimal check = new BigDecimal("0.001");
        for(int i=0; i<arr.length-1; i++){
            if(max.compareTo(fractionPart(arr[i]))<0){
                if(max.subtract(arr[i]).compareTo(check) < 0) {
                    max = fractionPart(arr[i]);
                    index = i;
                }
            }
        }
        mainRow = index;
        return index;
    }

    BigDecimal fractionPart(BigDecimal number){
        BigDecimal n= new BigDecimal(String.valueOf(number));
        BigDecimal ceil = number
                .setScale(0, RoundingMode.FLOOR);
        BigDecimal result = new BigDecimal(String.valueOf(n.subtract(ceil)));
        if (result.compareTo(BigDecimal.valueOf(0.0001))<0)
            return BigDecimal.valueOf(0);
        else if(result.compareTo(BigDecimal.valueOf(0.99))>0) return BigDecimal.valueOf(0);
        else return result;

    }
    void restrictions() throws InterruptedException {
        BigDecimal[][] table = new  BigDecimal[this.table.length+1][this.table[0].length];
        String [] base = new String[this.base.length+1];
        for(int i=0; i<this.table.length-1; i++){
            for(int j=0; j< table[0].length;j++){
                table[i][j] = this.table[i][j];

            }
            base[i]=this.base[i];
        }



        addW(table);
        base[base.length-1] = "w1";

        for(int i=0; i< table[0].length;i++){
            table[table.length-1][i] = this.table[table.length-2][i];
        }

        this.base = base;
        this.table = table;
        negative(table.length-2);
        this.setAll(this.table);

    }

    void addW( BigDecimal[][] table){
        BigDecimal zero = new  BigDecimal(0);
        for(int i=0; i < table[0].length;i++){
            if(this.table[mainRow][i].compareTo(zero) > 0 ){
                table[table.length-2][i] =
                        fractionPart(this.table[mainRow][i]);
                // выделяем дробную часть
            } else {
                BigDecimal ceil = new BigDecimal(String.valueOf(this.table[mainRow][i]));
                BigDecimal fr =  ceil.remainder( BigDecimal.ONE );
                ceil = ceil.subtract(fr);
                table[table.length-2][i]=ceil.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(-1))
                        .add(this.table[mainRow][i]);
            }
        }
    }

}
