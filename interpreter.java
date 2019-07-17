
import java.io.*;
import java.util.*;

/**
 * Created by jeffhejna on 3/5/18.
 */
public class interpreter {

    public static void interpreter(String input, String output) throws IOException {
        Scanner reader = new Scanner(new File(input));
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        ArrayList<String> inputlist = new ArrayList<>();

        ArrayList<Stack<String[]>> mystack = new ArrayList<>();
        ArrayList<HashMap<String,String[]>> binding_map = new ArrayList<>();
        HashMap<String,ArrayList<String>> functionMap = new HashMap<>();

        boolean infunction =false;

        //Stack is_string_boolstack = new Stack();

        //reading input file and storing in a linked list to be traversed
        while (reader.hasNext()){
            String current = reader.nextLine();
            if(current.startsWith("push")){
                String rest = current.substring(5);
                inputlist.add("push");
                inputlist.add(rest);
            }
            else if(current.startsWith("fun") && !current.equals("funEnd")){
                String rest = current.substring(4);
                String[] strings = rest.split(" ");
                inputlist.add("fun");
                for (int i = 0; i < strings.length; i++) {
                    inputlist.add(strings[i]);
                }
            }
            else if(current.startsWith("inOut")){
                String rest = current.substring(6);
                String[] strings = rest.split(" ");
                inputlist.add("inOut");
                for (int i = 0; i < strings.length; i++) {
                    inputlist.add(strings[i]);
                }
            }
            else inputlist.add(current);
        }
        reader.close();

        int curr = 0;
        mystack.add(new Stack<>());
        binding_map.add(new HashMap<>());
        for (int i = 0; i < inputlist.size(); i++) {
            //System.out.println(inputlist.get(i));
            switch (inputlist.get(i)){
                case "push":
                    String mystring = inputlist.get(i+1);
                    System.out.println(mystring);
                    if(mystring.contains("\"")){
                        mystring = mystring.replaceAll("\"", ""); //https://stackoverflow.com/questions/2608665/how-can-i-trim-beginning-and-ending-double-quotes-from-a-string/38568614
                        mystack.get(curr).push(new String[] {mystring,"STR"});
                    }
                    else if(functionMap.containsKey(mystring)){
                        mystack.get(curr).push(new String[]{mystring,"FUN"});
                    }
                    else {
                        try {
                            int x = Integer.parseInt(inputlist.get(i + 1));
                            if (inputlist.get(i + 1).contains(".")) {
                                mystack.get(curr).push(new String[]{":error:", "ERROR"});
                            } else if (x == -0) {
                                mystack.get(curr).push(new String[]{"0", "INT"});
                            } else {
                                mystack.get(curr).push(new String[]{inputlist.get(i + 1), "INT"});
                            }

                            //System.out.println(inputlist.get(i+1));
                        } catch (NumberFormatException e) {

                            switch (mystring) {
                                case ":true:":
                                case ":false:":
                                    mystack.get(curr).push(new String[]{mystring, "BOOL"});
                                    break;
                                case ":error:":
                                    mystack.get(curr).push(new String[]{":error:", "ERROR"});
                                    break;
                                default:
                                    mystack.get(curr).push(new String[]{mystring, "NAME"});
                                    break;
                            }

                        }
                    }
                    break;

                case "pop":

                    if(mystack.get(curr).empty()){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else{
                        String popped= mystack.get(curr).pop()[0];
                        if(binding_map.get(curr).containsKey(popped)) {
                            binding_map.get(curr).remove(popped);
                        }
                    }
                    break;

                case "add":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String y_item = y_pair[1];
                        String x_string = x_pair[0];
                        String x_item = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);


                        if (y_isUnit && x_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && binding_map.get(curr).get(x_string)[1].equals("INT")){
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                int result = x+y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }


                        }
                        else if(y_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && x_item.equals("INT")){
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                int result = x+y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            if(binding_map.get(curr).get(x_string)[1].equals("INT") && y_item.equals("INT")) {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int result = x+y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            if(y_item.equals("INT") && x_item.equals("INT")){
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                mystack.get(curr).push(new String[]{Integer.toString(x+y),"INT"});
                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "sub":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String y_item = y_pair[1];
                        String x_string = x_pair[0];
                        String x_item = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);


                        if (y_isUnit && x_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && binding_map.get(curr).get(x_string)[1].equals("INT")){
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                int result = x-y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }


                        }
                        else if(y_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && x_item.equals("INT")){
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                int result = x-y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            if(binding_map.get(curr).get(x_string)[1].equals("INT") && y_item.equals("INT")) {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int result = x-y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            if(y_item.equals("INT") && x_item.equals("INT")){
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                mystack.get(curr).push(new String[]{Integer.toString(x-y),"INT"});
                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "mul":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String y_item = y_pair[1];
                        String x_string = x_pair[0];
                        String x_item = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);


                        if (y_isUnit && x_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && binding_map.get(curr).get(x_string)[1].equals("INT")){
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                int result = x*y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }


                        }
                        else if(y_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && x_item.equals("INT")){
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                int result = x*y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            if(binding_map.get(curr).get(x_string)[1].equals("INT") && y_item.equals("INT")) {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int result = x*y;
                                mystack.get(curr).push(new String[]{Integer.toString(result),"INT"});

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            if(y_item.equals("INT") && x_item.equals("INT")){
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                mystack.get(curr).push(new String[]{Integer.toString(x*y),"INT"});
                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "div":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String y_item = y_pair[1];
                        String x_string = x_pair[0];
                        String x_item = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);


                        if (y_isUnit && x_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && binding_map.get(curr).get(x_string)[1].equals("INT")){
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x/y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }


                        }
                        else if(y_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && x_item.equals("INT")){
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x/y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            if(binding_map.get(curr).get(x_string)[1].equals("INT") && y_item.equals("INT")) {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x/y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            if(y_item.equals("INT") && x_item.equals("INT")){
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x/y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "rem":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String y_item = y_pair[1];
                        String x_string = x_pair[0];
                        String x_item = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);


                        if (y_isUnit && x_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && binding_map.get(curr).get(x_string)[1].equals("INT")){
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x%y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }


                        }
                        else if(y_isUnit){
                            if(binding_map.get(curr).get(y_string)[1].equals("INT") && x_item.equals("INT")){
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x%y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            if(binding_map.get(curr).get(x_string)[1].equals("INT") && y_item.equals("INT")) {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x%y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            if(y_item.equals("INT") && x_item.equals("INT")){
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                if(y!=0){
                                    mystack.get(curr).push(new String[]{Integer.toString(x%y),"INT"});
                                }
                                else{
                                    mystack.get(curr).push(x_pair);
                                    mystack.get(curr).push(y_pair);
                                    mystack.get(curr).push(new String[]{":error:","ERROR"});
                                }

                            } else {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "neg":
                    if (mystack.get(curr).empty()){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] x_pair = mystack.get(curr).pop();
                        String x_string = x_pair[0];
                        if(binding_map.get(curr).containsKey(x_string)){
                            if(binding_map.get(curr).get(x_string)[1].equals("INT")){
                                String[] oldpair = binding_map.get(curr).get(x_string);
                                int x = Integer.parseInt(oldpair[0]);
                                if (x<0){
                                    x = Math.abs(x);
                                }
                                else x=0-x;
                                mystack.get(curr).push(new String[]{Integer.toString(x),"INT"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }

                        }
                        else {
                            try {
                                int x = Integer.parseInt(x_string);
                                if (x < 0) {
                                    x = Math.abs(x);
                                } else {
                                    x = 0 - x;
                                }
                                mystack.get(curr).push(new String[]{Integer.toString(x), "INT"});

                            } catch (NumberFormatException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "swap":
                    if (mystack.get(curr).empty() || mystack.get(curr).size()==1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        mystack.get(curr).push(y_pair);
                        mystack.get(curr).push(x_pair);
                    }
                    break;

                case "cat":
                    if (mystack.get(curr).empty() || mystack.get(curr).size()==1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else{
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_item = y_pair[0];
                        String y_type = y_pair[1];
                        String x_item = x_pair[0];
                        String x_type = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_item);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_item);


                        if (y_isUnit && x_isUnit){
                            boolean y_isstring = binding_map.get(curr).get(y_item)[1].equals("STR");
                            boolean x_isstring = binding_map.get(curr).get(x_item)[1].equals("STR");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            String x_string = binding_map.get(curr).get(x_item)[0];
                            if (y_isstring && x_isstring){
                                mystack.get(curr).push(new String[]{x_string+y_string,"STR"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(y_isUnit){
                            boolean y_isstring = binding_map.get(curr).get(y_item)[1].equals("STR");
                            boolean x_isstring = x_type.equals("STR");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            if (y_isstring && x_isstring){
                                mystack.get(curr).push(new String[]{x_item+y_string,"STR"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            boolean x_isstring = binding_map.get(curr).get(x_item)[1].equals("STR");
                            boolean y_isstring = y_type.equals("STR");
                            String x_string = binding_map.get(curr).get(x_item)[0];
                            if (y_isstring && x_isstring){
                                mystack.get(curr).push(new String[]{x_string+y_item,"STR"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            boolean y_isstring = y_type.equals("STR");
                            boolean x_isstring = x_type.equals("STR");
                            if (y_isstring && x_isstring){
                                mystack.get(curr).push(new String[]{x_item+y_item,"STR"});
                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "and":
                    if (mystack.get(curr).empty() || mystack.get(curr).size()==1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else{
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_item = y_pair[0];
                        String y_type = y_pair[1];
                        String x_item = x_pair[0];
                        String x_type = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_item);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_item);


                        if (y_isUnit && x_isUnit){
                            boolean y_isBool = binding_map.get(curr).get(y_item)[1].equals("BOOL");
                            boolean x_isBool = binding_map.get(curr).get(x_item)[1].equals("BOOL");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            String x_string = binding_map.get(curr).get(x_item)[0];
                            boolean y_true = y_string.equals(":true:");
                            boolean x_true = x_string.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true && x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(y_isUnit){
                            boolean y_isBool = binding_map.get(curr).get(y_item)[1].equals("BOOL");
                            boolean x_isBool = x_type.equals("BOOL");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            boolean x_true = x_item.equals(":true:");
                            boolean y_true = y_string.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true && x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            boolean x_isBool = binding_map.get(curr).get(x_item)[1].equals("BOOL");
                            boolean y_isBool = y_type.equals("BOOL");
                            String x_string = binding_map.get(curr).get(x_item)[0];
                            boolean y_true = y_item.equals(":true:");
                            boolean x_true = x_string.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true && x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            boolean y_isBool = y_type.equals("BOOL");
                            boolean x_isBool = x_type.equals("BOOL");
                            boolean y_true = y_item.equals(":true:");
                            boolean x_true = x_item.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true && x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "or":
                    if (mystack.get(curr).empty() || mystack.get(curr).size()==1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else{
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_item = y_pair[0];
                        String y_type = y_pair[1];
                        String x_item = x_pair[0];
                        String x_type = x_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_item);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_item);


                        if (y_isUnit && x_isUnit){
                            boolean y_isBool = binding_map.get(curr).get(y_item)[1].equals("BOOL");
                            boolean x_isBool = binding_map.get(curr).get(x_item)[1].equals("BOOL");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            String x_string = binding_map.get(curr).get(x_item)[0];
                            boolean y_true = y_string.equals(":true:");
                            boolean x_true = x_string.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true || x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(y_isUnit){
                            boolean y_isBool = binding_map.get(curr).get(y_item)[1].equals("BOOL");
                            boolean x_isBool = x_type.equals("BOOL");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            boolean x_true = x_item.equals(":true:");
                            boolean y_true = y_string.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true || x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            boolean x_isBool = binding_map.get(curr).get(x_item)[1].equals("BOOL");
                            boolean y_isBool = y_type.equals("BOOL");
                            String x_string = binding_map.get(curr).get(x_item)[0];
                            boolean y_true = y_item.equals(":true:");
                            boolean x_true = x_string.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true || x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            boolean y_isBool = y_type.equals("BOOL");
                            boolean x_isBool = x_type.equals("BOOL");
                            boolean y_true = y_item.equals(":true:");
                            boolean x_true = x_item.equals(":true:");

                            if (y_isBool && x_isBool){
                                if(y_true || x_true){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }

                            }
                            else{
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "not":
                    if (mystack.get(curr).empty()){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String y_item = y_pair[0];
                        String y_type = y_pair[1];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_item);

                        if (y_isUnit) {
                            boolean y_isBool = binding_map.get(curr).get(y_item)[1].equals("BOOL");
                            String y_string = binding_map.get(curr).get(y_item)[0];
                            boolean y_true = y_string.equals(":true:");
                            if (y_isBool) {
                                if (y_true) {
                                    mystack.get(curr).push(new String[]{":false:", "BOOL"});
                                } else {
                                    mystack.get(curr).push(new String[]{":true:", "BOOL"});
                                }
                            } else {
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        } else {
                            boolean y_isBool = y_type.equals("BOOL");
                            boolean y_true = y_item.equals(":true:");
                            if (y_isBool) {
                                if (y_true) {
                                    mystack.get(curr).push(new String[]{":false:", "BOOL"});
                                } else {
                                    mystack.get(curr).push(new String[]{":true:", "BOOL"});
                                }
                            } else {
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;
                case "equal":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String x_string = x_pair[0];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);

                        if (y_isUnit && x_isUnit){
                            try {
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                boolean result = x==y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (NumberFormatException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(y_isUnit){
                            try {
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                boolean result = x==y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (NumberFormatException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            try {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                boolean result = x==y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (NumberFormatException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            try{
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                boolean result = x==y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (NumberFormatException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "lessThan":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] y_pair = mystack.get(curr).pop();
                        String[] x_pair = mystack.get(curr).pop();
                        String y_string = y_pair[0];
                        String x_string = x_pair[0];
                        boolean y_isUnit = binding_map.get(curr).containsKey(y_string);
                        boolean x_isUnit = binding_map.get(curr).containsKey(x_string);

                        if (y_isUnit && x_isUnit){
                            try {
                                int x= Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                boolean result = x<y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (ClassCastException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(y_isUnit){
                            try {
                                int x = Integer.parseInt(x_string);
                                int y = Integer.parseInt(binding_map.get(curr).get(y_string)[0]);
                                boolean result = x<y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (ClassCastException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else if(x_isUnit){
                            try {
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(binding_map.get(curr).get(x_string)[0]);
                                boolean result = x<y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (ClassCastException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                        else {
                            try{
                                int y = Integer.parseInt(y_string);
                                int x = Integer.parseInt(x_string);
                                boolean result = x<y;
                                if(result){
                                    mystack.get(curr).push(new String[]{":true:","BOOL"});
                                }
                                else{
                                    mystack.get(curr).push(new String[]{":false:","BOOL"});
                                }


                            } catch (ClassCastException e) {
                                mystack.get(curr).push(x_pair);
                                mystack.get(curr).push(y_pair);
                                mystack.get(curr).push(new String[]{":error:","ERROR"});
                            }
                        }
                    }
                    break;

                case "bind":
                    if (mystack.get(curr).empty() || mystack.get(curr).size() == 1){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    else {
                        String[] item = mystack.get(curr).pop();
                        String[] name = mystack.get(curr).pop();

                        //if the name is not a Name, if the item is of type Error, if the item is a name that is not bound
                        if(!name[1].equals("NAME") ||item[1].equals("ERROR") || (item[1].equals("NAME") && !binding_map.get(curr).containsKey(item[0]))){
                            mystack.get(curr).push(item);
                            mystack.get(curr).push(name);
                            mystack.get(curr).push(new String[]{":error:","ERROR"});
                        }
                        //if we are binding name to an item that is bound to a value
                        else if(binding_map.get(curr).containsKey(item[0])){
                            String[] item_pair = binding_map.get(curr).get(item[0]);
                            binding_map.get(curr).put(name[0],item_pair);
                            mystack.get(curr).push(new String[]{":unit:","UNIT"});
                        }
                        //if we are updating a name that is already bound to something
                        else if(binding_map.get(curr).containsKey(name[0])){
                            String[] oldpair = binding_map.get(curr).get(name[0]);
                            String[] newpair = new String[]{item[0],item[1]};
                            binding_map.get(curr).replace(name[0],oldpair,newpair);
                            mystack.get(curr).push(new String[]{":unit:", "UNIT"});
                        }

                        else{
                            binding_map.get(curr).put(name[0], new String[]{item[0], item[1]});
                            mystack.get(curr).push(new String[]{":unit:","UNIT"});
                        }

                    }
                    break;
                case "if":
                    if(mystack.get(curr).size()<3){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }
                    String[] x = mystack.get(curr).pop();
                    String[] y = mystack.get(curr).pop();
                    String[] z = mystack.get(curr).pop();

                    if (!z[1].equals("BOOL")){
                        mystack.get(curr).push(z);
                        mystack.get(curr).push(y);
                        mystack.get(curr).push(x);
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                    }else{
                        if (z[0].equals(":true:")){
                            mystack.get(curr).push(y);
                        }else{
                            mystack.get(curr).push(x);
                        }
                    }
                    break;

                case "let":
                    mystack.add(new Stack<>());
                    HashMap<String,String[]> newbindingmap = new HashMap<>(binding_map.get(curr));
                    binding_map.add(newbindingmap);
                    curr++;
                    break;

                case "end":
                    String[] last_item = mystack.get(curr).pop();
                    curr--;
                    mystack.get(curr).push(last_item);
                    break;

                case "fun":
                    ++i;
                    String fun_name = inputlist.get(i);
                    ++i;
                    String argument = inputlist.get(i);
                    ++i;
                    functionMap.put(fun_name,new ArrayList<>());
                    functionMap.get(fun_name).add(Integer.toString(curr));
                    functionMap.get(fun_name).add(argument);
                    while(!inputlist.get(i).equals("funEnd")){
                        functionMap.get(fun_name).add(inputlist.get(i));
                        ++i;
                    }
                    ++i;
                break;

                case "inOutFun":
                    ++i;
                    String IOfun_name = inputlist.get(i);
                    ++i;
                    String IOargument = inputlist.get(i);
                    ++i;
                    functionMap.put(IOfun_name,new ArrayList<>());
                    functionMap.get(IOfun_name).add(Integer.toString(curr));
                    functionMap.get(IOfun_name).add(IOargument);
                    while(!inputlist.get(i).equals("funEnd")){
                        functionMap.get(IOfun_name).add(inputlist.get(i));
                        ++i;
                    }
                    ++i;
                    break;

                case "call":
                    if(mystack.isEmpty() || mystack.size()<2){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                        break;
                    }
                    String arg = mystack.get(curr).pop()[0];
                    String fun = mystack.get(curr).pop()[0];

                    if(!functionMap.containsKey(fun)){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                        break;
                    }

                    String at = Integer.toString(curr);

                    ++curr;

                    if(!at.equals(functionMap.get(fun).get(0))){
                        mystack.get(curr).push(new String[]{":error:","ERROR"});
                        break;
                    }


                    break;
                case "quit":
                    while (!mystack.get(curr).empty()){
                        out.write(mystack.get(curr).pop()[0] + '\n');
                    }
                    out.close();
                    i = inputlist.size();
                    break;

                default: break;
            }

        }

    }

}



