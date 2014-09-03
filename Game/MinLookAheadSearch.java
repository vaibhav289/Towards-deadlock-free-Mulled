package Game;




import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javax.swing.text.html.HTMLDocument.Iterator;




class MinLookAheadSearch 
{
	static int no_of_columns = 5; //Gving the number of columns in the given grid
	static int no_of_nodes_evaluated = 0;
	static int no_of_deadlocks_found = 0;
	public static int globalvariable = 0;
	public static ArrayList<StringBuilder> listOfInitial = new ArrayList();
	 // A* priority queue storing the open nodes
 Queue<States> queue = new LinkedList<States>();
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
	        		//long elapsed = System.currentTimeMillis();
	               // printAll();
	                //System.out.println("elapsed (ms) = " + elapsed);
	                //System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated + "No of deadlocks found = " + no_of_deadlocks_found);
	                //System.exit(0);
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
	        
	        String left(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "right" || globalvariable == 1)
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
	        					System.out.println("Type of Deadlock 1 - Left   " + nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock1";
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("deadlock 3 found  -Left  " + nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock3";
	        				}
	        				
	        				return nextstate;
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

			String right(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "left" || globalvariable == 1)
	        		
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
	        					System.out.println("Type of Deadlock 1 - Right   "+nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock1";
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("deadlock 3 found - Right   " + nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock3";
	        				}
	        				
	        				return nextstate;
	        			}	
	        		}
	        	}
	        	}
	        	return null;
	        }
	        
	        String up(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "down" || globalvariable == 1)
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
	        					System.out.println("Type of Deadlock 1 - Up    " + nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock1";
	        				}
	        				
	        				if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	        					System.out.println("deadlock 3 found -Up   " + nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock3";
	        				}
	        				
	        				return nextstate;
	        				
	        			}	
	        				
	        		}
	        		
	        	}}
	        	return null;
	        }
	        
	        String down(int indexofG)
	        {
	        	if(rememberlastoperator.get(currentstate) != "up" || globalvariable == 1)
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
	        					System.out.println("Type of Deadlock 1 - Down   " + nextstate);
	        					no_of_deadlocks_found++;
	        					return "Deadlock1";
	        				}
	    					
	    					if(checkForBlockedGoal(nextstate, indexofG)==true)
	        				{
	    						System.out.println("deadlock 3 found  - Down   " + nextstate);
	    						no_of_deadlocks_found++;
	    						return "Deadlock3";
	        				}
	    					
	    					return nextstate;
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
		
		//String initialstate = "BBWAXABBXABBXAAAXXGA";
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABXBAAAABAABAAAABW";
		
		//String initialstate = "ABBAXBAAXAWBXABBXXGA";
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABW";
		
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAAAABAAAABBXXXAAAAAAAAAAAAW";
		
		//String initialstate = "XXXAAAAAAAAAAAAAGBABBAABABAABAABAABBBBAAAABAAAAABBAAAAAABBAAAAABBBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABABBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAAAABAAAABBXXXAAAAAABBBBBBW";
		
//		String initialstate = "AAAAAAA"
//							+ "AAXXXXA"
//							+ "XAXXXXA"
//							+ "ABXXXXA"
//							+ "GAAAABW"; //GAABWAA  DeadEnd calculation with this
		//String initialstate = "AAAAAAXAAXXXAXXAXXXAXGBXXXAXXAAAABW";

//		String initialstate = "AAAAAAA"
//							+ "AAXXXXA"
//							+ "XAXXXXA"
//							+ "BBXXXXA"
//							+ "GAAAABW"; //GAABWAA  DeadEnd calculation with this
//		String initialstate = "AAAAAAA"
//							+ "AAAXXXA"
//							+ "AAXXXXA"
//							+ "ABAXXXA"
//							+ "GAAAABW";
		//String initialstate = "XXAABABABWXGABA";
		//String initialstate = "XXAABAWABAXAABG";
		//System.out.println("hi");
		//String initialstate = "ABBWXABBXABBXAAAXXGA";
		//String initialstate = "GABBABAAABAAAABAABBW";
		//String initialstate = "ABBGBAABAAABWAAB";
		
		//String initialstate = "GAABAAW";
		
		
		
		
//		String listOfStrings[] = {"GAAABAAAAABAAAABAAABBBAAW"
//				,"GAABAAAAAABAAAABAAABAABBW"
//				,"GAAAAAABABAABABABBAAAAAAW"
//				,"GAAABABAAABBAAABAABAAAAAW"
//				,"GAABBBAAABAAABAABAAAAAAAW"
//				,"GBAAAAAAAAAAAAAABABBAABBW"
//				,"GAAAAAAAABAABBBAABABAAAAW"
//				,"GAAAAAAAAAAAABAABABABBABW"
//				,"GABAAAABAAAAABABBAAABAAAW"
//				,"GAAABABAABBAAABAAAABAAAAW"
//				,"GBAABAAAAAAABBAABAAABAAAW"
//				,"GABAAAAABAAAAABBAAABBAAAW"
//				,"GABAAAAABAAAABABAAABABAAW"
//				,"GAAABAABAABBAAAAAAAABABAW"
//				,"GBAAAAABABAAAAABAAAABABAW"
//				,"GBAAAAAAAABABAABAAAAAABBW"
//				,"GBAAAABAAAAAABAAAABABBAAW"
//				,"GAAAAAAABABABAAAAABABABAW"
//				,"GABABAAABABAAAAAAAAABABAW"
//				,"GABBABAAAAAAAAABAABAABAAW"
//				,"GAAABABABBBAAAAAABAAAAAAW"};
//		
//		//for(int i=0; i<listOfStrings.length;i++)
//		//{
//			randomString:{
//			//System.out.println(listOfStrings[i]);
//		
//	
//		//String initialstate = listOfStrings[i];
//			String initialstate = "GBAAAABBBAAAAAABBAABAAAABABAAAAAAAAAAABBAAABBAAAAABBBAAAABBAAAAW";
		
		
		//for(int z=0; z<11;z++)
		//{
			randomString:{
		StringBuilder initial = new StringBuilder("G");
		int gridSize = 8;
		int lengthOfString = gridSize * gridSize; 
		for(int i=1; i<lengthOfString-1;i++)
			initial.append('A');
		
		initial.append('W');
		//System.out.println(initial);
		
		Random rand = new Random();
		int min = 1;
		int max = lengthOfString - 2;
		//System.out.println(min + "   " + max);
		int countOfBlackBalls = 0;
		while(countOfBlackBalls < Math.floor(Math.pow(gridSize, 1.4)))
		{
		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomNum = rand.nextInt((max - min) + 1) + min;
		    
		    if(initial.charAt(randomNum) == 'B')
		    	continue;
		    
		    initial.setCharAt(randomNum, 'B');
		    countOfBlackBalls++;
		    
		}
		
		listOfInitial.add(initial);
		System.out.println("Initial String =  " + initial);
		//String initialstate = initial.toString();
		
//		String initialstate = "XXXWAAAAAA"
//				+ 			  "XXXBXXXXXA"
//				+ 			  "XXXBXXXXXA"
//				+ 			  "GAAAAAAAAA"; //level 5
		
		String initialstate = "AAAAWAAAAAAAAAABBBBAGAAAA";
		long start = System.nanoTime();
		System.out.println("start time " + start);
		
		
		int indexofG = initialstate.indexOf('G'); //Find the index of goal
		//int indexofG = 0;
		MinLookAheadSearch e = new MinLookAheadSearch();// Make an object of the class
		
		
		//System.out.println(depthToSearch);
		e.queue.clear();
		e.closed.clear();
		
		e.queue.add(new States(initialstate, indexofG)); // ADD the initial state configurations
		
		while(!e.queue.isEmpty())
		{
		   States current = e.queue.remove(); 
		   e.closed.add(current);
           //no_of_nodes_evaluated++;
           
           int depthToSearch = e.depth(current.currentstate, indexofG);
           States next = e.moveToTake(current, depthToSearch + 1, indexofG);
           e.queue.add(next);
           
           System.out.println(next);
           
           if(next == null)
           {
        	long elapsed = System.nanoTime();
      		System.out.println("elapsed (ms) = " + elapsed);
            System.out.println("Total time taken  =  "+ (elapsed-start));
      		System.out.println("Solution does not exist  " +  "No of nodes evaluated =  "+no_of_nodes_evaluated + "     No of deadlocks found = " + no_of_deadlocks_found);
      		System.out.println();
      		System.out.println("*******************************************");
      		System.out.println();
      		System.out.println("*******************************************");
      		 break randomString;
      		
           }
           
           if(next != null && next.checkGoal(next.indexofW, indexofG))
           {
        	   long elapsed = System.nanoTime();
               next.printAll();
               System.out.println("elapsed (ms) = " + elapsed);
               System.out.println("Total time taken  =  "+ (elapsed-start));
               System.out.println("Number of nodes evaluated  =  "+no_of_nodes_evaluated + "    No of deadlocks found =  " + no_of_deadlocks_found);
               
               System.out.println("*******************************************");
       		   System.out.println();
       		   System.out.println("*******************************************");
       		   break randomString;
        	   //System.exit(0);
           }
		//}
		}
		no_of_nodes_evaluated = 0;
		no_of_deadlocks_found = 0;
		}
		//Keep running until the queue becomes empty  
		/*while (!e.queue.isEmpty()) 
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
		System.out.println("Solution does not exist     " +  no_of_nodes_evaluated + "     No of deadlocks found = " + no_of_deadlocks_found); */
		
	}

	States moveToTake(States currentstate, int depthToSearch, int indexofG) 
	{
		Queue<States> currentlevel = new LinkedList<States>();
		Queue<States> nextlevel = new LinkedList<States>();
		currentlevel.add(currentstate);
		int i=1;
		int c=1;
		int k;
		States left = null;
		States right = null;
		States up = null;
		States down = null;
		
		while(i<=depthToSearch)
		{
			int heuristicleft = 2000001;
			int heuristicright = 2000001;
			int heuristicup = 2000001;
			int heuristicdown = 2000001;
			
			if(currentlevel.size()==0)
			{
				break;
			}
			
			States current = currentlevel.remove();
			no_of_nodes_evaluated++;
		
			String templeft = current.left(indexofG); //Check if white ball can be moved to left
			if(templeft == "Deadlock1")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 999999; //Lower priority to deadlock 1 as compared to deadlock 3
					before = before.previous;
					j--;
				}
					
			}
			else if(templeft == "Deadlock3")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 1000001; //Higher priority to deadlock 3 as compared to deadlock 1
					before = before.previous;
					j--;
				} 
			}
			else if(templeft != "Deadlock1" && templeft != "Deadlock3" && templeft != null)
			{
				States templeftstates = new States(current, templeft, indexofG, "left");
				
				if(!closed.contains(templeftstates))
				{
					nextlevel.add(templeftstates);
					//System.out.println(templeftstates.currentstate + "   cost:  " + (templeftstates.g+templeftstates.h) + "   left");
				}
				
				//no_of_nodes_evaluated++;
				
				if(i==1)
				{
					//System.out.println("Has a    "+ "left child");
					left = templeftstates;
				}
				
				if(i==depthToSearch)
				{
					heuristicleft = templeftstates.h;
				 
				}
				 
  
			}
    	
			String tempright = current.right(indexofG); //Check if white ball can be moved to right
			if(tempright == "Deadlock1")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 999999; //Lower priority to deadlock 1 as compared to deadlock 3
					before = before.previous;
					j--;
				}
			}
			else if(tempright == "Deadlock3")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 1000001; //Higher priority to deadlock 3 as compared to deadlock 1
					before = before.previous;
					j--;
				} 
			}
			else if(tempright != "Deadlock1" && tempright != "Deadlock3" && tempright != null)
			{
				States temprightstates = new States(current, tempright, indexofG, "right");
				//System.out.println(temprightstates.currentstate);
				if(!closed.contains(temprightstates))
				{
					nextlevel.add(temprightstates);
					//System.out.println(temprightstates.currentstate + "   cost:  " + (temprightstates.g+temprightstates.h) + "   right");
				}
				
				//no_of_nodes_evaluated++;
				
				if(i==1)
				{
					//System.out.println("Has a    "+ "right child");
					right = temprightstates;
				}
				
				
				if(i==depthToSearch)
				{
					heuristicright = temprightstates.h;
				
				}
  
			}
    	
			String tempup = current.up(indexofG); //Check if white ball can be moved up
			if(tempup == "Deadlock1")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 999999; //Lower priority to deadlock 1 as compared to deadlock 3
					before = before.previous;
					j--;
				}
			}
			else if(tempup == "Deadlock3")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 1000001; //Higher priority to deadlock 3 as compared to deadlock 1
					before = before.previous;
					j--;
				} 
			}
			else if(tempup != "Deadlock1" && tempup != "Deadlock3" && tempup != null)
			{
				States tempupstates = new States(current, tempup, indexofG, "up");
				
				if(!closed.contains(tempupstates))
				{
					nextlevel.add(tempupstates);
					//System.out.println(tempupstates.currentstate + "   cost:  " + (tempupstates.g+tempupstates.h) + "   up");
				}
				
				//no_of_nodes_evaluated++;
				
				if(i==1)
				{
					//System.out.println("Has a    "+ "up child");
					up = tempupstates;
				}
				
				if(i==depthToSearch)
				{
					heuristicup = tempupstates.h;
					
				}
  
			}
		
			String tempdown = current.down(indexofG); //Check if white ball can be moved down
			if(tempdown == "Deadlock1")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 999999; //Lower priority to deadlock 1 as compared to deadlock 3
					before = before.previous;
					j--;
				}
			}
			else if(tempdown == "Deadlock3")
			{
				States before = current;
				int j=i;
				
				while(j>=2)
				{
					before.h = 1000001; //Higher priority to deadlock 3 as compared to deadlock 1
					before = before.previous;
					j--;
				} 
			}
			else if(tempdown != "Deadlock1" && tempdown != "Deadlock3" && tempdown != null)
			{
				States tempdownstates = new States(current, tempdown, indexofG, "down");
				
				if(!closed.contains(tempdownstates))
				{
					nextlevel.add(tempdownstates);
					//System.out.println(tempdownstates.currentstate + "   cost:  " + (tempdownstates.h) + "   down");
				}
				
				//no_of_nodes_evaluated++;
				
				if(i==1)
				{
					//System.out.println("Has a    "+ "down child");
					down = tempdownstates;
				}
				
				if(i==depthToSearch)
				{
					heuristicdown = tempdownstates.h;
					 
				}
  
			}
			
			
			if(i==depthToSearch)
			{
				int minimum = Math.min(Math.min(heuristicleft, heuristicright), Math.min(heuristicup, heuristicdown));
				//System.out.println(minimum);
				
				States before = current;
				int j=i;
				
			
				while(j>=2)
				{
					
					if(before.h!=1000001 && before.h!=999999)
					{
						if(j==2)
						{
							if(minimum<before.h)
								before.h = minimum; //Higher priority to deadlock 3 as compared to deadlock 1
						}
						else
							before.h = minimum;
					}
					
					before = before.previous;
					j--;
				}
						
			}
			
			if(currentlevel.isEmpty())
			{
				i++;
				currentlevel = nextlevel;
				nextlevel = new LinkedList<States>();
			}
			
	
		}
		
		//System.out.println(currentlevel.size());
		//System.out.println(currentstate.currentstate);
		//System.out.println(left.h + "    "  + "    " + down.h);
		
		int x = Math.min(Math.min(left!=null?left.h:2000001, right!=null?right.h:2000001), Math.min(up!=null?up.h:2000001, down!=null?down.h:2000001));
		//System.out.println(x);
		//System.out.println(left!=null?left.h == 1000001:true);
		//System.out.println(left!=null?(left.h == 1000001?true:false):true);
		
		backtrack:{
		if(x==999999)// To check whether both are 999999 meaning both throwing Deadlock 1 then take min of backed up children and take that move
		{
			
			labelDeadlock1:{
			if(left!=null)
			{
				if(left.h == 1000001)//To check if any other path has 1000001; meaning if any other path throwing Deadlock 3 then take this path itself
				{
					break labelDeadlock1;	
				}
			}
			if(right!=null)
			{
				if(right.h == 1000001)//To check if any other path has 1000001; meaning if any other path throwing Deadlock 3 then take this path itself
				{
					break labelDeadlock1;	
				}
			}
			
			if(up!=null)
			{
				if(up.h == 1000001)//To check if any other path has 1000001; meaning if any other path throwing Deadlock 3 then take this path itself
				{
					break labelDeadlock1;	
				}
			}
			
			if(down!=null)
			{
				if(down.h == 1000001)//To check if any other path has 1000001; meaning if any other path throwing Deadlock 3 then take this path itself
				{
					break labelDeadlock1;	
				}
			}
			//System.out.println(x);
			//System.out.println("HIe");
			int minimumvalue = 55599999;
			States havingminvalue = null;
			int limit = currentlevel.size();
			//System.out.println(currentlevel.size());
			
			//To check if there is any leaf node there
			//If no leaf node meaning everyone leading to a deadlock hence its better to backtrack
			if(currentlevel.size() == 0)
			{
				//System.out.println("hi");
				break backtrack;
			}
			for(int z=0;z<limit;z++)
			{
				//System.out.println("hi");
				States temporary = currentlevel.remove();
				//System.out.println(temporary.currentstate + "    " + temporary.h);
				if(temporary.h<minimumvalue)
				{
					
					minimumvalue = temporary.h;
					havingminvalue = temporary;
					//System.out.println("entered");
				}
				
			}
			//System.out.println(minimumvalue);
			
			if(havingminvalue != null)
			{
				States before = havingminvalue;
				int j=depthToSearch;
			
				while(j>=1)
				{
				
					before.h = minimumvalue;
					before = before.previous;
					j--;
				}
			}
			
			if(left!= null && left.h==minimumvalue)
				return left;
			if(right!= null && right.h==minimumvalue)
				return right;
			if(up!= null && up.h==minimumvalue)
				return up;
			if(down!= null && down.h==minimumvalue)
				return down;
		}
		}
		
		else if(x==1000001)
		{
			
			labelDeadlock3:{
			int minimumvalue = 555999999;
			States havingminvalue = null;
			int limit = currentlevel.size();
			//System.out.println(limit);
			//To check if there is any leaf node there
			//If no leaf node meaning everyone leading to a deadlock hence its better to backtrack
			if(currentlevel.size()==0)
			{
				
				break backtrack;
			}
			for(int z=0;z<limit;z++)
			{
				States temporary = currentlevel.remove();
				if(temporary.h<minimumvalue)
				{
					minimumvalue = temporary.h;
					havingminvalue = temporary;
				}
				
			}
			
			if(havingminvalue != null)
			{
				States before = havingminvalue;
				int j=depthToSearch;
			
				while(j>=1)
				{
				
					before.h = minimumvalue;
					before = before.previous;
					j--;
				}
			
	
			if(left!= null && left.h==minimumvalue)
				return left;
			if(right!= null && right.h==minimumvalue)
				return right;
			if(up!= null && up.h==minimumvalue)
				return up;
			if(down!= null && down.h==minimumvalue)
				return down;
			}
		}
		}
		//System.out.println("hi");
		//System.out.println(left + "     " + "    " + left.h);
		//System.out.println(x);
		
		//System.out.println(right.currentstate + "   " + right.h);
		if(left!= null && left.h==x && !closed.contains(left))
			return left;
		if(right!= null && right.h==x  && !closed.contains(right))
			return right;
		if(up!= null && up.h==x  && !closed.contains(up))
			return up;
		if(down!= null && down.h==x  && !closed.contains(down))
			return down;
		//System.out.println("hi");
		}
		/*
		 * If you reached here means there are no moves left or also if there are moves they are directly leading to deadlock1 or deadlock3;
		 * so this means you need to backtrack.But backtrack does not mean going to the parent as position of black balls will change.You cannot
		 * directly go to your parent as it is real time and hence you can only apply the inverse operator to go back the path you came in the
		 * hope to find another way to goal
		 */
		
		//Backtracking Starts
		//System.out.println(currentstate.currentstate + "     " + currentstate.g + "     " + currentstate.h);
		//System.out.println(currentstate.rememberlastoperator.get(currentstate.currentstate));
		
		if(currentstate.rememberlastoperator.get(currentstate.currentstate) == "left")
		{
			globalvariable = 1;
			String tempright = currentstate.right(indexofG); //Check if white ball can be moved to left
			globalvariable = 0; 
			if(tempright != "Deadlock1" && tempright != "Deadlock3" && tempright != null)
			{
				States temprightstates = new States(currentstate, tempright, indexofG, "right");
			
				if(!closed.contains(temprightstates))
				{
					return temprightstates;
			
				//System.out.println(templeftstates.currentstate + "   cost:  " + (templeftstates.g+templeftstates.h) + "   left");
				}
			}
			
			

		}
		else if(currentstate.rememberlastoperator.get(currentstate.currentstate) == "right")
		{
			globalvariable = 1;
			String templeft = currentstate.left(indexofG); //Check if white ball can be moved to left
			globalvariable = 0; 
			if(templeft != "Deadlock1" && templeft != "Deadlock3" && templeft != null)
			{
				States templeftstates = new States(currentstate, templeft, indexofG, "right");
			
				if(!closed.contains(templeftstates))
				{
					return templeftstates;
			
				//System.out.println(templeftstates.currentstate + "   cost:  " + (templeftstates.g+templeftstates.h) + "   left");
				}
			}
			
			

		}
		else if(currentstate.rememberlastoperator.get(currentstate.currentstate) == "up")
		{
			globalvariable = 1;
			String tempdown = currentstate.down(indexofG); //Check if white ball can be moved to left
			globalvariable = 0; 
			if(tempdown != "Deadlock1" && tempdown != "Deadlock3" && tempdown != null)
			{
				States tempdownstates = new States(currentstate, tempdown, indexofG, "right");
			
				if(!closed.contains(tempdownstates))
				{
					return tempdownstates;
			
				//System.out.println(templeftstates.currentstate + "   cost:  " + (templeftstates.g+templeftstates.h) + "   left");
				}
			}
			
			globalvariable = 0; 

		}
		else if(currentstate.rememberlastoperator.get(currentstate.currentstate) == "down")
		{
			globalvariable = 1;
			String tempup = currentstate.up(indexofG); //Check if white ball can be moved to left
			globalvariable = 0; 
			if(tempup != "Deadlock1" && tempup != "Deadlock3" && tempup != null)
			{
				States tempupstates = new States(currentstate, tempup, indexofG, "right");
			
				if(!closed.contains(tempupstates))
				{
					return tempupstates;
			
				//System.out.println(templeftstates.currentstate + "   cost:  " + (templeftstates.g+templeftstates.h) + "   left");
				}
			}
			
			

		}
		
//		if(left!=null)
//			return left;
//		if(right!=null)
//			return right;
//		if(up!=null)
//			return up;
//		if(down!=null)
//			return down;
		//All states gone through and now no result possible. Hence return null and hence there is no solution to this initial state.
		System.out.println("Missed everything");
		return null;
		
	}


	int depth(String currentstate, int indexofG) 
	{
		//System.out.println(currentstate + "    " + indexofG);
		int indexofW = currentstate.indexOf('W');
		int colW = indexofW%(no_of_columns); // colW holds the value of the column in which W resides c can be between (0,1,2,3,4 for columns = 5)
		int rowW = indexofW/(no_of_columns); // rowW holds the value of the row in which W resides r can be between (0,1,2 for columns = 5)
		int colG = indexofG%(no_of_columns); // colG holds the value of the column in which G resides
		int rowG = indexofG/(no_of_columns); // rowG holds the value of the row in which G resides
		
		int colDiff = Math.abs(colW - colG);
		int rowDiff = Math.abs(rowW - rowG);
		//return Math.abs(colW - colG) + Math.abs(rowW - rowG);
		//System.out.println(colW + "   " + rowW + "    " + colG + "    " + rowG);
		
		int smallerRow = rowW<rowG ? rowW : rowG;
		int smallerCol = colW<colG ? colW : colG;
		int greaterRow = rowW>rowG ? rowW : rowG;
		int greaterCol = colW>colG ? colW : colG;
		
		//System.out.println(smallerRow + "    " + smallerCol + "    " + greaterRow + "   " + greaterCol);
		int Path1BlackBalls = 0;
		int Path2BlackBalls = 0;
		int incrementor1 = indexofG - (rowDiff * no_of_columns);
		int incrementor2 = indexofW + (rowDiff * no_of_columns);
		int incrementor3 = indexofW - (rowDiff * no_of_columns);
		int incrementor4 = indexofG + (rowDiff * no_of_columns);
		if(indexofW < indexofG)
		{
			if(colG < colW)
			{
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(incrementor1 + i) == 'B')
						Path1BlackBalls ++;
				}
				
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(indexofG + i) == 'B')
						Path2BlackBalls ++;
				}	
				
			}
			else
			{
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(indexofW + i) == 'B')
						Path1BlackBalls ++;
				}
				
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(incrementor2 + i) == 'B')
						Path2BlackBalls ++;
				}
			}
			
			int dummy = indexofG;
			int i=0;
			while(i<rowDiff)
			{
				if(currentstate.charAt(dummy)=='B')
					Path1BlackBalls ++;
				
				dummy-=no_of_columns;
				i++;
			}
			
			int dummypath2 = indexofW;
			int j=0;
			while(j<rowDiff)
			{
				if(currentstate.charAt(dummypath2)=='B')
					Path2BlackBalls ++;
				
				dummypath2+=no_of_columns;
				j++;
			}
			
		}
		else
		{
			if(colG < colW)
			{
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(indexofG + i) == 'B')
						Path1BlackBalls ++;
				}
				
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(incrementor4 + i) == 'B')
						Path2BlackBalls ++;
				}
			
			}
			
			else
			{
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(incrementor3 + i) == 'B')
						Path1BlackBalls ++;
				}
				
				for(int i=0; i<=colDiff; i++)
				{
					if(currentstate.charAt(indexofW + i) == 'B')
						Path2BlackBalls ++;
				}
				
			}
			
			int dummy = indexofW;
			int i=0;
			while(i<rowDiff)
			{
				if(currentstate.charAt(dummy)=='B')
					Path1BlackBalls ++;
				
				dummy-=no_of_columns;
				i++;
			}
			
			
			int dummypath2 = indexofG;
			int j=0;
			while(j<rowDiff)
			{
				if(currentstate.charAt(dummypath2)=='B')
					Path2BlackBalls ++;
				
				dummypath2+=no_of_columns;
				j++;
			}
			
			
		}
		
		
		//System.out.println(Path1BlackBalls + "    " + Path2BlackBalls);
		return Math.max(Path1BlackBalls, Path2BlackBalls);
	}

	

}
