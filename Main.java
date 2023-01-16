import java.awt.*;
import java.io.IOException;

class Main {

    public static void main(String[] args) {
        String toOpenFilePath = args.length>0 ? args[0] : "./Directory/exampleFile4.txt";
        System.out.print("\u001b[0;0H");// Move to local top left of the screen/TODO: MAKE IN UI
        UI.clear();
        Dimension terminalDimensions = UI.getTerminalDimensions("Press enter to open the file \""+toOpenFilePath+"\" or ctrl+c to cancel:");
        int terminalWidth = (int) terminalDimensions.getWidth();
        int terminalHeight = (int) terminalDimensions.getHeight();

        Edit curFil = openFile(toOpenFilePath, terminalWidth, terminalHeight);
        int exitCode;
        do{
            UI.clear();
            curFil.printFile();
            for (int i = 0; i < curFil.getListSize(); i++) {
                UI.moveCU();
            }
            exitCode = curFil.editFile();
            switch(exitCode){
                case 5: //Save
                    curFil.writeFile();//Save the file
                    break;
            }
        }while(exitCode != 1);//Save

    }

//    static private void getValues(){
//        while(true){
//            UI.getChrNumFromPress();
//        }
//    }

    private static Edit openFile(String fileLocation, int terminalWidth, int terminalHeight){
        Edit openedFile = new Edit(fileLocation, terminalWidth, terminalHeight);
        return openedFile;
    }

    private static int makeFile(){
        return 0;
    }

}
