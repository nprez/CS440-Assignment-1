import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


public class GridWorld {
	static char unblocked = ' ';
	static char blocked = 'X';
	static int numAgents = 5;
	cell[][] grid;
	int gID;
	int counter;
	Stack<cell> myStack;
	ArrayList<cell> closedSet;
	ArrayList<cell> openSet;
	char[][] statusGrid;
	static int missCounter = 0;
	static GridWorld[] workSpace;
	cell[] s;
	cell g;
	
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
	    		grid[x][y].f = Integer.MAX_VALUE;
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
	
	public static void printGridWorld(GridWorld g) {
		for(int j=0; j<g.grid[0].length; j++){
			for(int i=0; i<g.grid.length; i++){
				boolean isStart = false;
				for(cell s: g.s){
					if(g.grid[i][j] == s){
						isStart = true;
						break;
					}
				}
				if(isStart)
					System.out.println("S");
				else if(g.grid[i][j] == g.g)
					System.out.println("G");
				else
					System.out.println(g.grid[i][j].status);
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
	public static void testGridWorld(GridWorld gw, int numTimes, int size, boolean deep, boolean adaptive, boolean forward){
		cell goal = gw.g;
		gw.setupGrid(goal);
		
		for(int startNum=0; startNum<numTimes; startNum++){
			cell start = gw.s[startNum];
			
			//printGridWorld(gw);
			
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
	
	public static void smallTester(int size){
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
		
		testGridWorld(gw, size, 1, false, false, false);
	}
	
	public static void loadGrids(){
		//create the workspace for the 50 grid worlds
		workSpace = new GridWorld[50];

		//create 50 grid worlds to put in the array and within the grid worlds initiate all the cells
		for(int i = 0; i < 50; i++) {
			GridWorld g = new GridWorld(i);
			g.gID = i;
			g.s = new cell[numAgents];
			
			String fileName = "grids/"+g.gID+".txt";
			
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(fileName);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			for(int j = 0; j < 101; j++) {
				for(int k = 0; k < 101; k++) {
					g.grid[j][k] = new cell();
					g.grid[j][k].setCellCoordinates(j, k);
			        
					char c = 0;
			        
			        try{
			        	c = (char) bufferedReader.read();
			        }
			        catch(FileNotFoundException ex) {
			            System.out.println("Unable to open file '" + fileName + "'");                
			        }
			        catch(IOException ex) {
			            System.out.println("Error reading file '" + fileName + "'");
			        }
			        g.grid[j][k].setStatus(c);
				}
			}
			
			try {
				int gx = Integer.parseInt(""+(char)bufferedReader.read());
				int gy = Integer.parseInt(""+(char)bufferedReader.read());
				g.g = g.grid[gx][gy];
				
				for(int a=0; a<numAgents; a++){
					int sx = Integer.parseInt(""+(char)bufferedReader.read());
					int sy = Integer.parseInt(""+(char)bufferedReader.read());
					g.s[a] = g.grid[sx][sy];
				}
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			workSpace[i] = g;
		}
	}
	
	public static void main(String[] args) {
		boolean largerG1 = false; 
		boolean adaptive1 = false;
		boolean forwards1 = false;
		loadGrids();
		missCounter = 0;
		long startTime1 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG1, adaptive1, forwards1);
			System.out.println();
		}
		long averageElapsedTime1 = (System.currentTimeMillis() - startTime1)/50;
		int m1 = missCounter;
		
		boolean largerG2 = false;
		boolean adaptive2 = false;
		boolean forwards2 = true;
		loadGrids();
		missCounter = 0;
		long startTime2 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG2, adaptive2, forwards2);
			System.out.println();
		}
		long averageElapsedTime2 = (System.currentTimeMillis() - startTime2)/50;
		int m2 = missCounter;
		
		boolean largerG3 = false;
		boolean adaptive3 = true;
		boolean forwards3 = false;
		loadGrids();
		missCounter = 0;
		long startTime3 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG3, adaptive3, forwards3);
			System.out.println();
		}
		long averageElapsedTime3 = (System.currentTimeMillis() - startTime3)/50;
		int m3 = missCounter;
		
		boolean largerG4 = false;
		boolean adaptive4 = true;
		boolean forwards4 = true;
		loadGrids();
		missCounter = 0;
		long startTime4 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG4, adaptive4, forwards4);
			System.out.println();
		}
		long averageElapsedTime4 = (System.currentTimeMillis() - startTime4)/50;
		int m4 = missCounter;
		
		boolean largerG5 = true;
		boolean adaptive5 = false;
		boolean forwards5 = false;
		loadGrids();
		missCounter = 0;
		long startTime5 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG5, adaptive5, forwards5);
			System.out.println();
		}
		long averageElapsedTime5 = (System.currentTimeMillis() - startTime5)/50;
		int m5 = missCounter;
		
		boolean largerG6 = true;
		boolean adaptive6 = false;
		boolean forwards6 = true;
		loadGrids();
		missCounter = 0;
		long startTime6 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG6, adaptive6, forwards6);
			System.out.println();
		}
		long averageElapsedTime6 = (System.currentTimeMillis() - startTime6)/50;
		int m6 = missCounter;
		
		boolean largerG7 = true;
		boolean adaptive7 = true;
		boolean forwards7 = false;
		loadGrids();
		missCounter = 0;
		long startTime7 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG7, adaptive7, forwards7);
			System.out.println();
		}
		long averageElapsedTime7 = (System.currentTimeMillis() - startTime7)/50;
		int m7 = missCounter;
		
		boolean largerG8 = true;
		boolean adaptive8 = true;
		boolean forwards8 = true;
		loadGrids();
		missCounter = 0;
		long startTime8 = System.currentTimeMillis();
		for(int i = 0; i < 50; i++) {
			testGridWorld(workSpace[i], numAgents, 101, largerG8, adaptive8, forwards8);
			System.out.println();
		}
		long averageElapsedTime8 = (System.currentTimeMillis() - startTime8)/50;
		int m8 = missCounter;
		
		System.out.println("Prefers "+(largerG1?"larger":"smaller")+" g values; " + (adaptive1?"":"Not ")+"Adaptive; " + (forwards1?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime1+" ("+m1+" misses)");
		
		System.out.println("Prefers "+(largerG2?"larger":"smaller")+" g values; " + (adaptive2?"":"Not ")+"Adaptive; " + (forwards2?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime2+" ("+m2+" misses)");
		
		System.out.println("Prefers "+(largerG3?"larger":"smaller")+" g values; " + (adaptive3?"":"Not ")+"Adaptive; " + (forwards3?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime3+" ("+m3+" misses)");
		
		System.out.println("Prefers "+(largerG4?"larger":"smaller")+" g values; " + (adaptive4?"":"Not ")+"Adaptive; " + (forwards4?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime4+" ("+m4+" misses)");
		
		System.out.println("Prefers "+(largerG5?"larger":"smaller")+" g values; " + (adaptive5?"":"Not ")+"Adaptive; " + (forwards5?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime5+" ("+m5+" misses)");
		
		System.out.println("Prefers "+(largerG6?"larger":"smaller")+" g values; " + (adaptive6?"":"Not ")+"Adaptive; " + (forwards6?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime6+" ("+m6+" misses)");
		
		System.out.println("Prefers "+(largerG7?"larger":"smaller")+" g values; " + (adaptive7?"":"Not ")+"Adaptive; " + (forwards7?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime7+" ("+m7+" misses)");
		
		System.out.println("Prefers "+(largerG8?"larger":"smaller")+" g values; " + (adaptive8?"":"Not ")+"Adaptive; " + (forwards8?"Forwards":"Backwards") + ":");
		System.out.println("Average time elapsed per grid (in milliseconds): "+averageElapsedTime8+" ("+m8+" misses)");
	}
}