package Game;




import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;




class MulledAstarwithoutDeadlock 
{
	static int no_of_columns = 12; //Gving the number of columns in the given grid
	static int no_of_nodes_evaluated = 0;
	static int no_of_deadlocks_found = 0;
	static long time_taken_for_one = 0;
	static int n = 100;
	public static ArrayList<StringBuilder> listOfInitial = new ArrayList();
	static float total_no_nodes_evaluated = 0;
	static float total_no_of_deadlocks_evaluated = 0;
	static float total_time_taken = 0;
	static long start = 0;
	static long elapsed = 0;
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
	        
	        boolean checkGoal(int indexofW, int indexofG)
	        {
	        	if(indexofW==indexofG)
	        	{
	        		return true;
	        	}
	        	return false;
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
	        		if(lastindexofA != -1 || isGthere != -1)
	        		{
	        			if(lastindexofA != -1)
	        				lastindexofA += w-c; //Get the correct index of A by adding w-c
	        			else
	        				lastindexofA = indexofG;
	        			
	        			
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
	        				//checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method	
	        				
	        				if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("Type of Deadlock 1 - Left   " + nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("deadlock 3 found  -Left  " + nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
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
	        		
	        	   boolean availableleft = false;
	        	   boolean availableright = false;
	        	//This check is for horizontal movement
	        		int c = (indexofG)%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	    		
    			
	        		//To check if there is an A position to its left
	        		String rowWelementsleft = nextstate.substring(indexofG-c, indexofG); //rowWelements hold the elemnts in the same row of W
	        		//int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	        		//System.out.println(leftindexofA);
	        		for(int i=0;i<rowWelementsleft.length();i++)
	        		{
	        			if(rowWelementsleft.charAt(i)=='A' || rowWelementsleft.charAt(i)=='W')
	        			{
	        				availableleft = true;
	        				break;
	        			}
	        				
	        		}
	        		
	        		String rowWelementsright = nextstate.substring(indexofG, indexofG + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	        		
	        		
	        		for(int i=0;i<rowWelementsright.length();i++)
	        		{
	        			if(rowWelementsright.charAt(i)=='A' || rowWelementsright.charAt(i)=='W')
	        			{
	        				availableright = true;
	        				break;
	        			}
	        				
	        		}
	        		
		        	//int rightindexofA = rowWelements.indexOf("A"); //finds the firstindex of A in the rowlements
		        	//System.out.println(rightindexofA);
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to left or right than no blocked goal
	    			if(availableleft==true && availableright==true)
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
		        			
		        			if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
		    				if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
	    		
	    			boolean availableleft = false;
	    			boolean availableright = false;
	    			//To check if there is an A or W position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG-1-c, indexofG - 1); //rowWelements hold the elemnts in the same row of W
	    			
	    			for(int i=0; i<rowWelementsleft.length(); i++)//finds the firstindex of A in the rowlements
	    			{
	    				if(rowWelementsleft.charAt(i) == 'A' || rowWelementsleft.charAt(i) == 'W')
	    				{
	    					availableleft = true;
	    					break;
	    				}
	    			}
	    			
	    			//int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(leftindexofA);
	    			
	    			//To check if there is an A or W position to its right
	    			String rowWelementsright = nextstate.substring(indexofG - 1 + 1, indexofG - 1 + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			
	    			for(int i=0; i<rowWelementsright.length(); i++)//finds the firstindex of A in the rowlements
	    			{
	    				if(rowWelementsright.charAt(i) == 'A' || rowWelementsright.charAt(i) == 'W')
	    				{
	    					availableright = true;
	    					break;
	    				}
	    			}
	    			
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			//If it can be moved to right than no blocked goal
	    			if(availableleft== true && availableright == true )
	    				return false;
	    			
	    			//Now we need to check whether it can be moved vertically
	    			
	    			//To check if there is an A or W position above the left neighbor
	    			int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		        	boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of left neighbor is not in first row than you there might be a possibility of moving up
		        	if(indexofG - 1>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG - 1) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
		    				if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
	    		
	    			boolean availableleft = false;
	    			boolean availableright = false;
	    			//To check if there is an A or W position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG+1-c, indexofG + 1); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsleft);
	    			//int counterofAtoleft = 0;
	    			for(int i=0; i<rowWelementsleft.length(); i++)//finds the firstindex of A in the rowlements
	    			{
	    				if(rowWelementsleft.charAt(i) == 'A' || rowWelementsleft.charAt(i)=='W')
	    				{
	    					availableleft = true;
	    					break;
	    				}
	    			}
	    			
	    			//System.out.println(counterofAtoleft);

	    			
	    			//To check if there is an A or W position to its right
	    			String rowWelementsright = nextstate.substring(indexofG + 1 + 1, indexofG + 1 + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsright);
	    			//int rightindexofA = rowWelementsright.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(rightindexofA);
	    			for(int i=0;i<rowWelementsright.length();i++)
	    			{
	    				if(rowWelementsright.charAt(i) == 'A' || rowWelementsright.charAt(i)=='W')
	    				{
	    					availableright = true;
	    					break;
	    				}
	    			}
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to right than no blocked goal
	    			if(availableleft==true && availableright==true)
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
		        			
		        			if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
		    				if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
	    		
	    			boolean availableleft = false;
	    			boolean availableright = false;
	    			//To check if there is an A or W position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG-no_of_columns-c, indexofG - no_of_columns); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsleft);
	    			//int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(leftindexofA);
	    			
	    			for(int i=0;i<rowWelementsleft.length();i++)
	    			{
	    				if(rowWelementsleft.charAt(i) == 'A' || rowWelementsleft.charAt(i)=='W')
	    				{
	    					availableleft = true;
	    					break;
	    				}
	    			}

	    			
	    			//To check if there is an A or W position to its right
	    			String rowWelementsright = nextstate.substring(indexofG - no_of_columns + 1, indexofG - no_of_columns + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsright);
	    			//int rightindexofA = rowWelementsright.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(rightindexofA);
	    			
	    			for(int i=0;i<rowWelementsright.length();i++)
	    			{
	    				if(rowWelementsright.charAt(i) == 'A' || rowWelementsright.charAt(i)=='W')
	    				{
	    					availableright = true;
	    					break;
	    				}
	    			}
	    			
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to left or right than no blocked goal
	    			if(availableleft==true && availableright==true)
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
		        			
		        			if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
		        			{
		        				
		        				availableup = true; //This means we found an A position
		        				break;
		        			}
		        			
		        			dummycounter += no_of_columns;
		        		}
	    			}
		        	
		        	
		        	//To check if there is are 2 A positions below the up neighbor
		        	dummycounter = indexofG - no_of_columns; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
		    		//int counterOfAdown = 0; //available to check the availability of A
		    		boolean availabledown=false;
		    		//Only if position of w is not in last row than you there might be a possibility of moving down
		    		if(indexofG - no_of_columns < (nextstate.length() - no_of_columns))
		    		{
		    			//Check the availability of A
		    			while(dummycounter<nextstate.length()) //Check all the elements above w in the same column to check whether some position is available or not
		    			{
		    				if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
		    				{
		    					availabledown=true;
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
	    			if(availableup && availabledown)
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
	    		
	    			boolean availableleft = false;
	    			boolean availableright = false;
	    			//To check if there is an A position to its left
	    			String rowWelementsleft = nextstate.substring(indexofG + no_of_columns-c, indexofG + no_of_columns); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsleft);
	    			//int leftindexofA = rowWelementsleft.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(leftindexofA);
	    			for(int i=0;i<rowWelementsleft.length();i++)
	    			{
	    				if(rowWelementsleft.charAt(i) == 'A' || rowWelementsleft.charAt(i)=='W')
	    				{
	    					availableleft = true;
	    					break;
	    				}
	    			}


	    			
	    			//To check if there is an A position to its right
	    			String rowWelementsright = nextstate.substring(indexofG + no_of_columns + 1, indexofG + no_of_columns + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
	    			//System.out.println(rowWelementsright);
	    			//int rightindexofA = rowWelementsright.indexOf("A"); //finds the lastindex of A in the rowlements
	    			//System.out.println(rightindexofA);
	    			//System.out.println(leftindexofA);
	    			for(int i=0;i<rowWelementsright.length();i++)
	    			{
	    				if(rowWelementsright.charAt(i) == 'A' || rowWelementsright.charAt(i)=='W')
	    				{
	    					availableright = true;
	    					break;
	    				}
	    			}
	    			
	    			/*
	    			 * You should check for X between the index of A to the left and last index of A to the right
	    			 * But in this game X dont come between 2 A's hence not cheked. Sorry
	    			 */
	    			
	    			//If it can be moved to left or right than no blocked goal
	    			if(availableleft && availableright)
	    				return false;
	    			
	    			//Now we need to check whether it can be moved vertically
	    			
	    			//To check if there are 2 A positions above the down neighbor
	    			int dummycounter = c; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
	    			//int counterOfAup = 0; //available to check the availability of A
	    			boolean availableup = false;
	    			
	    			//boolean availableup = false; //available to check the availability of A
		        	
		        	//Only if position of up neighbor is not in first row than you there might be a possibility of moving up
		        	if(indexofG + no_of_columns>=no_of_columns)
		        	{
		        		//Check the availability of A
		        		while(dummycounter<indexofG + no_of_columns) //Check all the elements above w in the same column to check whether some position is available or not
		        		{
		        			
		        			if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
		        			{
		        				
		        				availableup = true;
		        				break;
		        				
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
		    				if(nextstate.charAt(dummycounter) == 'A' || nextstate.charAt(dummycounter) == 'W')
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
	    			if(availableup && availabledown)
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
	        		if(firstindexofA != -1 || isGthere != -1)
	        		{
	        			if(firstindexofA != -1)
	        				firstindexofA += w + 1; //Get the correct index of A by adding w + 1
	        			else
	        				firstindexofA = indexofG;
	        			
	        		
	        			//comparison between position of A and G and the lesser value wins...done using ternary operator
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
	        				//checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method
	        				
	        				if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("Type of Deadlock 1 - Right   "+nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("deadlock 3 found - Right   " + nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
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
	        	
	        	int dummycounter = w; //dummy counter so that g can be used as the counter to check the availability of the A in that column containing w  
	        	boolean available = false; //available to check the availability of A
	        	int nearestAindex; //variable to hold nearestAindex
	        	//Only if position of w is not in first row than you there might be a possibility of moving up
	        	if(w>=no_of_columns)
	        	{
	        		
	        			
	        			
	        			
	        			//Find nearest occurence of A or G by going in reverse order from w and than up
	        			while(dummycounter >= c)
	        			{
	        				if(currentstate.charAt(dummycounter)=='A' || currentstate.charAt(dummycounter)=='G')
	        				{
	        					available = true;
	        					break;
	        				}
	        				
	        				dummycounter -= no_of_columns;
	        			}
	        			
	        			if(available == true)
	        			{
	        				
	        			
	        				nearestAindex = dummycounter;
	        		
	        			
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
	        				//checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method
	        				
	        				if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("Type of Deadlock 1 - Up    " + nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("deadlock 3 found -Up   " + nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
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
	    		int nearestAindex; //variable to hold nearestAindex
	    		//Only if position of w is not in last row than you there might be a possibility of moving down
	    		if(w < (currentstate.length() - no_of_columns))
	    		{
	    			
	    			
	    			//Only if an A position or Goal position is there in that column below w or if the immediate bottom element is Goal than only we can move W down  
	    				//Find nearest occurence of A by going in straight order from w and than down
	    				while(dummycounter < currentstate.length())
	    				{
	    					if(currentstate.charAt(dummycounter)=='A' || currentstate.charAt(dummycounter)=='G')
	    					{
	    						available = true;
	    						break;
	    					}
	    					
	    					dummycounter += no_of_columns;
	    				}
	    				
	    				if(available == true)
	    				{
	    					nearestAindex = dummycounter;
	    				
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
	    					//checkGoal(nextstate.indexOf('W'), indexofG); //Call checkgoal method
	    					
	    					if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
	        				{
	        					//System.out.println("Type of Deadlock 1 - Down   " + nextstate);
	        					no_of_deadlocks_found++;
	        					//return null;
	        				}
	    					
	    					if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	    						//System.out.println("deadlock 3 found  - Down   " + nextstate);
	    						no_of_deadlocks_found++;
	    						//return null;
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
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABXBAAAABAABAAAABW";
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABW";
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBABBAABABAABAABAABBBBAAAABAAAAABBAAAAAABBAAAAABBBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABABBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAAAABAAAABBXXXAAAAAABBBBBBW";
		//String initialstate = "WBAGA";
		
		
		/*
		 * Trying to generate random instanes of a fix grid
		 * Grid size is a square s*s
		 * W ball is to the farthest bottom
		 * G ball is to the top left.
		 * Grid does not have any no cells field like X
		 * Number of black balls is also fixed = s+1;
		 */
		String listOfInitialStrings[] = {"GABBBBBBABAABABAABABAAABBAAAABABBBBBABBBBBBAAABBABAAABBABBBABBBAAABABAABABABAAAAABBABABBABBABABBABBAAAAABAABBABABBABAABBABAAABBBBBBAAAABAABABAAW",
				"GAAAAABABBAABBBAAAABBABABBABBBBBBAABBAAABAABBBBBBABBABABBABAAABABBBABBBBABBABBABBBBBBAABAABAAABABAAABAAAAAABBABAABABAAAAABBABBAABBABBAABBABBAAAW",
				"GBAABBBABABBBBAAABABBABBAAABBABAABBBABBBBBAAABAAABAAAAAAABAAAABBBBBBBBABABABABBBBABAAABAAAABAAAABABBABABBBBAAAABBBBBABBBBABAABBAAABAABAABAABBABW",
				"GBABAABBABABBAAAAABAABBBBBABBBAAAABBBABBAABABBBABABABBABBAAABAAAAAABAABBAABABBAABABBABABAABABBBBABBAABBAAAAABBABABAABBBBBABBAAABBABBBBBAAAAABABW",
				"GBBABAAABBBBABBABBAABAABBAABBBBAAABBBAABABAABBABAAABABAAAABAAAAAABABBBBBBBAAAAABABBBBBABBABAAAAABABBAABABBBBBAAAABBBBBAAABABBABBAABAAAABBBBAABBW",
				"GABBBAABBBBBBBABBBAABAAABBAABBBABAAABABAAAAAABABBAABBABBBAABAAABAAABABAAABBAABAABAABBAABBAABABAABABABBBAAAABBABABABABAABABBBAABBBBBBBBAAABBABBBW",
				"GBBABBABBAABAAABABBBBABABABABAAAABBAABBBABBBAABBBABBBAAABABAABAABBBBBABAABBAAABAAAABABAABBBBABAAAABAABBAABABBBBABBAABAABAAABABABBBBBBBBAAABAAAAW",
				"GBABABBBBBBBBBBBABABBAABABAAAAAAAABBABAAAAABBAAAAAABBBBBABBABBAAABBBABBAAAAAABAAAAAABAABAAAABAAABAAABBBBBABBAABBBBBABAABBBABBBABBABBABBABBABBBAW",
				"GBABBABBAAAABBAABBABBBAABABAAAABBBBBBABBBBAABBAAABBABABAABABBAABBBBAAABABABBBABBAABAAAABAABBAAAAABBBBABAAABAAAABBBBBBABAAABBAAAABBBABBBBBAABAAAW",
				"GAABABAABBABABBBBBBBBAABBBAAABAABAAAABBABAABAABBBAABABBAAABABBAABABAABABBABABBAABABABBBBBAAABBBAAABAAAABBBBBAAAAAAABBABABBABBABBBABABAABABAABBBW",
				"GABBAABBBABABBBABBAAABABBAABAABBBBAAABBABAAABAAAAABABAAABBBAABBABABBBBAAAABBAABAABBABAAAAABABBABABAABBBABBBABBBAAAAAAABABBAAABAABBBBBABBBBBBBBAW",
				"GBBBBAAABBAAABAABBABBBBBBBAABBBAAAABBBBBABABAAABBAABBBBABAABBABBBBBBAAAABAAAAABABAABAABABAABBABBAABBBBBBAABBAAABAAAABBABBAAABAAAAABABAABABBABABW",
				"GAAABBBABBABABABBABBAAABABBBABBBBBAAABAAAABBBBABBAAABBBABABBBABBBBBBBABAAAAABBABAABBABBBAABBAABAAABBAAABAAABBBBBBABBAABABABBAAAAAAAAABBAABAAABAW",
				"GABBAABBBAABBABAAAABBBBAAABAAAAABABBBBBABAAABABBBABAAABBAABABAABBAAABBBABBBBAAABABAABAABABBBBAABABBBABBBABBBABBBBABABBBABAAAABBABABAAABBAAAAABAW",
				"GBBBAAAABABAAAAABBABBAAAAABBBBBAAABABAAAABBBBAAABAAABABAABAAABBABBAAABBABABAABBBABAABBBBABABABABBABBBAABABBBBABBBAAAABAABBBBAABBABBBBBAAABBABABW",
				"GBAAABBBBBBBBAAABBABBABBBAAABAABBAAABAABBBBBBBAABBBBABAAABBAAABBBBAAABBBBAAABAABBAABABBAABAABABBBAAABBBAABAABBBAABAABBBABAAAAAAABAABABBABBABAAAW",
				"GAABBAAAAABABABABBBBBBAABAAABBABABABBBABBAABAABBBAAAABBBABBBABAABAABAABBABBBBABAAAABABBBABABAABABABBABBAAABAABBABBAABBBBABAABAABAABABAABABBBBAAW",
				"GABBBAABBAABABABABABABBAABABBBBAAABAABABABAABABAABAAABBBABBBABBAAABBAAABBBAABBBAAAAABABAABABABABBAAAABBBABBABBBBABBAAABABBBBBABABAABBBAABABAABAW",
				"GBAAAABAABBBAAAABBABABABAABBABBBABABBBAABABBAAABBAABBABABABBABBABABAABBAABBBABBABAAABBABBAAAAAAABABABBABBBBABABAAAABBABBABBABBBBBAAABBABAABBAAAW",
				"GABBBBBBBBBBBABAAAABBBBBBBBBAABABAAABBBBAAABBBABABABAABAAAAABABAAABBBABBABBABAABBABAAABBAABBBABBAABBBABAAABABAABABAAABBAAABAABABAAABBAABBAABAAAW",
				"GBBABABABBBAAAABAABBBABAAAABABABAAAABAABBBBBBBAABABBBABAAAAAABABAAAABABBAABBAAAAABBABBAABAAAAABBBBAABAABBABBAABABBBABBBABBAABBABBBABBBABAABBBBBW",
				"GBAAABBBABAABAAAAABAABBABABBBABBAABBBBBAABABBABAABBBBABABAAABABAAABAABAABBBBBAAAABBAAAAAABBABABBBABAAAABABBBAABABBBABBBBABBAABBBBABBAAABBAAABABW",
				"GBABABABAAABBABBAABABBBABBABABBBAAAAAAABBABBAABAAAABAAABAABBABBAAABABBBBABAABBBBBAABBBBBBAAABABAABBBAABAAABBAABABABABAABBAAAABABABBABABBBBABBBAW",
				"GAABAABBBAAAABAABAABBABAAABABBAABBAABABBABBABBAABABBBBAABAAAABAAABBABBBBBBABBBAAAABBAABAABBBBBBBBABAABAABBBBABABBAABAABBAAAABBABAABBBBAABBAAABAW",
				"GBABBBAABBBBBAAABBBAAAABBABABABABABBABBABABBABABAAABBAABBBBBABAAAAAABBBABBAABAABAABAABBAABABAAABBBAAABAABBAABBABABBBBBBAAAABABAABABBABABAABABBAW",
				"GBABAABBAABBAABAAAAAABBABABBBAAAABBABBBAABABAABBAABBAABAABAABBBBAAABBBABABABBBBBBBBABBBAABAABABABABABABBBAAABBABABBBABABBBAAAABAAABAAAABABBAABBW",
				"GBBBAABAAABAAABABBBABBBBABBBAAAAABBBBABBBABAAABBBBAAAABABBBBBBAAABABABBAABBBABAABBBBABABAABBABAAAAABBAAAAABAAABBABAAABBABAABBABBAABBAABBAAABBABW",
				"GBAABBBABBBAABAABAABBABBAABBBABBAABABABABBBBAAAAAABABBAABABAABAAAAAAAABBAABBAAAABBBBABABBAABABAAAAAABAABBBBBABBBBABBAAABABBBABABBABABABBABBBBABW",
				"GAAAAABABAABAAAABBAAABABBABBABBBBBABBAABAAAABABAABBABAABBBBABABAAABBBABBABAABAABABBBABBABBABBBBAABABBBBBAAAAABBBAABAAABABBAABBABAAABBABBBBABBAAW",
				"GAABBBABABABABAABBABBBABAAABBBBABABAAABBBBABBABBBBAABBABBAAABAABBABBBABAAAAABAAAAABBAAABBBBABAAABBBAABAAABBBAAAABABBAAABBBBAAABBABABABAABABBBBAW",
				"GBAAABBBBABABBBBAAAABBABBBABABBBBAABABBAABAABAABAAABBAAAABABABBBBBBABBAABBABAAAABABBBBAABABABABAABABBBAABAAABAAABBABBABABBABBBBBBBAAABAAAABAAAAW",
				"GABBABBABBABAABBBAABBABBBAABBAAABABABABBBABBBBABAAAAABBBBABBBAAAAAAAABABABABABBAABABBBABAABABBABBAAAABAAABAABABBAABABABABBABBABBBBAABABBAAAABBAW",
				"GBAABBBBBABAABAABBBABBAAABBABAABBABABBBBBBBAABABAABBBAABABAABAABAAABBAABABBABBAAABABAABBBBAABBBABABBABBABABAAAABABAAABBBAABBBAAAABBBAAABAAAABBAW",
				"GBBBAABAABAABBAABABBBAABBBBBAAAAABBBAABBABBBABABBAABAABABABABBBAABBBBABBBABABABAAAAABBABAAAAAAAABABABAAAAAABBBBABBBAAAAAAAABBBABBABBABBBBABBBABW",
				"GABBBBABBBBABAABBBABAAABAAAAABBBBBABABAAABAAAABBBABBBAABAAAABBABBABBBBAABABABAAABBBAAABBBAABBAABBABABBBBABAABABAABAABABABBBBBAABBAAABABBAAAAABAW",
				"GAAABABABAAABABBBBBBABBBBAABAABBAAAABBBBBAABBBBBBAABBBBAABAABABABBBBBABAAAAABBAAAABAAAABBBABBABABBBBBBAAAABBAAABBAABAABABBBAAAABABABBAABABABAAAW",
				"GBABBBBBBBBBABABAABABBABAABBAAAAABBAABAAAABAAAAAAABBBABBBAAAAABAAAAABABABAABAAABBABBBAAABBBBBABBAABBABAABBBABABBAABAAABBBBAABBAABAABBBBABBABBBBW",
				"GBBBBAABBABBBABBBBABABBBAABABAABABAABAAAABBAABABABABAABAAAABABBBBAABBABBBBBBABAAAAABAAABABBBBAAAAABABABBABBABBBAAABBBABBBBBBBABBAAAAAAAAAAAABABW",
				"GAABBABBAAABAABBAABABAABAAAABABBAAAABAABBBBBAABBABABAAAABABABBABBAABABAABBBBBBAAABAABBABABBBBBBBBAABBBAABABBAABBAAABABABBBABABABBABAABBAABAABBAW",
				"GBBAABABBABABAAABAAABAAAABABBAAABBABABAAAABABABAAAAABBBAAAABBAABBBBABABBABABBBABABABBBABBBBBBBABABBAABAABAAAABBABBBAAAABABAAAAABBBBBBABAABBBBBBW",
				"GABAABBBBABBBBAABAAAABABABBBBABABBABBBBBBABAAABAABBBAAAABBAAABBBAAAABBAABABABBAABABBBBABABBAAAAABABABAAAAABAABABBAAAABAABAABABABBBABBBBABAABBBBW",
				"GAAAAABBAAAABBBABABBABBBBBBABBAABAABAABABBBBABAABBABABBAABABBBBABAABBABAAAABBBBBAABABABBBBAAABAAAAAAAAAAAAAABABBBBBABAABAAABBBAAABBABBBBBBAABBBW",
				"GAABBBBABAAABBABBAABAAAABBAAAAAAABABBABABAAAABBBBBAABABBBABABBABAABBABABBAAABABBBABABBBBAABBABBBBABBBBABAAAAABBAABABABAAAABABBABBBAAABABBAABBABW",
				"GBAABBBAABBBBABBBABAABBBBBBBAABBABABAAAABAAABAAAABBABBBABBBABAAABABABBBBBABAABBAABABBAAABABABABBBAABAAABBBABABABBAAAABBBAAAABAAAAABABAAABABBBABW",
				"GABBABABBABBBABABBAABBABABAABBBBAABABBBABBBABBAAAAAABAAABABBABAAAABBBABABBAAABABABAAAAABBBBBABBBBAAAAABBABABABAABBAAABAAABABABABBAABBABBBABABABW",
				"GABABAABABBAAABBBAABBBBBBBBBBBAAAAABBAABAABAABAABBABBBBBAABAAABABBABABABBABAABAABAAAAABBBBBABBABABBABBBAAABAABAABABAAAABBABABBBBBAAAABBBBAABAAAW",
				"GBBBBABABBABAABBABABBBABABABBABBABBBBAABABAABAAAABBAABAAAAABBAABAAAABBBAAABABABABBAABABBBBABBABBBAAABBBBABAAABBABABAABBBAABBAAABBABAABBBBAAAAAAW",
				"GBBABBBBABAABBBBAABBBAABAABBBBBBBBAABAABAABAAAABAAAABBBBBABBBBAAABBAABBBAAAAABAABBBBBAAAAAAABABAABBBBAABBABBBAABABAAABAAAAAAABBAABAABABABBBBBBAW",
				"GABAABBAAABBBBABABBABAAABABBABABBBBBABBBBBAAABABAAABAAAAABABBABBABBBABABAABAABBBAAAABBBABABAAAAABBABAAABABAAABBABABBABBBBAABBAAAAABABBBABBAABBBW",
				"GABBBBBAABAAABBBBBBBBAABAAABBBBBABAAAABAAABAAABBBABABBAABBBAABBAAAAABAAABBBBABBBABAAABBBAABAABBABBABBAAAABABBBAABBABABABBBAABABBAAAAABBBAAABBAAW",
				"GBBBAABBAABAAAABBAABAABAAABBBBABABBABAABAAABBBABAAABAAABBAABBAABABBBAAAABBBABAABAAAABBABBAABABBBBAABBBBBBAAAABBAABABBAABABABABBBBABBBBAABBBBAAAW",
				"GABBABBBBBAAABAABBBABAAABABBAAAABAAAABBAAAAAABABBBBBABABBBBABBABBBAAABBBBAAABBBBBABAABBAAAABBAAABBBABBAABAAAABBBBBBBBAABABBBABBABABAAABAAABAAAAW",
				"GBBAAABABBAAAAABBAAAAABBBBAAABABABABBBABABBAABBABBAABBBBBBBBBBBBBABBBAAABBABBBAABAABBABABBBAABBBBBBAAABABBAAABAAABAAABABBAAAABBAAABAAABBAAAAABAW",
				"GABABBAABAAAABABBBBBBAAABAABBABBABBAABBABAABBABAABAABABABBBBBABABAABAAAAABBBBAABBAABBBABAABAABAABABABAAAAABABBAABABBABBAAABBBBBABBBBAABABAABBABW",
				"GAABABAAAAAABAABABAABBBABAAAABBAABAABBAABAAABABABBBBAABBAABAAAABAAABBBBBAAABBAABBBBABABBBAAABABBBAABBABBABAAABBBBABBBABBBBAABBBAABBBABAABBBAABBW",
				"GBBAABABBBABBBABBBAAABBABABBABBABBAAAAAABBABBAABBBBAAABAAAAAAAABABABBBBABABAABBBABBBAABABAAAABABABAAABBBBABBBBBAABBBABBAAABABBABAAABAABBAAAABABW",
				"GBBBAABABBBBBABABBBBBBBAABBBAABBABBAABBABBBBAAABBABAABABBBAABBABAABBABBAABAAABABABAAABABABABBAAAAAAABAABAAAAAABBAAABBABBAABAAABBBBAABAAABBABABBW",
				"GABABBBABBAAAAABBBBBAABAAABABBAAAAAABBBBAABAAABABAAAAABABBBAAABABAABBBBBABBBBBBBAAAAAAAAABBBABBABBAAAAABBAABBBBBBBBABAAAABBAAABABABABBBBBBBABBAW",
				"GBBABAAABABAAABBBBABBABAAABBBAAABAABBBAABBBAABBAABBAAAABBAAABBABAABAABBBBAABBBABBBABBBBAABBBAAABABBBBAABABABAAAABAAABAABABAAABBBABBBAAABBBAABBAW",
				"GAAABBBABAAAABAABBBBBBAABAABBABBAABBABAABBABBBBAABABAABBABBBABBBAAAABAAAAAAABBBBAAAAABAABBBBABBBABBAABABABBABABBAAABABABBBAABBBABABAAABABBBAABAW",
				"GBBBAABBBBAABAAAABBBABAABBABBABABABBABBAAABAABBAAABABABAABBBBABBABABAABAABBAAABABBABABABBBABBABBAABBABBAAABBBBBABAAAABABBBBBBABAAAAAAAAABABAABAW",
				"GBAABBBABAABBABBABAABBAABABBBBAAAAABABABABBBBBBAABBBAAAAABAAAAAABBAAABAAABBAAABAAABBABBBAABBAABBABBBBBABBBBBBAAABAAAAABBABAAABBAAABBBBBABABBBABW",
				"GABBBBBAAAABBABBAABBBAABBBAABBBABAABBAABBABBAABABABBAAABAABABAAABBBABAABABBBBBBABAAABABABABABBABABBBAAABBBBABBABBAAAABABAABAAAAAABBABABAAABBAABW",
				"GABBBBBABABBABAAAABABBABABAABABBABBAAAABBBABBAAAAABBBAABAAAAAAABAAABABBABBBBABBAAABBBABBBBAABBAABBABBAABBBABBBBAAABABAAABBBBBBAAAAABBBBAABAABAAW",
				"GABAABABABBBBABAAABBBBAAAABBBAABAABBAABBBAAABAABBABBAAAABBAAAABABAABBBBABBABABBBABBABABABBAABABAAABBBAABABBABBBBBAAABABABBBBBBBBABAAABAAAABAAAAW",
				"GBBBAABAABABAABBABABAABABBBAABABAABABABBABABBABBAABBBBBAABAAAAAABBBBBBABBAABBBAAAABAABBBBAABABBBABBABBAABBAAAABAAABAAAAAAABBBAABAAABBABBBBBBABAW",
				"GBBAAAAAABABBABBBBBBBBAABABABABBAAAAAABBBBAAAAAABAABAABBBABBABAABAABBBBBAAABAABBBBBAABBBBBAAABAAAABBABBAABAABBABBBAAABBAABBBBBABBABAAABABBAABAAW",
				"GBBBBABBABABABBBABAAAAAABBAAAABBBAABBBAAABAAABABBABABBBBABABBBAABBABAABBABBBAAAAAABABABBBAABBABBBABBABABABAABBBABAABBBBBABAAABBBAAAAAAAABBAAABAW",
				"GAAABBAAABABBABAABAABBBAAABABBAAABAAABAAABBBBABBAAABBAAABABBBBABBBABBAABBBBAABAAAABAAABBAABBBBBAAABABABABBABBBAAABBAAABBABABABBABABBBAAABABBBBBW",
				"GBBBAABABAABBAABAAABBABAABBBABBABABBABAAAABAABBBAAAAABBBBAAABBABBBABBAABAABAABABAAABAABABBBBBBABBAAABBABBBBABAAAAABABBBBAAABBABABBAAAABABAABBBBW",
				"GABBBABAAAABBBABAABBBAAAABBAABABAABABBAAAABABABBABBBBBAAAABBBAABAABABBBBBBBBAABABAABBAAABBAABABAAAABAAABABABBABABBABBABBABAABABAABBABAABBBBABABW",
				"GBBBAAABAABBAABBBABAABBABBABBBBABABAABBABBABBABBBAAAABABAAABABBAABAABBBABAAABBAABABABAABABBAABABBABBAAAABABABBBAABBBABBBAABBBABAAABABABAAABABAAW",
				"GABBBBABABBBABABBAABBBAABAABBBBAAAAAABBABBBAAABABBBABBBBBBBBABBABABBBAABAAAAABAAABBAABAAABABAAABAAAABBBABBBAABAAABAABAABABABABABBABBBAABAABBBAAW",
				"GAAAABBBBBABBABABAABBAABBAAAABBABABABABBBAABAABAABBBAAABAABABAAABABBBBABBABBABBBAAABABAABABBABABBAAABBBAAABAAABAABABBABBAABABBBABBBABABBAABBAABW",
				"GBABBAABABBBBABBBBBABBAABBBBBABAAAAAAABBAAABAAAABBABABBABBAABAABBAAAAABBBBBAABAAAABAAABAAABABABBAAAABBABBBBBBABBABABBBBABBAABABAABAABBABBBAAABAW",
				"GBBABAAABBBABBBAAABABAABBBBBBABBAABBABABBAABBBBBAAAABAABABABBBAAAAAAABBAAAABAAAABAABAABABABBBBABBBAABBABABBAAAABBAABBBBBBAAAAABABABBBBABBAAAABBW",
				"GAAABAABBBBABBBBAABAABABBAABAAABAABAABAABBBBAAAAAAAAABBBBBBBABABAAABBAABBBBBABAABAAABBAAABBBABBBBBAABAAAABBBABAABBABBAAAABBBBABABAAABABBABBBABBW",
				"GBABABABBABBAABAAAAABBBBBABABBABBABBAAAAABAAAAAABBBBAABBABBABABAABBABAABBBBABBABAAAABABABBBBBBAAAAABABBBAABBBABBBAAABABABAAAAABBBBABAABBABBABAAW",
				"GABABBAAABBABBBAABAABABABBBBAAABBABBAAABBABBBBBAAABBABBBBBBBBBAABBAAAABABBBAAAAABAAABAAAAAABBAABBABABABABBAAABBABBABAAAAAABABBAABBBAABBBBAABBABW",
				"GABABBAAABBBBABBABBABBAABABBBBABBAAABBAAAABBABAABAAABABABBBABBBAAAABAABAAAABAABBBBABBBBABBBAABBAABBBABBABAAABBAABBAAAABABABAAAAABBAABBBAABBABABW",
				"GBABAABBBBAAABBAAAAAABBBAABBBAABAABBBAABBBBABBBBBAABBBABABBAAABBAAAABBBBAAAABAABBBBBAABAAABBABAABBBBBAAABBBABBABAAABBBAABBABBAAAAABABAAABAABABAW",
				"GBAABBAAABBBABAABABABAABBBBBAABAAAAABAAABBBBAAABAABBBBAAABBBAAABAABABBABABABBBABBBABBAABBBABAAAAAABABABABAABBAAAAABBBABBBABABBAABBABAABABBBABBBW",
				"GBAAABAABABBBBAABAABABAABAABBABBABBBAAAABABBBAAAABBABBBBBABBBBBAABABAABBAABAABABBBBBAAAAAAABABABBBAABBBBBBBAABAAABBBAABBBBABABAABAAABBBAAABAAAAW",
				"GBABBBBABBBAAABABABAABABBBBBABABABAABBAAAABABABBAABBBBABBABABABBABBABBBABABBBABABBBABBBAAABABAAAABAAABABBABBABAABABABAAABAABAABBBABAAAAABAAABAAW",
				"GABAAABAABBABABBAAABBAABAABAAAABBAABBBABBAAABABBBBBBBABABBBABABABBABAABAABAAAABABABABBAABABABABAABAABBBBBBBBBABABAABAABBAAAABBBABBABABABABABABAW",
				"GABABBAABABAABABBAABBABAABBABAAAAABBABBBBAABAAABBBBBABBAABAABBBAABABBAABABBBAAABAABBBAABABABAABABBBAABBAAABABBBBBBAAAAAABBBABAAABBBABAAABBBABBAW",
				"GAAABBBBBBAABAAABAABBBBAABABABBABBABAABABAABABAAABBABABBABBAABBAAABAABBBABABABBABBAABBBABABBBBBABBAAABABBBAAABABABBBABBABAAAAABBABAAAAABBAAABABW",
				"GABAABBAABBAABABABBABBABBABAABBBBBBABBABABAABBBABAAAABAAABABABBABAABAAABAAABBABAAABABABBABABBBBBABABAAABBBABBABBBABBBABABBBAABABAAABAABAABAABAAW",
				"GABAAAAAABAAAAABBBBBABAABBABBBABAAAABBBBAAAAABBBBABAABBBABAAABAAAABBBAABBBABBABAABBBBBAAAABBABAABBBAABBABABBAABABBAABBABBABBAABABBAAABABBBBBBAAW",
				"GBBAAABAABAABBAABBABBABAABAAAAABBBBBBBAAABABABAABBBABBBBABABABABBABBBBABBBBAABAAAAAABBAAAABBAAAABAAABAABABBAABABBBBBBAABBBBABABABBBBABAAABAABAAW",
				"GBBAABBAABBBBBAAAABBAAABAAABBAABBABAABBBABAABBABBBAABBAAAAABAABAAAABBAAABAAAABBAABBBBBBBABBAAAABAAABBAABBBBBBBBABBBBABBBAAABAAAAABBBAAABABBBBBAW",
				"GABAAAAABBBAABBBBBBBBAAAAAAABBBBAABBAAABAABBAABBBBBAABBBABBABABAAABBAAABBBBBABBAABAABABABBABBBBBABAABBAAAAAAAABABABABAABBBABAAABBABBBBAABAAAABBW",
				"GAAABBBBBBBBABAAAABAABBBBBBBABAAAAABBBBBBABBAABAAABBBABBAAABABABAABBAAAAABAABBABBBBABBBAABBBAABBABAAAABAABABBAAAAAAABABABAABABBBAAABAABBBABABBBW",
				"GABBABBBBAAABABAABAABBBABABBAABBBAABABBAABBAABBBBBAABBABABAAABAABABBABAAAABABBAAABBABBABBBBBBABBABABBAABBAAAABAAAABAAAABBAABBBABAABABABAABBAABBW",
				"GBBABBBBABABABBABABBAAAAABBBBBAAABAAABAABBBAABABAAAAABAABBAAAABBABBBBBBABABABBBAAABBAAAAABABAAABBBBBBABBABABAABAABABAABABAABBABBABBBBAAAAAABBBBW",
				"GBBABBBAABBAABAAAABABBBAAAAABBBBBBBBBBBAAAABBBABABBABBABABBAAABAAAABBBBABABAABBAAABBBBAABAABAAAABABAABBBABABABBBAAABBABAAABAABBBAAABBBAAABAAABBW",
				"GBBABAAAAAABAABBBABABBBBBBABAAABABAABABABAAAABAAABBAAABBBBAABBBBBABABBBBBBAABBBBAAABAABBBBAABBAAABAABAAAABBBBAAABBABBBBBAAAABBAAAABABBABABAABBAW",
				"GAAAABAAAABAABABBABBBBBBABABBAABAAAAABAABBABBBBABAABBABBAABAAABABBAABBAAAAABBBBBBBABABBABAABBAAABBBBAAAABABABAAABBBBBABBBABBBBBAAABAABABAABAABBW",
				"GBAAAABBBAABBABABBBBBABABABBBABAABABAAAABBAAABABAAABABABBABABABBABABBAABABBBBAAAAAABBABAAABAAAAABBBBBBBABBBBAAAABABBABBAAABBBABAAABABABBBAABBABW",
				"GABBABABABBBABAABABBBBBBBABABBABABABAABBBAAABBABBABBABBAAAABBABAAAAAABABBAABBAAAABAABBABABBBAABBAABBABBABABABABAAABBAABABAAABABABABBBAAABBABAABW"};
		
		
//		System.out.println("Initial String =  " + initial);
//		String initialstate = initial.toString();
//		String initialstate = "GBBABA"
//				+  			  "ABABAB"
//				+  "BAAAAB"
//				+ "ABABAA"
//				+ "AAAABA"
//				+ "ABAAAW";
		
		
		for(int i=0; i<listOfInitialStrings.length;i++)
		{
			randomString:{
			System.out.println(listOfInitialStrings[i]);
		
	
		String initialstate = listOfInitialStrings[i];
		
		start = System.nanoTime();
		System.out.println("start time " + start);
		//String initialstate = "ABBWXABBXABBXAAAXXGA"; //level 5
		
		int indexofG = initialstate.indexOf('G'); //Find the index of goal
		//int indexofG = 18;
		MulledAstarwithoutDeadlock e = new MulledAstarwithoutDeadlock();// Make an object of the class
		
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
        	if(templeft!= null && templeft.checkGoal(templeft.indexofW, indexofG)==true)
        	{
        		elapsed = System.nanoTime();
                //templeft.printAll();
                System.out.println("elapsed (ms) = " + elapsed);
                System.out.println("Total time taken  =  "+ (elapsed-start));
                System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated);
                
                System.out.println("*******************************************");
        		System.out.println();
        		System.out.println("*******************************************");
        		e.queue = null;
        		e.closed =null;
        		break randomString;
        	}
        	
        	if(templeft != null && !e.closed.contains(templeft) && !e.queue.contains(templeft))
        	{
        		e.queue.add(templeft);
        		//templeft = null;
        		//System.out.println(templeft.currentstate + "   cost:  " + (templeft.g+templeft.h) + "   left");
        		//no_of_nodes_evaluated++;
      
        	}
        	
        	States tempright = currentstate.right(indexofG); //Check if white ball can be moved to right
        	if(tempright!= null && tempright.checkGoal(tempright.indexofW, indexofG)==true)
        	{
        		elapsed = System.nanoTime();
        		//tempright.printAll();
                System.out.println("elapsed (ms) = " + elapsed);
                System.out.println("Total time taken  =  "+ (elapsed-start));
                System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated);
                
                System.out.println("*******************************************");
        		System.out.println();
        		System.out.println("*******************************************");
        		e.queue = null;
        		e.closed =null;
        		break randomString;
        	}
        	if(tempright != null && !e.closed.contains(tempright)  && !e.queue.contains(tempright))
        	{
        		e.queue.add(tempright);
        		//tempright = null;
        	    //System.out.println(tempright.currentstate + "  cost:  " + (tempright.g+tempright.h) + "   right");
        	    //no_of_nodes_evaluated++;
        	}
        	
        	States tempup = currentstate.up(indexofG); //Check if white ball can be moved up
        	if(tempup!= null && tempup.checkGoal(tempup.indexofW, indexofG)==true)
        	{
        		elapsed = System.nanoTime();
        		//tempup.printAll();
                System.out.println("elapsed (ms) = " + elapsed);
                System.out.println("Total time taken  =  "+ (elapsed-start));
                System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated);
                
                System.out.println("*******************************************");
        		System.out.println();
        		System.out.println("*******************************************");
        		e.queue = null;
        		e.closed =null;
        		break randomString;
        	}
        
    		if(tempup != null && !e.closed.contains(tempup)  && !e.queue.contains(tempup))
        	{
    			e.queue.add(tempup);
    			//tempup = null;
    		 //System.out.println(tempup.currentstate + "  cost:  " + (tempup.g+tempup.h) + "   up");
    		 //no_of_nodes_evaluated++;
        	}
    		
    		
    		States tempdown = currentstate.down(indexofG); //Check if white ball can be moved down
    		if(tempdown!= null && tempdown.checkGoal(tempdown.indexofW, indexofG)==true)
        	{
    			elapsed = System.nanoTime();
    			//tempdown.printAll();
                System.out.println("elapsed (ms) = " + elapsed);
                System.out.println("Total time taken  =  "+ (elapsed-start));
                System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated);
                
                System.out.println("*******************************************");
        		System.out.println();
        		System.out.println("*******************************************");
        		e.queue = null;
        		e.closed =null;
        		break randomString;
        	}
    		if(tempdown != null && !e.closed.contains(tempdown)  && !e.queue.contains(tempdown))
    		{
        		e.queue.add(tempdown);
        		//tempdown = null;
    		 //System.out.println(tempdown.currentstate + "    cost:  " + (tempdown.g+tempdown.h) + "   down");
    		 //no_of_nodes_evaluated++;
    		}
    		
    		currentstate = null;
    		templeft = null;
    		tempright = null;
    		tempdown = null;
    		tempup = null;
    		
    		
    		if(no_of_nodes_evaluated > 100000)
    		{
    			n--;
    			break;
    		}
    		if(no_of_deadlocks_found > 1000)
    		{
    			n--;
    			break;
    		}
     
		}
		elapsed = System.nanoTime();
		 System.out.println("elapsed (ms) = " + elapsed);
         System.out.println("Total time taken  =  "+ (elapsed-start));
		
		System.out.println("Solution does not exist   and  " +  "  Number of nodes evaluated  =  " +no_of_nodes_evaluated);
		System.out.println();
		System.out.println("*******************************************");
		System.out.println();
		System.out.println("*******************************************");
		break randomString;
		
		
	}
		if(no_of_deadlocks_found != 1001 && no_of_nodes_evaluated != 100001)
		{
			total_no_nodes_evaluated += no_of_nodes_evaluated;
			total_no_of_deadlocks_evaluated  += no_of_deadlocks_found;
			total_time_taken += elapsed - start;
			
		}
		no_of_nodes_evaluated = 0;
		no_of_deadlocks_found = 0;
		start = 0;
		elapsed = 0;
		
	}
//		System.out.println();
//		java.util.Iterator<StringBuilder> in = listOfInitial.iterator();
//		while(in.hasNext())
//		{
//			System.out.println(in.next());
//		}
		
//		System.out.println("Total nodes evaluated  =  " + total_no_nodes_evaluated);
//		System.out.println("Total time taken =  " + total_time_taken);
//		System.out.println("Total cases considered =  " + n);
//		float total_nodes = total_no_nodes_evaluated/n;
//		float total_time = total_time_taken/n;
//		System.out.println("Average nodes evaluated  =  " +  total_nodes);
//		System.out.println("Average time taken  =  " + total_time);
		
		
		System.out.println("Total nodes evaluated  =  " + total_no_nodes_evaluated);
		System.out.println("Total deadlocks found =  " + total_no_of_deadlocks_evaluated);
		System.out.println("Total time taken =  " + total_time_taken);
		System.out.println("Total cases considered =  " + n);
		float total_nodes = total_no_nodes_evaluated/n;
		float total_deadlocks = total_no_of_deadlocks_evaluated/n;
		float total_time = total_time_taken/n;
		System.out.println("Average nodes evaluated  =  " +  total_nodes);
		System.out.println("Average deadlocks evaluated  =  " + total_deadlocks);
		System.out.println("Average time taken  =  " + total_time);
	}
}
