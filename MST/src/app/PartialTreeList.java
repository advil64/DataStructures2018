package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.*;

/**
 * Stores partial trees in a circular linked list
 *
 */
public class PartialTreeList implements Iterable<PartialTree> {

	/**
	 * Inner class - to build the partial tree circular linked list 
	 *
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;

		/**
		 * Next node in linked list
		 */
		public Node next;

		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 *
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;

	/**
	 * Number of nodes in the CLL
	 */
	private int size;

	/**
	 * Initializes this list to empty
	 */
	public PartialTreeList() {
		rear = null;
		size = 0;
	}

	/**
	 * Adds a new tree to the end of the list
	 *
	 * @param tree Tree to be added to the end of the list
	 */
	public void append(PartialTree tree) {
		Node ptr = new Node(tree);
		if (rear == null) {
			ptr.next = ptr;
		} else {
			ptr.next = rear.next;
			rear.next = ptr;
		}
		rear = ptr;
		size++;
	}

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 *
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		//partialtree to return
		PartialTreeList toreturn = new PartialTreeList();
		//Create a partial tree list for each vertex
		for(Vertex v : graph.vertices){
			//create a temporary partial tree object
			PartialTree vert = new PartialTree(v);
			//current pointer to loop through the adjacency linked list
			Vertex.Neighbor current = v.neighbors;
			//loop through the neighbors of the vertex
			while(current != null) {
				//load in the arcs to the minheap
				vert.getArcs().insert(new Arc(v, current.vertex, current.weight));
				//traversal
				current = current.next;
			}
			//add the new partial tree into the list
			toreturn.append(vert);
		}
		//return the newly created tree list
		return toreturn;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 *
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		//the arcs which make up the minimum spanning tree
		ArrayList<Arc> mst = new ArrayList<>();
		//initialize temp node
		PartialTree PTX;
		//temp priority queue
		MinHeap<Arc> PQX;
		//secondary temp partial tree
		PartialTree PTY;
		//top of the minheap of pqx
		Arc min;
		//loop through until there's only one element left in the ptlist
		while(ptlist.size()!=1){
			//the first partial tree in the list
			PTX = ptlist.remove();
			//the priority queue of PTX
			PQX = PTX.getArcs();
			//give min a value
			min = PQX.deleteMin();
			//check if v2 belongs to the same partial tree
			while(min.getv2().getRoot().equals(PTX.getRoot())){ min = PQX.deleteMin(); }
			//once we got an arc that is not in the ptx, add to mst
			mst.add(min);
			//try to find the v2 tree in the list
			PTY = ptlist.removeTreeContaining(min.getv2());
			//merge
			PTX.merge(PTY);
			//append the resulting list to the ptlist
			ptlist.append(PTX);
		}

		return mst;
	}

	/**
	 * Removes the tree that is at the front of the list.
	 *
	 * @return The tree that is removed from the front
	 * @throws NoSuchElementException If the list is empty
	 */
	public PartialTree remove()
			throws NoSuchElementException {

		if (rear == null) {
			throw new NoSuchElementException("list is empty");
		}
		PartialTree ret = rear.next.tree;
		if (rear.next == rear) {
			rear = null;
		} else {
			rear.next = rear.next.next;
		}
		size--;
		return ret;

	}

	/**
	 * Removes the tree in this list that contains a given vertex.
	 *
	 * @param vertex Vertex whose tree is to be removed
	 * @return The tree that is removed
	 * @throws NoSuchElementException If there is no matching tree
	 */
	public PartialTree removeTreeContaining(Vertex vertex)
			throws NoSuchElementException {
		//find the root of the vertex
		Vertex root = vertex.getRoot();
		//now traverse through the list
		Node curr = rear;
		//also get prev to build the bridge over curr
		Node prev;
		//check if there has indeed been a curr which equals the root
		boolean eqroot = false;
		//traversal
		do{
			//update prev to curr
			prev = curr;
			//change curr to its neighbor
			curr = curr.next;
			//check if the current node is equal to the desired root
			if(curr.tree.getRoot().parent == root.parent){eqroot = true; break;}
		}while(curr != rear);
		//check if there was a match
		if(eqroot){
			//build a bridge over the instance of the partial list
			if(size > 2){
				prev.next = curr.next;
				rear = prev;
			}
			//else you are only left with one element
			else{
				prev.next = prev;
				rear = prev;
			}
			//decrease the size
			size--;
			//return the deleted node
			return curr.tree;
		}
		//throw a nosuchelement excpetion
		else{throw new NoSuchElementException();}
	}

	/**
	 * Gives the number of trees in this list
	 *
	 * @return Number of trees
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns an Iterator that can be used to step through the trees in this list.
	 * The iterator does NOT support remove.
	 *
	 * @return Iterator for this list
	 */
	public Iterator<PartialTree> iterator() {
		return new PartialTreeListIterator(this);
	}

	private class PartialTreeListIterator implements Iterator<PartialTree> {

		private PartialTreeList.Node ptr;
		private int rest;

		public PartialTreeListIterator(PartialTreeList target) {
			rest = target.size;
			ptr = rest > 0 ? target.rear.next : null;
		}

		public PartialTree next()
				throws NoSuchElementException {
			if (rest <= 0) {
				throw new NoSuchElementException();
			}
			PartialTree ret = ptr.tree;
			ptr = ptr.next;
			rest--;
			return ret;
		}

		public boolean hasNext() {
			return rest != 0;
		}

		public void remove()
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}
}


