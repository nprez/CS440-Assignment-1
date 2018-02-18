
public class cell {
	int g;
	int h;
	int f;
	char status;
	cell prev;
	
	public cell(){
		g = Integer.MAX_VALUE;
		h = 0;	//set later to Manhattan distance
		f = Integer.MAX_VALUE;
		//unblocked = u , blocked = b, unsure = ?
		status = '?';
		prev = null;
	}
}
