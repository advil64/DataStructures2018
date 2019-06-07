package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

    public static String delims = " \t*+-/()[]";

    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     *
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        /** COMPLETE THIS METHOD **/
        /** DO NOT create new vars and arrays - they are already created before being sent in
         ** to this method - you just need to fill them in.
         **/

        //First we need to get rid of any trailing or leading spaces in the expression
        expr = expr.trim();

        //Now we need to get rid of all spaces in expr
        expr = expr.replaceAll("\\s", "");

        //lets make out regex pattern, we are first looking for any variables
        Pattern pattern = Pattern.compile("[a-zA-Z]+");

        //Lets start matching!
        Matcher matcher = pattern.matcher(expr);

        //Lets iterate through everything that the matcher can find in the expression
        Variable varInsert;
        Array arrInsert;

        while (matcher.find()) {

            //Creating the objects to insert
            String insert = (expr.substring(matcher.start(), matcher.end()));
            varInsert = new Variable(insert);
            arrInsert = new Array(insert);

            if(matcher.end() >= expr.length()){

                if(!vars.contains(varInsert))
                    vars.add(new Variable(insert));

                continue;
            }

            else if (expr.charAt(matcher.end()) != ('[') && !vars.contains(varInsert)) {

                vars.add(varInsert);
            }

            else if(!arrays.contains(arrInsert) && expr.charAt(matcher.end()) == ('[')){

                arrays.add(arrInsert);
            }
        }
    }

    /**
     * Loads values for variables and arrays in the expression
     *
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
            throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
                continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
                arr = arrays.get(arri);
                arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;
                }
            }
        }
    }

    /**
     * Evaluates the expression.
     *
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        /** COMPLETE THIS METHOD **/


        //First we need to get rid of any trailing or leading spaces in the expression and other spaces
        expr = expr.trim().replaceAll("\\s", "");


        String val = new String();
        Stack<Float> toAdd = new Stack<>();
        Stack<Character> parens = new Stack<>();
        Stack<Character> bracks = new Stack<>();
        float add;
        char operator;
        float value = 0;
        int i = 0;

        //Traverse the string one character at a time
        while(i < expr.length()){

            //Detect if the character is a variable
            if(expr.substring(i, i+1).matches("[a-zA-Z]+")){

                //Find the first letter of the variable
                val += expr.charAt(i); i++;

                //if the variable is the last letter in the expression, do not proceed
                if(i < expr.length()) {

                    //Find the complete variable
                    while (expr.substring(i, i + 1).matches("[a-zA-Z]+")) {

                        //Adds characters to the variable
                        val += expr.charAt(i);
                        i++;

                        //If the variable ever increases past the length of the string, break the loop
                        if(i >= expr.length())
                            break;
                    }

                    //Check if the variable is an array
                    if (i < expr.length() && expr.charAt(i) == '[') {

                        //if the expression has something at i and it is a bracket, recursively call evaluate on the expression inside the bracket
                        toAdd.push((float) arrays.get(arrays.indexOf(new Array(val))).values[(int) evaluate(expr.substring(i + 1), vars, arrays)]);
                        //adjust the index appropriately
                        while(i < expr.length()){

                            //if you encounter a paren push into stack
                            if(expr.substring(i,i+1).matches("\\[")){
                                bracks.push('[');}

                            //If you encounter a closing paren, pop from stack
                            else if(expr.substring(i,i+1).matches("]") && !bracks.isEmpty()){
                                bracks.pop();}

                            i++;

                            //when the array is empty, break and get index
                            if(bracks.isEmpty())
                                break;
                        }

                    } else{

                        //If it does not have a bracket, then it is a variable and add it to the variables stack
                        toAdd.push((float) vars.get(vars.indexOf(new Variable(val))).value);
                    }

                } else{

                    //If the variable is the last letter in the expression, you know that it is a variable in vars
                    toAdd.push((float) vars.get(vars.indexOf(new Variable(val))).value);
                }

                if(i >= expr.length()){

                    break;
                }

                //reset the value of val after every variable
                val = "";
            }

            //If the char is a digit
            else if(expr.substring(i, i+1).matches("[0-9]+")){

                //Get the first digit
                val += expr.charAt(i); i++;

                //if the digit is at the end of the string, this if statement will not be entered
                if(i < expr.length()) {

                    //get the complete float
                    while (expr.substring(i, i + 1).matches("[0-9]+")) {

                        val += expr.charAt(i); i++;

                        if(i >= expr.length())
                            break;
                    }
                }

                //push the new float
                toAdd.push(Float.parseFloat(val)); val = "";
            }

            //If char is a paren
            else if(expr.charAt(i) == '('){

                //push into the toAdd stack
                toAdd.push(evaluate(expr.substring(i+1), vars, arrays));

                //adjust the index appropriately
                while(i < expr.length()){

                    //if you encounter a paren push into stack
                    if(expr.substring(i,i+1).matches("\\(")){
                        parens.push('(');}

                    //If you encounter a closing paren, pop from stack
                    else if(expr.substring(i,i+1).matches("\\)") && !parens.isEmpty()){
                        parens.pop();}

                    i++;

                    //when the array is empty, break and get index
                    if(parens.isEmpty())
                        break;
                }
            }

            //Finds if there is a closing bracket or paren and returns the sum of the stack
            else if(expr.charAt(i) == ')' || expr.charAt(i) == ']'){

                //resets add, just in case
                add = 0;

                //loops through the stack
                while(!toAdd.isEmpty()){

                    add += toAdd.pop();
                }

                return add;
            }

            //We have hit an operator
            else {

                //Get the operator and continue to the next character
                operator = expr.charAt(i); i++;

                //Getting the float
                if(expr.substring(i, i+1).matches("[0-9]+")){

                    val = ""; val += expr.substring(i,i+1); i++;

                    if(i < expr.length()) {

                        while (expr.substring(i, i + 1).matches("[0-9]+")) {

                            val += expr.charAt(i); i++;

                            if(i >= expr.length())
                                break;
                        }
                    }
                    value = Float.parseFloat(val); val = "";
                } else if(expr.substring(i, i+1).matches("[a-zA-Z]+")){

                    //Find the first letter of the variable
                    val += expr.charAt(i); i++;

                    //if the variable is the last letter in the expression, do not proceed
                    if(i < expr.length()) {

                        //Find the complete variable
                        while (expr.substring(i, i + 1).matches("[a-zA-Z]+")) {

                            //Adds characters to the variable
                            val += expr.charAt(i); i++;

                            //If the variable ever increases past the length of the string, break the loop
                            if(i >= expr.length())
                                break;
                        }

                        //Check if the variable is an array
                        if (i < expr.length() && expr.charAt(i) == '[') {

                            //if the expression has something at i and it is a bracket, recursively call evaluate on the expression inside the bracket
                            value = ((float) arrays.get(arrays.indexOf(new Array(val))).values[(int) evaluate(expr.substring(i + 1), vars, arrays)]);

                            //adjust the index appropriately
                            while(i < expr.length()){

                                //if you encounter a brack push into stack
                                if(expr.charAt(i) == '[')
                                    bracks.push('[');

                                    //If you encounter a closing brack, pop from stack
                                else if(expr.charAt(i) == (']') && !bracks.isEmpty())
                                    bracks.pop();

                                i++;

                                //when the array is empty, break and get index
                                if(bracks.isEmpty())
                                    break;
                            }
                        } else
                            //If it does not have a bracket, then it is a variable and add it to the variables stack
                            value = ((float) vars.get(vars.indexOf(new Variable(val))).value); val = "";


                    } else
                        //If the variable is the last letter in the expression, you know that it is a variable in vars
                        value = ((float) vars.get(vars.indexOf(new Variable(val))).value); val = "";


                    //i should never surpass the length of the expression
                    if(i > expr.length())
                        break;

                    //reset the value of val after every variable
                    val = "";

                } else if(expr.charAt(i) == '('){

                    value = evaluate(expr.substring(i+1), vars, arrays);

                    //adjust the index appropriately
                    while(i < expr.length()){

                        //if you encounter a paren push into stack
                        if(expr.substring(i,i+1).matches("\\("))
                            parens.push('(');

                        //If you encounter a closing paren, pop from stack
                        else if(expr.substring(i,i+1).matches("\\)") && !parens.isEmpty())
                            parens.pop();

                        i++;

                        //when the array is empty, break and get index
                        if(parens.isEmpty())
                            break;
                    }
                } else if(expr.charAt(i) == ')' || expr.charAt(i) == ']'){

                    //resets add just in case
                    add = 0;

                    while(!toAdd.isEmpty())
                        add += toAdd.pop();


                    i++; return add;
                }

                //Finds the operator if any
                switch (operator){

                    case '*': toAdd.push(toAdd.pop() * value); break;
                    case '/': toAdd.push(toAdd.pop() / value); break;
                    case '-': toAdd.push(value * -1); break;
                    case '+': toAdd.push(value); break;
                }
            }
        }

        //Reset add just in case
        add = 0;

        //Adds all of the stuff in the stack
        while(!toAdd.isEmpty())
            add += toAdd.pop();


        return add;
    }
}