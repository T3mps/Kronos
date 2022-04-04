package net.acidfrog.kronos.core.io.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class CSVParser {
    
    public static final String DELIMITER = ",";

    public static List<String> parse(String csv) {
        return parse(csv, DELIMITER);
    }

    public static List<String> parse(String csv, String delimiter) {
        List<String> result = new ArrayList<String>();

        try(BufferedReader csvReader = new BufferedReader(new StringReader(csv))) {
            String row;
            while((row = csvReader.readLine()) != null) {
                String[] data = row.split(delimiter);
                for (String s : data) result.add(s);
            }
            csvReader.close();
        } catch(IOException e) {
            Logger.logError("Error parsing CSV data: " + csv);
        }

        return result;
    }
    
}
