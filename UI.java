public class UI{
    public final static String[] cmd1 = {"/bin/sh", "-c", "stty raw </dev/tty"};
    public final static String[] cmd2 = {"/bin/sh", "-c", "stty cooked </dev/tty"};

    public final static String UP    = "\u001B[A";
    public final static String DOWN  = "\u001B[C";
    public final static String RIGHT = "\u001B[B";
    public final static String LEFT  = "\u001B[D";

    static public void clear(){System.out.print("\033\143");}


// CURSOR CONTROL
    
    public static void moveCU(){System.out.print(UP);}
    public static void moveCD(){System.out.print(RIGHT);}
    public static void moveCR(){System.out.print(DOWN);}
    public static void moveCL(){System.out.print(LEFT);}

    public static void moveCUB(){System.out.print("\u001B[A\b\b\b\b");}
    public static void moveCDB(){System.out.print("\u001B[B\b\b\b\b");}
    public static void moveCRB(){System.out.print("\u001B[C\b\b\b\b");}
    public static void moveCLB(){System.out.print("\u001B[D\b\b\b\b");}

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
        String printHolder = "";//Store as variable to avoid unnecessary calls to System
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