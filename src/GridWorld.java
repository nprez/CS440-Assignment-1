import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


public class GridWorld {
	static char unblocked = ' ';
	static char blocked = 'X';
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
	/*public ArrayList<cell> A_Star(cell start, cell goal){
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
	    	if(current.x < grid.length-1){
	    		//statusGrid[current.x+1][current.y] = grid[current.x+1][current.y].status;
	    		if(statusGrid[current.x+1][current.y] == unblocked)
	    			neighbors.add(grid[current.x+1][current.y]);
	    	}
	    	if(current.x > 0){
	    		//statusGrid[current.x-1][current.y] = grid[current.x-1][current.y].status;
	    		if(statusGrid[current.x-1][current.y] == unblocked)
	    			neighbors.add(grid[current.x-1][current.y]);
	    	}
	    	if(current.y < grid[current.x].length-1){
	    		//statusGrid[current.x][current.y+1] = grid[current.x][current.y+1].status;
	    		if(statusGrid[current.x][current.y+1] == unblocked)
	    			neighbors.add(grid[current.x][current.y+1]);
	    	}
	    	if(current.y > 0){
	    		//statusGrid[current.x][current.y-1] = grid[current.x][current.y-1].status;
	    		if(statusGrid[current.x][current.y-1] == unblocked)
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
	}*/
	
	/*//returns short path using repeated calls to A*, gaining new information as it goes
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
	    		
	    		statusGrid[x][y] = unblocked;
	    	}
	    }

	    // The cost of going from start to start is zero.
	    start.g = 0;
	    // For the first node, the f value is completely heuristic.
	    start.f = start.h;
	    
	    
	    ArrayList<cell> idealPath = null;
	    ArrayList<cell> ret = new ArrayList<cell>();
	    ret.add(start);
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
		    	if(grid[next.x][next.y].status == blocked){	//ideal path is blocked; recalculate
		    		statusGrid[next.x][next.y] = blocked;
		    		break;
		    	}
		    	if(ret.size()>0 && ret.get(ret.size()-1)!=current)
		    		ret.add(current);
		    	current = next;
		    }
		    
		    if(current == goal){
		    	ret.add(current);
		    	searching = false;
		    }
		    else{	//reset
		    	//while(!closedSet.isEmpty())
		    		//closedSet.remove(0);
			    
			    while(!openSet.isEmpty())
			    	openSet.remove(0);
			    openSet.add(current);
			    
			    for(int x=0; x<grid.length; x++){
			    	for(int y=0; y<grid[x].length; y++){
			    		cell c = grid[x][y];
			    		if(!ret.contains(c)){
			    			c.g = Integer.MAX_VALUE;
			    			c.f = Integer.MAX_VALUE;
			    		}
			    		else if(c.g != Integer.MAX_VALUE){
			    			cell temp = current;
			    			while(temp != c){
			    				grid[x][y].g--;
			    				temp = temp.prev;
			    			}
			    			c.f = c.g + c.h;
			    		}
			    		//grid[x][y].g = Integer.MAX_VALUE;
			    		//grid[x][y].f = Integer.MAX_VALUE;
			    	}
			    }
			    current.g = 0;
			    current.f = current.h;
		    }
	    }
	    
	    return ret;
	}*/
	
	//returns shortest path given currently known information
	public void A_Star(cell goal){
		cell min = null;
		if(!openSet.isEmpty()){
			min = openSet.get(0);
			for(int i=0; i<openSet.size(); i++){
				cell temp = openSet.get(i);
				if((temp.g + temp.h) < (min.g + min.h))
					min = temp;
			}
		}
		
		while((!openSet.isEmpty()) && goal.g > (min.g + min.h)){
			openSet.remove(min);
			closedSet.add(min);
			
			//list of all (assumed) unblocked neighbors
	    	ArrayList<cell> neighbors = new ArrayList<cell>();
	    	if(min.x < grid.length-1){
	    		//statusGrid[min.x+1][min.y] = grid[min.x+1][min.y].status;
	    		if(statusGrid[min.x+1][min.y] == unblocked)
	    			neighbors.add(grid[min.x+1][min.y]);
	    	}
	    	if(min.x > 0){
	    		//statusGrid[min.x-1][min.y] = grid[min.x-1][min.y].status;
	    		if(statusGrid[min.x-1][min.y] == unblocked)
	    			neighbors.add(grid[min.x-1][min.y]);
	    	}
	    	if(min.y < grid[min.x].length-1){
	    		//statusGrid[min.x][min.y+1] = grid[min.x][min.y+1].status;
	    		if(statusGrid[min.x][min.y+1] == unblocked)
	    			neighbors.add(grid[min.x][min.y+1]);
	    	}
	    	if(min.y > 0){
	    		//statusGrid[min.x][min.y-1] = grid[min.x][min.y-1].status;
	    		if(statusGrid[min.x][min.y-1] == unblocked)
	    			neighbors.add(grid[min.x][min.y-1]);
	    	}
	    	
	    	for(cell n: neighbors){
	    		if(closedSet.contains(n))
	    			continue;
	    		
	    		if(n.search < counter){
	    			//n.g = Integer.MAX_VALUE;
	    			n.search = counter;
	    		}
	    		if(n.g > (min.g + 1)){
	    			n.g = min.g + 1;
	    			n.prev = min;
	    		}
	    		if(openSet.contains(n))
    				openSet.remove(n);
    			n.f = n.g + n.h;
    			openSet.add(n);
	    		
	    	}
	    	if(!openSet.isEmpty()){
				min = openSet.get(0);
				for(int i=0; i<openSet.size(); i++){
					cell temp = openSet.get(i);
					if((temp.g + temp.h) < (min.g + min.h))
						min = temp;
					else if((temp.g + temp.h) == (min.g + min.h))
						if(temp.h < min.h)
							min = temp;
				}
			}
		}
		if(goal.g <= (min.g + min.h)){
			openSet.add(min);
		}
	}
	
	//returns short path using repeated calls to A*, gaining new information as it goes
	public ArrayList<cell> Repeated_Forward_A_Star(cell start, cell goal){
		counter = 0;
		// The set of nodes already evaluated
	    closedSet = new ArrayList<cell>();

	    // The set of currently discovered nodes that are not evaluated yet.
	    // Initially, only the start node is known.
	    openSet = new ArrayList<cell>();
	    
	    //current information about the grid
	    //u: unblocked; b: blocked
	    statusGrid = new char[grid.length][grid[0].length];
	    for(int i=0; i<statusGrid.length; i++){
	    	for(int j=0; j<statusGrid[0].length; j++){
	    		statusGrid[i][j] = unblocked;
	    	}
	    }
	    
	    ArrayList<cell> ret = new ArrayList<cell>();
	    ret.add(start);
	    
	    for(int x=0; x<grid.length; x++){
	    	for(int y=0; y<grid[x].length; y++){
	    		grid[x][y].h = h_value(grid[x][y], goal);
	    		grid[x][y].g = Integer.MAX_VALUE;
	    	}
	    }
		
		while(start != goal){
			counter++;
			start.g = 0;
			start.f = start.h;
			start.search = counter;
			goal.g = Integer.MAX_VALUE;
			goal.search = counter;
			while(!openSet.isEmpty())
		    	openSet.remove(0);
			while(!closedSet.isEmpty())
		    	closedSet.remove(0);
			openSet.add(start);
			A_Star(goal);
			ArrayList<cell> idealPath = getPath(goal);
			System.out.println(idealPath);
			if(openSet.isEmpty())
				return null;
			int index = 0;
			boolean foundBlock = false;
			cell reset = start;
			while(start != goal){
		    	index++;
		    	cell next = idealPath.get(index);
		    	if(grid[next.x][next.y].status == blocked && !foundBlock){	//ideal path is blocked; recalculate
		    		statusGrid[next.x][next.y] = blocked;
		    		foundBlock = true;
		    		reset = start;
		    		if(ret.size()>0 && ret.get(ret.size()-1)!=start)
			    		ret.add(start);
		    		grid[next.x][next.y].g = Integer.MAX_VALUE;
		    		grid[next.x][next.y].prev = null;
		    		//updated the increased action costs
		    		//break;
		    	}
		    	if(!foundBlock && ret.size()>0 && ret.get(ret.size()-1)!=start)
		    		ret.add(start);
		    	start = next;
		    }
			if(foundBlock)
				start = reset;
			for(int i=0; i<grid.length; i++){
				for(int j=0; j<grid[0].length; j++){
					if(!ret.contains(grid[i][j]))
						grid[i][j].g = Integer.MAX_VALUE;
				}
			}
		}
		
		ret.add(goal);
		
	    return ret;
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
		} else if(r == 4 && options.indexOf(unblocked) >= 0) {
			if(!g.grid[x + 1][y].visited) {
				return true;
			}
		}
		return false;
	}
	
	public static void printGridWorld(GridWorld g) {
		for(int j = 0; j < 101; j++) {
			for(int i = 0; i < 101; i++) {
				System.out.print(g.grid[j][i].status);
			}
			System.out.println();
		}
	}
	
	public static void printPath(ArrayList<cell> path){
		if(path==null){
			System.out.println("No path found");
			return;
		}
		for(int i=0; i<path.size(); i++){
			cell c = path.get(i);
			if(i!=0){
				System.out.print(" > ");
			}
			System.out.print("("+c.x+", "+c.y+")");
		}
		System.out.println();
	}
	
	public static void nickTester(){
		Random rand = new Random();
		
		GridWorld gw = new GridWorld(696969);
		int size = 6;
		gw.grid = new cell[size][size];
		for(int i=0; i<size; i++){
			for(int j=0; j<size; j++){
				gw.grid[i][j] = new cell(i, j);
				int r = rand.nextInt(4);
				//if(r==0)
					//gw.grid[i][j].status = blocked;
			}
		}
		
		//cell safe1 = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		//cell safe2 = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		cell safe1 = gw.grid[0][0];
		cell safe2 = gw.grid[5][5];
		while(safe2 == safe1){
			safe2 = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		}
		safe1.status = unblocked;
		safe2.status = unblocked;
		
		//cell start = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		cell start = gw.grid[0][0];
		while(start.status == blocked){
			start = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		}
		//cell goal = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		cell goal = gw.grid[5][5];
		while(goal.status == blocked || goal==start){
			goal = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		}
		
		gw.grid[1][0].status = blocked;
		gw.grid[2][0].status = blocked;
		gw.grid[3][0].status = blocked;
		gw.grid[4][0].status = blocked;
		gw.grid[5][0].status = blocked;
		gw.grid[5][1].status = blocked;
		gw.grid[5][2].status = blocked;
		gw.grid[5][3].status = blocked;
		gw.grid[5][4].status = blocked;
		
		gw.grid[0][1].status = unblocked;
		gw.grid[0][2].status = unblocked;
		gw.grid[0][3].status = unblocked;
		gw.grid[0][4].status = unblocked;
		gw.grid[0][5].status = unblocked;
		gw.grid[1][5].status = unblocked;
		gw.grid[2][5].status = unblocked;
		gw.grid[3][5].status = unblocked;
		gw.grid[4][5].status = unblocked;
		
		for(int j = 0; j < size; j++) {
			for(int i = 0; i < size; i++) {
				if(gw.grid[i][j]==start)
					System.out.print('S');
				else if(gw.grid[i][j]==goal)
					System.out.print('G');
				else
					System.out.print(gw.grid[i][j].status);
			}
			System.out.println();
		}
		
		ArrayList<cell> answer = gw.Repeated_Forward_A_Star(start, goal);
		
		printPath(answer);
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
					g.grid[x][y].setStatus(unblocked);
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
							options = options.replace(unblocked, 'q');
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
						int blockedNum = rand.nextInt(100);
						if(blockedNum <= 30) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus(blocked);
							g.counter++;
						} else if(blockedNum > 70) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus(unblocked);
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
							options = options.replace(unblocked, 'q');
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
						int blockedNum = rand.nextInt(100);
						if(blockedNum <= 30) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus(blocked);
							g.counter++;
						} else if(blockedNum > 70) {
							g.grid[x][y].setVisited();
							g.grid[x][y].setStatus(unblocked);
							g.myStack.push(g.grid[x][y]);
							g.counter++;
						}
					}
				}

			}
		}
		/*for(int i = 0; i < 50; i++) {
			printGridWorld(workSpace[i]);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}*/
		
		for(int i=0; i<3; i++)
			nickTester();
	
	}
}