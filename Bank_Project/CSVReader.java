import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CSVReader implements Serializable {
    private static File file = new File("exchange-rate.csv");

    private CSVReader(String fileName){
        file = new File(fileName);
    }
    public static void setCSVReaderFile(String fileName){
        new CSVReader(fileName);
    }
    private static ArrayList<String[]> read() throws FileNotFoundException {
        ArrayList<String[]> CSVFile = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] fields = line.split(",");
            CSVFile.add(fields);
        }
        return CSVFile;
    }
    public static boolean exist(){ return file.exists(); }
    public static HashMap<String, String> readCSVFile() throws FileNotFoundException {
        ArrayList<String[]> file;
        file = read();
        HashMap<String, String> currencyCode = new HashMap<>();
        file.remove(0);

        for (String[] line: file) {
            currencyCode.put(line[0], line[1] + "," + line[2]);
        }
        return currencyCode;
    }
}
