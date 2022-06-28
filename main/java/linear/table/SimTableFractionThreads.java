package linear.table;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import forAll.*;

public class SimTableFractionThreads implements SimTableThreads {
    private Map<String,Integer> goal;
    private Fraction[][] table;
    private  Fraction[][] tableColumn;
    private  Fraction[][] koef ;
    private  Fraction[][] koefColumn;
    private Fraction f[];
    private Fraction b[];
    private  String [] base;
    private  String [] free;
    private Fraction[][] arr;
    private Fraction mainElem;
    private  int mainRow;
    private  int mainColumn;

    private ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private AtomicInteger atomicInteger = new AtomicInteger();


    public SimTableFractionThreads(Fraction[][] table, String[] base, String[] free) throws InterruptedException {
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
        /*
        setBase(base);
        setFree(free);
        setTable(table);*/
        threadPool.invokeAll(mc);
        // убрать сеты
    }


    public SimTableFractionThreads getSimTable() throws InterruptedException {
        SimTableFractionThreads tab = new SimTableFractionThreads(table,base, free);
        tab.setGoal(goal);
        return tab;
    }


    public Fraction[][] getTable() {
        return table;
    }

    public void setGoal(Map<String, Integer> goal) {
        this.goal = goal;
    }

    /* Табличные действия */
    public void setTableColumn(Fraction[][] tableColumn) {
        this.tableColumn = new Fraction[tableColumn[0].length][tableColumn.length];

        for(int i=0; i< tableColumn[0].length; i++){
            for(int j=0; j < tableColumn.length; j++){
                this.tableColumn[i][j]=tableColumn[j][i];
            }
        }
    }


    public void setKoefColumn() {
        this.koefColumn = new Fraction[table[0].length-1][table.length-1];

        for(int i=0; i< koefColumn.length; i++){
            for(int j=0; j < koefColumn[0].length; j++){
                this.koefColumn[i][j]=table[j][i];
            }
        }
    }

    public void setF(Fraction[] f) {
        this.f = new Fraction[f.length-1];
        if (f.length - 1 >= 0) System.arraycopy(f, 0, this.f, 0, f.length - 1);

    }

    public void setB(Fraction[][] table) {
        this.b= new Fraction[table.length-1];
        for(int i=0; i<table.length-1; i++){
            this.b[i]=table[i][table[0].length-1];
        }
    }

    public void setKoef(Fraction[][] table) {
        this.koef = new Fraction[table.length - 1][table[0].length - 1]; // if ???
        for (int i = 0; i < table.length - 1; i++) {
            System.arraycopy(table[i], 0, koef[i], 0, table[0].length - 1);
        }
    }

    public void setAll(Fraction[][] table) throws InterruptedException {
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
    public void setTable(Fraction[][] table) throws InterruptedException {
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

    public boolean min(Fraction[] arr) { // колонку
        Fraction min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if(min.compareTo(arr[i]) > 0) {
                min = arr[i];
                index = i;
            }
        }

        mainColumn = index;
        if(min.compareTo(Fraction.valueOf(0)) < 0){
            return true;}
        else return false;
    }

    // надо ли B передавать в параметры?

    public void minPositive(Fraction[] b, Fraction[] arr){ // строку
        Fraction elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if(
                    arr[i].compareTo(Fraction.valueOf(0)) >0 &&(
                            b[index].divide(elem, 5, RoundingMode.CEILING)
                                    .compareTo(b[i].divide(arr[i], 4, RoundingMode.CEILING)) >0
                                    || elem.compareTo(Fraction.valueOf(0))<0)

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
        arr = new Fraction[this.table.length][this.table[0].length];
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
        this.table[this.mainRow][j] = this.table[this.mainRow][j].divide(mainElem, 5, RoundingMode.CEILING);

    }

    void changeMainColumn(int i, int j){
        this.table[i][this.mainColumn] = this.table[i][this.mainColumn].divide(mainElem, 5, RoundingMode.CEILING);
        this.table[i][this.mainColumn] = this.table[i][this.mainColumn].multiply(Fraction.valueOf(-1));

    }

    void changeOthers (int i, int j){
        this.table[i][j] = // исправить на другое выражение
                this.mainElem.multiply(this.table[i][j]).subtract(
                        this.arr[this.mainRow][j].multiply(arr[i][this.mainColumn])).
                        divide(this.mainElem, 5, RoundingMode.CEILING);

    }

    // Result
    public void simplex() throws InterruptedException {
        while (min(this.f)) {
            this.mainElem();
            this.changeTable();
        }
    }


    /* methods.ArtificialBasis*/


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
        Fraction[][] table = new Fraction[size][size0];
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
    void letters(){ // оптимизировать метод
        // работает
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

    Fraction[] f(int size, int size0){ // изменить все
        // оптимизировать
// строку b rjgbhyenm


        //!! интегер в bigdec исправить, ибо дорого валуеоф делать
        Fraction[]f = new Fraction[size];
        Fraction a = new Fraction(0, 1);
        for(int i =0; i<size; i++) { // goal create
            if(i<(size-1) && goal.containsKey(this.free[i])) {
                a= a.subtract(Fraction.valueOf(goal.get(this.free[i])));

            }
            for(int j=0; j<size0; j++){
                if(j<(size0-1) && goal.containsKey(this.base[j]))
                    a= a.add(tableColumn[i][j].multiply(Fraction.valueOf(goal.get(this.base[j]))));
            }
            f[i]=a;

            a = Fraction.valueOf(0); // создать отдельно 0ую переменную
        }
        return f;
    }


    public void artificialBasis() throws InterruptedException {
        simplex();
        newSim();
        simplex();
    }


    /* methods.DualSimplex*/

    /* MainElem*/
    public void mainElem1() { // главный элемент возращать
        min1(this.b);
        minPositive1(this.f, this.koef[mainRow]);

    }

    public boolean min1(Fraction[] arr) { // колонку
        Fraction min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (
                    min.compareTo(arr[i])>0
            ) {
                min = arr[i];
                index = i;
            }
        }

        mainRow= index; //  и здесь
        return  min.compareTo(Fraction.valueOf(0))<0;
    }

    public void minPositive1(Fraction[] b, Fraction[] arr){ // строку
        Fraction elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if(
                    (arr[i].compareTo(Fraction.valueOf(0))<0 &&
                            (b[index].divide(elem, 5, RoundingMode.CEILING).compareTo(
                                    b[i].divide(arr[i], 5, RoundingMode.CEILING))>0))
                            || arr[index].compareTo(Fraction.valueOf(0))>0) {
                elem = arr[i];
                index = i;
            }
        }

        mainElem = elem; // вот здесь все портится
        mainColumn = index;  // и здесь artbas or dual
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
        //  max(b);
        while(max(b)!=-1){
            // добавляем новое ограничение
            // преобразуем

            restrictions(); // новое ограничение
            dualSimplex();
        };
        negative(table.length - 1);
    }


    void negative(int index){ // сделать по индексу

        for(int i=0; i<table[0].length; i++){
            table[index][i].setNumenator(-table[index][i].getNumenator());
        }

    }

    int max(Fraction[] arr){
        // если нет дроби то -1 возращаем
        Fraction max = Fraction.valueOf(0);
        int index = -1;

        for(int i=0; i<arr.length; i++){
            if(max.compareTo(arr[i].fractionPart())<0){
                max=arr[i].fractionPart();
                index=i;
            }
        }
        mainRow = index;
        return index;
    }


    void restrictions() throws InterruptedException{
        Fraction[][] table = new Fraction[this.table.length+1][this.table[0].length];
        String [] base = new String[this.base.length+1];
        for(int i=0; i<this.table.length-1; i++){
            for(int j=0; j< table[0].length;j++){
                table[i][j] = this.table[i][j];

            }
            base[i]=this.base[i];
        }



        addW(table);
        base[base.length-1] = "w1";
        // вести отчет ограничений и приписывать цифру нужную
        for(int i=0; i< table[0].length;i++){
            table[table.length-1][i] = this.table[table.length-2][i];
        }

        this.base = base;
        this.table = table;
        negative(table.length-2);
        this.setAll(this.table);

    }

    void addW(Fraction[][] table){
        Fraction zero = new Fraction(0, 1);
        for(int i=0; i < table[0].length;i++){
            if(this.table[mainRow][i].compareTo(zero) > 0 ){
                table[table.length-2][i] =
                        this.table[mainRow][i].fractionPart();
                // выделяем дробную часть
            } else {
                table[table.length-2][i]= Fraction.valueOf((this.table[mainRow][i].ceil()-1)*-1)
                        .add(this.table[mainRow][i]);
            }
        }
    }

}

