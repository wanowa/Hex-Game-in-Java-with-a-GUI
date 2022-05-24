//package swing;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Color;
import javax.swing.JButton;

class DrawButton extends JButton {
        private static final long serialVersionUID = 1L;
        private int row = 0;
        private int column = 0;
        private boolean isSelected = false;	//isSelected field'im, altigenimi boyayip boyamamam icin bir flag
        private static int turn = 0;	//turn%2 == 0 ise user1'in sirasi, turn%2 == 1 ise user2'nin sirasi. Her turda turn++ yapiyorum.
        private int initColor = 0;	//initColor bana polygon'umun rengini belli ediyor
        private boolean draw = false;	//draw false ise polygon'u ne ciziyorum, ne boyuyorum
        
        public DrawButton(int row, int column) {
            setContentAreaFilled(false);
            setFocusPainted(true);
            setBorderPainted(false);
            this.row = row;
            this.column = column;
            
        }
        
        public DrawButton() {
        	
        }

        @Override
        public void paintComponent(Graphics g) {	//Override ettigim paintComponent methodu
            if(draw) {	//draw false ise girmiyorum bile
            	super.paintComponent(g);
                Polygon hex = new Polygon();            
                hex.addPoint(24,66);		//elimle cizdigim altigenin koordinatlarini polygon'uma tek tek ekledim
                hex.addPoint(48, 48);
                hex.addPoint(48, 18);
                hex.addPoint(24, 0);
                hex.addPoint(0, 18);
                hex.addPoint(0, 48);
                

               if(isSelected == true) {
            	   if(initColor == 0) {
            		   if(turn%2 == 0) {
    	                   	g.setColor(Color.BLUE);
    	                   	initColor = 1;	//1 sayisi bana blue'yu temsil ediyor
    	               	}
            		   else if(turn%2 == 1) {
            			   g.setColor(Color.RED);
            			   initColor = 2;	//2 sayisi bana red'i temsil ediyor
            		   }
            	   }
            	   else {
            		   if(initColor == 1) {
            			   g.setColor(Color.BLUE);
            		   }
            		   else if(initColor == 2) {
            			   g.setColor(Color.RED);
            		   }
            	   }
                	g.fillPolygon(hex);
                	
                }
                g.drawPolygon(hex);
            }
        }

        public boolean getSelected() {
        	return isSelected;
        }
        
        public void setSelected(boolean var) {
        	isSelected = var;
        }
        
        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
        
        public int getTurn() {
        	return turn;
        }
        
        public void setTurn(int var) {
        	turn = var;
        }
        
        public int getInitColor() {
        	return initColor;
        }
        public void setInitColor(int var) {
        	initColor = var;
        }
        
        public boolean getDraw() {
        	return draw;
        }
        public void setDraw(boolean var) {
        	draw = var;
        }
}