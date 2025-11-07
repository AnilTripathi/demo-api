package com.myhealth.smoke.util;

import io.cucumber.datatable.DataTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTableUtil {
    
    public static Map<String, String> toMap(DataTable dataTable) {
        Map<String, String> result = new HashMap<>();
        List<List<String>> rows = dataTable.cells();
        
        for (List<String> row : rows) {
            if (row.size() >= 2) {
                result.put(row.get(0), row.get(1));
            }
        }
        
        return result;
    }
}