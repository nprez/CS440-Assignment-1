import java.util.ArrayList;

public class GridWorld {
	cell[][] grid;
	
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
	
	public ArrayList<cell> A_star(cell start, cell goal){
		// The set of nodes already evaluated
	    ArrayList<cell> closedSet = new ArrayList<cell>();

	    // The set of currently discovered nodes that are not evaluated yet.
	    // Initially, only the start node is known.
	    ArrayList<cell> openSet = new ArrayList<cell>();
	    openSet.add(start);

	    // For each node, which node it can most efficiently be reached from.
	    // If a node can be reached from many nodes, cameFrom will eventually contain the
	    // most efficient previous step.
	    for(int x=0; x<grid.length; x++){
	    	for(int y=0; y<grid[x].length; y++){
	    		grid[x][y].prev = null;
	    		// For each node, the cost of getting from the start node to that node.
	    		grid[x][y].g = Integer.MAX_VALUE;
	    		// For each node, the total cost of getting from the start node to the goal
	    	    // by passing by that node. That value is partly known, partly heuristic.
	    		grid[x][y].f = Integer.MAX_VALUE;
	    		grid[x][y].h = h_value(grid[x][y], goal);
	    	}
	    }

	    // The cost of going from start to start is zero.
	    start.g = 0;
	    // For the first node, the f value is completely heuristic.
	    start.f = start.h;

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
	    	
	    	ArrayList<cell> neighbors = new ArrayList<cell>();
	    	if(current.x < grid.length)
	    		neighbors.add(grid[current.x+1][current.y]);
	    	if(current.x > 0)
	    		neighbors.add(grid[current.x-1][current.y]);
	    	if(current.y < grid[current.x].length)
	    		neighbors.add(grid[current.x][current.y+1]);
	    	if(current.y > 0)
	    		neighbors.add(grid[current.x][current.y-1]);
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
	
	public static void main(String[] args) {
		GridWorld gridWorld = new GridWorld();
	}
	
}
