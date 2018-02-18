import java.util.Random;
import java.util.Stack;


public class GridWorld {
	cell[][] grid;
	int gID;

	public GridWorld(int gID) {
		grid = new cell[101][101];
		this.gID = gID;
	}


	public static void main(String[] args) {
		//GridWorld gridWorld = new GridWorld();
		GridWorld[] workSpace = new GridWorld[50];
		for(int i = 0; i < 50; i++) {
			workSpace[i] = new GridWorld(i);
		}

		//this is how to setup the enviroment (must do for all grids)

		for(GridWorld g: workSpace) {
			Stack myStack = new Stack();
			Random rand = new Random();
			int x  = rand.nextInt(101);
			int y = rand.nextInt(101);
			g.grid[x][y].visited = true;
			g.grid[x][y].setStatus('u');
			
			int c = 0;
			
			do {
				if(c == 1) {
					x-=1;
				} else if(c == 2) {
					x+=1;
				} else if(c == 3) {
					y-=1;
				} else if(c == 4) {
					y+=1;
				}
				else {
					
				}
				int r = rand.nextInt(100);
				if(r < 25) {
					x+=1;
					c = 1;
				} else if(r >=25 && r<50) {
					x-=1;
					c = 2;
				} else if( r >=50 && r < 75) {
					y+=1;
					c = 3;
				} else if(r >-75) {
					y-=1;
					c = 4;
				}
			} while(g.grid[x][y].visited);
			
			int r = rand.nextInt(100);
			
			if(r <= 30) {
				g.grid[x][y].setStatus('b');
			} else if(r > 30) {
				g.grid[x][y].setStatus('u');
				myStack.push(g.grid[x][y]);
			}
		}



		//derp derp

	}
}
