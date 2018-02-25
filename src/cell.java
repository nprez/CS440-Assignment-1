

public class cell {
	int x;
	int y;
	int g;
	int h;
	int f;
	char status;
	cell prev;
	boolean visited;
	
	public cell(int x, int y){
		this.x = x;
		this.y = y;
		g = Integer.MAX_VALUE;
		h = 0;	//set later to Manhattan distance
		f = Integer.MAX_VALUE;
		//unblocked = u, blocked = b
		status = 'u';
		prev = null;
		visited = false;
	}

	public cell(){
		x=0;
		y=0;
		g = Integer.MAX_VALUE;
		h = 0;	//set later to Manhattan distance
		f = Integer.MAX_VALUE;
		//unblocked = u, blocked = b
		status = 'u';
		prev = null;
		visited = false;
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
