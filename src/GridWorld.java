import java.util.ArrayList;

public class GridWorld {
	cell[][] grid;
	ArrayList<cell> closedSet;
	ArrayList<cell> openSet;
	char[][] statusGrid;
	
	public GridWorld() {
		grid = new cell[101][101];
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
	
	public static void main(String[] args) {
		GridWorld gridWorld = new GridWorld();
	}
	
}
