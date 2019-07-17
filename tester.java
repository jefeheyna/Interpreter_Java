import javafx.util.Pair;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by jeffhejna on 3/19/18.
 */
public class tester {
    public static void main(String[] args) throws IOException {
        //Stack<Pair> mystack = new Stack<>();
        interpreter test = new interpreter();
        test.interpreter("src/input1.txt","src/output1.txt");
        //String mystring = "\"Hello\"";
        //System.out.println(mystring);
        //System.out.println(mystring.contains("\""));


    }
}
