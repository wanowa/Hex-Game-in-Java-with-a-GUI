//package swing;

public interface HexGame {
	int is_x_win(int row,int column);
	int is_o_win(int row,int column);	
	void drawHexs();
	void play(int row, int column);
	void startBoard();
	int getBoardsize();
	boolean winCheck();
}