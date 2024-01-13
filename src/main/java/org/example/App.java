package org.example;

import java.io.IOException;
import java.lang.management.MemoryType;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void test(){
        String path = CSVrw.class.getClassLoader().getResource("").getPath();
        String apiFile = path + "api_out_1.csv";
        String mashupFile = path + "mashup_out_2.csv";
        Graph g = new Graph(apiFile, mashupFile);
        Map<Integer, Map<Integer,Double>> edges =  g.getEdges();
        int a = 0;
        int b = 0;
        int c = 0;
        Set<Integer> s = new HashSet<>();
        for(Map.Entry<Integer, Map<Integer, Double>> entry: edges.entrySet()){
            Map<Integer, Double> map = entry.getValue();
            for(Map.Entry<Integer, Double> entry1: map.entrySet()){
//                System.out.println("i = " + entry.getKey() + ", j = " + entry1.getKey() + ", length = " + entry1.getValue());

                s.add(entry.getKey());
                s.add(entry1.getKey());
            }

        }

        for(int i: s){
            if(i < 6000){
                a = Math.max(a, i);
            }

            if(i > 1000){
                b ++;
            }
            c++;
        }
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

    }


    public static void main( String[] args ) throws IOException {
        String path = CSVrw.class.getClassLoader().getResource("").getPath();
        String apiFile = path + "api_out_1.csv";
        String mashupFile = path + "mashup_out_2.csv";
        Graph g = new Graph(apiFile, mashupFile);
//        String[] s = new String[]{"Social", "Photos", "Video"};
        String[] s = new String[]{"Social", "Photos"};
        Nodes nodes = g.getNodes();

        Map<String, Set<Integer>> cate2ids = nodes.getCate2id();
        Set<Integer> nids = nodes.getNodeIdsSetByCategory("Blogging");


        Algo algo = new Algo(s, g);
        List<Integer> keyPoints = algo.calculate();
        Map<Integer, Node> nodemap = nodes.getNodeMap();
        for(int i: keyPoints){
            System.out.print(i + ": ");
            System.out.println(nodemap.get(i));
        }
        algo.printOnePath();
        algo.printPath(keyPoints.get(0), keyPoints.get(1));
        algo.printPath(keyPoints.get(1), keyPoints.get(0));
    }
}
