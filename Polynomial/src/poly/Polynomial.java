package poly;

import java.io.IOException;
import java.util.Currency;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 *
 * @author runb-cs112
 *
 */
public class Polynomial {

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3
	 * </pre>
	 *
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc)
			throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}

	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 *
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {

		//Declaring the traversal nodes, the end of the new Node, and the degrees, as well as a variable to add floats
		Node Current1 = poly1;
		Node Current2 = poly2;
		Node NewPoly = null;
		int Curr1Deg;
		int Curr2Deg;
		float NewCoeff;

		if(poly1 == null && poly2 == null){

			return null;
		}

		while(Current1 != null && Current2 != null){

			Curr1Deg = Current1.term.degree;
			Curr2Deg = Current2.term.degree;

			//Compares the degrees of the two polynomials
			if(Curr1Deg == Curr2Deg){

				//Adds the coefficients of the two terms
				NewCoeff = Current1.term.coeff + Current2.term.coeff;
				//Sets the next node of the NewPolynomial linked list which contains the addition of the two polys
				NewPoly = new Node(NewCoeff, Curr1Deg, NewPoly);
				//Increase both nodes to the next to traverse
				Current1 = Current1.next;
				Current2 = Current2.next;
			}

			//Checks which degree is lower and puts the lower one into the new linked list
			else if(Curr1Deg < Curr2Deg){

				//Adding the Node to the new List
				NewPoly = new Node(Current1.term.coeff, Curr1Deg, NewPoly);
				//Traversal
				Current1 = Current1.next;
			}

			else if(Curr2Deg < Curr1Deg){

				//Adding the Node to the new List
				NewPoly = new Node(Current2.term.coeff, Curr2Deg, NewPoly);
				//Traversal
				Current2 = Current2.next;
			}
		}

		//If either of the polynomials are null, just return the other or return null
		while(Current1 == null && Current2 != null) {

			//Adding the rest of the nodes in the remaining list
			NewPoly = new Node(Current2.term.coeff, Current2.term.degree, NewPoly);
			//Traversing
			Current2 = Current2.next;
		}

		while(Current2 == null && Current1 != null) {

			//Adding the rest of the nodes in the remaining list
			NewPoly = new Node(Current1.term.coeff, Current1.term.degree, NewPoly);
			//Traversing
			Current1 = Current1.next;
		}

		return reverse(NewPoly);
	}

	//reverses a linked list given the first node (called the head)
	private static Node reverse(Node head) {

		//initializes all the variables needed
		Node current = head;
		Node next = head.next;
		Node prev = null;

		if(head != null){

			//makes the headnext null because it will now bwcome the tail
			head.next = null;
			//previous Node becomes current
			prev = current;
			//current jumps to next
			current = next;

			while(current != null){

				//next also jumps, now we have three nodes at three positions, previous is seperate from other two
				next = current.next;
				//bridges the gap between current, next and previous, now next is seperate from others
				current.next = prev;
				//previous jumps to current
				prev = current;
				//current jumps to next
				current = next;
				//process is repeated
			}

			//at the end, the head would be the last element of the original array
			head = prev;
		}

		//returning the new front of the array
		return head;
	}

	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 *
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {

		//Declaring the traversal nodes, the end of the new Node, and the degrees, as well as a variable to add floats
		Node Current1 = poly1;
		Node Current2 = poly2;
		Node NewPoly = null;
		Node NewPolyTemp = null;
		float NewCoeff;
		int NewDegree;

		if(poly1 == null || poly2 == null) {

			return null;
		}

		//Traverses through the first polynomial
		while(Current1 != null){

			//Traverses through second polynomial
			while(Current2 != null){

				//Calculating the new coefficient
				NewCoeff = Current1.term.coeff * Current2.term.coeff;
				//Calculating the new degree
				NewDegree = Current1.term.degree + Current2.term.degree;
				//adding the new multiplied Nodes to the new polynomial
				NewPolyTemp = new Node (NewCoeff, NewDegree, NewPolyTemp);
				//Traversal
				Current2 = Current2.next;
			}

			//Add the two lists after multiplication
			NewPoly = add(NewPoly, reverse(NewPolyTemp));
			//Make the temp null
			NewPolyTemp = null;
			//Reset Current2 to the start
			Current2 = poly2;
			//Traversal
			Current1 = Current1.next;
		}

		return NewPoly;
	}

	/**
	 * Evaluates a polynomial at a given value.
	 *
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */

	//Basically evaluvates, code is simple no comments necessary
	public static float evaluate(Node poly, float x) {

		float value = 0;

		while(poly != null){

			value += poly.term.coeff * Math.pow(x, poly.term.degree);
			poly = poly.next;
		}

		return value;
	}

	/**
	 * Returns string representation of a polynomial
	 *
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		}

		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
			 current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
}
