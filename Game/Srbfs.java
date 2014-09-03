package Game;

import java.util.ArrayList;

public class Srbfs {
	int count = 0;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		for (int depth =4 ; depth <= 4; depth++) {

			ArrayList<Object> prevList1 = null;
			//ArrayList<Object> prevList2 = null;
			Node n = null;
			//Node d = null;
			for (int i = depth; i >= 0; i--) {

				ArrayList<Object> cluster = new ArrayList<>();
				//ArrayList<Object> dummyCluster = new ArrayList<>();
				ArrayList<Node> newList = new ArrayList<Node>();
				//ArrayList<Node> dummyList = new ArrayList<Node>();

				for (int j = 0; j < Math.pow(2, i); j++) {
					if (prevList1 != null) {
						n = new Node("n" + i + j, i,
								(ArrayList<Node>) prevList1.get(j));
						//d = new Node("n" + i + j, i,
							//	(ArrayList<Node>) prevList1.get(j));
					} else {
						n = new Node("n" + i + j, i, null);
						//d = new Node("n" + i + j, i, null);
					}
					newList.add(n);
					//dummyList.add(d);

					if (j % 2 != 0) {
						cluster.add(newList);
					//	dummyCluster.add(dummyList);
						newList = new ArrayList<Node>();
						//dummyList = new ArrayList<Node>();
					}
				}
				prevList1 = cluster;
				//prevList2 = dummyCluster;
			}

			Srbfs algo = new Srbfs();
			int ret = algo.runSRBFS(n, 0, 999);
			System.out.println(ret + "   :    " + algo.count);
		}

	}

	public void resetChildValues(ArrayList<Node> child, int depth) {
		// setting the cost function as depth
		for (Node n : child) {
			n.val = depth;
		}
	}

	public int runSRBFS(Node n, int storedValue, int bound) {
		if (n.val > bound) {
			return n.val;
		} else {
			if (n.child != null) {
				if (n.child.size() == 0) {
					return 999;
				} else {
					//count = count + n.child.size();
				}
			} else {
				return 999;
			}
			
			
			for(int i=0; i<n.child.size(); i++)
			{
				if(storedValue > n.staticValue)
				{
					n.child.get(i).val = storedValue>n.child.get(i).val ? storedValue : n.child.get(i).val;
				}
				
			}
			// sort the child array list to find the minimum child val
			// resetChildValues(n.child, depth + 1);
			Node[] minAry = sort(n.child);
			 for (int i = 0; i < n.child.size(); i++) {
			
			// System.out.println("parent: "+ n.label + " , child "
			// + n.child.get(i).label );
			 }
				//System.out.println();
			

			int retVal = minAry[0].val;
			
			if(retVal <= bound && retVal != 999)
			{
				for(int i=0; i<n.child.size();i++)
				{
					System.out.println("Parentsize:  " + n.child.size() + "  Child:    " + n.child.get(i).label);
				}
				count = count + 2;
			}
			
			while (retVal <= bound && retVal != 999) {
//				System.out.println(n.label +" : "+minAry[0].label);
				//count = count + 1;
				retVal = runSRBFS(minAry[0], minAry[0].val,
						minAry[1].val < bound ? minAry[1].val : bound);

				for (int i = 0; i < n.child.size(); i++) {
					if (n.child.get(i).label
							.equalsIgnoreCase(minAry[0].label)) {
						n.child.get(i).val = retVal;
						break;
					}
				}
				// call sort again
				minAry = sort(n.child);
				retVal = minAry[0].val;
			}
			
			
			for(int i=0;i<n.child.size();i++)
			{
				n.child.get(i).val = n.child.get(i).staticValue;
			}
			return retVal;
		}

	}

	public Node[] sort(ArrayList<Node> child) {
		Node[] ary = new Node[2];
		for (int i = 0; i < child.size(); i++) {
			for (int j = 1; j < (child.size() - i); j++) {
				int val1 = child.get(i).val;
				int val2 = child.get(j).val;
				if (val1 > val2) {
					// swap node i and j
//					Node temp = child.get(i);
//					child.add(i, child.get(j));
//					child.add(j, temp);
					ary[0] = child.get(j);
					ary[1] = child.get(i);
					
				}
				else
				{
					ary[0] = child.get(i);
					ary[1] = child.get(j);
				}
			}
		}
		
		return ary;
	}

}

class Node {
	String label;
	int val;
	int staticValue;
	String parent;
	ArrayList<Node> child;
	//ArrayList<Node> dummyChild;
	boolean isEnd;

	Node(String label, int dep, ArrayList<Node> child) {
		this.label = label;
		this.child = child;
		val = dep;
		staticValue = dep;
		//this.dummyChild = dummyChild;
	}

	Node() {

	}

}
