package com.bank.Utilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CSVReader implements Serializable {
    private static File file = new File("exchange-rate.csv");
    private static URL url;

    static {
        try {
            url = new URL("htllltp://www.usman.cloud/banking/exchange-rate.csv");
        } catch (MalformedURLException ignored) {}
    }

    private CSVReader(String fileName){
        file = new File(fileName);
    }
    public static void setCSVReaderFile(String fileName){
        new CSVReader(fileName);
    }
    private static ArrayList<String[]> read() throws FileNotFoundException {
        ArrayList<String[]> CSVFile = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String[] lines = response.body().split("\n");
            for (String line : lines) {
                String[] fields = line.split(",");
                CSVFile.add(fields);
            }
        }catch (URISyntaxException | InterruptedException | IOException | NullPointerException e) {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                CSVFile.add(fields);
            }
        }
        return CSVFile;
    }

    public static boolean exist() {
            return file.exists();
    }
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
