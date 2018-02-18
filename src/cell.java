
public class cell {
	int g;
	int h;
	int f;
	char status;
	cell prev;
	
	public cell(){
		g = 0;
		h = 0;
		f = 0;
		//unblocked = u , bloocked = b, unsure = u
		status = 'u';
		prev = null;
	}
}
