import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GridWorld {
	cell[][] grid;
	int gID;
	int counter;
	Stack<cell> myStack;
	ArrayList<cell> closedSet;
	ArrayList<cell> openSet;
	char[][] statusGrid;
	
	public GridWorld(int gID) {
		grid = new cell[101][101];
		this.gID = gID;
		counter = 0;
		myStack = new Stack<cell>();
	}
	
	public int h_value(cell start, cell goal){
		int ret = 0;
		ret += Math.abs(start.x-goal.x);
		ret += Math.abs(start.y-goal.y);
		return ret;
	}
	
	public ArrayList<cell> getPath(cell end){
		ArrayList<cell> ret = new ArrayList<cell>();
		cell temp = end;
		while(temp != null){
			ret.add(0, temp);
			temp = temp.prev;
		}
		return ret;
	}
	
	//returns shortest path given currently known information
	public ArrayList<cell> A_Star(cell start, cell goal){
		while(!openSet.isEmpty()){
	    	//node in openSet with lowest f value
	    	cell current = openSet.get(0);
	    	for(cell c: openSet){
	    		if(c.f < current.f)
	    			current = c;
	    	}
	    	
	    	if(current == goal)
	    		return getPath(current);
	    	
	    	openSet.remove(current);
	    	closedSet.add(current);
	    	
	    	//list of all (assumed) unblocked neighbors
	    	ArrayList<cell> neighbors = new ArrayList<cell>();
	    	if(current.x < grid.length){
	    		//statusGrid[current.x+1][current.y] = grid[current.x+1][current.y].status;
	    		if(statusGrid[current.x+1][current.y] == 'u')
	    			neighbors.add(grid[current.x+1][current.y]);
	    	}
	    	if(current.x > 0){
	    		//statusGrid[current.x-1][current.y] = grid[current.x-1][current.y].status;
	    		if(statusGrid[current.x-1][current.y] == 'u')
	    			neighbors.add(grid[current.x-1][current.y]);
	    	}
	    	if(current.y < grid[current.x].length){
	    		//statusGrid[current.x][current.y+1] = grid[current.x][current.y+1].status;
	    		if(statusGrid[current.x][current.y+1] == 'u')
	    			neighbors.add(grid[current.x][current.y+1]);
	    	}
	    	if(current.y > 0){
	    		//statusGrid[current.x][current.y-1] = grid[current.x][current.y-1].status;
	    		if(statusGrid[current.x][current.y-1] == 'u')
	    			neighbors.add(grid[current.x][current.y-1]);
	    	}
	    	for(cell c: neighbors){
	    		if(closedSet.contains(c))
	    			continue;	// Ignore the neighbor which is already evaluated.
	    		
	            if (!openSet.contains(c))	// Discover a new node
	                openSet.add(c);
	            
	            // The distance from start to a neighbor
	            //the "dist_between" function may vary as per the solution requirements.
	            int tentative_gScore = current.g + 1;
	            if(tentative_gScore >= c.g)
	                continue;		// This is not a better path.
	
	            // This path is the best until now. Record it!
	            c.prev = current;
	            c.g = tentative_gScore;
	            c.f = c.g + c.h;
	    	}
	    }
		return null;
	}
	
	//returns short path using repeated calls to A*, gaining new information as it goes
	public ArrayList<cell> Repeated_Forward_A_Star(cell start, cell goal){
		// The set of nodes already evaluated
	    closedSet = new ArrayList<cell>();

	    // The set of currently discovered nodes that are not evaluated yet.
	    // Initially, only the start node is known.
	    openSet = new ArrayList<cell>();
	    openSet.add(start);
	    
	    //current information about the grid
	    //u: unblocked; b: blocked
	    statusGrid = new char[grid.length][grid[0].length];

	    for(int x=0; x<grid.length; x++){
	    	for(int y=0; y<grid[x].length; y++){
	    		// For each node, which node it can most efficiently be reached from.
	    		grid[x][y].prev = null;
	    		// For each node, the cost of getting from the start node to that node.
	    		grid[x][y].g = Integer.MAX_VALUE;
	    		// For each node, the total cost of getting from the start node to the goal
	    	    // by passing by that node. That value is partly known, partly heuristic.
	    		grid[x][y].f = Integer.MAX_VALUE;
	    		grid[x][y].h = h_value(grid[x][y], goal);
	    		
	    		statusGrid[x][y] = 'u';
	    	}
	    }

	    // The cost of going from start to start is zero.
	    start.g = 0;
	    // For the first node, the f value is completely heuristic.
	    start.f = start.h;
	    
	    
	    ArrayList<cell> idealPath = null;
	    cell current = start;
	    boolean searching = true;
	    while(searching){
	    	idealPath = A_Star(current, goal);
		    if(idealPath == null)
		    	return null;
		    
		    int i = 0;
		    while(current != goal){
		    	i++;
		    	cell next = idealPath.get(i);
		    	if(grid[next.x][next.y].status == 'b'){	//ideal path is blocked; recalculate
		    		statusGrid[next.x][next.y] = 'b';
		    		break;
		    	}
		    	current = next;
		    }
		    
		    if(current == goal)
		    	searching = false;
	    }

	    return idealPath;
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
					g.grid[x][y].setStatus(' ');
					//increment the counter to show one more cell has been visited
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
							g.grid[x][y].setStatus('X');
							g.counter++;
						} else if(blocked > 70) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus(' ');
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
							g.grid[x][y].setStatus('X');
							g.counter++;
						} else if(blocked > 70) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus(' ');
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
}