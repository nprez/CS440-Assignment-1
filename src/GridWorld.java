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
	//if deep is true, prefers larger g values (smaller h values)
	//otherwise, prefers smaller g values (larger h values)
	public void A_Star(cell goal, boolean deep){
		cell min = null;
		if(!openSet.isEmpty())
			min = openSet.get(0);
		
		while((!openSet.isEmpty()) && goal.g > (min.g + min.h)){
			openSet.remove(min);
			closedSet.add(min);
			
			//list of all (assumed) unblocked neighbors
	    	ArrayList<cell> neighbors = new ArrayList<cell>();
	    	if(min.x < grid.length-1){
	    		if(statusGrid[min.x+1][min.y] == unblocked)
	    			neighbors.add(grid[min.x+1][min.y]);
	    	}
	    	if(min.x > 0){
	    		if(statusGrid[min.x-1][min.y] == unblocked)
	    			neighbors.add(grid[min.x-1][min.y]);
	    	}
	    	if(min.y < grid[min.x].length-1){
	    		if(statusGrid[min.x][min.y+1] == unblocked)
	    			neighbors.add(grid[min.x][min.y+1]);
	    	}
	    	if(min.y > 0){
	    		if(statusGrid[min.x][min.y-1] == unblocked)
	    			neighbors.add(grid[min.x][min.y-1]);
	    	}
	    	
	    	for(cell n: neighbors){
	    		if(closedSet.contains(n))
	    			continue;
	    		
	    		if(n.search < counter){
	    			n.g = Integer.MAX_VALUE;
	    			n.search = counter;
	    		}
	    		if(n.g > (min.g + 1)){
	    			n.g = min.g + 1;
	    			n.prev = min;
	    			if(openSet.contains(n))
	    				openSet.remove(n);
	    			n.f = n.g + n.h;
	    			openSet.add(n);
	    		}
	    	}
	    	if(!openSet.isEmpty()){
				min = openSet.get(0);
				for(int i=0; i<openSet.size(); i++){
					cell temp = openSet.get(i);
					if((temp.g + temp.h) < (min.g + min.h))
						min = temp;
					else if((temp.g + temp.h) == (min.g + min.h)){
						if(deep && (temp.g > min.g))
							min = temp;
						else if(!deep && (temp.g < min.g))
							min = temp;
					}
				}
			}
		}
		if(goal.g <= (min.g + min.h)){
			openSet.add(min);
		}
	}
	
	public void setupGrid(cell goal){
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
	    
	    for(int x=0; x<grid.length; x++){
	    	for(int y=0; y<grid[x].length; y++){
	    		grid[x][y].h = h_value(grid[x][y], goal);
	    		grid[x][y].g = Integer.MAX_VALUE;
	    	}
	    }
	}
	
	//returns short path using repeated calls to A*, gaining new information as it goes
	//if deep is true, prefers larger g values (smaller h values)
	//otherwise, prefers smaller g values (larger h values)
	public ArrayList<cell> Repeated_Forward_A_Star(cell start, cell goal, boolean deep){
	    setupGrid(goal);
	    
	    ArrayList<cell> ret = new ArrayList<cell>();
	    ret.add(start);
		
		while(start != goal){
			counter++;
			start.g = 0;
			start.f = start.h;
			start.search = counter;
			goal.g = Integer.MAX_VALUE;
			goal.search = counter;
			start.prev = null;
			goal.prev = null;
			
			while(!openSet.isEmpty())
		    	openSet.remove(0);
			while(!closedSet.isEmpty())
		    	closedSet.remove(0);
			openSet.add(start);
			A_Star(goal, deep);
			ArrayList<cell> idealPath = getPath(goal);
			if(openSet.isEmpty())
				return null;
			int index = 0;
			while(start != goal){
		    	index++;
		    	cell next = idealPath.get(index);
		    	if(grid[next.x][next.y].status == blocked){	//ideal path is blocked; recalculate
		    		statusGrid[next.x][next.y] = blocked;
		    		if(ret.size()>0 && ret.get(ret.size()-1)!=start)
			    		ret.add(start);
		    		break;
		    	}
		    	if(ret.size()>0 && ret.get(ret.size()-1)!=start)
		    		ret.add(start);
		    	start = next;
		    }
		}
		
		ret.add(goal);
		
	    return ret;
	}
	
	//identical to repeated forward a star except subsequent calls use updated information and h values
	public ArrayList<cell> Adaptive_A_Star(cell start, cell goal, boolean deep){
		//setupGrid(goal);	//only do this the first time
		
		ArrayList<cell> ret = new ArrayList<cell>();
		ret.add(start);
		
		while(start != goal){
			counter++;
			start.g = 0;
			start.f = start.h;
			start.search = counter;
			goal.g = Integer.MAX_VALUE;
			goal.search = counter;
			start.prev = null;
			goal.prev = null;
			
			while(!openSet.isEmpty())
		    	openSet.remove(0);
			while(!closedSet.isEmpty())
		    	closedSet.remove(0);
			openSet.add(start);
			A_Star(goal, deep);
			ArrayList<cell> idealPath = getPath(goal);
			if(openSet.isEmpty())
				return null;
			int index = 0;
			while(start != goal){
		    	index++;
		    	cell next = idealPath.get(index);
		    	if(grid[next.x][next.y].status == blocked){	//ideal path is blocked; recalculate
		    		statusGrid[next.x][next.y] = blocked;
		    		if(ret.size()>0 && ret.get(ret.size()-1)!=start)
			    		ret.add(start);
		    		break;
		    	}
		    	if(ret.size()>0 && ret.get(ret.size()-1)!=start)
		    		ret.add(start);
		    	start = next;
		    }
		}
		
		ret.add(goal);
		
		for(cell c: ret){
			c.h = goal.g - c.g;
		}
		
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
	
	//if deep is true, prefers larger g values (smaller h values)
	//otherwise, prefers smaller g values (larger h values)
	public static void testGridWorld(GridWorld gw, int numTimes, int size, boolean deep, boolean adaptive, boolean forward){
		Random rand = new Random();
		
		ArrayList<cell> safeCells = new ArrayList<cell>();
		for(int i=0; i<numTimes+1; i++){
			cell temp = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
			while(safeCells.contains(temp)){
				temp = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
			}
			temp.status = unblocked;
			safeCells.add(temp);
		}
		
		cell goal = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		while(goal.status == blocked){
			goal = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
		}
		gw.setupGrid(goal);
		
		for(int startNum=0; startNum<numTimes; startNum++){
			cell start = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
			while(start.status == blocked || start==goal){
				start = gw.grid[rand.nextInt(size)][rand.nextInt(size)];
			}
			
			/*for(int j = 0; j < size; j++) {
				for(int i = 0; i < size; i++) {
					if(gw.grid[i][j]==start)
						System.out.print('S');
					else if(gw.grid[i][j]==goal)
						System.out.print('G');
					else
						System.out.print(gw.grid[i][j].status);
				}
				System.out.println();
			}*/
			
			ArrayList<cell> answer;
			
			if(forward){
				if(adaptive)
					answer = gw.Adaptive_A_Star(start, goal, deep);
				else
					answer = gw.Repeated_Forward_A_Star(start, goal, deep);
			}
			else{
				if(adaptive)
					answer = gw.Adaptive_A_Star(goal, start, deep);
				else
					answer = gw.Repeated_Forward_A_Star(goal, start, deep);
			}
			
			printPath(answer);
			System.out.println();
		}
	}
	
	public static void nickTester(int size){
		Random rand = new Random();
		
		GridWorld gw = new GridWorld(696969);
		gw.grid = new cell[size][size];
		for(int i=0; i<size; i++){
			for(int j=0; j<size; j++){
				gw.grid[i][j] = new cell(i, j);
				int r = rand.nextInt(4);
				if(r==0)
					gw.grid[i][j].status = blocked;
			}
		}
		
		testGridWorld(gw, size, 1, false, false, true);
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
		long startTime000 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, true, true, true);
			System.out.println();
		}
		long averageElapsedTime000 = (System.currentTimeMillis() - startTime000)/50;
		
		long startTime001 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, true, true, false);
			System.out.println();
		}
		long averageElapsedTime001 = (System.currentTimeMillis() - startTime001)/50;
		
		long startTime011 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, true, false, false);
			System.out.println();
		}
		long averageElapsedTime011 = (System.currentTimeMillis() - startTime011)/50;
		
		long startTime010 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, true, false, true);
			System.out.println();
		}
		long averageElapsedTime010 = (System.currentTimeMillis() - startTime010)/50;
		
		long startTime100 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, false, true, true);
			System.out.println();
		}
		long averageElapsedTime100 = (System.currentTimeMillis() - startTime100)/50;
		
		long startTime110 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, false, false, true);
			System.out.println();
		}
		long averageElapsedTime110 = (System.currentTimeMillis() - startTime110)/50;
		
		long startTime101 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, false, true, false);
			System.out.println();
		}
		long averageElapsedTime101 = (System.currentTimeMillis() - startTime101)/50;
		
		long startTime111 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], 5, 101, false, false, false);
			System.out.println();
		}
		long averageElapsedTime111 = (System.currentTimeMillis() - startTime111)/50;
		
		
		System.out.println("Average time elapsed per grid when breaking ties with larger G, using adaptive A*, and using repeated forward A* (in milliseconds): "+averageElapsedTime000+"");
		System.out.println("Average time elapsed per grid when breaking ties with larger G, using adaptive A*, and using repeated backwards A* (in milliseconds): "+averageElapsedTime001+"");
		System.out.println("Average time elapsed per grid when breaking ties with larger G, not using adaptive A*, and using repeated backwards A* (in milliseconds): "+averageElapsedTime011+"");
		System.out.println("Average time elapsed per grid when breaking ties with larger G, not using adaptive A*, and using repeated forward A* (in milliseconds): "+averageElapsedTime010+"");
		System.out.println("Average time elapsed per grid breaking ties with smaller G, using adaptive A*, and using repeated forward A* (in milliseconds): "+averageElapsedTime100+"");
		System.out.println("Average time elapsed per grid when breaking ties with smaller G, not using adaptive A*, and using repeated forward A* (in milliseconds): "+averageElapsedTime110+"");
		System.out.println("Average time elapsed per grid when breaking ties with smaller G, using adaptive A*, and using repeated backwards A* (in milliseconds): "+averageElapsedTime101+"");
		System.out.println("Average time elapsed per grid when breaking ties with smaller G, not using adaptive A*, and using repeated backwards A* (in milliseconds): "+averageElapsedTime111+"");
		
	
	}
}