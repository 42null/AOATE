import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
// import java.io.FileWriter;   // Import the FileWriter class
import java.util.Scanner; // Import the Scanner class to read text files


class Converters{

    public static ArrayList<String> fromFile(String filepath/*, String fileName_*/){

        ArrayList<String> returnList = new ArrayList<String>();

        try{
            File textFile = new File("./"+filepath);
            Scanner myReader = new Scanner(textFile);

            String deconstruct;
            do{//ByRow
                deconstruct = myReader.nextLine();
                returnList.add(deconstruct);
            }while(myReader.hasNext());

            myReader.close();
        }catch(Exception e){System.out.println("Error with Creation file :"+e);}
        return returnList;
    }







}