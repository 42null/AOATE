import java.util.regex.Pattern;

import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;  // Delete file contents before saving

import static java.lang.Thread.sleep;

class Edit {

    private int line = 0, pos = 0;
    private String filePath;
    private ArrayList<String> listOfLines;
    
    
    public int getListSize(){return listOfLines.size();};

    public Edit(String filePath){
        this.filePath = filePath;
        listOfLines = Converters.fromFile(filePath);
    }

    public void printFile(){printFile(0);}
    public void printFile(int startingFrom){
        for(int i = startingFrom; i < listOfLines.size(); i++){
            System.out.print(listOfLines.get(i)+"\n");
        }
    }

    public int editFile(){
        char[] cCmd = {'\0','\0'};
        Pattern regPat = Pattern.compile("[a-z]|[A-Z]|[0-9]");

        int savePos = pos;
        boolean moveBack = true;

        do{

            cCmd[0] = cCmd[1];
            cCmd[1] = UI.getTypedChr(false);


            if(cCmd[0]=='['){
                if(cCmd[1]=='A'){//UP
                    UI.moveBack(4);
                    rewrite4Press();
                    if(line > 0){//Keep within page limits
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
                    }else{
                        while(pos > 0){//Move up on first line to move to front of the very start
                            pos--;
                            UI.moveCL();
                        }
                    }

                }else if(cCmd[1]=='B'){//DOWN
                    UI.moveBack(4);
                    rewrite4Press();
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
                        while(pos < listOfLines.get(line).length()){//Down on last line moves to end of last line
                            pos++;
                            UI.moveCR();
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
                            line--;//TODO://See if this is more efficient
                            pos = listOfLines.get(line).length();
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
            }else if(((cCmd[0]!='^' && cCmd[1]!='[') &&
                      (cCmd[0]!='[' && cCmd[1]!='[') &&
                      (cCmd[0]!='[' && cCmd[1]!='C') &&
                      (cCmd[1]!='\0') &&
                      (regPat.matcher(cCmd[1]+"").find()) ||
                       cCmd[1]==' ')){
                String tmpStr = listOfLines.get(line);
                
                String tmpStr2 = tmpStr.substring(0,pos)+cCmd[1]+tmpStr.substring(pos);
                listOfLines.set(line,tmpStr2);
                pos++;
                savePos = pos;

                completeLine();
            }else if(Character.getNumericValue(cCmd[1])==-1){

                //ATTEMPTNG TO KEEP TO CONTENTION WITH EXIT CODES - https://tldp.org/LDP/abs/html/exitcodes.html
                // 130 = ctrl+c
                
                /*NOT IN CONVENTION
                5 = save
                
                */
                switch((byte) cCmd[1] ){

                    case 1: //ctrl+a
                        System.out.print("\n\n\n\n\n\n"+listOfLines);
                    case 3: //ctrl+c
                        UI.clear();
                        return 1;
                    case 9: // Tab, ctrl+i
                        break;
                    case 13: //Enter
                        listOfLines.add(line+1,listOfLines.get(line).substring(pos));//New line from existing after pos where enter was hit
                        String tmpStore = listOfLines.get(line).substring(0,pos);//The rest of that line, stored so clearScreen can work with existing date

                        listOfLines.remove(line);

                        clearScreenFromCurrentOnlyNeeded(line,-1);
                        listOfLines.add(line,tmpStore);
                        String clearingMovement = ""; //Created as a string here to avoid calling System.out.print... repeatedly for efficiency
                        for(int i=0; i< (listOfLines.get(line+1).length())+2; i++){//TODO: Make more efficient with mutable
                            clearingMovement = " "+clearingMovement;
                            clearingMovement += UI.LEFT;
                        }
                        clearingMovement = UI.UP +clearingMovement;//Move up to where cursor was before enter
                        for(int i=0; i< (listOfLines.get(line+1).length())+2; i++){
                            clearingMovement += UI.LEFT;
                        }
                        clearingMovement += UI.DOWN;//Cursor also needs to move down, was already moved up to assist clearing the screen
                        for (int i = 0; i < pos+2; i++) {
                            clearingMovement += UI.LEFT;//Cursor moves back to pos 0 while also taking care of the extra 2 spaces for keycode
                        }
                        System.out.print(clearingMovement);//Applies above work to clear the line where it needs to be replaced and move to new printing position
                        reprintScreen(line);
                        UI.moveCD();//reprintScreen places where it was before enter, need to move back down
                        pos = 0;
                        savePos = 0;
                        line++;

                        break;
                    case 17: //ctrl+q
                        return 130;
                    case 18: //ctrl+r
                        reprintScreen();
                        //send back to 0/0
//        for(int i=startingFrom; i<listOfLines.size(); i++){
//            UI.moveCU();
//        }
//        for(int i=0; i<pos;i++){//Moveback
//            System.out.print("\b");
//        }
//        pos=0;
//                        for(int i=0; i < line; i++){//TODO: make more efficient with string
//                             UI.moveCD();
//                        }
//                        for(int i=0; i < pos; i++){//TODO: make more efficient with string
//                            UI.moveCR();
//                        }
                        break;
                    case 19: //ctrl+s
                        UI.moveBack(2);//TODO: Merge?
                        completeLine(2);
//                        writeFile();
                        return 5;
//                    break;
                    case 126: //del
                        break;
                    case 127: //Backspace
                        backspace(false);
                        savePos = pos;
                        break;
                    default:
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

    private void reprintScreen(int startingFrom){
        int endingFrom = listOfLines.size();
        String printLine = "";
        for(int i = startingFrom; i < endingFrom; i++){
            printLine+=listOfLines.get(i)+"\n";

            for(int j=0; j<listOfLines.get(i).length()-1; j++){
                printLine+=UI.LEFT;
            }
        }
        System.out.print(printLine);
        for(int i=0; i < endingFrom-startingFrom; i++){
            UI.moveCU();
        }
    }

    private void clearScreenFromCurrentOnlyNeeded(int startingFrom, int endingFrom){//TODO: Optimize
        String printStr = "";
        for(int i = startingFrom; i < listOfLines.size(); i++){
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
            printStr = "";
            System.out.println(printStr);
            printStr = "";

        }
//        System.out.println(printStr);

        for(int i=startingFrom; i<listOfLines.size()-1; i++){
            UI.moveCU();
        }
        for(int i=0; i<pos;i++){//Move back
            UI.moveCR();
        }
    }

    private void clearLine(int lineNum){
        String printStr = "";
        String printStr2 = "";
        if(listOfLines.size()>lineNum){
            for(int i=0; i < listOfLines.get(lineNum).length(); i++){
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
    }
    private void completeLine(final int addSpaces){
        completeLine(addSpaces,true);
    }
    private void completeLine(final int addSpaces, final boolean moveBack){
        System.out.print(listOfLines.get(line).substring(pos));
        String backStr="";
        for(int i=0;i<addSpaces;i++){backStr+=" ";}
        for(int i=0; i<listOfLines.get(line).length()-pos;i++){
            backStr+="\b";
        }
        for(int i=0;i<addSpaces && moveBack;i++){backStr+=UI.LEFT;}
        System.out.print(backStr);
    }

    private void backspace(boolean justRewrite){
//        if(pos < 1){//Should never be less than 0 but just in case
//            System.out.print("\b\b"+listOfLines.get(line).substring(0,(listOfLines.get(line).length()<2? listOfLines.get(line).length():2))+"\b\b");
//            return;
//        }
        System.out.print("\b\b\b");
        completeLine(3);
        String tmpStr = listOfLines.get(line);
        if(pos < 1){
            if(line <1){
                return;
            }
            pos = listOfLines.get(line-1).length();//In case of very long lines?
            listOfLines.set(line,listOfLines.get(line-1)+tmpStr);
            line--;
            listOfLines.remove(line);
            clearScreenFromCurrentOnlyNeeded(line,-1);
            UI.moveCU(2);
            UI.moveCL(pos);
            reprintScreen(line);
            UI.moveCR(pos);
            pos++;//TODO: Figure out if this is nessery with switch above to length-1
        }else{
            String tmpStr2 = tmpStr.substring(0,pos-1)+tmpStr.substring(pos);
            listOfLines.set(line,tmpStr2);
        }
        pos--;
    }

    private void deleate(){
        // System.out.print("\b\b\b");
        // compleateLine(true);
        // String tmpStr = listOfLines.get(line);
        // String tmpStr2 = tmpStr.substring(0,pos)+tmpStr.substring(pos+1);
        // listOfLines.set(line,tmpStr2);
    }

    private void rewrite4Press(){rewriteNumPress(4);}

    private void rewriteNumPress(int spaces){
//         if(line < 0) line = 0;
//         if(pos < 0) pos = 0;

        int end = pos+spaces;
        String addsToEnd = "";//TODO: MAKE MORE EFFICENT
        
        int spacesNeeded = end - listOfLines.get(line).length();

        for(int i=0; i < spacesNeeded; i++){
            addsToEnd+=" ";
        }

        if(end > listOfLines.get(line).length()-1){
            end = listOfLines.get(line).length();//@@@_LOOKHERE
        }

        System.out.print(listOfLines.get(line).substring(pos, Math.max(end, pos))+addsToEnd);

    }


    public void writeFile(){
        try {

            //Delete the contents of the current file //TODO: CreateBackup and check for proper saving





            PrintWriter writer = new PrintWriter(filePath);
            writer.close();

            // File myObj = new File("filename.txt");
            // if (myObj.createNewFile()){
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
        } catch (IOException e){
            System.out.println("An error occurred during writeFile.");
            e.printStackTrace();
        }
    }

    public static void saveDebug(String str){
        try{
        FileWriter myWriter = new FileWriter("Save.txt",true);
        BufferedWriter bw = new BufferedWriter(myWriter);


        bw.write(str+"\n");
        bw.write(""+Character.getNumericValue(str.charAt(0)));
        bw.close();
        }catch(Exception e){

        }
    }


}