package Game;




import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;




class MulledAstar 
{
	static int no_of_columns = 3; //Gving the number of columns in the given grid
	static int no_of_nodes_evaluated = 0;
	static int no_of_deadlocks_found = 0;
	 // A* priority queue storing the open nodes
    PriorityQueue <States> queue = new PriorityQueue<States>(100, new Comparator<States>() {
        @Override
        public int compare(States a, States b) { 
            return a.priority() - b.priority();
        }
    });

    // The closed state set.
    HashSet <States> closed = new HashSet <States>();
   
	   static class States 
	   {
	        String currentstate;    // State in terms of left to right, top to bottom.
	        int indexofW;   		// Index of position of White ball in the String  
	        int g;            		// Number of moves from start.
	        int h;            		// Heuristic value (difference from goal)
	        States previous;         // Previous state in solution chain.
	        Map<String,String> rememberlastoperator = new HashMap<String,String>(); // Map to remember the last move to reduce the branching factor

	        // A* priority function (often called F in books).
	        int priority() {
	            return g + h;
	        }

	        //toString method the print the states stores by the closed Hashset
	        public String toString() 
	        {
	        	return currentstate;
	        }
	        
	        // Build a start state.
	        States(String initialstate, int indexofG) {
	        	currentstate = initialstate;
	        	indexofW = currentstate.indexOf('W');
	            g = 0;
	            h = calculateHeuristic(indexofW, indexofG);
	            previous = null;
	            rememberlastoperator.put(currentstate, null);
	        }
	        
	        States(States previous, String nextstate, int indexofG, String lastoperator) {
	            currentstate = nextstate;
	            indexofW = currentstate.indexOf('W');
	            g = previous.g + 1;
	            h = calculateHeuristic( indexofW, indexofG);;
	            this.previous = previous;
	            rememberlastoperator.put(currentstate, lastoperator);
	        }
	        
	        void checkGoal(int indexofW, int indexofG)
	        {
	        	if(indexofW==indexofG)
	        	{
	        		long elapsed = System.currentTimeMillis();
	                printAll();
	                System.out.println("elapsed (ms) = " + elapsed);
	                System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated + "No of deadlocks found = " + no_of_deadlocks_found);
	                System.exit(0);
	        	}
	        }
	        static int calculateHeuristic(int indexofW, int indexofG)
	        {
	        	/*
	        	 * Algorithm to calculate the heuristic is to find the absolute difference between the rows of W and G + absolute difference between
	        	 * the columns of W and G 
	        	 */
	    		int colW = indexofW%(no_of_columns); // colW holds the value of the column in which W resides c can be between (0,1,2,3,4 for columns = 5)
	    		int rowW = indexofW/(no_of_columns); // rowW holds the value of the row in which W resides r can be between (0,1,2 for columns = 5)
	    		int colG = indexofG%(no_of_columns); // colG holds the value of the column in which G resides
	    		int rowG = indexofG/(no_of_columns); // rowG holds the value of the row in which G resides
	    		
	    		return Math.abs(colW - colG) + Math.abs(rowW - rowG);
	        }
	        // Print this state.
	        void print() {
	            System.out.println("p = " + priority() + " = g+h = " + g + "+" + h);
	            System.out.println("State: " + this.currentstate);
	        }

			// Print the solution chain with start state first.
	        void printAll() {
	            if (this.previous != null) this.previous.printAll();
	            System.out.println();
	            print();
	        }
	        @Override
	        public boolean equals(Object obj) {
	            if (obj instanceof States) {
	                States other = (States)obj;
	                return currentstate.equals(other.currentstate);
	            }
	            return false;
	        }

	        @Override
	        public int hashCode() {
	            return currentstate.hashCode();
	        }
	        
	        States left(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "right")
	        	{
	        
	        	int w = this.indexofW; // w holds the index of white position
	        	int c = w%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	        	
	        	String rowWelements = this.currentstate.substring(w-c, w); //rowWelements hold the elemnts in the same row of W
	        	
	        	int lastindexofA = rowWelements.lastIndexOf("A"); //finds the lastindex of A in the rowlements
	        	int isGthere = rowWelements.indexOf('G'); //finds whether G is present in that row 
	        
	        	if(c != 0)
	        	{
	        		//Check if there is an available position in the row or the Goal is to your immediate left
	        		if(lastindexofA != -1 || this.currentstate.charAt(w-1)=='G')
	        		{
	        			if(lastindexofA != -1)
	        				lastindexofA += w-c; //Get the correct index of A by adding w-c
	        			else
	        				lastindexofA = w-1;
	        			
	        			//comparison between position of A and G and the bigger value wins...done using ternary operator
	        			if(isGthere != -1)
	        				lastindexofA = lastindexofA > indexofG ? lastindexofA : indexofG ;
	        			
	        			String checkforX = this.currentstate.substring(lastindexofA+1, w); //Create a substring from lastindexofa to w tio check if there is an X between them
	        			int indexofX = checkforX.indexOf('X'); //Check the presence of X
	        		
	        			//If X is not present you can move to the left to the available position
	        			if(indexofX == -1)
	        			{
	        				//Create the new state with elemnts till A + elements from A+1,W+1 + A + remaining elements
	        				String nextstate = this.currentstate.substring(0, lastindexofA)+currentstate.substring(lastindexofA + 1, w+1)+"A"+currentstate.substring(w+1); 
	        				//System.out.println(nextstate + " left " +"    " + (g+h));
	        				checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method	
	        				
	        				if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("Type of Deadlock 1 - Left");
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("deadlock 3 found" + nextstate);
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	        				
	        				return new States(this, nextstate, indexofG, "left");
	        			}	
	        		}
	        	}
	        	}
	        	return null;
	        	
	        }
	        
	        boolean checkForImmovableBlackOnGoal(String nextstate, int indexofG) 
	        {
	        	
				//Check if Black ball is on goal
	        	if(nextstate.charAt(indexofG)!='B')
	        		return false;
	        	
	        		//If we have reached here means black ball is on Goal
	        		//Need to check whether this black ball can be pushed horizontally and vertically
	        		//If it can be pushed then np
	        		
	        	
	        	//This check is for horizontal movement
	        		int c = (indexofG)%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		
    			
	        		//To check if there is an A position to its left
	        		String rowWelementsleft = nextstate.substring(indexofG-c, indexofG); //rowWelements hold the elemnts in the same row of W
	        		int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	        		//System.out.println(leftindexofA);
	        		
	        		String rowWelements = nextstate.substring(indexofG, indexofG + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	        		
		        	int rightindexofA = rowWelements.indexOf("A"); //finds the firstindex of A in the rowlements
		        	//System.out.println(rightindexofA);
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to left or right than no blocked goal
	    			if(leftindexofA != -1 && rightindexofA != -1)
	    				return false;
	        	
	    			//System.out.println("hieee");
	    			
	    			//Now this check is for vertical movement
	    			
	    			//Up movement
	    			int r = indexofG/(no_of_columns);
		        	
		        	int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		        	boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of w is not in first row than you there might be a possibility of moving up
		        	if(indexofG>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A')
		        			{
		        				
		        				availableup = true; //This means we found an A position
		        				break;
		        			}
		        			
		        			dummycounter += no_of_columns;
		        		}
		        	}
		        	//System.out.println(availableup);
		        	
		        	//Down Movement
		        	dummycounter = indexofG; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		    		boolean availabledown = false; //available to check the availability of A
		    		
		    		//Only if position of w is not in last row than you there might be a possibility of moving down
		    		if(indexofG < (nextstate.length() - no_of_columns))
		    		{
		    			//Check the availability of A
		    			while(dummycounter<nextstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
		    			{
		    				if(nextstate.charAt(dummycounter) == 'A')
		    				{
		    					availabledown = true; //This means we found an A position
		    					break;
		    				}
		    				
		    				dummycounter += no_of_columns;
		    			}
		    		}
		    		//System.out.println(availabledown);
		    		if(availableup == true && availabledown == true)
		    			return false;
		    		
				return true;
				
			}

			/*
	         THIRD DEADLOCK SOLVING METHOD
	        This method checks for deadlocks of type 3 which checks whther the neighbours are blocked indirectly making goal blocked; so no point 
	        of storing this state in Queue as it is a deadlock state
	        */
	        boolean checkForBlockedGoal(String nextstate, int indexofG) 
	        {
	        	//To check if all the neighbors of G are X and B as otherwise it is not blocked
	        	
	        	//Get the row and column of G to get its neighbors
	        	int colG = indexofG%(no_of_columns); // colG holds the value of the column in which G resides
	    		int rowG = indexofG/(no_of_columns); // rowG holds the value of the row in which G resides
	    		
	    		int countTheBlockedNeighbors = 0;
	    		int numberOfNeighborsOfGoal = 0;
	    		boolean upneighbor = false;
	    		boolean downneighbor = false;
	    		boolean leftneighbor = false;
	    		boolean rightneighbor = false;
	    		//System.out.println("column " + colG +"  row " + rowG + "   index of G " + indexofG + "   nexstate " + nextstate);
	        	
	    		//For checking the left neighbor
	    		if(colG>0)
	    		{
	    			leftneighbor = true;
	    			numberOfNeighborsOfGoal++;
	    			if(nextstate.charAt(indexofG - 1) == 'B' || nextstate.charAt(indexofG - 1) == 'X' )
	    			{
	    				countTheBlockedNeighbors++;
	    			}
	    		}
	    		
	    		//For checking the right neighbor
	    		if(colG<no_of_columns - 1)
	    		{
	    			rightneighbor = true;
	    			numberOfNeighborsOfGoal++;
	    			if(nextstate.charAt(indexofG + 1) == 'B' || nextstate.charAt(indexofG + 1) == 'X' )
	    				countTheBlockedNeighbors++;
	    		}
	    		
	    		//For checking the up neighbor
	    		if(rowG>0)
	    		{
	    			upneighbor = true;
	    			numberOfNeighborsOfGoal++;
	    			if(nextstate.charAt(indexofG - no_of_columns ) == 'B' || nextstate.charAt(indexofG - no_of_columns) == 'X' )
	    				countTheBlockedNeighbors++;
	    		}
	    		
	    		//For checking the down neighbor
	    		if(rowG< ((nextstate.length()/no_of_columns) - 1))
	    		{
	    			downneighbor = true;
	    			numberOfNeighborsOfGoal++;
	    			if(nextstate.charAt(indexofG + no_of_columns ) == 'B' || nextstate.charAt(indexofG + no_of_columns) == 'X' )
	    				countTheBlockedNeighbors++;
	    		}
	    		
	    		//System.out.println(numberOfNeighborsOfGoal + "     " + countTheBlockedNeighbors);
	    		
	    		if(numberOfNeighborsOfGoal != countTheBlockedNeighbors)
	    			return false;
	    		
	    		/*
	    		 * If you reach here it means that all its neighbors are B or X; A not possible 
	    		 */
	    		
	    		
	    		//Check if the left neighbour can be moved or not
	    		if(leftneighbor)
	    		{
	    			
	    			if(nextstate.charAt(indexofG - 1) == 'B')
	    			{
	    			int c = (indexofG - 1)%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		
	    			
	    			//To check if there is an A position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG-1-c, indexofG - 1); //rowWelements hold the elemnts in the same row of W
	    			int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(leftindexofA);
	    			
	    			//To check if there is are 2 A positions to its right
	    			String rowWelementsright = nextstate.substring(indexofG - 1 + 1, indexofG - 1 + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			int counterofAtoright = 0;
	    			for(int i=0; i<rowWelementsright.length(); i++)//finds the firstindex of A in the rowlements
	    			{
	    				if(rowWelementsright.charAt(i) == 'A')
	    					counterofAtoright++;
	    			}
	    			
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			//If it can be moved to right than no blocked goal
	    			if(leftindexofA != -1 && counterofAtoright >= 2 )
	    				return false;
	    			
	    			//Now we need to check whether it can be moved vertically
	    			
	    			//To check if there is an A position above the left neighbor
	    			int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		        	boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of left neighbor is not in first row than you there might be a possibility of moving up
		        	if(indexofG - 1>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG - 1) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A')
		        			{
		        				
		        				availableup = true; //This means we found an A position
		        				break;
		        			}
		        			
		        			dummycounter += no_of_columns;
		        		}
	    			}
		        	
		        	
		        	dummycounter = indexofG - 1; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		    		boolean availabledown = false; //available to check the availability of A
		    		
		    		//Only if position of w is not in last row than you there might be a possibility of moving down
		    		if(indexofG - 1 < (nextstate.length() - no_of_columns))
		    		{
		    			//Check the availability of A
		    			while(dummycounter<nextstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
		    			{
		    				if(nextstate.charAt(dummycounter) == 'A')
		    				{
		    					availabledown = true; //This means we found an A position
		    					break;
		    				}
		    				
		    				dummycounter += no_of_columns;
		    			}
		        	
		    		}
		    		
		    		
		    		/*
	    			 * You should check for X between the index of A to the top and index of A to the bottom
	    			 * But in this game X don't come between 2 A's hence not checked. Sorry
	    			 */
		    		
		    		//It can be moved up or down and hence no blocked goal
		    		if(availableup == true && availabledown == true)
		    			return false;
		    		
		    		
	    			}
	    		} // left neighbor checking done
	    		
	    		
	    		
	    		//Check if the right neighbour can be moved or not
	    		if(rightneighbor)
	    		{
	    			
	    			if(nextstate.charAt(indexofG + 1) == 'B')
	    			{
	    			int c = (indexofG + 1)%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		
	    			
	    			//To check if there are 2 A positions to its left
	    			String rowWelementsleft = nextstate.substring(indexofG+1-c, indexofG + 1); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsleft);
	    			int counterofAtoleft = 0;
	    			for(int i=0; i<rowWelementsleft.length(); i++)//finds the firstindex of A in the rowlements
	    			{
	    				if(rowWelementsleft.charAt(i) == 'A')
	    					counterofAtoleft++;
	    			}
	    			
	    			//System.out.println(counterofAtoleft);

	    			
	    			//To check if there is an A position to its right
	    			String rowWelementsright = nextstate.substring(indexofG + 1 + 1, indexofG + 1 + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsright);
	    			int rightindexofA = rowWelementsright.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(rightindexofA);
	    			
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to right than no blocked goal
	    			if(counterofAtoleft >= 2 &&  rightindexofA != -1)
	    				return false;
	    
	    			//Now we need to check whether it can be moved vertically
	    			
	    			//To check if there is an A position above the left neighbor
	    			int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		        	boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of right neighbor is not in first row than you there might be a possibility of moving up
		        	if(indexofG + 1>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG + 1) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A')
		        			{
		        				
		        				availableup = true; //This means we found an A position
		        				break;
		        			}
		        			
		        			dummycounter += no_of_columns;
		        		}
	    			}
		        	
		        	//To check if there is an A position below the left neighbor
		        	dummycounter = indexofG + 1; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		    		boolean availabledown = false; //available to check the availability of A
		    		
		    		//Only if position of w is not in last row than you there might be a possibility of moving down
		    		if(indexofG + 1 < (nextstate.length() - no_of_columns))
		    		{
		    			//Check the availability of A
		    			while(dummycounter<nextstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
		    			{
		    				if(nextstate.charAt(dummycounter) == 'A')
		    				{
		    					availabledown = true; //This means we found an A position
		    					break;
		    				}
		    				
		    				dummycounter += no_of_columns;
		    			}
		        	
		    		}
		    		
		    		
		    		/*
	    			 * You should check for X between the index of A to the top and index of A to the bottom
	    			 * But in this game X don't come between 2 A's hence not checked. Sorry
	    			 */
		    		
		    
		    		//It can be moved up or down and hence no blocked goal
		    		if(availableup == true && availabledown == true)
		    			return false;
		    		
		    		
		    		
	    			}
	    		} // right neighbor checking done
	    		
	    		
	    		
	    		
	    		//Check if the up neighbour can be moved or not
	    		if(upneighbor)
	    		{
	    			
	    			if(nextstate.charAt(indexofG - no_of_columns) == 'B')
	    			{
	    			int c = (indexofG - no_of_columns)%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		
	    			
	    			//To check if there is an A position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG-no_of_columns-c, indexofG - no_of_columns); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsleft);
	    			int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(leftindexofA);
	    			

	    			
	    			//To check if there is an A position to its right
	    			String rowWelementsright = nextstate.substring(indexofG - no_of_columns + 1, indexofG - no_of_columns + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsright);
	    			int rightindexofA = rowWelementsright.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(rightindexofA);
	    			
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to left or right than no blocked goal
	    			if(leftindexofA != -1 && rightindexofA != -1)
	    				return false;
	    			
	    			//Now we need to check whether it can be moved vertically
	    			
	    			//To check if there is an A position above the left neighbor
	    			int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		        	boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of up neighbor is not in first row than you there might be a possibility of moving up
		        	if(indexofG - no_of_columns>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG - no_of_columns) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A')
		        			{
		        				
		        				availableup = true; //This means we found an A position
		        				break;
		        			}
		        			
		        			dummycounter += no_of_columns;
		        		}
	    			}
		        	
		        	
		        	//To check if there is are 2 A positions below the up neighbor
		        	dummycounter = indexofG - no_of_columns; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		    		int counterOfAdown = 0; //available to check the availability of A
		    		
		    		//Only if position of w is not in last row than you there might be a possibility of moving down
		    		if(indexofG - no_of_columns < (nextstate.length() - no_of_columns))
		    		{
		    			//Check the availability of A
		    			while(dummycounter<nextstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
		    			{
		    				if(nextstate.charAt(dummycounter) == 'A')
		    				{
		    					counterOfAdown++; //This means we found an A position
		    				}
		    				
		    				dummycounter += no_of_columns;
		    			}
		        	
		    		}
		    		
		    		
		    		/*
	    			 * You should check for X between the index of A to the top and index of A to the bottom
	    			 * But in this game X don't come between 2 A's hence not checked. Sorry
	    			 */
		    		
		    		//System.out.println(availableup);
		    		//System.out.println(counterOfAdown);
		    		
		    		//If it can be moved vertically down than no blocked goal
	    			if(availableup && counterOfAdown >= 2)
	    				return false;
		    		
	    			//System.out.println("hieee");
	    			}
	    		} // up neighbor checking done
	    		
	    		
	    		
	    		//Check if the down neighbor can be moved or not
	    		if(downneighbor)
	    		{
	    			
	    			if(nextstate.charAt(indexofG + no_of_columns) == 'B')
	    			{
	    			int c = (indexofG + no_of_columns)%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		
	    			
	    			//To check if there is an A position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG + no_of_columns-c, indexofG + no_of_columns); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsleft);
	    			int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(leftindexofA);
	    			

	    			
	    			//To check if there is an A position to its right
	    			String rowWelementsright = nextstate.substring(indexofG + no_of_columns + 1, indexofG + no_of_columns + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsright);
	    			int rightindexofA = rowWelementsright.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(rightindexofA);
	    			
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to left or right than no blocked goal
	    			if(leftindexofA != -1 && rightindexofA != -1)
	    				return false;
	    			
	    			//Now we need to check whether it can be moved vertically
	    			
	    			//To check if there are 2 A positions above the down neighbor
	    			int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
	    			int counterOfAup = 0; //available to check the availability of A
	    			
	    			
	    			//boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of up neighbor is not in first row than you there might be a possibility of moving up
		        	if(indexofG + no_of_columns>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG + no_of_columns) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A')
		        			{
		        				
		        				counterOfAup++; //This means we found an A position
		        				
		        			}
		        			
		        			dummycounter += no_of_columns;
		        		}
	    			}
		        	
		        	
		        	//To check if there is an A position below the down neighbor
		        	dummycounter = indexofG + no_of_columns; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		        	boolean availabledown = false; //available to check the availability of A
		    		
		    		//Only if position of w is not in last row than you there might be a possibility of moving down
		    		if(indexofG + no_of_columns < (nextstate.length() - no_of_columns))
		    		{
		    			//Check the availability of A
		    			while(dummycounter<nextstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
		    			{
		    				if(nextstate.charAt(dummycounter) == 'A')
		    				{
		    					availabledown = true; //This means we found an A position
		    					break;
		    				}
		    				
		    				dummycounter += no_of_columns;
		    			}
		        	
		    		}
		    		
		    		
		    		/*
	    			 * You should check for X between the index of A to the top and index of A to the bottom
	    			 * But in this game X don't come between 2 A's hence not checked. Sorry
	    			 */
		    		
		    		//System.out.println(availableup);
		    		//System.out.println(counterOfAdown);
		    		
		    		//If it can be moved vertically down than no blocked goal
	    			if(counterOfAup >= 2 && availabledown)
	    				return false;
		    		
	    			//System.out.println("hieee");
	    			}
	    		} // down neighbor checking done
	    		
	    		
	    		
				return true;
			}

			States right(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "left")
	        		
	        	{
	        	
	        	
	        	int w = this.indexofW; // w holds the index of white position
	        	int c = w%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	        		
	        	String rowWelements = this.currentstate.substring(w+1, w + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	        		
	        	int firstindexofA = rowWelements.indexOf("A"); //finds the firstindex of A in the rowlements
	        	int isGthere = rowWelements.indexOf('G'); //finds whether G is present in that row
	        	
	        	
	        	if((c+1)%no_of_columns != 0)
	        	{
	        	//Check if there is an available position in the row or the Goal is to your immediate right
	        		if(firstindexofA != -1 || this.currentstate.charAt(w+1)=='G')
	        		{
	        			if(firstindexofA != -1)
	        				firstindexofA += w + 1; //Get the correct index of A by adding w + 1
	        			else
	        				firstindexofA = w+1;
	        		
	        			//comparison between position of A and G and the bigger value wins...done using ternary operator
	        			if(isGthere != -1)
	        				firstindexofA = firstindexofA < indexofG ? firstindexofA : indexofG ;
	        			
	        			String checkforX = this.currentstate.substring(w+1,firstindexofA); //Create a substring from w+1 to firstindexofa to check if there is an X between them
	        			int indexofX = checkforX.indexOf('X'); //Check the presence of X
	        			
	        			//If X is not present you can move to the left to the available position
	        			if(indexofX == -1)
	        			{
	        				//Create the new state with elemnts till A + elements from A+1,W+1 + A + remaining elements
	        				String nextstate = this.currentstate.substring(0, w)+ "A" + this.currentstate.substring(w, firstindexofA)+this.currentstate.substring(firstindexofA + 1); 
	        				//System.out.println(nextstate + " right " +"    " + (g+h));
	        				checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method
	        				
	        				if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("Type of Deadlock 1 - Right");
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("deadlock 3 found" + nextstate);
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	        				
	        				return new States(this, nextstate, indexofG, "right");
	        			}	
	        		}
	        	}
	        	}
	        	return null;
	        }
	        
	        States up(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "down")
	        	{
	        		
	        	
	        	int w = this.indexofW; // w holds the index of white position
	        	int c = w%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	        	int r = w/(no_of_columns);
	        	
	        	int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
	        	boolean available = false; //available to check the availability of A
	        	
	        	//Only if position of w is not in first row than you there might be a possibility of moving up
	        	if(w>=no_of_columns)
	        	{
	        		//Check the availability of A
	        		while(dummycounter<w) //Check all the elements above w in the same column to check whether some position is available or not
	        		{
	        			
	        			if(currentstate.charAt(dummycounter) == 'A')
	        			{
	        				
	        				available = true; //This means we found an A position
	        				break;
	        			}
	        			
	        			dummycounter += no_of_columns;
	        		}
	        		
	        		if(currentstate.charAt(w-no_of_columns)=='G') //Also if the goal is to immediate up available is true 
	        			available = true;
	        		
	        		//Only if an A position is there in that column above w or if the immediate top element is Goal than only we can move W up  
	        		if(available == true)
	        		{
	        			
	        			int dummyoccurencecheck = w; //dummy occurence check to find the nearest position of A from w in bottom to top manner
	        			int nearestAindex; //variable to hold nearestAindex
	        			
	        			//Find nearest occurence of A or G by going in reverse order from w and than up
	        			while(dummyoccurencecheck >= c)
	        			{
	        				if(currentstate.charAt(dummyoccurencecheck)=='A' || currentstate.charAt(dummyoccurencecheck)=='G')
	        				{
	        					break;
	        				}
	        				
	        				dummyoccurencecheck -= no_of_columns;
	        			}
	        			
	        			if(dummyoccurencecheck > 0)// if its greater than 0 means it found A
	        				nearestAindex = dummyoccurencecheck;
	        			else
	        				nearestAindex = w-no_of_columns; //if its < 0 means it missed A and hence G is at its immediate upward element
	        			
	        			//Find if there is X between nearestAindex and W
	        			int dummycheckforX = w; //dummy to check for X
	        			
	        			while(dummycheckforX > nearestAindex)
	        			{
	        				if(currentstate.charAt(dummycheckforX)=='X')
	        				{
	        					dummycheckforX = -99; //Value of -99 is assigned to identify the presence of X between A and W
	        					break;
	        				}
	        				
	        				dummycheckforX -= no_of_columns;
	        			}
	        			
	        			int dummynewstatebuilder = nearestAindex; //dummynewstatebuilder is used to generate the recursive string between a and w 
	        			StringBuilder intermediatestring = new StringBuilder(); //intermediatestring is used for the recursive string
	        			
	        			if(dummycheckforX != -99)
	        			{
	        				
	        				//Genearate the recursive string between a and w
	        				while(dummynewstatebuilder <  w) 
	        				{
	        					//keep appending to the intermediate string
	        					intermediatestring.append(currentstate.charAt(dummynewstatebuilder + no_of_columns) + currentstate.substring((dummynewstatebuilder + 1),(dummynewstatebuilder + 1 + no_of_columns -1)));
	        					dummynewstatebuilder += no_of_columns;	//keep incrementing it by the number of columns
	        				}
	        				//Next state generated using intermediatestring and adding additional elements
	        				String nextstate = currentstate.substring(0, nearestAindex) + intermediatestring + "A" + currentstate.substring(w+1);
	        				//System.out.println(nextstate + " up " +"    " + (g+h));
	        				checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method
	        				
	        				if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("Type of Deadlock 1 - Up");
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("deadlock 3 found" + nextstate);
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	        				
	        				return new States(this, nextstate, indexofG,"up");
	        				
	        			}	
	        				
	        		}
	        		
	        	}}
	        	return null;
	        }
	        
	        States down(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "up")
	        	{
	        	int w = this.indexofW; // w holds the index of white position
	    		int c = w%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		int r = w/(no_of_columns);
	    		
	    		int dummycounter = w; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
	    		boolean available = false; //available to check the availability of A
	    		
	    		//Only if position of w is not in last row than you there might be a possibility of moving down
	    		if(w < (currentstate.length() - no_of_columns))
	    		{
	    			//Check the availability of A
	    			while(dummycounter<currentstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
	    			{
	    				if(currentstate.charAt(dummycounter) == 'A')
	    				{
	    					available = true; //This means we found an A position
	    					break;
	    				}
	    				
	    				dummycounter += no_of_columns;
	    			}
	    			
	    			if(currentstate.charAt(w+no_of_columns)=='G') //Also if the goal is to immediate down available is true 
	    				available = true;
	    			
	    			//Only if an A position is there in that column below w or if the immediate bottom element is Goal than only we can move W down  
	    			if(available == true)
	    			{
	    				int dummyoccurencecheck = w; //dummy occurence check to find the nearest position of A from w in top to bottom manner
	    				int nearestAindex; //variable to hold nearestAindex
	    				
	    				//Find nearest occurence of A by going in straight order from w and than down
	    				while(dummyoccurencecheck < currentstate.length())
	    				{
	    					if(currentstate.charAt(dummyoccurencecheck)=='A' || currentstate.charAt(dummyoccurencecheck)=='G')
	    					{
	    						break;
	    					}
	    					
	    					dummyoccurencecheck += no_of_columns;
	    				}
	    				
	    				if(dummyoccurencecheck < currentstate.length())// if its less than total length means it found A
	    					nearestAindex = dummyoccurencecheck;
	    				else
	    					nearestAindex = w+no_of_columns; //if its > total length means it missed A and hence G is at its immediate downward element
	    				
	    				//Find if there is X between W and nearestAindex
	    				int dummycheckforX = w; //dummy to check for X
	    				
	    				while(dummycheckforX < nearestAindex)
	    				{
	    					if(currentstate.charAt(dummycheckforX)=='X')
	    					{
	    						dummycheckforX = -99; //Value of -99 is assigned to identify the presence of X between A and W
	    						break;
	    					}
	    					
	    					dummycheckforX += no_of_columns;
	    				}
	    				
	    				int dummynewstatebuilder = w; //dummynewstatebuilder is used to generate the recursive string between w and a 
	    				StringBuilder intermediatestring = new StringBuilder(); //intermediatestring is used for the recursive string
	    				
	    				if(dummycheckforX != -99)
	    				{
	    					//Genearate the recursive string between w and a
	    					while(dummynewstatebuilder < nearestAindex) 
	    					{
	    						//keep appending to the intermediate string
	    						intermediatestring.append(currentstate.substring(dummynewstatebuilder + 1, dummynewstatebuilder + 1 + no_of_columns - 1) + currentstate.charAt(dummynewstatebuilder));
	    						dummynewstatebuilder += no_of_columns;	//keep incrementing it by the number of columns
	    					}
	    					
	    					//Next state generated using intermediatestring and adding additional elements
	    					String nextstate = currentstate.substring(0, w) + "A" + intermediatestring + currentstate.substring(nearestAindex + 1);
	    					//System.out.println(nextstate + " down " +"    " + (g+h));
	    					checkGoal(nextstate.indexOf('W'), indexofG); //Call checkgoal method
	    					
	    					if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("Type of Deadlock 1 - Down");
	        					no_of_deadlocks_found++;
	        					return null;
	        				}
	    					
	    					if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	    						System.out.println("deadlock 3 found" + nextstate);
	    						no_of_deadlocks_found++;
	    						return null;
	        				}
	    					
	    					return new States(this, nextstate, indexofG, "down");
	    				}	
	    			}
	    			
	    		}
	        	}
	    		return null;
	        }    
	        
	   }
	   
	public static void main(String[] args) 
	{
		//String initialstate = "XXAAAABABBXGABW";
		//String initialstate = "WAXXBXXBXXAGXAXXAX"; level 3
		//String initialstate = "GAAW"; //level 1
		//String initialstate = "ABGBAAXBXAAAWAA";
		//String initialstate = "XXAABABABWXGBAA"; //2nd deadlock type
		//String initialstate = "GAABW";
		//String initialstate = "XXAAAABABBXGABW";
		//String initialstate = "XXAXXXABXXABGBAXXBAXXXBXXXXWXX"; //level 5
		
		//String initialstate = "BBAAAAGBBW";
		//String initialstate = "ABBAXBAAXAWAXABBXXGB";
		//String initialstate = "XXAABABBWAXGBAA";
		
		
		//Results
		//String initialstate = "GAAW";
		//String initialstate = "WBAGA";
		//String initialstate = "WAXXBXXBXXAGXAXXAX";
		//String initialstate = "ABBWXABBXABBXAAAXXGA";
		//String initialstate = "XXAAAABABBXGABW";
		//String initialstate = "ABBAXBAAXAWBXABBXXGA";
		//String initialstate = "XXXAAAAAGBAABAABAABAAAAABBAAAAAAAAAAAAABXBAAAABW";
		//String initialstate = "XXXAAAAAGBAABAABAABAAAAABBAAAAAAAAAAAAABABAAAABAAAAAAAAAABAABAABAABAAAAABBAAAAAAAAAAAAABXBAAAABW";
		//String initialstate = "XXXAAAAAAAAAGBAABAABAAAAAABAAAAAAAAABBAAAAAAABAAAAAAAAABAAAAABAAAABAAAAAAAAAAAAAAAAABAAAAAABAAAAAAAAAAAAAAAAAAABAAAAAAABAAAAAAAAAAABXXXAAAAAABBW";
		//String initialstate = "XXXAAAAAAAAAGBAABAABAAAAAABAAAAAAAAABBAAAAAAABAAAAAAAAABAAAAABAAAABAAAAAAAAAAAAAAAAABAAAAAABAAAAAAAAAAAAAAAAAAABAAAAAAABAAAAAAAAAAABAAAAAAAAABBAAAAAAAAAAAAAABAABAABAAAAAABAAAAAAAAABBAAAAAAABAAAAAAAAABAAAAABAAAABAAAAAAAAAAAAAAAAABAAAAAABAAAAAAAAAAAAAAAAAAABAAAAAAABAAAAAAAAAAABXXXAAAAAABBW";
		
		//String initialstate = "XAGBXAXW"; 
		
		
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABXBAAAABAABAAAABW";
		
		//String initialstate = "ABBAXBAAXAWBXABBXXGA";
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABW";
		
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAAAABAAAABBXXXAAAAAAAAAAAAW";
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBABBAABABAABAABAABBBBAAAABAAAAABBAAAAAABBAAAAABBBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABABBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAAAABAAAABBXXXAAAAAABBBBBBW";
		
		
		
		String initialstate = "XAAWBGXXA";
		long start = System.currentTimeMillis();
		System.out.println("start time " + start);
		//String initialstate = "ABBWXABBXABBXAAAXXGA"; //level 5
		
		int indexofG = initialstate.indexOf('G'); //Find the index of goal
		//int indexofG = 18;
		MulledAstar e = new MulledAstar();// Make an object of the class
		
		e.queue.clear();
		e.closed.clear();
		
		e.queue.add(new States(initialstate, indexofG)); // ADD the initial state configurations
		
		//Keep running until the queue becomes empty  
		while (!e.queue.isEmpty()) 
		{
            // Get the lowest priority state.
            States currentstate = e.queue.poll();
            
            e.closed.add(currentstate);
            no_of_nodes_evaluated++;
            
        	States templeft = currentstate.left(indexofG); //Check if white ball can be moved to left
        	if(templeft != null && !e.closed.contains(templeft))
        	{
        		e.queue.add(templeft);
        		System.out.println(templeft.currentstate + "   cost:  " + (templeft.g+templeft.h) + "   left");
        		//no_of_nodes_evaluated++;
      
        	}
        	
        	States tempright = currentstate.right(indexofG); //Check if white ball can be moved to right
        	if(tempright != null && !e.closed.contains(tempright))
        	{
        		e.queue.add(tempright);
        	    System.out.println(tempright.currentstate + "  cost:  " + (tempright.g+tempright.h) + "   right");
        	    //no_of_nodes_evaluated++;
        	}
        	
        	States tempup = currentstate.up(indexofG); //Check if white ball can be moved up
    		if(tempup != null && !e.closed.contains(tempup))
        	{
    			e.queue.add(tempup);
    		 System.out.println(tempup.currentstate + "  cost:  " + (tempup.g+tempup.h) + "   up");
    		 //no_of_nodes_evaluated++;
        	}
    		
    		
    		States tempdown = currentstate.down(indexofG); //Check if white ball can be moved down
    		if(tempdown != null && !e.closed.contains(tempdown))
    		{
        		e.queue.add(tempdown);
    		 System.out.println(tempdown.currentstate + "    cost:  " + (tempdown.g+tempdown.h) + "   down");
    		 //no_of_nodes_evaluated++;
    		}
    		
    		
    		
     
		}
		System.out.println("Solution does not exist     " +  no_of_nodes_evaluated + "No of deadlocks found = " + no_of_deadlocks_found);
		
	}


}
