package com.bank.Utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class CurrencyReader{
    public static CurrencyReader getInstance(String type) throws Exception{
        if(type.equalsIgnoreCase("file")) {
            return new FileHook();
        }
        else if(type.equalsIgnoreCase("webservice")) {
            return new HTTPHook();
        }
        else {
            throw new Exception("Type "+type+ " not understood!");
        }
    }

    private ArrayList<String> readCurrencies() throws Exception{
        //get an input steam
        InputStream in=getInputStream();
        //Create stream readers / buffered reader
        BufferedReader br=new BufferedReader(new InputStreamReader(in));

        ArrayList<String> list=new ArrayList<String>();

        String line=null;
        //read lines
        while((line=br.readLine())!=null) {
            //add lines to arraylist
            list.add(line);
        }

        //return array list

        return list;

    }

    public static boolean isUsable() {
        return Configuration.supportCurrencies();
    }
    public static HashMap<String, String> readCSVFile() throws Exception {
        ArrayList<String> file;
        file = getInstance(Configuration.getCurrencySource()).readCurrencies();
        HashMap<String, String> currencyCode = new HashMap<>();


        for (String line: file) {
            if (line.contains(",") && !line.contains(":")){
                if (line.contains("Silver")){
                    currencyCode.put("XAG", "Silver (troy ounce,22.2833)");
                }else {
                    currencyCode.put(line.substring(0, 3), line.substring(4));
                }
            }
        }

        return currencyCode;
    }

    protected abstract InputStream getInputStream() throws Exception;


}

