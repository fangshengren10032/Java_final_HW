package org.example;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class CSVrw {
    public static void read_mashup(String readFile, String outFile){
        //读取 id, apiName, Primary Category
        CsvReader csvReader = null;
        CsvWriter csvWriter = null;
        try {
            csvReader = new CsvReader(readFile, ',', Charset.forName("UTF-8"));
            csvWriter = new CsvWriter(outFile, ',', Charset.forName("UTF-8"));
            String[]  headers ={"Name", "API"};
            csvWriter.writeRecord(headers);
            // 读表头
            csvReader.readHeaders();

            // 读取每行的内容
            while (csvReader.readRecord()) {

                // 获取内容的两种方式11

                String name = csvReader.get("Name");
//                String[] apis = csvReader.get("Related APIs").split(",");
                String api = csvReader.get("Related APIs");
                String[] record = new String[2];
                record[0] = name;
                record[1] = api;
                csvWriter.writeRecord(record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(csvReader != null){
                csvReader.close();
            }
            if(csvWriter != null){
                csvWriter.close();
            }

        }

    }
    public static void read1(String readFile, String outFile)  {
        //读取 id, apiName, Primary Category
        CsvReader csvReader = null;
        CsvWriter csvWriter = null;
        try {
            csvReader = new CsvReader(readFile, ',', Charset.forName("UTF-8"));
            csvWriter = new CsvWriter(outFile, ',', Charset.forName("UTF-8"));
            String[]  headers ={"", "Name", "Primary Category"};
            try {
                csvWriter.writeRecord(headers);
                csvReader.readHeaders();
                while (csvReader.readRecord()) {

                    String id = csvReader.get("");
                    System.out.println("id: " + id);
                    String name = csvReader.get("Name");
                    String priCategory = csvReader.get("Primary Category");
                    String[] newStr = new String[]{id, name, priCategory};
                    csvWriter.writeRecord(newStr);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            if(csvReader != null){
                csvReader.close();
            }
            if(csvWriter != null){
                csvWriter.close();
            }

        }


    }
    public static void read2(String readFile, String outFile){
        //读取 id, apiName, Primary Category, Secondary Category
        CsvReader csvReader = null;
        CsvWriter csvWriter = null;
        try {
            csvReader = new CsvReader(readFile, ',', Charset.forName("UTF-8"));
            csvWriter = new CsvWriter(outFile, ',', Charset.forName("UTF-8"));
            String[]  headers ={"", "Name", "Primary Category", "Secondary Categories"};
            try {
                csvWriter.writeRecord(headers);
                csvReader.readHeaders();
                while (csvReader.readRecord()) {

                    String id = csvReader.get("");
                    System.out.println("id: " + id);
                    String name = csvReader.get("Name");
                    String priCategory = csvReader.get("Primary Category");
                    String secCategory = csvReader.get("Secondary Categories");
                    String[] newStr = new String[]{id, name, priCategory, secCategory};
                    csvWriter.writeRecord(newStr);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            if(csvReader != null){
                csvReader.close();
            }
            if(csvWriter != null){
                csvWriter.close();
            }

        }
    }
    public static void writeEdge(){
        String path = CSVrw.class.getClassLoader().getResource("").getPath();
        String apiFile = path + "api_out_1.csv";
        String mashupFile = path + "mashup_out_2.csv";

        Graph g = new Graph(apiFile, mashupFile);
        Map<Integer, Map<Integer,Double>> edges =  g.getEdges();
        CsvWriter csvWriter = null;
        String outpath1 = path + "edges2.csv";
        csvWriter = new CsvWriter(outpath1, ',', Charset.forName("UTF-8"));

        for(Map.Entry<Integer, Map<Integer, Double>> entry: edges.entrySet()){
            Map<Integer, Double> map = entry.getValue();
            for(Map.Entry<Integer, Double> entry1: map.entrySet()){
//                System.out.println("i = " + entry.getKey() + ", j = " + entry1.getKey() + ", length = " + entry1.getValue());
                try {
                    String[] record = {entry.getKey() + "", entry1.getKey() + "", entry1.getValue() + ""};
                    csvWriter.writeRecord(record);
//                    csvWriter.write(entry.getKey() + "," + entry1.getKey() + "," + entry1.getValue() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        csvWriter.close();
    }
    public static void main(String[] args) throws IOException {
//        String path = CSVrw.class.getClassLoader().getResource("").getPath();//注意getResource("")里面是空字符串
//        String filepath = path + "api.csv";
//        String outpath1 = path + "api_out_1.csv";
//        String outpath2 = path + "api_out_2.csv";
//
//        String mashpu_path = path + "mashup.csv";
//        read1(filepath, outpath1);
//        String outpath3 = path + "mashup_out_2.csv";
//        read_mashup(mashpu_path, outpath3);
        writeEdge();

    }
}
