import java.util.regex.Pattern;

import java.util.ArrayList;
// import java.lang.ArrayIndexOutOfBounds;
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class
import java.io.BufferedWriter;
import java.io.PrintWriter;  // Deleate file contents before saving

class CurrentFile{

    private int line = 0, pos = 0;
    private String filePath = "";
    private ArrayList<String> listOfLines;
    
    
    public int getListSize(){return listOfLines.size();};

    // private int presentLine

    public CurrentFile(String filePath_){
        filePath = filePath_;
        listOfLines = Converters.fromFile(filePath_);
    }

    public void printFile(){printFile(0);}
    public void printFile(int startingFrom_){
        for(int i = startingFrom_; i < listOfLines.size(); i++){
            System.out.print(listOfLines.get(i)+"\n");
        }
    }

    public int editFile(){
        char[] cCmd = {'\0','\0'};
        String lsCmd = "";
        boolean lastWasUpDown = false, secondLastWasUpDown = false;
        // Pattern regPat = Pattern.compile(".*i[a-z].*");
        Pattern regPat = Pattern.compile("[a-z]|[A-Z]|[0-9]");

        String addToLine = "";
        int savePos = pos;
        boolean moveBack = true;
//        boolean endOfCommandCharacter = false;

        do{
            boolean moved = false;//Assume did not move
            boolean typed = false;//Assume did not move
            boolean controlKeyed = false;
            boolean endOfCharacter = false;

            cCmd[0] = cCmd[1];
            cCmd[1] = UI.getTypedChr(false);


            if(cCmd[0]=='['){
                endOfCharacter = true;
                if(cCmd[1]=='A'){//UP
                    UI.moveBack(4);
                    rewrite4Press();
                    UI.moveCU();

                    while(pos < savePos){
                        pos++;
                        UI.moveCR();
                    }

                    while(pos > listOfLines.get(line-(line==0?0:1)).length()){
                        pos--;
                        System.out.print("\b");
                    }

                    line--;



                }else if(cCmd[1]=='B'){//DOWN
                    UI.moveBack(4);

                    rewrite4Press();
//                    UI.moveCD();
//                    savePos = pos;
//                    if(listOfLines.size()>line+1){listOfLines.add("");}
                    if(listOfLines.size()>line+1){//Keep within page limits
                        UI.moveCD();
                        while(pos < savePos){
                            pos++;
                            UI.moveCR();
                        }

                        while(pos > listOfLines.get(line+1).length()){
                            pos--;
                            System.out.print("\b");
                        }
                        line++;
                    }else{
                        while(pos < listOfLines.get(line).length()){
                            pos++;
                            UI.moveCR();
                            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        }
                    }

                }else if(cCmd[1]=='D'){//LEFT
                    UI.moveBack(4);
                    rewrite4Press();
                    UI.moveCL();
                    pos--;

                    if(pos==-1){
                        if(--line == -1){//Move to < for slightly less efficient but better at correcting?
                            pos=0;
                        }else{
                            UI.moveCU();
                            String moveForwards = "";
                            while(pos < listOfLines.get(line).length()){
                                moveForwards+="\u001B[C";
                                pos++;
                            }
                            System.out.print(moveForwards);
                        }
                    }

                    savePos = pos;
                }else if(cCmd[1]=='C'){//RIGHT
                    UI.moveBack(4);
                    rewrite4Press();
                    // UI.moveCR();
                    UI.moveCR();
                    String backspaces = "";
                    if(pos > listOfLines.get(line).length()-1){
//                        pos = listOfLines.get(line).length()-2;
                        while(pos > 0){//For some reason having this only here works
                            pos--;
                            backspaces+="\b";
                        }
                        pos=0;
                        UI.moveCR();
                    }else{
                        pos++;
                    }

                    while(pos > listOfLines.get(line).length()){//For some reason having this only here works
                        pos--;
                        backspaces+="\b";
                    }

                    if(pos==0){
                        if(++line < listOfLines.size()){
                            UI.moveCD();
                            UI.moveCL();
                            UI.moveCL();
                            System.out.print(backspaces);
                        }else{
                            line--;//See if this is more efficient
                            pos = listOfLines.get(line).length();
//                            moveBack = false;
//                            UI.moveBack(4);
                            UI.moveCL();
                            UI.moveCL();
                        }
                    }
                    savePos = pos;
                }
                if(moveBack){
                    UI.moveBack(4);
                }else{
                    moveBack = false;//This way for efficiency
                }
                // }else if(cCmd[0]!='[' && !moved){
            }else if(((cCmd[0]!='^' && cCmd[1]!='[') &&
                      (cCmd[0]!='[' && cCmd[1]!='[') &&
                      (cCmd[0]!='[' && cCmd[1]!='C') &&
                      (cCmd[1]!='\0') &&
                      (regPat.matcher(cCmd[1]+"").find()) ||
                    //   ( && !(Character.getNumericValue( (cCmd[1]+"").charAt(0))==-1)) ||
                       cCmd[1]==' ')){
                endOfCharacter = true;

                // }else if((cCmd[0]!='^' && cCmd[1]!='[') && true){
                addToLine = cCmd[1]+"";//TODO: Allow special modifiers to change this.
                String tmpStr = listOfLines.get(line);
                
                String tmpStr2 = tmpStr.substring(0,pos)+cCmd[1]+tmpStr.substring(pos);
                listOfLines.set(line,tmpStr2);
                pos++;
                savePos = pos;

                completeLine();
                typed = true;
            }else if(Character.getNumericValue(cCmd[1])==-1){
                endOfCharacter = true;

                //ATTEMPTNG TO KEEP TO CONTENTION WITH EXIT CODES - https://tldp.org/LDP/abs/html/exitcodes.html
                // 130 = ctrl+c
                
                /*NOT IN CONVENTION
                5 = save
                
                */
                controlKeyed = true;//Assume that control exists, if switch defaults then it is set to false again.
                switch((byte) cCmd[1] ){

                    case 1: //ctrl+a
                        System.out.print("\n\n\n\n\n\n"+listOfLines);
                    case 3: //ctrl+c
                        return 130;
                    case 9: // Tab, ctrl+i
                        break;
                    case 13: //enter
                        clearScreenFromCurrentOnyNeeded(line,-1);
                        UI.moveCU();
                        listOfLines.add(line,"");
                        reprintScreen(line);
                        rewriteNumPress(2);
                        UI.moveBack(2);
                        break;
                    case 17: //ctrl+q
                        return 130;
                    case 18: //ctrl+r
                        reprintScreen();
                        //send back to 0/0
//        for(int i=startingFrom_; i<listOfLines.size(); i++){
//            UI.moveCU();
//        }
//        for(int i=0; i<pos;i++){//Moveback
//            System.out.print("\b");
//        }
//        pos=0;
//                        for(int i = 0; i < line; i++){//TODO: make more efficient with string
//                             UI.moveCD();
//                        }
//                        for(int i = 0; i < pos; i++){//TODO: make more efficient with string
//                            UI.moveCR();
//                        }
                        break;
                    case 19: //ctrl+s
                        // return 5;
                        UI.moveBack(2);//TODO: Merge?
                        completeLine(2);
                        writeFile();
                        break;
                    case 126: //del
                        break;
                    case 127: //Backspace
                        if(pos<1)
                            backspace(true);
                        else{
                            backspace(false);
                            pos--;}
                        savePos = pos;
                        break;
                    default:
                        controlKeyed = false;
                        break;
                }
            }

            if(pos < 0) pos =0;
            if(line< 0) line=0;

        
            
            
        }while(true);
    }

    private void reprintScreen(){
        UI.clear();
        reprintScreen(0);/*line=0*/;
        line=0;
        pos=0;
        System.out.print("\033[H");//Move to top left
    }
    private void reprintScreen(int startingFrom_){
//         printFile(startingFrom_);
//        String printLine="";
//        for(int i = startingFrom_; i < listOfLines.size(); i++){
//            printLine=listOfLines.get(i);
//            for(int j=0; j<listOfLines.get(i+(i>=listOfLines.size()-1?0:1)).length(); j++){
//            // for(int j=0; j<50; j++){
//                printLine+=" ";
//            }
//                UI.moveCL();
//            System.out.print(printLine+"\n");
//        }


//        System.out.print("\033[H");//Move to top left and clears screen
//        System.out.print("\033[2J");//Clears screen from current to top
//        printFile();
        int endingFrom = listOfLines.size();
        String printLine = "";
        for(int i = startingFrom_; i < endingFrom; i++){
            printLine+=listOfLines.get(i)+"\n";

            for(int j=0; j<listOfLines.get(i).length()-1; j++){
                printLine+=UI.WEST;
            }
        }
        System.out.print(printLine);
        for (int i = 0; i < endingFrom-startingFrom_; i++) {
            UI.moveCU();
        }
    }

    private void reprintScreenFromCurrent(){
//        reprintScreen(line,-1);
    }

    private void insertAndPrintLineAt(int startingFrom_, int endingFrom_){//@@@
        String printStr = "";
        for(int i = startingFrom_; i < listOfLines.size(); i++){
            for(int j=0; j<listOfLines.get(i).length(); j++){
                UI.moveCL();
            }
            for(int j=0; j<listOfLines.get(i).length(); j++){
                printStr+="#";
            }
            System.out.print(printStr);
            for(int j=0; j<listOfLines.get(i).length(); j++){
                UI.moveCL();
            }
//            System.out.print(printStr);
            printStr = "";
//            printStr += listOfLines.get(i);
            System.out.println(printStr);
            if(i==startingFrom_){
//                UI.moveCU();
            }
            printStr = "";

        }
//        System.out.println(printStr);

        for(int i=startingFrom_; i<listOfLines.size()-1; i++){
            UI.moveCU();
        }
        for(int i=0; i<pos;i++){//Move back
            UI.moveCR();
        }
    }


    private void clearScreenFromCurrentOnyNeeded(int startingFrom_, int endingFrom_){//TODO: Optimize
        String printStr = "";
        for(int i = startingFrom_; i < listOfLines.size(); i++){
            for(int j=0; j<listOfLines.get(i).length(); j++){
                UI.moveCL();
            }
            for(int j=0; j<listOfLines.get(i).length(); j++){
                printStr+=" ";
            }
            System.out.print(printStr);
            for(int j=0; j<listOfLines.get(i).length(); j++){
                UI.moveCL();
            }
//            System.out.print(printStr);
            printStr = "";
//            printStr += listOfLines.get(i);
            System.out.println(printStr);
//            if(i==startingFrom_){
//                UI.moveCU();
//            }
            printStr = "";

        }
//        System.out.println(printStr);

        for(int i=startingFrom_; i<listOfLines.size()-1; i++){
            UI.moveCU();
        }
        for(int i=0; i<pos;i++){//Move back
            UI.moveCR();
        }
    }

    private void clearLine(int lineNum_) {
        String printStr = "";
        String printStr2 = "";
        if(listOfLines.size()>lineNum_){
            for (int i = 0; i < listOfLines.get(lineNum_).length(); i++) {
                printStr += " ";
                printStr2 += "\b";
            }
            System.out.print(printStr + printStr2);
        }
    }
    private void moveToEndOfLine(){

    }

    private void completeLine(){
        completeLine(0,true);
    }
    private void completeLine(boolean moveBack){
        if(pos == listOfLines.get(line).length()){
            rewrite4Press();
        }else{
            //            completeline(listOfLines.get(line).length(), moveBack);
            System.out.print(listOfLines.get(line).substring(pos));
            while(pos<listOfLines.get(line).length() && moveBack){
                UI.moveCR(); //TODO: Make more efficient
                pos++;
            }
            pos=listOfLines.get(line).length();
        }
//        System.out.println("~~~~~~~~~~~~~~~~ = "+(pos == listOfLines.get(line).length()));
//        System.out.print(listOfLines.get(line).substring(listOfLines.get(line).length()-pos));
    }
    private void completeLine(final int addSpaces_){
        completeLine(addSpaces_,true);
    }
    private void completeLine(final int addSpaces_, final boolean moveBack){
        System.out.print(listOfLines.get(line).substring(pos));
        String backStr="";
        for(int i=0;i<addSpaces_;i++){backStr+=" ";}
        // for(int i=0; i<listOfLines.get(line).length()-pos+(addSpace_?1:0);i++){
        for(int i=0; i<listOfLines.get(line).length()-pos;i++){
            backStr+="\b";
        }
        // if(addSpace_)//TODO: Cleanup
        //     backStr+="\b\b";
        for(int i=0;i<addSpaces_ && moveBack;i++){backStr+="\b";}
        System.out.print(backStr);
    }

    private void backspace(boolean justRewrite_){
        if(justRewrite_){
            System.out.print("\b\b"+listOfLines.get(line).substring(0,(listOfLines.get(line).length()<2? listOfLines.get(line).length():2))+"\b\b");
            return;
        }
        System.out.print("\b\b\b");
        completeLine(3);
        String tmpStr = listOfLines.get(line);
        String tmpStr2 = tmpStr.substring(0,pos-1)+tmpStr.substring(pos);
        listOfLines.set(line,tmpStr2);
    }

    private void deleate(){
        // System.out.print("\b\b\b");
        // compleateLine(true);
        // String tmpStr = listOfLines.get(line);
        // String tmpStr2 = tmpStr.substring(0,pos)+tmpStr.substring(pos+1);
        // listOfLines.set(line,tmpStr2);
    }

    private void rewrite4Press(){rewriteNumPress(4);}

    private void rewriteNumPress(int spaces_){
        // if(line < 0) line = 0;
        // if(pos < 0) pos = 0;

        int end = pos+spaces_;
        String addsToEnd = "";//TODO: MAKE MORE EFFICENT
        
        int spacesNeeded = end - listOfLines.get(line).length();

        for (int i = 0; i < spacesNeeded; i++) {
            addsToEnd+=" ";
        }

        // if(listOfLines.get(line).length()-1 < end){
        //     end = listOfLines.get(line).length();
        //     for (int i = 0; i < end - (pos+4); i++) {
        //         addsToEnd.concat("~");
        //     }
        // }
        // System.out.print(listOfLines.get(line).substring(pos,end-spacesNeeded)+addsToEnd);
        // for(int i = 0; i < spacesNeeded; i++ ){
        //     pos--;
        // }

        if(end > listOfLines.get(line).length()-1){
            end = listOfLines.get(line).length();//@@@_LOOKHERE
        }

        System.out.print(listOfLines.get(line).substring(pos,end)+addsToEnd);
        // +"~~"+spacesNeeded+"~~");
                // System.out.print("\n\n\n\n\n\nSPACESNEEDED: ("+(end - listOfLines.get(line).length())+")");

        // for (int i = 0; i < 40; i++) {
        //     UI.moveCU();
        // }
        // for (int i = 0; i < 40; i++) {
            // UI.moveCL();
        // }

    }


    // SAVEFILE
    public void writeFile(){
        try {

            //DELEATE EVERYTHING IN FILE
            PrintWriter writer = new PrintWriter(filePath);
            writer.close();

            // File myObj = new File("filename.txt");
            // if (myObj.createNewFile()) {
            //     System.out.println("File created: " + myObj.getName());
            // } else {
            //     System.out.println("File already exists.");
            //     System.out.println("File Deleated");
            // }
            FileWriter myWriter = new FileWriter(filePath,true);
            
            for(int i=0;i<listOfLines.size()-1;i++){//Write to file each row by row. Skip the last line
                myWriter.write(listOfLines.get(i)+"\n");
            }
            try{
                myWriter.write(listOfLines.get(listOfLines.size()-1));//Instead of including in the loop, for efficiency place in last line by itself without the \n
                myWriter.close();
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Your file is empty");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during writeFile.");
            e.printStackTrace();
        }
    }

    public static void saveDebug(String str_){
        try{
        FileWriter myWriter = new FileWriter("Save.txt",true);
        BufferedWriter bw = new BufferedWriter(myWriter);


        bw.write(str_+"\n");
        bw.write(""+Character.getNumericValue(str_.charAt(0)));
        bw.close();
        }catch(Exception e){

        }
    }


}
