
public class cell {
	int x;
	int y;
	int g;
	int h;
	int f;
	char status;
	cell prev;
	
	public cell(int x, int y){
		this.x = x;
		this.y = y;
		g = Integer.MAX_VALUE;
		h = 0;	//set later to Manhattan distance
		f = Integer.MAX_VALUE;
		//unblocked = u, blocked = b
		status = 'u';
		prev = null;
	}
}
