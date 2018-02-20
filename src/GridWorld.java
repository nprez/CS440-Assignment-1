import java.util.Random;
import java.util.Stack;


public class GridWorld {
	cell[][] grid;
	int gID;
	int counter;
	Stack<cell> myStack;

	public GridWorld(int gID) {
		grid = new cell[101][101];
		this.gID = gID;
		counter = 0;
		myStack = new Stack<cell>();
	}


	public static void main(String[] args) {
		//create the workspace for the 50 gridworlds
		GridWorld[] workSpace = new GridWorld[50];

		//create 50 gridworlds to put in the array and within the gridworlds initiate all the cells
		for(int i = 0; i < 50; i++) {
			GridWorld g = new GridWorld(i);
			for(int j = 0; j < 101; j++) {
				for(int k = 0; k < 101; k++) {
					g.grid[j][k] = new cell();
					g.grid[j][k].setCellCoordinates(j, k);
				}
			}
			workSpace[i] = g;
		}
		
		Random rand = new Random();

		//have to setup the gridworld "maze" for each gridworld in the workspace
		for(GridWorld g: workSpace) {
			//need to visit all 101x101 cells
			int x;
			int y;
			while(g.counter != (101*101)) {
				//if the stack is empty we want to just pick a random unvisited cell and start from there
				if(g.myStack.isEmpty()) {
					//start by picking a random cell
					cell starter = selectRandomCell(g);
					x = starter.x;
					y = starter.y;
					//if we pick a cell thats already been visited we want to pick another one at random until we find one that hasnt
					while(g.grid[x][y].visited) {
						starter = selectRandomCell(g);
						x = starter.x;
						y = starter.y;
					}
					//mark the random cell as visited and unblocked

					g.grid[x][y].setVisited();
					g.grid[x][y].setStatus('u');
					//incrimint the counter to show one more cell has been visited
					g.counter++;

					//select a random neighbor
					//1 = move left, 2 = move right, 3 = go up, 4 = go down
					//x is up and down, y is left or right
					while(!deadEnd(g, x, y)) {
						String options = "lrud";
						if(y - 1 < 0) {
							options = options.replace('l', 'q');
						}
						if(y + 1 >= 101) {
							options = options.replace('r', 'q');
						}
						if(x + 1 >=101 ) {
							options = options.replace('u', 'q');
						}
						if(x - 1 < 0) {
							options = options.replace('d', 'q');
						}

						int r;
						do {
							r = rand.nextInt(4) +  1;
						}while(!randomNeighborChecker(g, x, y, options, r));

						//now that we know which way we are going we can move where we need to go
						if(r == 1) {
							y-=1;
						}
						else if(r == 2) {
							y+=1;
						}
						else if(r == 3) {
							x-=1;
						}
						else if(r == 4) {
							x+=1;
						}

						//the new coordinates of the cell to work on have been chosen
						//with 30% chance mark the cell as blocked, with 70% chance mark the cell as unblocked and add to stack
						int blocked = rand.nextInt(100);
						if(blocked <= 30) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus('b');
							g.counter++;
						} else if(blocked > 70) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus('u');
							g.myStack.push(g.grid[x][y]);
							g.counter++;
						}
					}

				}

				//if the stack is not empty we do the same exact process 
				//only difference is we dont choose a random cell we choose the one we popped from the stack
				else {

					cell temp = g.myStack.pop();
					x = temp.x;
					y = temp.y;


					//select a random neighbor
					//1 = move left, 2 = move right, 3 = go up, 4 = go down
					//x is up and down, y is left or right
					while(!deadEnd(g, x, y)) {
						String options = "lrud";
						if(y - 1 < 0) {
							options = options.replace('l', 'q');
						}
						if(y + 1 >= 101) {
							options = options.replace('r', 'q');
						}
						if(x + 1 >=101 ) {
							options = options.replace('u', 'q');
						}
						if(x - 1 < 0) {
							options = options.replace('d', 'q');
						}

						int r;
						do {
							r = rand.nextInt(4) +  1;
						}while(!randomNeighborChecker(g, x, y, options, r));

						//now that we know which way we are going we can move where we need to go
						if(r == 1) {
							y-=1;
						}
						else if(r == 2) {
							y+=1;
						}
						else if(r == 3) {
							x-=1;
						}
						else if(r == 4) {
							x+=1;
						}

						//the new coordinates of the cell to work on have been chosen
						//with 30% chance mark the cell as blocked, with 70% chance mark the cell as unblocked and add to stack
						int blocked = rand.nextInt(100);
						if(blocked <= 30) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus('b');
							g.counter++;
						} else if(blocked > 70) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus('u');
							g.myStack.push(g.grid[x][y]);
							g.counter++;
						}
					}
				}

			}
		}
		for(int i = 0; i < 50; i++) {
			printGridWorld(workSpace[i]);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}
	
	}




	public static cell selectRandomCell(GridWorld g) {
		Random rand = new Random();
		int x = rand.nextInt(101);
		int y = rand.nextInt(101);
		cell ret = g.grid[x][y];
		ret.setCellCoordinates(x, y);
		return ret;
	}

	public static boolean deadEnd(GridWorld g, int x, int y) {
		boolean ret = false;
		if(x + 1 < 101) {
			if(g.grid[x + 1][y].visited) {
				ret = true;
			} else {
				ret = false;
			}
		}
		if(x - 1 >= 0) {
			if(g.grid[x - 1][y].visited) {
				ret = true;
			} else {
				ret = false;
			}
		}
		if(y + 1 < 101) {
			if(g.grid[x][y+1].visited) {
				ret = true;
			} else {
				ret = false;
			}
		}
		if(y - 1 >= 0) {
			if(g.grid[x][y-1].visited) {
				ret = true;
			} else {
				ret = false;
			}
		}
		return ret;
	}

	public static boolean randomNeighborChecker(GridWorld g, int x, int y, String options, int r) {
		if(r == 1 && options.indexOf('l') >= 0) {
			if(!g.grid[x][y - 1].visited) {
				return true;
			}
		} else if(r == 2 && options.indexOf('r') >= 0) {
			if(!g.grid[x][y + 1].visited) {
				return true;
			}
		} else if(r == 3 && options.indexOf('d') >= 0) {
			if(!g.grid[x - 1][y].visited) {
				return true;
			}
		} else if(r == 4 && options.indexOf('u') >= 0) {
			if(!g.grid[x + 1][y].visited) {
				return true;
			}
		}
		return false;
	}

	public static void printGridWorld(GridWorld g) {
		for(int i = 0; i < 101; i++) {
			for(int j = 0; j < 101; j++) {
				System.out.print(g.grid[i][j].status);
			}
			System.out.println();
		}
	}

}