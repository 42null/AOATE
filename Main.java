class Main {

    public static void main(String[] args) {
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

    static private void getValues(){
        while(true){
            UI.getChrNumFromPress();
        }
    }

    private static Edit openFile(String fileLocation){
        Edit openedFile = new Edit(fileLocation);
        return openedFile;
    }

    private static int makeFile(){
        return 0;
    }

}
