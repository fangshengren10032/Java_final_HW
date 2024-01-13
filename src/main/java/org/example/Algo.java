package org.example;

import java.util.*;

public class Algo {
    class qnode{
        /**
         * 优先队列节点，用于dijkstra算法
         */
        int idx; //节点index
        double dps;// dp数组的值

        public qnode(int idx, double dps) {
            this.idx = idx;
            this.dps = dps;
        }
    }
    List<Integer> keyPoints;  // 所有关键点


    //"Google Chart", "DocuSign Enterprise", "411Sync"
    Graph graph; //图结构
    Map<Integer, Node> nodeMap; //所有点的集合
    Map<Integer, Map<Integer,Double>> edges; //所有边的集合

    List<List<Integer>> keyPointsList;//每个category对应一个set
    List<Integer> api_ids;//关键路径中的所有的点

    long[] paths;

    static final int keyPointslen = 5;

    double minWeight;
    public Algo(String[] categories, Graph graph){
        minWeight = Double.MAX_VALUE;
        paths = new long[graph.getEdge_max_n()];

        Arrays.fill(paths, -1);


        this.graph = graph;
        nodeMap = graph.getNodes().getNodeMap();
        edges = graph.getEdges();
        keyPoints = new ArrayList<>();
        keyPointsList = new ArrayList<>();
        createKeyPointList(categories); //构建关键节点的列表

    }

    private void createKeyPointList(String[] categories){
        Nodes nodes = graph.getNodes();
        //根据categories找到所有相关的点，找到KeyPoints
        for(String category: categories){
            Set<Integer> nids = nodes.getNodeIdsSetByCategory(category);
            delUnUsedNodes(nids);
            Set<Integer> randSet = new HashSet<>();
            List<Integer> tmpList = new ArrayList<>(nids);
            int idx = 0;

            Random random = new Random();
            while(true){
                if(randSet.size() >= tmpList.size()){
                    break;
                }
                idx = random.nextInt(tmpList.size());
                randSet.add(tmpList.get(idx));
                if(randSet.size()>=keyPointslen){
                    break;
                }
            }
            keyPointsList.add(new ArrayList<>(randSet));
        }
    }

    private void delUnUsedNodes(Set<Integer> nids){
        List<Integer> removeIds = new ArrayList<>();
        //剪枝：如果没有边则不放到数组中
        for(int nid: nids){
            if(!edges.containsKey(nid)){
                removeIds.add(nid);
            }
        }
        for(int nid: removeIds){
            if(nids.contains(nid)){
                nids.remove(nid);
            }
        }
    }

    public List<Integer> calculateCombination(List<List<Integer>> inputList) {

        List<Integer> combination = new ArrayList<Integer>();
        int n = inputList.size();
        for (int i = 0; i < n; i++) {
            combination.add(0);
        }
        int i = 0;
        boolean isContinue=false;
        double res = Double.MAX_VALUE;
        int len = 0;
        do{
//            if(len % 20 == 0){
//                System.out.println(len);
//            }
            len ++; //计数器，测试用
            keyPoints = new ArrayList<>();
            //打印一次循环生成的组合
            for (int j = 0; j < n; j++) {

                keyPoints.add(inputList.get(j).get(combination.get(j)));
            }

            i++;

            combination.set(n-1, i);
            for (int j = n-1; j >= 0; j--) {
                if (combination.get(j)>=inputList.get(j).size()) {
                    combination.set(j, 0);
                    i=0;
                    if (j-1>=0) {
                        combination.set(j-1, combination.get(j-1)+1);
                    }
                }
            }
            isContinue=false;
            for (Integer integer : combination) {
                if (integer != 0) {
                    isContinue=true;
                }
            }

            double algo_res = this.algo();
            if(algo_res < res){
                res = algo_res;

            }

        }while (isContinue);
        minWeight = res;
        System.out.println("min res weight = " + res);
        return keyPoints;
    }
    public List<Integer> calculate(){
        System.out.println(keyPointsList.size());
        for(List<Integer> l: keyPointsList){
            for(int key: l){
                System.out.print(key + " ");
            }
            System.out.println();
        }

        return calculateCombination(keyPointsList);
    }

    public void printOnePath(){
        for(int i = 0; i < paths.length; i ++){
            System.out.print(paths[i] + " ");
        }
        System.out.println();
    }
    public void printPath(int n1, int n2){
        long index = n1;
        while(index != -1 && index != n2){
            System.out.print(index+"->");
            index = paths[(int)index];
        }
        if(index == n2){
            System.out.println(index);
        }else{
            System.out.println();
        }
    }
    //算法的输入应该是：点的数量，边，关键节点和关键节点个数
    public double algo(){
        int n = graph.getEdge_max_n();
        int k = keyPoints.size();

        double[][] dp = new double[n][(1 << k)+1]; // 存储状态压缩DP的结果，表示以i为根节点状态S时的最小权重和

        // 初始化距离和状态数组
        for (int i = 0; i < n; i++) {
            Arrays.fill(dp[i], Double.MAX_VALUE); // 初始化状态DP为最大值
        }

        // 记录最后一个关键点
        int y = -1;
        // 标记关键节点，并初始化状态DP值
        for (int i = 0; i < k; i++) {
            int x = keyPoints.get(i); //关键点
            dp[x][1 << i] = 0; // 以输入的关键点为根，二进制中对应关键节点位置为1的dp总权重为0
            y = x; // 记录最后一个关键节点
        }


        // 使用状态压缩DP求解最小路径和
        // 遍历从单个节点开始，直到满足所有关键节点都连同的所有可能状态
        for (int s = 1; s < 1 << k; s++) {
            // 遍历以节点i为根节点，状态为s时的情况
            for (int i = 0; i < n; i++) {
                // 遍历s二进制的所有子集，执行i的度数大于1时的操作
                for (int t = s & (s - 1); t > 0; t = (t - 1) & s) {
                    dp[i][s] = Math.min(dp[i][s], dp[i][t] + dp[i][s ^ t]);
                }
            }
            // 处理状态为s的情况下最短路径
            deal(s, n, dp);
        }
//        System.out.println(dp[y][(1 << k) - 1]); // 输出结果
        return dp[y][(1 << k) - 1];

    }
    // 处理状态s下的最短路径情况
    private void deal(int s, int n, double[][] dp) {
        // 建立优先队列，数组中索引值为1的数字越小，优先级越高
        PriorityQueue<qnode> pq = new PriorityQueue<>((o1, o2) -> Long.compare(o1.idx, o2.idx));
        boolean[] vis = new boolean[n]; // 记录已经访问的节点

        // 遍历所有节点，将所有满足当前状态的节点加入优先队列
        for (int i = 0; i < n; i++) {
            if (dp[i][s] != Double.MAX_VALUE) {
                pq.add(new qnode(i, dp[i][s]));
            }
        }

        // 使用Dijkstra算法求解最短路径
        // 即求解以每个节点为顶点，满足状态s（包含对应的点）的权重和的最小值
        while (!pq.isEmpty()) {
            qnode tmp = pq.poll();
            // 如果已经访问，则跳过
            if (vis[(int) tmp.idx]) {
                continue;
            }
            vis[(int) tmp.idx] = true;
            int midNode = -1;
            for (int i = 0; i < n; i++) {
                // 如果i为当前节点，或者i和当前节点不连通，则跳过
                // 不连通：g.get((int) tmp[0])中不含有 i
                if(i == tmp.idx || (!edges.containsKey((int) tmp.idx)) ||(!edges.get((int) tmp.idx).containsKey(i))){
                    continue;
                }
                if (tmp.dps + edges.get((int) tmp.idx).get(i) < dp[i][s]) {
                    //tmp[0]是mid节点
                    paths[(int)tmp.idx] = i;
                    midNode = (int)tmp.idx;
                    dp[i][s] = (tmp.dps + edges.get((int) tmp.idx).get(i));
                    pq.add(new qnode(i, dp[i][s]));
                }
                //上面的代码与下面等价
//                if (i == tmp[0] || pow[(int) tmp[0]][i] == Integer.MAX_VALUE) {
//                    continue;
//                }
//                if (tmp[1] + pow[(int) tmp[0]][i] < dp[i][s]) {
//                    dp[i][s] = (tmp[1] + pow[(int) tmp[0]][i]);
//                    pq.add(new long[]{i, dp[i][s]});
//                }
            }

        }


    }
//    public void calPath(int p1, int p2){
//        Set<Integer> s1 = new HashSet<>();
//        for(int i = 0; i < paths[p1].length; i ++){
//            if(paths[p1][i] != -1){
//                s1.add(i);
//            }
//        }
//        Set<Integer> s2 = new HashSet<>();
//        for(int i = 0; i < paths[p2].length; i ++){
//            if(paths[p1][i] != -1){
//                s2.add(i);
//            }
//        }
//
//        for(int tmp_s1:s1){
//            if(s2.contains(tmp_s1)){
//                System.out.println("out!");
//                break;
//            }
//        }
//    }
}
