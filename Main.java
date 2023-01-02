import java.io.IOException;

class Main {

    public static void main(String[] args) {

        try {
//            System.out.println(TerminalTest.getWidth());
//            System.out.println(TerminalTest.getTerminalWidth());
            int terminalWidth = (int) UI.getTerminalDimensions().getWidth();
            for (int i = 0; i < terminalWidth; i++) {
                System.out.print("_");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Edit curFil = openFile( args.length>0 ? args[0] : "./Directory/exampleFile4.txt");
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

    private static Edit openFile(String fileLocation){
        Edit openedFile = new Edit(fileLocation);
        return openedFile;
    }

    private static int makeFile(){
        return 0;
    }

}
