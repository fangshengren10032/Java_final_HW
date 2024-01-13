package org.example;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

class Node{
    /**API 名字*/
    public int id;// csv文件中的id

    public String name;// csv文件中的name
    public String priCategory;// primary category
    public Node() {
        this.id = -1;
        this.name = "";
        this.priCategory = "";
    }
    public Node(int id, String name, String priCategory) {
        this.id = id;
        this.name = name;
        this.priCategory = priCategory;
    }
    @Override
    public String toString(){
        return "[id]: " + id
                + "; [name]: " + name
                + "; [Primary Category]: " + priCategory
                + "\n";
    }
}

class Nodes{
    /**
     * 所有Node的集合，采用Map的形式存储，同时也包括了name->id的反向映射
     * */
    private Map<Integer, Node> nodeMap; //所有node的集合

    private Map<String, Integer> name2id; //name->id的映射

    private Map<String, Set<Integer>> cate2id;//TODO 一个 category 的 所有 api 的id，



    public Nodes(String apiFile){
        initName2id_list(apiFile);
    }

    private void initName2id_list(String apiFile){
        nodeMap = new HashMap<>(); // 存储所有node
        name2id = new HashMap<>(); // 存储所有name->id 对
        cate2id = new HashMap<>(); // 存储 有 该priority category 的 所有 node id

        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(apiFile, ',', Charset.forName("UTF-8"));
            String[]  headers ={"", "Name", "Primary Category"};

            csvReader.readHeaders();
            while (csvReader.readRecord()){
                String id = csvReader.get("");
                String name = csvReader.get("Name");
                String priCategory = csvReader.get("Primary Category");

                this.name2id.put(name, Integer.valueOf(id));

                nodeMap.put(Integer.parseInt(id), new Node(Integer.parseInt(id), name, priCategory)); // 存储所有node
                if(this.cate2id.containsKey(priCategory)){
                    Set<Integer> l = this.cate2id.get(priCategory);
                    if(Integer.valueOf(id) > 2000){
                        continue;
                    }
                    l.add(Integer.valueOf(id));
                    this.cate2id.put(priCategory, l);
                }else{
                    Set<Integer> l = new HashSet<>();
                    if(Integer.valueOf(id) > 2000){
                        continue;
                    }
                    l.add(Integer.valueOf(id));
                    this.cate2id.put(priCategory, l);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(csvReader != null){
                csvReader.close();
            }
        }
    }

    public Map<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    public Map<String, Integer> getName2id() {
        return name2id;
    }

    public Map<String, Set<Integer>> getCate2id() {
        return cate2id;
    }
    public Set<Integer> getNodeIdsSetByCategory(String priCategory){
        return cate2id.get(priCategory);
    }

}


public class Graph {
    private Nodes nodes; //存储了所有api和 api-name对应关系
    private int node_n;  //

    private int edge_max_n = 2000; //TODO 目前已知：除了一个6786， 在图上的点的坐标最大为1858，所以设最大1900
    private Map<Integer, Map<Integer,Double>> edges;

    public Graph(String apiFile,String mashupFile){
        nodes = new Nodes(apiFile);

        node_n = 0;

        edges = new HashMap<Integer,Map<Integer, Double>>();


        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(mashupFile, ',', Charset.forName("UTF-8"));
            String[]  headers ={"Name", "API"};
            csvReader.readHeaders();
            while(csvReader.readRecord()){
                node_n ++ ;
                String name = csvReader.get("Name");
                String[] apis = csvReader.get("API").split(",");
                addEdges(apis);
            }
            calculateWeight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(csvReader != null){
                csvReader.close();
            }
        }

    }

    private void calculateWeight(){
        //计算权重，1，取倒数 2， 用一个大数字减去当前边长
        for(Map.Entry<Integer, Map<Integer, Double>> entry: edges.entrySet()){
            Map<Integer, Double> map = entry.getValue();
            for(Map.Entry<Integer, Double> entry1: map.entrySet()){
                map.put(entry1.getKey(), 1/entry1.getValue());
            }
            edges.put(entry.getKey(), map);

        }
    }
    private void addEdges(String[] apis){
        for(int i = 0; i < apis.length; i ++){
            String api = apis[i];
            if(!nodes.getName2id().containsKey(api)){
                continue;
            }

            int id1 = nodes.getName2id().get(api);
            for(int j = i + 1; j < apis.length; j ++){
                String api2 = apis[j];
                if(api2.equals(api)){
                    continue;
                }
                if(!nodes.getName2id().containsKey(api2)){
                    continue;
                }
                int id2 = nodes.getName2id().get(api2);


                if(edges.containsKey(id1)){
                    if(edges.get(id1).containsKey(id2)){
                        double oldN =edges.get(id1).get(id2);
                        edges.get(id1).put(id2, oldN + 1);
                    }else{
                        edges.get(id1).put(id2, 1.0);
                    }
                }else{
                    Map<Integer, Double> m = new HashMap<>();
                    m.put(id2, 1.0);
                    edges.put(id1, m);
                }
                if(edges.containsKey(id2)){
                    if(edges.get(id2).containsKey(id1)){
                        double oldN =edges.get(id2).get(id1);
                        edges.get(id2).put(id1, oldN + 1);
                    }else{
                        edges.get(id2).put(id1, 1.0);
                    }
                }else{
                    Map<Integer, Double> m = new HashMap<>();
                    m.put(id1, 1.0);
                    edges.put(id2, m);
                }

            }
        }
    }
    public Nodes getNodes() {
        return nodes;
    }

    public int getEdge_max_n() {
        return edge_max_n;
    }

    public int getNode_n() {
        return node_n;
    }

    public Map<Integer, Map<Integer, Double>> getEdges() {
        return edges;
    }
}
