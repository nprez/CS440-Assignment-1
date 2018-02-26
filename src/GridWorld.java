import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;


public class GridWorld {
	static char unblocked = ' ';
	static char blocked = 'X';
	static int numAgents = 3;
	static int gridDimmension = 101;	//101 default
	
	cell[][] grid;
	int gID;
	int counter;
	Stack<cell> myStack;
	ArrayList<cell> closedSet;
	PriorityQueue<cell> openSet;
	char[][] statusGrid;
	static int missCounter = 0;
	static GridWorld[] workSpace;
	cell[] s;
	cell g;
	
	public GridWorld(int gID) {
		grid = new cell[gridDimmension][gridDimmension];
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
			min = openSet.peek();
		
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
	    			openSet.add(n);
	    		}
	    	}
	    	if(!openSet.isEmpty()){
				min = openSet.peek();
			}
		}
		if(goal.g <= (min.g + min.h)){
			openSet.add(min);
		}
	}
	
	public void setupGrid(cell goal, boolean deep){
		counter = 0;
		// The set of nodes already evaluated
	    closedSet = new ArrayList<cell>();

	    // The set of currently discovered nodes that are not evaluated yet.
	    // Initially, only the start node is known.
	    int largeConstant = gridDimmension*gridDimmension;
	    if(deep)
	    	openSet = new PriorityQueue<cell>((a,b)->((Integer)(largeConstant*(a.g+a.h)+a.g)).compareTo((Integer)(largeConstant*(b.g+b.h)+b.g)));
	    else
	    	openSet = new PriorityQueue<cell>((a,b)->((Integer)(largeConstant*(a.g+a.h)+a.h)).compareTo((Integer)(largeConstant*(b.g+b.h)+b.h)));
	    
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
	    		grid[x][x].search = 0;
	    		grid[x][y].prev = null;
	    	}
	    }
	}
	
	//returns short path using repeated calls to A*, gaining new information as it goes
	//if deep is true, prefers larger g values (smaller h values)
	//otherwise, prefers smaller g values (larger h values)
	public ArrayList<cell> Repeated_Forward_A_Star(cell start, cell goal, boolean deep){
	    ArrayList<cell> ret = new ArrayList<cell>();
	    ret.add(start);
		
		while(start != goal){
			counter++;
			start.g = 0;
			start.search = counter;
			goal.g = Integer.MAX_VALUE;
			goal.search = counter;
			start.prev = null;
			goal.prev = null;
			
			while(!openSet.isEmpty())
		    	openSet.remove();
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

		ArrayList<cell> ret = new ArrayList<cell>();
		ret.add(start);
		
		while(start != goal){
			counter++;
			start.g = 0;
			start.search = counter;
			goal.g = Integer.MAX_VALUE;
			goal.search = counter;
			start.prev = null;
			goal.prev = null;
			
			while(!openSet.isEmpty())
		    	openSet.remove();
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
	
	public static void printGridWorld(GridWorld g) {
		for(int j=0; j<gridDimmension; j++){
			for(int i=0; i<gridDimmension; i++){
				boolean isStart = false;
				for(cell s: g.s){
					if(g.grid[i][j].equals(s)){
						isStart = true;
						break;
					}
				}
				if(isStart)
					System.out.print("S");
				else if(g.grid[i][j].equals(g.g))
					System.out.print("G");
				else
					System.out.print(g.grid[i][j].status);
			}
			System.out.println();
		}
	}
	
	public static void printPath(ArrayList<cell> path){
		if(path==null){
			missCounter++;
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
	public static void testGridWorld(GridWorld gw, boolean deep, boolean adaptive, boolean forward){
		if(adaptive && !forward){
			System.out.println("ERROR: cannot be backwards and adaptive");
			return;
		}
		cell goal = gw.g;
		gw.setupGrid(forward?goal:gw.s[0], deep);
		
		//printGridWorld(gw);
		
		for(int startNum=0; startNum<numAgents; startNum++){
			cell start = gw.s[startNum];
			
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
			
			//printPath(answer);
		}
		
		//System.out.println();
	}
	
	public static void loadGrids() throws IOException{
		//create the workspace for the 50 grid worlds
		workSpace = new GridWorld[50];

		//create 50 grid worlds to put in the array and within the grid worlds initiate all the cells
		for(int i = 0; i < 50; i++) {
			GridWorld g = new GridWorld(i);
			g.gID = i;
			g.s = new cell[numAgents];
			
			String fileName = "grids/"+g.gID+".txt";
			
			FileReader fileReader = null;
			fileReader = new FileReader(fileName);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String statuses = bufferedReader.readLine();
			
			for(int j = 0; j < gridDimmension; j++) {
				for(int k = 0; k < gridDimmension; k++) {
					g.grid[j][k] = new cell();
					g.grid[j][k].setCellCoordinates(j, k);
			        
					char c = statuses.charAt(j*gridDimmension + k);
			        
			        //c = (char) bufferedReader.read();
			        
			        g.grid[j][k].setStatus(c);
				}
			}
		
			int gx = Integer.parseInt(bufferedReader.readLine());
			int gy = Integer.parseInt(bufferedReader.readLine());
			//int gx = Integer.parseInt(""+(char)bufferedReader.read());
			//int gy = Integer.parseInt(""+(char)bufferedReader.read());
			g.g = g.grid[gx][gy];
			
			for(int a=0; a<numAgents; a++){
				int sx = Integer.parseInt(bufferedReader.readLine());
				int sy = Integer.parseInt(bufferedReader.readLine());
				//int sx = Integer.parseInt(""+(char)bufferedReader.read());
				//int sy = Integer.parseInt(""+(char)bufferedReader.read());
				g.s[a] = g.grid[sx][sy];
			}
			
			bufferedReader.close();
			
			workSpace[i] = g;
		}
	}
	
	public static void main(String[] args) throws IOException {
		int originalNumAgents = numAgents;
		
		boolean largerG1 = false; 
		boolean adaptive1 = true;
		boolean forwards1 = true;
		loadGrids();
		
		System.out.println("0/8 Tests Done");
		
		missCounter = 0;
		long startTime1 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG1, adaptive1, forwards1);
			//System.out.println();
		}
		long averageElapsedTime1 = (System.currentTimeMillis() - startTime1)/50;
		int m1 = missCounter;
		System.out.println("1/8 Tests Done");
		
		boolean largerG2 = false;
		boolean adaptive2 = false;
		boolean forwards2 = true;
		
		loadGrids();
		missCounter = 0;
		long startTime2 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG2, adaptive2, forwards2);
			//System.out.println();
		}
		long averageElapsedTime2 = (System.currentTimeMillis() - startTime2)/50;
		int m2 = missCounter;
		System.out.println("2/8 Tests Done");
		
		boolean largerG3 = true;
		boolean adaptive3 = true;
		boolean forwards3 = true;
		loadGrids();
		missCounter = 0;
		long startTime3 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG3, adaptive3, forwards3);
			//System.out.println();
		}
		long averageElapsedTime3 = (System.currentTimeMillis() - startTime3)/50;
		int m3 = missCounter;
		System.out.println("3/8 Tests Done");
		
		boolean largerG4 = true;
		boolean adaptive4 = false;
		boolean forwards4 = true;
		loadGrids();
		missCounter = 0;
		long startTime4 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG4, adaptive4, forwards4);
			//System.out.println();
		}
		long averageElapsedTime4 = (System.currentTimeMillis() - startTime4)/50;
		int m4 = missCounter;
		System.out.println("4/8 Tests Done");
		
		numAgents = 1;
		
		boolean largerG5 = false;
		boolean adaptive5 = false;
		boolean forwards5 = true;
		loadGrids();
		missCounter = 0;
		long startTime5 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG5, adaptive5, forwards5);
			//System.out.println();
		}
		long averageElapsedTime5 = (System.currentTimeMillis() - startTime5)/50;
		int m5 = missCounter;
		System.out.println("5/8 Tests Done");
		
		boolean largerG6 = true;
		boolean adaptive6 = false;
		boolean forwards6 = true;
		loadGrids();
		missCounter = 0;
		long startTime6 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG6, adaptive6, forwards6);
			//System.out.println();
		}
		long averageElapsedTime6 = (System.currentTimeMillis() - startTime6)/50;
		int m6 = missCounter;
		System.out.println("6/8 Tests Done");
		
		boolean largerG7 = false;
		boolean adaptive7 = false;
		boolean forwards7 = false;
		loadGrids();
		missCounter = 0;
		long startTime7 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG7, adaptive7, forwards7);
			//System.out.println();
		}
		long averageElapsedTime7 = (System.currentTimeMillis() - startTime7)/50;
		int m7 = missCounter;
		System.out.println("7/8 Tests Done");
		
		boolean largerG8 = true;
		boolean adaptive8 = false;
		boolean forwards8 = false;
		loadGrids();
		missCounter = 0;
		long startTime8 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], largerG8, adaptive8, forwards8);
			//System.out.println();
		}
		long averageElapsedTime8 = (System.currentTimeMillis() - startTime8)/50;
		
		int m8 = missCounter;
		System.out.println("8/8 Tests Done");
		
		System.out.print(
				"Prefers "+(largerG1?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive1?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards1?" Forwards":"Backwards")+'\t'
				+originalNumAgents+" agents" + ":"+'\t');
		System.out.println("("+m1+" misses/"+(originalNumAgents*50)+")"+'\t'+averageElapsedTime1+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG2?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive2?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards2?" Forwards":"Backwards")+'\t'
				+originalNumAgents+" agents" + ":"+'\t');
		System.out.println("("+m2+" misses/"+(originalNumAgents*50)+")"+'\t'+averageElapsedTime2+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG3?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive3?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards3?" Forwards":"Backwards")+'\t'
				+originalNumAgents+" agents" + ":"+'\t');
		System.out.println("("+m3+" misses/"+(originalNumAgents*50)+")"+'\t'+averageElapsedTime3+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG4?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive4?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards4?" Forwards":"Backwards")+'\t'
				+originalNumAgents+" agents" + ":"+'\t');
		System.out.println("("+m4+" misses/"+(originalNumAgents*50)+")"+'\t'+averageElapsedTime4+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG5?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive5?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards5?" Forwards":"Backwards")+'\t'
				+numAgents+" agents" + ":"+'\t');
		System.out.println("("+m5+" misses/"+(numAgents*50)+")"+'\t'+averageElapsedTime5+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG6?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive6?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards6?" Forwards":"Backwards")+'\t'
				+numAgents+" agents" + ":"+'\t');
		System.out.println("("+m6+" misses/"+(numAgents*50)+")"+'\t'+averageElapsedTime6+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG7?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive7?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards7?" Forwards":"Backwards")+'\t'
				+numAgents+" agents" + ":"+'\t');
		System.out.println("("+m7+" misses/"+(numAgents*50)+")"+'\t'+averageElapsedTime7+" average ms per grid");
		
		System.out.print(
				"Prefers "+(largerG8?"larger ":"smaller")+" g values;"+'\t'
				+ (adaptive8?"    ":"Not ")+"Adaptive;"+'\t'
				+(forwards8?" Forwards":"Backwards")+'\t'
				+numAgents+" agents" + ":"+'\t');
		System.out.println("("+m8+" misses/"+(numAgents*50)+")"+'\t'+averageElapsedTime8+" average ms per grid");
	}
}