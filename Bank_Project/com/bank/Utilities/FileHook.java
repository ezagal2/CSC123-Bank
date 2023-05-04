package com.bank.Utilities;

import java.io.FileInputStream;
import java.io.InputStream;

public class FileHook extends CurrencyReader{

    @Override
    protected InputStream getInputStream()throws Exception {
        return new FileInputStream("exchange-rate.csv");
    }


}