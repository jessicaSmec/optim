package linear.table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class SimTableDouble implements SimTable{
    private Map<String,Integer> goal;
    private double[][] table;
    private  double[][] tableColumn;
    private  double[][] koef ;
    private  double[][] koefColumn;
    private  double f[];
    private  double b[];
    private  String [] base;
    private  String [] free;
    private  double mainElem;
    private  int mainRow;
    private  int mainColumn;


    public double[][] getTable() {
        return table;
    }

    public void setGoal(Map<String, Integer> goal) {
        this.goal = goal;
    }

    public SimTableDouble(double[][] table, String[] base, String[] free){
        setBase(base);
        setFree(free);
        setTable(table);
    }

    public SimTableDouble(){

    }

    public SimTableDouble getSimTable() {
        SimTableDouble tab = new SimTableDouble(table,base, free);
        tab.setGoal(goal);
        return tab;
    }


    /* Табличные действия */
    public void setTableColumn(double[][] tableColumn) {
        this.tableColumn = new double [tableColumn[0].length][tableColumn.length];

        for(int i=0; i< tableColumn[0].length; i++){
            for(int j=0; j < tableColumn.length; j++){
                this.tableColumn[i][j]=tableColumn[j][i];
            }
        }
    }


    public void setKoefColumn(double[][] koef) {
        this.koefColumn = new double [koef[0].length][koef.length];

        for(int i=0; i< koef[0].length; i++){
            for(int j=0; j < koef.length; j++){
                this.koefColumn[i][j]=koef[j][i];
            }
        }
    }

    public void setF(double[] f) {
        this.f = new double[f.length-1];
        if (f.length - 1 >= 0) System.arraycopy(f, 0, this.f, 0, f.length - 1);

    }

    public void setB(double[][] table) {
        this.b= new double[table.length-1];
        for(int i=0; i<table.length-1; i++){
            this.b[i]=table[i][table[0].length-1];
        }
    }

    public void setKoef(double[][] table) {
        this.koef = new double[table.length - 1][table[0].length - 1]; // if ???
        for (int i = 0; i < table.length - 1; i++) {
            System.arraycopy(table[i], 0, koef[i], 0, table[0].length - 1);
        }
    }

    public void setAll(double [][] table){
        setKoef(table);
        setKoefColumn(this.koef);
        setF(table[table.length-1]);
        setB(table);
        setTableColumn(table);
    }
    public void setTable(double[][] table) {
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


    /* MainElem*/
    public void mainElem() { // главный элемент возращать
        min(this.f);
        // goto
        minPositive(this.b, this.koefColumn[this.mainColumn]);

    }

    public boolean min(double[] arr) { // колонку
        double min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (min > arr[i]) {
                min = arr[i];
                index = i;
            }
        }

        mainColumn = index;
        return min < 0;
    }

    // надо ли B передавать в параметры?

    public void minPositive(double[] b, double[] arr){ // строку
        double elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[i] > 0 && ((b[index]/elem > b[i]/arr[i]) || elem < 0)) {
                elem = arr[i];
                index = i;
            }
        }

        mainElem = elem;
        mainRow = index;
    }


    /* changeTable*/


    public  void changeName(){
        String a = this.base[this.mainRow];
        this.base[this.mainRow] =
                this.free[this.mainColumn];
        this.free[this.mainColumn] = a;

    }

    void changeTable(){
        double[][] arr = new double[this.table.length][this.table[0].length];
        for(int i =0; i<this.table.length;i++){
            for(int j =0; j<this.table[0].length;j++){
                arr[i][j] = this.table[i][j];
            } }

// убрать отдельные методы для каждого действия
        for (int i = 0; i < this.table.length; i++) {
            for (int j = 0; j < this.table[0].length; j++) {

                if(i==this.mainRow) {
                    changeMainRow(i, j); //  6 ненужных инициализаций  и одна ненужная ссылка
                } else {

                    if (j == this.mainColumn) {
                        changeMainColumn(i, j);

                    } else {
                        changeOthers(i, j, arr);
                    } } } }

        changeName();
        this.table[this.mainRow][this.mainColumn]/=mainElem;
        this.setAll(this.table);
    }


    void changeMainRow(int i, int j){
        this.table[this.mainRow][j]/=this.mainElem;
    }

    void changeMainColumn(int i, int j){
        this.table[i][this.mainColumn] /= -this.mainElem;
    }

    void changeOthers (int i, int j, double [][] arr){
        this.table[i][j] = // здесь для точности более медленное выражение
                (this.mainElem * this.table[i][j] -
                        arr[this.mainRow][j] * arr[i][this.mainColumn]) / this.mainElem;
    }

    /* Result */
    public void simplex(){
        while (min(this.f)) {
            this.mainElem();
            this.changeTable();
        }
    }



    /* ArtificialBasis*/


    public void newSim(){

        int length =0;
        for(String e: this.free){
            if (e.charAt(0) == 'w')
                length++;
        }
        String [] free = this.free;
        int size= this.table.length;

        int size0= this.table[0].length;
        size0--;
        double[][] table = new double[size][size0];
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

    double[] f(int size, int size0){ // изменить все
        // оптимизировать
// строку b rjgbhyenm

        double[]f = new double[size];
        int a =0;
        for(int i =0; i<size; i++) { // goal create
            if(i<(size-1) && goal.containsKey(this.free[i])) {
                a -= goal.get((this.free[i]));
            }
            for(int j=0; j<size0; j++){
                if(j<(size0-1) && goal.containsKey(this.base[j]))
                    a+=goal.get(this.base[j])*this.tableColumn[i][j];
            }
            f[i]=a;
            a =0;
        }
        return f; // пересчитать b
    }


    public void artificialBasis(){
        simplex();
        newSim();
        simplex();
    }




    /* DualSimplex*/

    /* MainElem*/
    public void mainElem1() { // главный элемент возращать
        min1(this.b);
        minPositive1(this.f, this.koef[mainRow]);

    }

    public boolean min1(double[] arr) { // колонку
        double min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (min > arr[i]) {
                min = arr[i];
                index = i;
            }
        }

        mainRow= index; //  и здесь
        return min < 0;
    }

    public void minPositive1(double[] b, double[] arr){ // строку
        double elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if((arr[i] < 0  && ((b[index]/elem > b[i]/arr[i]))) ||  arr[index]>0 ) {
                elem = arr[i];
                index = i;
            }
        }

        mainElem = elem;
        mainColumn = index;
    }




    public void dualSimplex(){
        while (min1(b)) { // f on b
            this.mainElem1();
            changeTable();
        }
    }

    public void gamory() {
        simplex();
        negative(table.length - 1);
        //  max(b);
        while(max(b)!=-1){
            // добавляем новое ограничение
            // преобразуем

            restrictions(); // новое ограничение
            dualSimplex();
        };
       // negative(table.length - 1);
    }


    void negative(int index){

        for(int i=0; i<table[0].length; i++){
            table[index][i]=table[index][i]*-1;
        }

    }

    int max(double[] arr){
        // если нет дроби то -1 возращаем
        double max = 0;
        int index = -1;
        double check = 0.001;
        for(int i=0; i<arr.length-1; i++){

            if( max<fractionPart(arr[i])){
                if((max-arr[i])<check) {
                    max = fractionPart(arr[i]);
                    index = i;
                }
            }
        }
        mainRow = index;
        return index;
    }

    double fractionPart(double number){
        BigDecimal n= new BigDecimal(String.valueOf(number));
        double ceil = n.setScale(0, RoundingMode.FLOOR).doubleValue();
        double result = number -ceil;
        if (result < 0.00001)
            return 0;
        else if(result>0.99) return 0;
        else return result;

    }
    void restrictions(){
        double[][] table = new  double[this.table.length+1][this.table[0].length];
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

    void addW( double[][] table){

        for(int i=0; i < table[0].length;i++){
            if(this.table[mainRow][i]>0){
                table[table.length-2][i] =
                        fractionPart(this.table[mainRow][i]);
                // выделяем дробную часть
            } else {
                BigDecimal ceil = new BigDecimal(String.valueOf(this.table[mainRow][i]));
                BigDecimal fr =  ceil.remainder( BigDecimal.ONE );
                ceil = ceil.subtract(fr);
                table[table.length-2][i]=(ceil.doubleValue()-1)*-1+
                        (this.table[mainRow][i]);
            }
        }
    }


}
