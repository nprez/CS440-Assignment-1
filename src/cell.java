

public class cell {
	int x;
	int y;
	int g;
	int h;
	int search;
	char status;
	cell prev;
	boolean visited;
	
	public cell(){
		this(0, 0);
	}
	
	public cell(int x, int y){
		this.x = x;
		this.y = y;
		g = Integer.MAX_VALUE;
		h = 0;	//set later to Manhattan distance
		//blocked or unblocked
		status = GridWorld.unblocked;
		prev = null;
		visited = false;
		search = 0;
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
	
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	public boolean equals(cell c){
		return x==c.x && y==c.y;
	}
}
