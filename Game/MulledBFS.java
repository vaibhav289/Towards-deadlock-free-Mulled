package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.omg.PortableServer.CurrentOperations;

import Game.MulledAstarwithoutDeadlock.States;

class MulledBFS 
{
	Queue<String> setofstates = new LinkedList<String>(); //Queue implemented using Link List to implement FIFO to store all the nodes in BFS 
	Map<String,Integer> depthofstate = new HashMap<String,Integer>(); // Map to detect duplicate nodes in BFS
	Map<String,String> parentchild = new HashMap<String,String>(); // Map to connect each child with its parent
	Map<String,String> rememberlastoperator = new HashMap<String,String>(); // Map to remember the last move to reduce the branching factor
	int no_of_columns = 11;
	static int no_of_nodes_evaluated = 0;
	static int n = 100;
	public static ArrayList<StringBuilder> listOfInitial = new ArrayList();
	static float total_no_nodes_evaluated = 0;
	static float total_time_taken = 0;
	static long start = 0;
	static long elapsed = 0;

//Add method to add the newstate with its parent to the Queue and both Maps
void add(String newstate, String oldstate, String lastoperator)
{
	
	if(!depthofstate.containsKey(newstate))
	{
		int newValue = oldstate==null ? 0 : depthofstate.get(oldstate) + 1; // Assign 0 for depth of root or find the depth of its parent and add 1 to it
		setofstates.add(newstate); // Add newstate to the queue 
		depthofstate.put(newstate, newValue); // Add depth of new state in depth maintaining map
		parentchild.put(newstate, oldstate);// Add newstate and oldstate relationship in the map maintaining parent child relationship
		rememberlastoperator.put(newstate, lastoperator);
	}
}

//down method to check whether the white ball can be moved downwards. After adding call the check goal method
String down(String currentstate, int indexofG)
{
	if(rememberlastoperator.get(currentstate) != "up")
	{
	int w = currentstate.indexOf('W'); // w holds the index of white position
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
				
				/*if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
				{
					//System.out.println("Type of Deadlock 1 - Down   " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}
				
				if(checkForBlockedGoal(nextstate, indexofG)==true)
				{
					//System.out.println("deadlock 3 found  - Down   " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}*/
				
				return nextstate;
			}	
		}
		
	}
	}
	return null;
	
		
}

//Up method to check whether the white ball can be moved upwards. After adding call the check goal method
String up(String currentstate, int indexofG)
{
	if(rememberlastoperator.get(currentstate) != "down")
	{
		
	
	int w = currentstate.indexOf('W'); // w holds the index of white position
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
				
				/*if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
				{
					//System.out.println("Type of Deadlock 1 - Up    " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}
				
				if(checkForBlockedGoal(nextstate, indexofG)==true)
				{
					//System.out.println("deadlock 3 found -Up   " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}*/
				
				return nextstate;
				
			}	
				
		}
		
	}}
	return null;
}

//Left method to check whether the white ball can be moved to the left. After adding call the check goal method
String left(String currentstate, int indexofG)
{
	if(rememberlastoperator.get(currentstate) != "right")
	{

	int w = currentstate.indexOf('W'); // w holds the index of white position
	int c = w%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
	
	String rowWelements = currentstate.substring(w-c, w); //rowWelements hold the elemnts in the same row of W
	
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
			
			String checkforX = currentstate.substring(lastindexofA+1, w); //Create a substring from lastindexofa to w tio check if there is an X between them
			int indexofX = checkforX.indexOf('X'); //Check the presence of X
		
			//If X is not present you can move to the left to the available position
			if(indexofX == -1)
			{
				//Create the new state with elemnts till A + elements from A+1,W+1 + A + remaining elements
				String nextstate = currentstate.substring(0, lastindexofA)+currentstate.substring(lastindexofA + 1, w+1)+"A"+currentstate.substring(w+1); 
				//System.out.println(nextstate + " left " +"    " + (g+h));
				//checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method	
				
				/*if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
				{
					//System.out.println("Type of Deadlock 1 - Left   " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}
				
				if(checkForBlockedGoal(nextstate, indexofG)==true)
				{
					//System.out.println("deadlock 3 found  -Left  " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}*/
				
				return nextstate;
			}	
		}
	}
	}
	return null;
	
}

//Left method to check whether the white ball can be moved to the right. After adding call the check goal method
String right(String currentstate, int indexofG)
{
	if(rememberlastoperator.get(currentstate) != "left")
		
	{
	
	
	int w = currentstate.indexOf('W'); // w holds the index of white position
	int c = w%(no_of_columns); // c holds the value of the column in which W resides c can be between (0,1,2,3,4)
		
	String rowWelements = currentstate.substring(w+1, w + no_of_columns - c); //rowWelements hold the elemnts in the same row of W
		
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
			
			String checkforX = currentstate.substring(w+1,firstindexofA); //Create a substring from w+1 to firstindexofa to check if there is an X between them
			int indexofX = checkforX.indexOf('X'); //Check the presence of X
			
			//If X is not present you can move to the left to the available position
			if(indexofX == -1)
			{
				//Create the new state with elemnts till A + elements from A+1,W+1 + A + remaining elements
				String nextstate = currentstate.substring(0, w)+ "A" + currentstate.substring(w, firstindexofA)+ currentstate.substring(firstindexofA + 1); 
				//System.out.println(nextstate + " right " +"    " + (g+h));
				//checkGoal(nextstate.indexOf('W'),indexofG); //Call checkgoal method
				
				/*if(checkForImmovableBlackOnGoal(nextstate, indexofG)==true)
				{
					//System.out.println("Type of Deadlock 1 - Right   "+nextstate);
					no_of_deadlocks_found++;
					return null;
				}
				
				if(checkForBlockedGoal(nextstate, indexofG)==true)
				{
					//System.out.println("deadlock 3 found - Right   " + nextstate);
					no_of_deadlocks_found++;
					return null;
				}*/
				
				return nextstate;
			}	
		}
	}
	}
	return null;
}
//Method to check whether goal is reached
boolean checkGoal(String newstate, String oldstate, int indexofG, String lastoperator) 
{
	add(newstate, oldstate, lastoperator); //addition of newstate in map and queue
	//compare indexofW with indexofG in initial state
	if(newstate.indexOf('W')==indexofG)
	{
//		System.out.println("Solution exists at level " + depthofstate.get(newstate) + " of the tree"); //print soluion level
//		String traceroute = newstate;
//		//while loop to print out the whole path by traversing through the parentchild map
//		while(traceroute != null)
//		{
//			System.out.println(traceroute + " at level " + depthofstate.get(traceroute));
//			traceroute = parentchild.get(traceroute);
//		}
//		long endtime = System.currentTimeMillis();
//		System.out.println("elapsed" + "  " + endtime);
//		System.out.println("Number of Nodes Evaluated  =  " + no_of_nodes_evaluated);
//		System.exit(0);	//After printing exit from the program
		return true;
	}
	return false;
	
	
}

public static void main(String[] args) 
{
	/* State is represented in the form of string where 
	 X - Not a valid position
	 A - Available Position
	 B - Position of Black Ball
	 G - Position of Goal
	 W - Position of White Ball
	 */
	//String initialstate = "WBAGA"; // Initial State represented as a string
	//String initialstate = "XXABAABWABXGBAA"; 
	//String initialstate = "XXAABBBAAAXGWBA"; 
	//String initialstate = "XXAABABABWXGABA";
	//String initialstate = "XXWABABBAAXGABA";
	
	//String initialstate = "XXAXXXABXXABGBAXXBAXXXBXXXXWXX"; //level 5
	//String initialstate = "WAXXBXXBXXAGXAXXAX"; level 3
	//String initialstate = "GAAW"; //level 1
	//String initialstate = "BBAAAAGBBW";
	//String initialstate = "XXAAAAAAAAXGBBW";
	//String initialstate = "ABBAXBAAXAWBXABBXXGA";
	//String initialstate = "GAABW";
	
	
	
	
	//Results
	//String initialstate = "GAAW";
	//String initialstate = "WBAGA";
	//String initialstate = "WAXXBXXBXXAGXAXXAX";
	//String initialstate = "ABBWXABBXABBXAAAXXGA";
	//String initialstate = "XXAAAABABBXGABW";
	//String initialstate = "ABBAXBAAXAWBXABBXXGA";
	//String initialstate = "XXXAAAAAGBAABAABAABAAAAABBAAAAAAAAAAAAABXBAAAABW";
	//String initialstate = "XXXAAAAAGBAABAABAABAAAAABBAAAAAAAAAAAAABABAAAABAAAAAAAAAABAABAABAABAAAAABBAAAAAAAAAAAAABXBAAAABW";
	//String initialstate = "XXXAAAAAAAAAGBAABAABAAAAAABAAAAAAAAABBAAAAAAABAAAAAAAAABAAAAABAAAABAAAAAAAAAAAAAAAAABAAAAAABAAAAAAAAAAAAAAAAAAABAAAAAAABAAAAAAAAAAABAAAAAAAAABBAAAAAAAAAAAAAABAABAABAAAAAABAAAAAAAAABBAAAAAAABAAAAAAAAABAAAAABAAAABAAAAAAAAAAAAAAAAABAAAAAABAAAAAAAAAAAAAAAAAAABAAAAAAABAAAAAAAAAAABXXXAAAAAABBW";
	
	
	
	//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABXBAAAABAABAAAABW";
	//String initialstate = "XXXAAAAAAAAAAAAAGBAABAABABAABAABAABAAAAAAABAAAAABBAAAAAABBAAAAAAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABW";
	
	//String initialstate = "XXXAAAAAAAAAAAAAGBABBAABABAABAABAABBBBAAAABAAAAABBAAAAAABBAAAAABBBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAABAAAAAAABABAAAABAABAAAABABBBAAAABAAAAAAABABAAAABAABAAAABAAAAAAAAAABAAAABBXXXAAAAAABBBBBBW";
	
	//String initialstate = "WBAGA";
	
	
	String listOfInitialStrings[] = {"GBBAABBABAABBBAABABBABAABBABAAAABAAABBBAABBBAAAAABABAABBAABABAAAABBAAABBBAABABBBABBBBABBAAAAABABABBBBBABBBBBBBBAABBABAAAW",
			"GBAABBAAAAAABBBBABBBBABABBBAABABBABAABBAAABABBABABABBBAABABBBBAABABBBABBBAABABABBABABBBBAABAAABBBAABAAAABAAABBAAAABBAAABW",
			"GBBBAABABBBAABBBBABBBBAABBBBAAAAAAABBAAAAABBBBBBAABBBABAAAABBAAAABABABABBABBBBBBBBAAAAAAABABBAAAAAABAAAABBBBAABABBABABBBW",
			"GAAAABBAABABAAAAAABABBBAABAABBBBBABABAAABAABBBBAAABBBBABAAABBAABAABBABAAABABBBAABBBAABBABAABABBAABBBBABABABBBBBABBBAAABBW",
			"GBABABBBBAABBABABABBBABBAABBABAABBBAAABBAAAAAABABAAABBBAAAAABBABABBABABBAABBABABAABABABBBBBABBAABBAAABBBABAABAAABBBBAAABW",
			"GBABAAABABABBAAAABBBAAAAABBBBBBAAABABAABBBBAABBBABABAAABBBBBBBBBBAABABBABBAABBBAAABBBABBAAABAAAABBBAABBBAABBAAABAAAABBAAW",
			"GBBBBAABBABBABABBBBAAABBBABABBABABBAABABABABBABABAABBBABABBABAAAAAAAABABAABAABBBBBBAAAAAAAABBBABBABBBBABAAABBABAABABBAAAW",
			"GABBBAABABAAAABAAABBABBBBABBBBAAAAAABBAAABAABBAABABBABBABBABABBBAABAABAABAABBBBAAAABABABBABBBBBBBBAAABABBABBAAABBBAAABABW",
			"GBAAABABBABBBAABAABBBBABAAAAAABABABAABABABBAABBBBBBBBABAABAAAAABBABBAAABABBABABABBBABABAAAAABAABABBBBABABBAAAAABBBABBBBBW",
			"GAABABAABABBBAAABBBBAABBABABBABAABBABBAABAABBBBABAABABBAAAAAAAABABBBBBBABBAABABAAAABAAAAAAAABBBBBABABAAABBBBBBBBAABBABBBW",
			"GABABBBBBBAABBBBAABAAABBABABBABABABBBABAABABBAAAAABAAAABABBABBBAAABBBBAAAAABABBBAABABABABBBBBAABAABABBBBABBBBAAAAAAABABAW",
			"GBBBABABAABAABBABAAAABBBAABABABABBBABBABABABBBAAAABABBBBBABBAAABAAAABABBBBBAABBAAAABABAABAABBAABABABAABAABBBABBABAAABBBBW",
			"GBABABBBBBBAABAABBBABABABAABBAAAAABBABBABAAABBBAABBABAAABAABABBABAABAABABBAABBBBAAAABBBBAAAABABBAAAABBAABAABABBBBBBBBABAW",
			"GABAAABBBBAAAABBBBABABBBAABABBBAAABAABBBBBABBAAABAAABBBABBAAABABBABBBAAAAABBBBBBBBABABAAABBBAAABABAABAAAABBBABAABAAABABBW",
			"GABABAABBBBABABBABABBABBABBBBBAAAAABAAAAABBABAABABABBBBAAAABBABBBABABABBBAAAAAAABABABABABABBAAABBBBAAAABABBAABBBBBABABBAW",
			"GBBAABBAABBBBAAAAAAAAABBAAABABBAABBBBABAAAAAABBABBBABBAAAAAABABAAABBABABBBAABABBABBBAABAAAABBBBBBBBBAABBBBBABBBAAABBBABAW",
			"GAABBAABBABABBABBBBBBABABBAAAAABABBABAAABBBBBABAAABBBABAAAABABBBAABABAAABBBBAAAABBBBAABBABAAAAABBAABBBAAABAABBABABBAABBBW",
			"GABBABBBABAAAABABBBAABAABAABBBBBAABBBAABBAABABBABBBBBAAABBBBBABBABBAABBBBAAABABABABBBABAABAAABAAAAABAAABAABBAABBAAABABABW",
			"GBBBABBAABAAABABBBBAABAABBAAABBBBBBBBBABBAABAAABAAAAABABAAABBAABBAAABABABBBBABBBAABBBBABAABBBAAAAABABBAAAABBAABBBAABABBAW",
			"GAAABBABABBBAABABBBBBAABABABBBBAABBAABAABBAAABBBBAAAABBABAABBAAABBAABBBAABABBBAAAAABBABBABABBBBBABAABAABBABBAABBABABAAAAW",
			"GBBABAAABBABBAABBBABBABBBAABAABBABABABBBBBABBAABAAAAAAAABAAABBBAABBABAAAAAABBABABBABAABBAABABABBABBABABABABBABABABBBBABAW",
			"GBABABBAABBABBAAABABABBAAAABBBBBAAAABBAAABBAABBABABABBBAAAAAABABBABBABBBBBBABBAABBABBBAAABBABAAAABBBABABBAABABBBAAABBAAAW",
			"GBABABABAABABABABBBBAABAABAABAABBBABBABABBBBBAAAAABABAABAABBABABABAABBBBABABAABABBBABBBAAABABAABBAAAABBBBAABBBBABAAABAABW",
			"GABBBAABBBAABAABBABBBBAAAABABABAABABABAAABBBABBBAABABBABABBAAABBABBBBABBBBBAABBABABAAAAABABAABBBAABAAAAABBBAABBAABBBAAABW",
			"GAABBBAABBABABBBBBABAAAAAAAAAABBAABBAABABAABAAABBABBBABABBBAAABBBABBBBBBBAABBABAAABAABBBBAABBAAABBABABBABABBABABBBAAABAAW",
			"GBAABABAABABABBBABAABBBBABBBBBAABBBABAABABBBABBBBBBABABAAAAABABABAAAABABBBABBABBABABABABAAABABBAABBAAAAABABAAAABBBBABAAAW",
			"GBBAABBABBBBAAABBAABBBBBABBBBAAAABAABAAAABABBBAAAAAABABBBBBBBABAAAAABABAABAAABAABABBABAAAABBAAABBBBBABBAAABABBBBABBBBABAW",
			"GBBABAABABABBAAAAAABBAAABAAABAABAABAABBAABBBBBABBBBBABABBAABBBAABAABAAABABAABABBBABBABBBBABABAABAABBABBABABBAAAABBBABBABW",
			"GAABBBAAAAAABABBAAAABBBABBAABBABBBAAABBBBBABBAAABABABBAABABABABABBBBBAABABBBBAAAAAAAAABBBABAABBAABABBBBABAAABBBBABBBBAAAW",
			"GABBBBAAABAABBABBBBABAABAABBBBABABABAAABABABABABBBAAAABBBABBAAAAAAABABBABABBBABBABBAABAAABBAABBABABAABAAAAAABABBBBABBBBBW",
			"GBABBAABAAAAAABBBABABAAAAABBABBBBBAABBABBBBBAAAABABBABBAAAABBABABBBBBAAAABABAABABBAABABBABBBBBABBBAABAAABBAAABBABABBBAAAW",
			"GAAABABABBBAABBAAABAAABABBAAABBAABBBAABBBAABBABBBAAABAAAABABBAAAABBABAABBBBBBBBABBBBAABAAABABBBBABBBABBBAABAAAABBAABBABAW",
			"GBAAAAABABBBBBABBBBBBBAABBBBAABABBABABBBAAABAABBBBABAABBAABABBAABABBBAABABAAAAABABAABBBAABABAAABBABAAABBBBAABBBAAABAAABAW",
			"GAAABBAABBAAABBBBBAABAABAAAAAAAABAAAAABBABBBABBAABBBBABBAAAAABBABAAABBABABBBBABBBABABBAAABBBABBBBBBBABAAABAAABBBBBAABBBAW",
			"GABABBABABABBBAAAABAABBBAAAABBABABBABBBABBBAABBAAABBBAABABBABBBAAABAABAAABBBBAAABABBAAABAABBBAABAAAABAAABBBBABABBAABBBBBW",
			"GABABAABAAAAABAAABABBBBBBBBBABABABAABAAABAABBABBBABABBBAAABBBABABBABAAABAABAAABABBBABBBBABBBABAABBBABBBABBBABAABAAAAABAAW",
			"GBAABBBBBAAAAABBABBBABBAABAABABBAABBBBBABAABBBBAABBBBBAABBABAAABBBBAAABAABAAAAABAAAAAAAABABABBBABBBABABABBBBAABBABBABAAAW",
			"GBBBBAABAAAABBBBBBABAAABBAAAABBABABBAABABBBABAAAAAAAABABABABBBBABABABAABBAABABABAAABBAABABABBABAABABBBABBABBBBBBBBBAAAAAW",
			"GABABBBABBBABBBBAABABAAABABBBABBABBBBBAABABABBBABBBBBAAAABBBAAAABBBABBAAAAAABABABBABABABABAAAABABAAABBAABAAAAABBABBBAAABW",
			"GABABAABAABBBABBABABBBAAABAAAAAABBABBBBAABABBBBBBAABAABBAABBAAAABABABBAAABABBABBABAABBBBBBABABABBBBAABABABBABAABABAAAAABW",
			"GBBABBBAAAABBBBBBABBBAABBABAABABAAABBBBBABAAABABABAABBAAAABBABAABAAAAABBBABAAABAAAABBBBABBABABABBAAABABABABABBBBBABABAABW",
			"GAAAAABBBBBAABABAABBBBBBABAAAAABBABABBBABABBABBABBAAABBBBBABABBBABABBBAAAAAABBBABAAAAABAAABBABBBBBAAAAABBBAABBAAAABBABBAW",
			"GAAABABBBAABBAABBBAAABAAABABABBBBBBAAAABBAABABABABAABBBBBBAABABBBBBBBAAABBBBBABBAAAAAABBAAAABABBAABABBBBBABAAABABABAABAAW",
			"GABAAAABAAABABBABAAABBBABBBABABABABBABABBAAAAABBBABAABAABAAAABBAAAABBAABBABBBABABBBBBBBAABBBBBAAABABBAABABBAAAABBAABBBBBW",
			"GBAABBBAABAAAAAAABAABBBBBBBBBBBAABABBAABABABABAAABABABAAAABBAAABABAABAABABABBABBBBABABBBBBBABBBAAAABABBBAABABABAAAABBBBAW",
			"GABABAABAAAAAABBABBBBAAAAAABBBBABABAABBAAABABBABABBBABBBBBAABBABABBBAAABBBABBAABBBBABBBABABBBBBBAAABABBAABAABBAAAAAABAAAW",
			"GBAAABBBABBABAAAABBABABAAABAABABBBBBABBBABAABABABBAABBAABAAABAABABBABBBAAABAAAABBBABBBBAABABAABBBABBAABABABBABABBBBAABAAW",
			"GABABAAAABABAAABABABBBBBAAAABABBABBBBABBABAAAABAAAABBAABABBBBAABBBBABBAAAABBAABBBBBBBBAAAABAABABBABBBBBAAAABAABABABBABABW",
			"GBABAAABBABBABAAABBABBBBABBBABAABAABAABAAABBBBABBAAABBAAABBBABBBAABAABBBABABAAABBABBBABBAAAABABAAABABABBBABAABBBAABAAABBW",
			"GAAABAABAAAAAABAABABAABAABABABAABAABBAAABBBBABABABABBBABBBABBBBBBABABABAABBBBBAABAABBBBBAABBBABBABAAAABABBABAAAABBABBABBW",
			"GBABAAABBAABAABBABBBBBAAAABBBBBBBBBABAABBBABBABBBAAAAABAABBAABBBAAAAABABABBABBAABBABBBAAABBABBAAABAAABAAAABBABBAABBABBAAW",
			"GABABBAABABBBBAAABBAAABABAABABABBBAAABAAAABBBAABABAABAABAABBBBABBBABAABBBBAABBBAAABAAAAABABABBAABBBABBBABAABABBBBBAABABBW",
			"GBBBBBBBBABBABABBBAAAAABBBAAAAABBBBBBAAABAABBBABAAABABBBBABABABBBAAABABAAABBBBBABBABBBABAABBAABABBAAABAAAAAAABAABBAAAABAW",
			"GBAABBBBAAABBBBBBABABBBABAABBAAABBAABAABBAAAABBABBBABBBBAAAAABBBBBAAABBAABAAABABBABAAAABABBAABAABABBABBBAABAABBABAABAABBW",
			"GAAABBABBBBABABABBBBBAAABABAAAAAABBABAAAAABABBBAABBBAABAABBABBBABABBBAABBBBBABBAABABABBAAABBAABBAAABABABBABBABBAABBBAAAAW",
			"GBBBBBBBABBABBBAABAAAABABBAAABAABBAABAABBBABBBBAAAABBABAAAAAABABABAABBBBBBBBABBBBBABBABBBABABAAABAABBBBAAAAAAAABAABAAAABW",
			"GABAABABBBABBAAAAAABBBBAABBAABBBBBABABABBBBBAAABAABAABBABAABBBBBABABAABABABABABABAABBBABBBAABAABAAABABBABBAABABAAABABABAW",
			"GBBBBABABABBBAAABBABABBBAAAABAABAAAABABBBBBABBBAAAAAABAABBAAABBBBBBBBABBABBABAAABBBBAABBABBABABAAAABBAABABBAAAABBAABBAAAW",
			"GBBABBBAAAABBBBAABBAABAABBBABABABAAAAABBABAABBAABBBBBAAABBABABAAAAAABABBBBBABAAABBABBAABABABBABBAABABBBABAAABAAABBBAABBBW",
			"GBBBAABABABBAABABABAABABBABABBBBBAAABBBABBABAAABBBBAABBAABBABBBAAABBAAABAABBABAAABABAAAABBAAAABBBBBABBABBABABABAAABABBAAW",
			"GABBABBBABBBABBBAABAABAAAAABBABAAABAABBAABBABBBBBBBABABBBBBAABAAAABAAAAAABBBBBBAAAAAABBBBBAABBABBBABBABABAAAAAABBABABBAAW",
			"GABBAABBABBBABAAABBABBBBBABBABAAABBBABAAAABABBABABBAAABAABABBABBAAABBABBAAABAAAABAABABAABBBABAAABABBBAABBABBAABBBABBABBAW",
			"GABAABBBAAABABABABBBAAAABBABABABBABABBABBBBAABAAABBABABBAAAABBABAAABBBBBABBBAABAABAABBBBAAABBAAAAABABBABBBBABABAAAABBABBW",
			"GBBABBABAAABAAABBABBBBAABBABAAABBABBABBBAABABBAAABABBBBBABBAAABBBABABBBBAAABAAABBABBAAABAABBAAAABABAABAABBBABBABBABABAAAW",
			"GAAABABAABABAAAAABBABAABBBAAABAAABBBABBAABBAABABBBABBBABABBAABBABBBAABAABBBAAABBBBAAAABABAABBBBBABBAAAAAABBABBABBBABABBBW",
			"GABBAAABBBABAAABABBABBBABBBABAABABBBAABBABBABAABABBABBAABBAABABAABAABABAAAABBBABBAABBAABBBABAAAAAAAABBBABBBBAABBBBABABAAW",
			"GAAABBAAAABBBABAABABABAABBABBAABABBAABABBAABABAABBBAABBABAABBBBAAABBBAABAABBBBABAABBBABBBBABBABBAABABAAAAABABBABBBABAAABW",
			"GBAABAAAABBBBBABAAAABBABAAABAABBBAAABBAABAABAABAAAAABBABBAAAABABABBAAAABABBBBBBBBABBABBBBBBBBBAAAABBABBBBBABAABBAABBBAAAW",
			"GAAAABBAABABAABABAAAAAABABABBAABBBBBBAABABBBAAABBBABBABAABAAAABAAABAAAABBBBBABBABABBBABBBBAABAABBABBBABBBBABBAABABAABBBAW",
			"GABBAAAAABBBBBBAAABABAABABABBAABABAAAABAAAABBBBAABBABAABBAABBBBABBABBBBBAAABABBBAAABBAABAAABABABAAABAAABBBAABBBBABABBBBBW",
			"GAABAABBBBAAAABBBBBABAABBABABAABAABBBAAAAAABAAAABBBBBAABABAABBBBAABBBAAABBAAAABBAAABBABABABAABBBABABBBBBABAABABBBBBBABAAW",
			"GABBAABABBAABBBABBBAAABBABBAABABABABBAABBBABBBBAABAAAABBAABAABBAABBAAABABAABBABBBBAABAAAABBBBBAAAABAAABABBBBBABBABBAABAAW",
			"GBABABABBABAAAAABAAAABABBABAAAAABAABBABBBAABBABBBAABBBBAABBBBAABBAAABBAAABBAAABAABBBABABABABABBABBBABBBBBBBABBAABAABABAAW",
			"GBAAAABBBABBABABBAABABBABBBAAABBAABBAABBBBABABABAAAABBBBABBABBAABABBBAABAABBABAAAAAAAABABAAAABBBBBBAABAABBABBBBAAABAABBBW",
			"GAAABBBABBBBABAABAABBABBBAAABABABABBBABBABABBAAABABBBAABAAAABAABBAAAABABABAAABABBBBBAAABBBBABBBBBBABBAAAABAABBAABAAAABBBW",
			"GBBAABABABABBAAAAAABBBBABBBAABBBABBAAABBBAAAAAABABBAAABABAAAAABBABBABAAAABAAAABBBBBABBABBBABABBBABABAABBABAABBBBBABAABBBW",
			"GBAAABBBAABBABAABBBABAAAABAABBBBBABBABBBABBBBABBABAAAABAAABAAAABAAABBABABABBAAAAAABBBAABBABAAAAABBBBBBABAABBBBBABABBBABAW",
			"GAAABABBBABAABAAABBAAABAABABBBABAABABBABBBBBABABABBBABAAABBBBBAAABABBAABAABBAABBABBBABABBBBAAAABABBBBAAAABBAABBAAAABBABAW",
			"GAAABAAAABBBBBAABBABBABAABBBABBABBBABBAAAABABABBAABAAAAABBBBAABBBBABAABBABBAAAABBABBABABBAAABBBBBABAAABBBABBBABAAAAAABBAW",
			"GBBBAAABABBBABBAABBBBBBBBAABABABBBBABABBBAAABBABAABBBABBBABABABBABAAABAABBAAAAABBBABAABAABAABAAABBAAAAAAABAABABABBBAAABBW",
			"GBAABAAAAAABAABBAAABBAABAABABBBAABABBBBBBBBBBABAAAABAAABBBAAABABBBBABABABABAABABABBABBAABBBAABBAAAABBBBBABBABABAABABBBAAW",
			"GAABAABBAAAAABBBABBBBAABABBBABBBABBABBAABBBBAAABAAABAABABBBAABBBAABBAAAABAABBAAABBAABAABABAAABABABBBBBABAABABABBBABABBBAW",
			"GBABBABBBBBAAAAABABABAABAAABBAAAABBBBBABBAAABAABBAAAABBBBABBABAABBAABABBABBBABABABAABBBBABAABABAABBBAABBBAAAAABAABBBAABBW",
			"GBAAABBAABAABBABBAABBBAABAAABBAAABBAABBBAAAABBABABBBBBBBBBABBAABBABBAAABAAAAABBAABBABABABBAABBABABBABAABAABBBAABABABBBAAW",
			"GBBAABAAABBAABABBBABAAABAAABABAAABBBABBAABBBBBAAAAAABBBBAABABBABBABBBBBABAABBAABBABBBABAABBAAAABBBABABBAAAABBBABAAAABABBW",
			"GABAABBABABBABBBBBBBABBBBABABABBABAAAABAABABABAABABBBAAABAAAABABABAABBAAABABBAAABBBABAAABBBABBBAABBBBBAAABAABABAABABBAABW",
			"GABAAABAAAABABABABABBBBAABBBAABBBBAABABABAAAAABAAAABABBBBAAABBBBABABABABAABBABBBBBBAABBABBBBAABBBBABBAAABBAABABAABABABAAW",
			"GBAAAAABAAABAABAAAAAABBBABBBBBABABBAAABAABBBAAAABBBBBBBBABBABBBABBAAABABAABABBBAABBBBBBBAABABABBBBAAABAAAABAAABAABABBABBW",
			"GBAABBABBABBBAAABAAAAABAABBAAABBBBBBBBAABBBBAABAABBABBABAABABBAABABAABABBBABBAAABBBABAAABBBABAABAABBABAABBBAAAABBBAABAABW",
			"GBAAABBAAAABBBAAABABBBBABBBABAABBBBABAABABBBAAAABBAABAAABBABBAAABAAABABABAABBAABBAABAABABABBBABAABBBBABBBABBBBABABBAAABAW",
			"GAAAABABBABABBBBABAABBBBABABBAABABBBAAAAABAAAABBABAABABBBBABABBBBABAABBABBBBAABAAAABBABBAAAAAAAABABBBBABBBBBBAABBBAAAABAW",
			"GBBBABBBAABABABAABAABAAAAABBBAAAABBAABABBABAABBAAABBAAABAABABBABBAAABAABABBAAABABBABABAABAABBABABBBABBBBABBBBBBBBAAAABBBW",
			"GBAAAAAABBABABBBBABBABABBBBAAABAABBABABBBAABAAABABAABAABBABBABAABABBAAAAAABABBABABBAABBAABBABBBABABBBBABBBBAAABBBAABAAABW",
			"GABABAAAABBBAAAABBBAAAABBABABBABAABABBBABBAABABAABAABABAAABBAABABBABBABBBBBAABBABBABBABBBBAAAAABABAAABBBAABBBAABBBAABABBW",
			"GAABBAABBABBBBABBBABABABAAABABBABBBAABAAAAABBAAAABABABBABBBBBBABBABAAABAABABBABBAABABAAAABBBBAAABABAABAABBBABABBABBBBAAAW",
			"GAABABBBBABBAAABABABBBBBABBBAAAAABBABABBAABBAABABBBAAABABAAABAABBBBAAABBAAABABBBBBAAABABBBAAAAABBAABBBBABAABBABBBABABAAAW",
			"GBAABBABBBABAABBBABAABBBBABABAABAABBAABABBAAAAABAABABBAABBABAABAABAAAABABBABBBABABBBABBBABBABABBABBABABBABBBAAAAABABBAAAW",
			"GAAABABBBBBBBABAABBABAAAAAAABBABABBBABAABABABABBABBBABBABABAABBABBBBAAABBABABBAAAABBBAABBABBAABAABBAABBABABABBBAAAABAAABW",
			"GAABBAABBABBBBABBABBAABBAABABBBABAABABAABAABABBBAABABABAABBBBABABABAAAAAABABABBAABABBABBBBAAAABAABABBBBBBABBABAABBAAABAAW",
			"GABBBBABBBABAABBBBBBAAAAABABAABABBABABBBABBAAABABABBBBBAABABBABBABAABBAAAAAABBABAAAABABBAABBAABBAABAABBAAAABAAABBABBBABBW"};
	
	
//	System.out.println("Initial String =  " + initial);
//	String initialstate = initial.toString();
//	String initialstate = "GBBABA"
//			+  			  "ABABAB"
//			+  "BAAAAB"
//			+ "ABABAA"
//			+ "AAAABA"
//			+ "ABAAAW";
	
	
	for(int i=0; i<listOfInitialStrings.length;i++)
	{
		randomString:{
		System.out.println(listOfInitialStrings[i]);
	

	String initialstate = listOfInitialStrings[i];
	
	
	
	start = System.nanoTime();
	System.out.println("start time " + start);
	//String initialstate = "ABBWXABBXABBXAAAXXGA"; //level 5
	
	int indexofG = initialstate.indexOf('G');
	
	MulledBFS e = new MulledBFS(); // Make an object of the class
	//System.out.println("Hello");
	
	e.add(initialstate, null, null); // ADD the initial state configurations
	
	// Run the while loop till the queue maintaining states becomes empty
	while(!e.setofstates.isEmpty())
	{
		
		String currentstate = e.setofstates.remove(); // Get the current state by getting the first state from the Queue
		no_of_nodes_evaluated++;
		
		String templeft = e.left(currentstate,indexofG); //Check if white ball can be moved to left
			if(templeft != null && e.checkGoal(templeft, currentstate, indexofG, "left") == true)
			{
//				System.out.println("Solution exists at level " + e.depthofstate.get(templeft) + " of the tree"); //print soluion level
//				String traceroute = templeft;
//				//while loop to print out the whole path by traversing through the parentchild map
//				while(traceroute != null)
//				{
//					System.out.println(traceroute + " at level " + e.depthofstate.get(traceroute));
//					traceroute = e.parentchild.get(traceroute);
//				}
				elapsed = System.nanoTime();
				System.out.println("elapsed" + "  " + elapsed);
				System.out.println("Number of Nodes Evaluated  =  " + no_of_nodes_evaluated);
				System.out.println();
				System.out.println("*******************************************");
				System.out.println();
				System.out.println("*******************************************");
				//System.exit(0);	//After printing exit from the program
				break randomString;
			}
			
		String tempright = e.right(currentstate,indexofG); //Check if white ball can be moved to right
		if(tempright != null && e.checkGoal(tempright, currentstate, indexofG, "right") == true)
		{
//			System.out.println("Solution exists at level " + e.depthofstate.get(tempright) + " of the tree"); //print soluion level
//			String traceroute = tempright;
//			//while loop to print out the whole path by traversing through the parentchild map
//			while(traceroute != null)
//			{
//				System.out.println(traceroute + " at level " + e.depthofstate.get(traceroute));
//				traceroute = e.parentchild.get(traceroute);
//			}
			elapsed = System.nanoTime();
			System.out.println("elapsed" + "  " + elapsed);
			System.out.println("Number of Nodes Evaluated  =  " + no_of_nodes_evaluated);
			System.out.println();
			System.out.println("*******************************************");
			System.out.println();
			System.out.println("*******************************************");
			//System.exit(0);	//After printing exit from the program
			break randomString;
		}
		
		String tempup = e.up(currentstate,indexofG); //Check if white ball can be moved up
		if(tempup != null && e.checkGoal(tempup, currentstate, indexofG, "up") == true)
		{
//			System.out.println("Solution exists at level " + e.depthofstate.get(tempup) + " of the tree"); //print soluion level
//			String traceroute = tempup;
//			//while loop to print out the whole path by traversing through the parentchild map
//			while(traceroute != null)
//			{
//				System.out.println(traceroute + " at level " + e.depthofstate.get(traceroute));
//				traceroute = e.parentchild.get(traceroute);
//			}
			elapsed = System.nanoTime();
			System.out.println("elapsed" + "  " + elapsed);
			System.out.println("Number of Nodes Evaluated  =  " + no_of_nodes_evaluated);
			System.out.println();
			System.out.println("*******************************************");
			System.out.println();
			System.out.println("*******************************************");
			//System.exit(0);	//After printing exit from the program
			break randomString;
		}
		
		
		String tempdown  = e.down(currentstate,indexofG); //Check if white ball can be moved down
		if(tempdown != null && e.checkGoal(tempdown, currentstate, indexofG, "down") == true)
		{
//			System.out.println("Solution exists at level " + e.depthofstate.get(tempdown) + " of the tree"); //print soluion level
//			String traceroute = tempdown;
//			//while loop to print out the whole path by traversing through the parentchild map
//			while(traceroute != null)
//			{
//				System.out.println(traceroute + " at level " + e.depthofstate.get(traceroute));
//				traceroute = e.parentchild.get(traceroute);
//			}
			elapsed = System.nanoTime();
			System.out.println("elapsed" + "  " + elapsed);
			System.out.println("Number of Nodes Evaluated  =  " + no_of_nodes_evaluated);
			System.out.println();
			System.out.println("*******************************************");
			System.out.println();
			System.out.println("*******************************************");
			//System.exit(0);	//After printing exit from the program
			break randomString;
		}
		
		
		currentstate = null;
		templeft = null;
		tempright = null;
		tempdown = null;
		tempup = null;
		
		
//		if(no_of_nodes_evaluated > 200000)
//		{
//			n--;
//			break;
//		}
		
		
	}
	
	elapsed = System.nanoTime();
	System.out.println("elapsed" + "  " + elapsed);
	System.out.println("Solution does not exist   " + no_of_nodes_evaluated);
	System.out.println();
	System.out.println("*******************************************");
	System.out.println();
	System.out.println("*******************************************");
	break randomString;
	
	}
	
	if(no_of_nodes_evaluated != 200001)
	{
		total_no_nodes_evaluated += no_of_nodes_evaluated;
		total_time_taken += elapsed - start;
	}
	
	
	no_of_nodes_evaluated = 0;
	start = 0;
	elapsed = 0;
	
	
	}
	
	System.out.println("Total nodes evaluated  =  " + total_no_nodes_evaluated);
	System.out.println("Total time taken =  " + total_time_taken);
	System.out.println("Total cases considered =  " + n);
	float total_nodes = total_no_nodes_evaluated/n;
	float total_time = total_time_taken/n;
	System.out.println("Average nodes evaluated  =  " +  total_nodes);
	System.out.println("Average time taken  =  " + total_time);
		
	
	}
	

}
