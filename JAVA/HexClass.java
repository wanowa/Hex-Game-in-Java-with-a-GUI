//package swing;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HexClass extends JPanel implements HexGame,Cloneable {
   
    class Cell{     //icerisinde birer setter/getter bulunan Cell class'im
        private char inside;
        public Cell() {
            this.inside = '.';
        }
        public void setInside(char c){  inside = c; }
        public char getInside() {   return inside;  }
    }
    
    private static final long serialVersionUID = 1L;
    private static final int DIAMETER = 48;     //Olusturdugum altigen'in capi
    private static DrawButton[][] buttons = new DrawButton[16][16]; //Butonlarim
    private static int boardsize;
    private static int[][] path = new int[16][16];  //Kazanani kontrol ederken kullandigim integer arrayim
    private static int[] score = new int[2];        //0. index user1 icin, 1. index user2 icin skor tutuyor.
    private static int lastRow;     //son oynanilan hamlenin satiri
    private static int lastColumn;  //son oynanilan hamlenin sutunu
    private static Cell[][] hexCells = new Cell[16][16];    //icinde 'x','o' ve '.' bulunan Cell tipinde arrayim
    private static boolean canUndo = false;     //Undo isleminin yapilip yapilamayacagini belirten flag

    public HexClass() {
        setLayout(null);
        //drawHexs();
    }


    public void drawHexs() {
        
        int x_coor = 90;    //baslangic x koordinatim
        int y_coor = 100;   //baslangic y koordinatim
        for(int row = 0; row < boardsize; row++) {  //boardsize*boardsize kadar yeni buton olusturuyor.
            for(int column = 0; column < boardsize; column++){
                buttons[row][column] = new DrawButton(row, column);
                buttons[row][column].setDraw(true);     //Boardsize degistiginde out of range'de kalan buttons'larin setDraw'ini false yapiyorum, 
                buttons[row][column].addActionListener(new ActionListener() {   //böylelikle onlari yazdirmiyor
                    public void actionPerformed(ActionEvent e) {
                        DrawButton clickedButton = (DrawButton) e.getSource();
                        lastRow = clickedButton.getRow();           //tiklanilan butonun indexini aliyorum
                        lastColumn = clickedButton.getColumn();
                        if(hexCells[lastRow][lastColumn].getInside() == '.') {  //eger tiklanilan hucrede '.' disinda bir karakter varsa hicbir islem gerceklestirilmiyor
                            canUndo = true;     //hucrede nokta varsa hamle basariyla yapilmistir, artik Undo islemi gerceklestirilebilir
                            play(lastRow,lastColumn);   //belirtilen index'e oynuyor
                            if(winCheck()) {    //winCheck() method'u true donerse kazanan var demektir
                                if(buttons[0][0].getTurn()%2 == 0) {
                                    JOptionPane.showMessageDialog(null, "RED WON!!");
                                }
                                
                                else {
                                    JOptionPane.showMessageDialog(null, "BLUE WON!!");
                                }
                                
                                for(int i=0;i<15;i++) {
                                    for(int j=0;j<15;j++) {
                                        buttons[i][j].setSelected(false);
                                        buttons[i][j].setInitColor(0);
                                        buttons[i][j].setDraw(false);
                                    }
                                }
                                
                                boardsize = Integer.parseInt(JOptionPane.showInputDialog("Enter new boardsize between 5 and 15 :"));    
                                while(boardsize<5 || boardsize>15) {    //boardsize valid olana kadar kullanicidan istiyorum
                                    JOptionPane.showMessageDialog(null, "Entered boardsize is invalid, try again.","Error",JOptionPane.WARNING_MESSAGE);
                                    boardsize = Integer.parseInt(JOptionPane.showInputDialog("Enter new boardsize between 5 and 15 :"));
                                }
                                buttons[0][0].setTurn(0);
                                lastRow = 16;
                                lastColumn = 16;
                                startBoard();
                                drawHexs();
                            }
                            else {
                                buttons[lastRow][lastColumn].setSelected(true);
                                buttons[lastRow][lastColumn].setTurn(buttons[lastRow][lastColumn].getTurn() + 1);
                            }
                        }
                    }
                });
                add(buttons[row][column]);  //pencereme butonu ekliyorum
                buttons[row][column].setBounds(x_coor, y_coor, 50, 68); //altigenimin kenar uzunluguna gore boyutunu ayarliyorum
                x_coor = x_coor + DIAMETER;     //Tam bir petek sekli cikmasi icin döngü her döndügünde altigenimi yatay genisligi kadar sağa otelemem gerekir
            }
            x_coor = x_coor - (boardsize*DIAMETER - DIAMETER/2);    //alt satira gecmek gerektiginde altigenimi sola oteliyorum
            y_coor = y_coor + DIAMETER; //alt satira gectigim icin altigenimi asagi da oteliyorum
        }
    }

    
///////////////////////////////////////////////////////////
    public static void main(String[] args) {
        
        final HexClass hex = new HexClass();
        hex.startBoard();
        
        JFrame frame = new JFrame();
        frame.setTitle("Hex Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(-10,0);
        
        JPanel panel1 = new JPanel();
        frame.getContentPane().add(panel1);
        panel1.setLayout(new GridLayout(1,3,20,20));
        panel1.setBounds(10, 20, 450, 30);
        
        JPanel panel2 = new JPanel();
        frame.getContentPane().add(panel2);
        panel2.setLayout(new GridLayout(2,1,20,10));
        panel2.setBounds(500, 10, 100, 60);
        
        
        JPanel panel3 = new JPanel();
        frame.getContentPane().add(panel3);
        panel3.setLayout(new GridLayout(2,1,20,10));
        panel3.setBounds(700, 10, 150, 60);
        
        
        JPanel panel4 = new JPanel();
        frame.getContentPane().add(panel4);
        panel4.setLayout(new GridLayout(3,3,20,20));
        panel4.setBounds(950, 10, 600, 150);
        
        final JLabel label1 = new JLabel("Enter Boardsize :");
        final JTextField text1 = new JTextField(10);
        final JButton sizeButton = new JButton("Enter");
        JButton loadButton = new JButton("Load Game");
        JButton saveButton = new JButton("Save Game");
        JButton undoButton = new JButton("Undo One Step");
        JButton resetButton = new JButton("Reset Game");
        JCheckBox cb1 = new JCheckBox("Get User1's (Red) Score");
        JCheckBox cb2 = new JCheckBox("Get User2's (Blue) Score");
        JRadioButton rb1 = new JRadioButton("User vs User",true);
        final JRadioButton rb2 = new JRadioButton("User vs Computer");
        JButton versusButton = new JButton("Vs.");
        JLabel empty = new JLabel("");
        final JLabel score1 = new JLabel("User1's (Red) Score is :" + score[0]);
        final JLabel score2 = new JLabel("User2's (Blue) Score is :" + score[1]);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rb1);
        bg.add(rb2);
        
        score1.setVisible(false);
        score2.setVisible(false);
        
        versusButton.setBounds(1300, 10, 100, 30);
        
        panel1.add(label1);
        panel1.add(text1);
        panel1.add(sizeButton);
        
        panel2.add(saveButton);
        panel2.add(loadButton);
        
        panel3.add(resetButton);
        panel3.add(undoButton);
        
        panel4.add(rb1);
        panel4.add(rb2);
        panel4.add(versusButton);
        panel4.add(cb1);
        panel4.add(cb2);
        panel4.add(empty);
        panel4.add(score1);
        panel4.add(score2);
        
        
        sizeButton.addActionListener(new ActionListener() { //size butonum
            
            public void actionPerformed(ActionEvent e) {
                
                final int temp = Integer.parseInt(text1.getText());
                if(temp<5 || temp>15) {
                    JOptionPane.showMessageDialog(null, "Boardsize should be between 5 and 15.","Error",JOptionPane.WARNING_MESSAGE);
                }
                else {
                    boardsize = temp;
                    hex.drawHexs();
                    sizeButton.setVisible(false);
                    text1.setVisible(false);
                    label1.setText("Boardsize = " + boardsize);
                }
            }
        });
        
        resetButton.addActionListener(new ActionListener() {    //reset butonum
            public void actionPerformed(ActionEvent e) {
                score[0] = 0;
                score[1] = 0;
                canUndo = false;
                for(int i=0;i<boardsize;i++) {
                    for(int j=0;j<boardsize;j++) {
                        hexCells[i][j].setInside('.');;
                        path[i][j]=0;
                        buttons[i][j].setSelected(false);
                        buttons[i][j].setInitColor(0);
                        buttons[0][0].setTurn(0);
                        lastRow = 16;
                        lastColumn = 16;
                    }
                }
            }
        });
        
        undoButton.addActionListener(new ActionListener() { //Undo butonum
            public void actionPerformed(ActionEvent e) {
                if(canUndo) {
                    hexCells[lastRow][lastColumn].setInside('.');
                    buttons[lastRow][lastColumn].setSelected(false);
                    buttons[lastRow][lastColumn].setInitColor(0);
                    buttons[0][0].setTurn(buttons[0][0].getTurn()-1);
                    canUndo = false;
                }
                else {
                    JOptionPane.showMessageDialog(null, "You haven't played yet or You can't Undo over and over");
                }
            }
        });
        
        versusButton.addActionListener(new ActionListener() {   //user vs user - user vs computer bilgisi alan butonum
            public void actionPerformed(ActionEvent e) {
                if(rb2.isSelected()) {
                    JOptionPane.showMessageDialog(null, "User vs Computer feature coming soon...");
                }
            }
        });
        
        cb1.addItemListener(new ItemListener() {    //check box'umun degisimini kontrol eden methodum
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    score1.setText("User1's (Red) Score is :" + score[0]);
                    score1.setVisible(true);
                }
                else {
                    score1.setVisible(false);
                }
            }
        });
        cb2.addItemListener(new ItemListener() {    //check box'umun degisimini kontrol eden methodum
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    score2.setText("User2's (Blue) Score is :" + score[1]);
                    score2.setVisible(true);
                }
                else {
                    score2.setVisible(false);
                }
            }
        });

        frame.add(hex);
        frame.setSize(1920, 1080);
        frame.setResizable(false);
        frame.setVisible(true);
    }
//////////////////////////////////////////////////////////////////    
    
    public Object clone() throws CloneNotSupportedException {       //Override edilmiş clone methodum
        HexClass temp = (HexClass) super.clone();
        for(int i=0;i<16;i++) {
            for(int j=0;j<16;j++) {
                HexClass.hexCells[i][j] = new Cell();
                HexClass.hexCells[i][j].inside = hexCells[i][j].inside;
            }
        }
        return temp;
    }
    
    public void play(int row, int column) {     //User icin bir hamle oynayan play methodum
        if(buttons[0][0].getTurn()%2 == 0) {
            hexCells[row][column].setInside('x');
        }
        else {
            hexCells[row][column].setInside('o');
        }
        
    }
    
    public void startBoard() {          //Board'i default haline getiriyor.
        score[0] = 0;
        score[1] = 0;
        for(int i=0;i<16;i++) {
            for(int j=0;j<16;j++) {
                buttons[i][j] = new DrawButton(i, j);
                hexCells[i][j] = new Cell();
                hexCells[i][j].setInside('.');
                path[i][j]=0;
                buttons[i][j].setSelected(false);
                buttons[i][j].setInitColor(0);
            }
        }
    }
    
    public int getBoardsize() {
        return boardsize;
    }


    public boolean winCheck(){  //Kazanma durumlarini kontrol ediyor
        int row=0,column=0,finish;
        if(buttons[0][0].getTurn() %2 == 0){        //User1'in kazanma durumunu kontrol ediyorum
            for(row=0;row<boardsize;row++){     //Eger sol duvardaki hucrelerden birinde 'x' varsa
                if(hexCells[row][column].getInside()=='x'){ //Kontrol etmeye basliyor, aksi takdirde geciyor.
                    finish=is_x_win(row,column);    //is_x_win fonksiyonu 0 disinda bir deger gonderirse
                    if(finish!=0){                  //User1 kazanmis demektir.
                        return true;    //Kazanan oldugunu belirtmek icin true donduruyorum.
                    }
                }
            }
        }

        else if(buttons[0][0].getTurn() %2 == 1){       //User1'in kazanma durumunu kontrol ediyorum
            for(column=0;column<boardsize;column++){    //Eger ust duvardaki hucrelerden birinde 'o' varsa
                if(hexCells[row][column].getInside()=='o'){ //Kontrol etmeye basliyor, aksi takdirde geciyor.
                    finish=is_o_win(row,column);    //is_o_win fonksiyonu 0 disinda bir deger gonderirse
                    if(finish!=0){              //User2 kazanmis demektir.
                        return true;    //Kazanan oldugunu belirtmek icin true donduruyorum.
                    }
                }
            }
        }
        return false;   //Kazanan yoksa false donduruyor.
    }
    
    public int is_x_win(int row,int column){
        int var=0;              //Bu fonksiyonda harf aramayi soldan saga dogru yapiyorum. 

        if(hexCells[row][column].getInside()!='x'){ //Bakilan hucrede x yoksa 0 dondur
            return 0;
        }
                                //Bakilan hucrede x varsa gelinen yolu unutmamak icin path
        path[row][column]=1;    //arrayinin ayni hucresine 1 koyuyorum.
        score[0]+=5;                //Eger ilk column'dan son column'a kadar gelinebilmisse
        if(column==boardsize-1){    //x dogru bir path kurmus demektir
            return 1;               //Galibiyet durumunda 1 dondur
        }

        if(row==0&&column==0){      //Buradaki if else bloklarinda tahtanin her bir ayri kosesi, duvari icin
            var=is_x_win(row,column+1); //ayri bir durumu kontrol etmem gerekti.
                                    //Mesela mevcut konum tahtanin sol ust kosedeki hucresiyse, bir hucre ustu veya
            if(var==0){             //bir hucre solu kontrol edemem, out of board'i kontrol etmis olurum.
                path[row][column]=0;//Tahtanin her spesifik kosesi/duvari icin farkli kontrol mekanizmasi.
            }                       //Yanlis yollardan donup dogru yola girebilmek icin recursion kullandim.
            return var;
        }
        else if(row==boardsize-1&&column==0){
            if(path[row][column+1]!=1){
                var=is_x_win(row,column+1);
            }
            if(path[row-1][column+1]!=1){
                var=var+is_x_win(row-1,column+1);
            }
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(row==0){
            if(path[row][column+1]!=1){
                var=var+is_x_win(row,column+1);
            }
            if(path[row+1][column]!=1){
                var=var+is_x_win(row+1,column);
            }       
            if(path[row+1][column-1]!=1){
                var=var+is_x_win(row+1,column-1);
            }
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(column==0){
            if(path[row-1][column+1]!=1){
                var=var+is_x_win(row-1,column+1);
            }       
            if(path[row][column+1]!=1){
                var=var+is_x_win(row,column+1);
            }
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(row==boardsize-1){
            if(path[row-1][column]!=1){
                var=var+is_x_win(row-1,column);
            }       
            if(path[row-1][column+1]!=1){
                var=var+is_x_win(row-1,column+1);
            }
            if(path[row][column+1]!=1){
                var=var+is_x_win(row,column+1);
            }       
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else{
            if(path[row-1][column+1]!=1){
                var=var+is_x_win(row-1,column+1);
            }       
            if(path[row][column+1]!=1){
                var=var+is_x_win(row,column+1);
            }
            if(path[row+1][column]!=1){
                var=var+is_x_win(row+1,column);
            }
            if(path[row-1][column]!=1){
                var=var+is_x_win(row-1,column);
            }
            if(path[row][column-1]!=1){
                var=var+is_x_win(row,column-1);
            }
            if(path[row+1][column-1]!=1){
                var=var+is_x_win(row+1,column-1);
            }       
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }   
    }

    public int is_o_win(int row,int column){
        int var=0;                  //is_x_win fonksiyonunun 'o' icin duzenlenmis hali.
        if(hexCells[row][column].getInside()!='o'){ //Mantik ayni.
            return 0;
        }
        
        path[row][column]=1;
        score[1]+=5;
        
        if(row==boardsize-1){
            return 1;
        }

        if(row==0&&column==0){
            var=is_o_win(row+1,column);
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(row==0&&column==boardsize-1){
            if(path[row+1][column]!=1){
                var=is_o_win(row+1,column);
            }
            if(path[row+1][column-1]!=1){
                var=var+is_o_win(row+1,column-1);
            }
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(row==0){
            if(path[row+1][column]!=1){
                var=var+is_o_win(row+1,column);
            }       
            if(path[row+1][column-1]!=1){
                var=var+is_o_win(row+1,column-1);
            }
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(column==0){
            if(path[row-1][column+1]!=1){
                var=var+is_o_win(row-1,column+1);
            }       
            if(path[row][column+1]!=1){
                var=var+is_o_win(row,column+1);
            }
            if(path[row+1][column]!=1){
                var=var+is_o_win(row+1,column);
            }
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else if(column==boardsize-1){
            if(path[row+1][column]!=1){
                var=var+is_o_win(row+1,column);
            }       
            if(path[row+1][column-1]!=1){
                var=var+is_o_win(row+1,column-1);
            }
            if(path[row][column-1]!=1){
                var=var+is_o_win(row,column-1);
            }       
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }
        else{
            if(path[row-1][column+1]!=1){
                var=var+is_o_win(row-1,column+1);
            }
            if(path[row][column+1]!=1){
                var=var+is_o_win(row,column+1);
            }
            if(path[row+1][column]!=1){
                var=var+is_o_win(row+1,column);
            }
            if(path[row-1][column]!=1){
                var=var+is_o_win(row-1,column);
            }
            if(path[row][column-1]!=1){
                var=var+is_o_win(row,column-1);
            }
            if(path[row+1][column-1]!=1){
                var=var+is_o_win(row+1,column-1);
            }       
            
            if(var==0){
                path[row][column]=0;
            }
            return var;
        }   
    }
}