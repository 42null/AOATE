public class UI{
    public final static String[] cmd1 = {"/bin/sh", "-c", "stty raw </dev/tty"};
    public final static String[] cmd2 = {"/bin/sh", "-c", "stty cooked </dev/tty"};

    public final static String NORTH = "\u001B[A";
    public final static String EAST  = "\u001B[B";
    public final static String SOUTH = "\u001B[C";
    public final static String WEST  = "\u001B[D";

    static public void clear(){System.out.print("\033\143");}


// CURSOR CONTROOL
    
    public static void moveCU(){System.out.print(NORTH);}
    public static void moveCD(){System.out.print(EAST);}
    public static void moveCR(){System.out.print(SOUTH);}
    public static void moveCL(){System.out.print(WEST);}

    public static void moveCUB(){System.out.print("\u001B[A\b\b\b\b");}
    public static void moveCDB(){System.out.print("\u001B[B\b\b\b\b");}
    public static void moveCRB(){System.out.print("\u001B[C\b\b\b\b");}
    public static void moveCLB(){System.out.print("\u001B[D\b\b\b\b");}
    
    public static void moveCU(boolean del_){System.out.print("\b\b\b\b    \b\b\b\b\u001B[A");}
    public static void moveCD(boolean del_){System.out.print("\b\b\b\b    \b\b\b\b\u001B[B");}
    public static void moveCR(boolean del_){System.out.print("\b\b\b\b    \b\b\b\b\u001B[C");}
    public static void moveCL(boolean del_){System.out.print("\b\b\b\b    \b\b\b\b\u001B[D");}

    public static void moveBack(int places_){
        String tmpStr="";
        for(int i=0; i<places_; i++){
            tmpStr += "\b";
        }
        System.out.print(tmpStr);
    }

    // public static void moveCU(boolean del_){System.out.print((del_? "\b\b\b\b":"")+"    \b\b\b\b\u001B[A");}
    // public static void moveCD(boolean del_){System.out.print((del_? "\b\b\b\b":"")+"    \b\b\b\b\u001B[B");}
    // public static void moveCR(boolean del_){System.out.print((del_? "\b\b\b\b":"")+"    \b\b\b\b\u001B[C");}
    // public static void moveCL(boolean del_){System.out.print((del_? "\b\b\b\b":"")+"    \b\b\b\b\u001B[D");}


    static public char getTypedChr(){
        return getTypedChr(true);
    }
    static public char getTypedChr(boolean newLine_){
        try{
            Runtime.getRuntime().exec(cmd1).waitFor();
            char c = (char) System.in.read();
            Runtime.getRuntime().exec(cmd2).waitFor();
//            System.out.print("\n>>>"+Byte.toUnsignedInt((byte) c)+"<<<");
            if(newLine_)
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

            String str_ = "";
            char c;
            while ((c=(char)System.in.read ()) !=
            '\n') {
            str_ += c;
            }
            // String str_ = System.in.read().toString();
            Runtime.getRuntime().exec(cmd2).waitFor();
            System.out.print("\n");
            return str_;
        }catch(Exception e){}
        return "null";
    }


}