package com.bank.Utilities;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Configuration {
    private static final HashMap<String, String> configFileContents = new HashMap<>();
    private static String configFile = "";
    static {
        try{
            File file = new File("config.txt");
            FileReader in = new FileReader(file);

            BufferedReader br = new BufferedReader(in);
            String currentLine;
            while ((currentLine = br.readLine()) != null){
                configFile += currentLine + "\n";
            }
            parseToHashMap();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private Configuration() {

    }

    private static void parseToHashMap(){
        String[] tempConfigFileContents = configFile.split("\n");
        for (String line : tempConfigFileContents) {
            String[] parts = line.split("=");
            String key = parts[0].trim();
            String value = parts[1].trim();
            configFileContents.put(key, value);
        }
    }

    public static boolean supportCurrencies(){

        return Boolean.parseBoolean(configFileContents.get("support.currencies"));
    }

    protected static String getCurrencySource(){
        return (String) configFileContents.get("currencies.source");
    }

}
