import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateGrids {
	static char unblocked = GridWorld.unblocked;
	static char blocked = GridWorld.blocked;
	static int numAgents = GridWorld.numAgents;
	static int gridDimmension = GridWorld.gridDimmension;
	
	public static void saveGrid(GridWorld gw) throws IOException{
		File dir = new File("grids");
		dir.mkdir();
		System.out.println("Saving grid "+gw.gID);
        String fileName = "grids/"+gw.gID+".txt";
        File f = new File(fileName);
    
		if(!f.createNewFile()){
			f.delete();
			f.createNewFile();
		}

        FileWriter fileWriter =
            new FileWriter(fileName);

        BufferedWriter bufferedWriter =
            new BufferedWriter(fileWriter);
        
        for(int i=0; i<gw.grid.length; i++){
        	for(int j=0; j<gw.grid[0].length; j++){
        		bufferedWriter.write(gw.grid[i][j].status);
        	}
        }
        bufferedWriter.newLine();
        bufferedWriter.write(""+gw.g.x);
        bufferedWriter.newLine();
        bufferedWriter.write(""+gw.g.y);
        for(int i=0; i<numAgents; i++){
        	bufferedWriter.newLine();
        	bufferedWriter.write(""+gw.s[i].x);
        	bufferedWriter.newLine();
        	bufferedWriter.write(""+gw.s[i].y);
        }

        bufferedWriter.close();
	}
	
	public static cell selectRandomCell(GridWorld g) {
		Random rand = new Random();
		int x = rand.nextInt(gridDimmension);
		int y = rand.nextInt(gridDimmension);
		cell ret = g.grid[x][y];
		ret.setCellCoordinates(x, y);
		return ret;
	}
	
	public static boolean deadEnd(GridWorld g, int x, int y) {
		boolean ret = false;
		if(x + 1 < gridDimmension) {
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
		if(y + 1 < gridDimmension) {
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
	
	public static void main(String[] args) throws IOException {
		//create the workspace for the 50 grid worlds
		GridWorld[] workSpace = new GridWorld[50];

		//create 50 grid worlds to put in the array and within the grid worlds initiate all the cells
		for(int i = 0; i < 50; i++) {
			GridWorld g = new GridWorld(i);
			for(int j = 0; j < gridDimmension; j++) {
				for(int k = 0; k < gridDimmension; k++) {
					g.grid[j][k] = new cell();
					g.grid[j][k].setCellCoordinates(j, k);
				}
			}
			workSpace[i] = g;
		}
		
		Random rand = new Random();

		//have to setup the grid world "maze" for each grid world in the workspace
		for(GridWorld g: workSpace) {
			//need to visit all 101x101 cells
			int x;
			int y;
			while(g.counter != (gridDimmension*gridDimmension)) {
				//if the stack is empty we want to just pick a random unvisited cell and start from there
				if(g.myStack.isEmpty()) {
					//start by picking a random cell
					cell starter = selectRandomCell(g);
					x = starter.x;
					y = starter.y;
					//if we pick a cell thats already been visited we want to pick another one at random until we find one that hasn't
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
						if(y + 1 >= gridDimmension) {
							options = options.replace('r', 'q');
						}
						if(x + 1 >=gridDimmension) {
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
				//only difference is we don't choose a random cell we choose the one we popped from the stack
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
						if(y + 1 >= gridDimmension) {
							options = options.replace('r', 'q');
						}
						if(x + 1 >= gridDimmension) {
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
		
		for(GridWorld gw: workSpace){
			//get goal
			cell goal = gw.grid[rand.nextInt(gridDimmension)][rand.nextInt(gridDimmension)];
			while(goal.status == blocked){
				goal = gw.grid[rand.nextInt(gridDimmension)][rand.nextInt(gridDimmension)];
			}
			gw.g = goal;
			
			//get start(s)
			gw.s = new cell[numAgents];
			for(int i=0; i<numAgents; i++){
				cell start = gw.grid[rand.nextInt(gridDimmension)][rand.nextInt(gridDimmension)];
				while(start.status == blocked || start.equals(goal)){
					start = gw.grid[rand.nextInt(gridDimmension)][rand.nextInt(gridDimmension)];
				}
				gw.s[i] = start;
			}
			
			saveGrid(gw);
		}
		
		System.out.println("Done");
	}
}
