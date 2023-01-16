import java.awt.*;
import java.util.Scanner;

public class UI{
    public final static String[] cmd1 = {"/bin/sh", "-c", "stty raw </dev/tty"};
    public final static String[] cmd2 = {"/bin/sh", "-c", "stty cooked </dev/tty"};

    public final static String UP    = "\u001B[A";
    public final static String DOWN  = "\u001B[C";
    public final static String RIGHT = "\u001B[B";
    public final static String LEFT  = "\u001B[D";

    static public void clear(){System.out.print("\033\143");}

// DYNAMIC INFO HELPERS


private static String getInput() {
    StringBuilder sb = new StringBuilder();
    final Scanner scanner = new Scanner(System.in);
    return scanner.next();
}

    static public Dimension getTerminalDimensions(String message){//TODO: BUG - only works once, then always returns the same and does not do rows yet until enter is pressed
//                        "\u001b[s"             // save cursor position
//                        "\u001b[5000;5000H"    // move to col 5000 row 5000
//                        "\u001b[6n"            // request cursor position
//                        "\u001b[u"             // restore cursor position
        System.out.print("\u001b[s");//Save the cursor position to be restored later
        System.out.print("\u001b[0;0H");// Move to local top left of the screen
        System.out.print("\n"+message);
        System.out.print("\u001b[999999;999999H");// Move to local bottom right of the screen
//        UI.moveCR(500);//TODO: repeat until found or infinite
//        UI.moveCD(500);//TODO: repeat until found or infinite
        String read = null;
        try {
//            System.out.print("\u001b[0;0H");// Move to local top left of the screen
            System.out.print("\u001B[6n");//+"Press enter so the editor can know your screen dimensions: ");// Request cursor position
            System.out.print("\u001b[0;0H");// Move to local top left of the screen
            read = getInput();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("\u001b[0;0H");
        System.out.print("\u001b[u");//Restore the cursor position using saved position
        int columns = Integer.parseInt(read.substring(read.indexOf(";")+1,read.length()-1));
        int rows = Integer.parseInt(read.substring(2, read.indexOf(";")));

        UI.clear();
        return new Dimension(columns,rows);
    }
// CURSOR CONTROL
    
    public static void moveCU(){System.out.print(UP);}
    public static void moveCD(){System.out.print(RIGHT);}
    public static void moveCR(){System.out.print(DOWN);}
    public static void moveCL(){System.out.print(LEFT);}


    public static void moveCU(int times){
        String printHolder = "";//Store as variable to avoid unnecessary calls to System
        for(int i = 0; i < times; i++){
            printHolder+="\u001B[A";
        }
        System.out.print(printHolder);
    }
    public static void moveCD(int times){
        String printHolder = "";//Store as variable to avoid unnecessary calls to System
        for(int i = 0; i < times; i++){
            printHolder+="\u001B[B";
        }
        System.out.print(printHolder);
    }
    public static void moveCR(int times){
        String printHolder = "";//Store as variable to avoid unnecessary calls to System
        for(int i = 0; i < times; i++){
            printHolder+="\u001B[C";
        }
        System.out.print(printHolder);
    }
    public static void moveCL(int times){
        String printHolder = "";//Store as vdariable to avoid unnecessary calls to System
        for(int i = 0; i < times; i++){
            printHolder+="\u001B[D";
        }
        System.out.print(printHolder);
    }

    public static void moveCU(boolean del){System.out.print("\b\b\b\b    \b\b\b\b\u001B[A");}
    public static void moveCD(boolean del){System.out.print("\b\b\b\b    \b\b\b\b\u001B[B");}
    public static void moveCR(boolean del){System.out.print("\b\b\b\b    \b\b\b\b\u001B[C");}
    public static void moveCL(boolean del){System.out.print("\b\b\b\b    \b\b\b\b\u001B[D");}

    public static void moveBack(int places){
        String tmpStr="";
        for(int i=0; i<places; i++){
            tmpStr += "\b";
        }
        System.out.print(tmpStr);
    }


    static public char getTypedChr(){
        return getTypedChr(true);
    }
    static public char getTypedChr(boolean newLine){
        try{
            Runtime.getRuntime().exec(cmd1).waitFor();
            char c = (char) System.in.read();
            Runtime.getRuntime().exec(cmd2).waitFor();
            if(newLine)
                System.out.print("\n");
            return c;
        }catch(Exception e){
            System.out.println("Error getting typed char: "+e);
        }
        return '\0';
    }

    static public char getChrNumFromPress(){
        try{
            Runtime.getRuntime().exec(cmd1).waitFor();
            byte tmp = (byte) System.in.read();
            clear();
            System.out.print("\n>>>"+tmp
            +"<<<");
            Runtime.getRuntime().exec(cmd2).waitFor();
        }catch(Exception e){
        }
        return '\0';
    }

    static public String getTypedStr(){
        try{
            Runtime.getRuntime().exec(cmd1).waitFor();

            String str = "";
            char c;
            while ((c=(char)System.in.read ()) !=
            '\n') {
            str += c;
            }
            // String str_ = System.in.read().toString();
            Runtime.getRuntime().exec(cmd2).waitFor();
            System.out.print("\n");
            return str;
        }catch(Exception e){}
        return "null";
    }


}