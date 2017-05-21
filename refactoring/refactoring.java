import java.io.*;
import java.util.HashMap;
import java.util.Map;

interface DataConnection {
    int loadDatas(int year) throws IOException;
    void saveData(int year, int value) throws IOException;
}

public class MyApp implements DataConnection {
    private String inputFile;
    private String outputFile;
    private int inputLinesNumber = 0;
    private int outputLinesNumber = 0;
    private Map<Integer, Integer> sumByYear;

    public MyApp(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public int loadDatas(int year) throws IOException {
        if (sumByYear == null) {
            inputLinesNumber = readFile();
        }

        return sumByYear.getOrDefault(year, 0);
    }

    private int readFile() throws IOException {
        sumByYear = new HashMap<>();
        int numberOfLines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split("\t");
            if (tokens.length > 3) {
                int year = Integer.parseInt(tokens[2]);
                int value = Integer.parseInt(tokens[3]);
                sumByYear.put(year, sumByYear.getOrDefault(year, 0) + value);
            }
            numberOfLines++;
            reader.readLine();
        }

        return numberOfLines;
    }

    public void saveData(int year, int value) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(new File(outputFile), true);
        String s = String.format("%d\t%d\t%d\n", outputLinesNumber, year, value);
        outputStream.write(s.getBytes());
        outputLinesNumber++;
    }

    public int getInputLinesNumber() {
        return inputLinesNumber;
    }
}

public class Main {
    public static final String APP_VERSION = "app v.1.13";

    public static void main(String[] args) {
        int startYear = 1990;
        int endYear = 2020;
        MyApp myApp = new MyApp("1.txt", "statistika.txt");

        System.out.println(APP_VERSION);
        try {
            for (int year = startYear; year < endYear; year++) {
                int sumByYear = myApp.loadDatas(year);
                double aggregatedValue = myApp.getInputLinesNumber() > 0 ? (double)sumByYear / myApp.getInputLinesNumber() : 0.0;
                if (aggregatedValue > 0) {
                    System.out.println(String.format("%d %.3f", year, aggregatedValue));
                }
                myApp.saveData(year, (int)aggregatedValue);
            }
            System.out.println("Done!");
        } catch (IOException e) {
            System.out.println(String.format("Error: %s", e.getMessage()));
        }
    }
}
