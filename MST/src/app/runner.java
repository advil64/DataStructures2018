package app;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class runner {

    public static void main(String[] args){
        //declare that there is a Graph named graph
        Graph graph;
        //try to initialize and find the shortest route
        try {
            //create a new graph from the given text file
            graph = new Graph("graph7.txt");
            //we make a list of all the partial trees with the arcs and vertices
            PartialTreeList partialTreeList = PartialTreeList.initialize(graph);
            //print the newly created partial trees using an iterator
            Iterator<PartialTree> iter = partialTreeList.iterator();
            //go through all the partial trees
            while (iter.hasNext()) {
                //gets each partial tree in the list
                PartialTree pt = iter.next();
                //print each newly created partial tree
                System.out.println(pt);
            }
            //order the arcs in a manner where the least are selected
            ArrayList<Arc> arcArrayList = PartialTreeList.execute(partialTreeList);
            //print the minimum spanning tree
            for (int i = 0; i < arcArrayList.size(); i++) {
                //gets each arc in the list
                Arc anArcArrayList = arcArrayList.get(i);
                //prints the given arc
                System.out.println(anArcArrayList);
            }

        }
        //catch a file not found error
        catch (IOException e) { e.printStackTrace(); }
    }
}
