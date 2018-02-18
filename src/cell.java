
public class cell {
	int g;
	int h;
	int f;
	char status;
	cell prev;
	boolean visited;
	int x;
	int y;
	
	public cell(){
		g = Integer.MAX_VALUE;
		h = 0;	//set later to Manhattan distance
		f = Integer.MAX_VALUE;
		//unblocked = u , blocked = b, unsure = ?
		status = '?';
		prev = null;
		visited = false;
		x = 0;
		y = 0;
	}
	
	public void setCellCoordinates(int i, int j) {
		x = i;
		y = j;
	}
	
	public void setVisited() {
		visited = true;
	}
	public void setStatus(char c) {
		status = c;
	}
}
