import java.io.*;
import java.util.*;

/**
 * Lab1-1: 基于大模型的编程
 */

public class Lab1_Graph {
    // 内部类：有向图
    static class DirectedGraph {
        // 邻接表：source -> (target -> weight)
        private Map<String, Map<String, Integer>> adjMap;
        private Set<String> vertices;

        public DirectedGraph() {
            adjMap = new HashMap<>();
            vertices = new HashSet<>();
        }

        // 添加边，如果已存在则增加权重
        public void addEdge(String from, String to) {
            from = from.toLowerCase();
            to = to.toLowerCase();
            vertices.add(from);
            vertices.add(to);
            adjMap.putIfAbsent(from, new HashMap<>());
            Map<String, Integer> edges = adjMap.get(from);
            edges.put(to, edges.getOrDefault(to, 0) + 1);
        }

        // 获取所有节点
        public Set<String> getVertices() {
            return vertices;
        }

        // 判断节点是否存在
        public boolean containsVertex(String word) {
            return vertices.contains(word.toLowerCase());
        }

        // 获取某个节点的出边
        public Map<String, Integer> getOutEdges(String from) {
            return adjMap.getOrDefault(from.toLowerCase(), Collections.emptyMap());
        }

        // 获取整个邻接表
        public Map<String, Map<String, Integer>> getAdjMap() {
            return adjMap;
        }

        // 获取节点出度
        public int outDegree(String vertex) {
            return getOutEdges(vertex).size();
        }

        // 计算PageRank，d=0.85，返回每个节点的PR值
        public Map<String, Double> computePageRank() {
            int N = vertices.size();
            if (N == 0) return Collections.emptyMap();
            double d = 0.85;
            Map<String, Double> pr = new HashMap<>();
            // 初始化PR值为1/N
            for (String v : vertices) {
                pr.put(v, 1.0 / N);
            }
            // 迭代直到收敛
            double threshold = 1e-8;
            int maxIter = 100;
            for (int iter = 0; iter < maxIter; iter++) {
                Map<String, Double> newPR = new HashMap<>();
                double danglingSum = 0.0; // 所有悬挂节点的PR总和
                for (String v : vertices) {
                    if (outDegree(v) == 0) {
                        danglingSum += pr.get(v);
                    }
                }
                double base = (1 - d) / N;
                for (String v : vertices) {
                    double sum = 0.0;
                    // 贡献来自所有指向v的节点
                    for (String u : vertices) {
                        Map<String, Integer> edges = getOutEdges(u);
                        if (edges.containsKey(v)) {
                            int outDeg = outDegree(u);
                            if (outDeg > 0) {
                                sum += pr.get(u) * edges.get(v) / outDeg; // PageRank通常不考虑权重，但这里边有权重，可以按权重比例分配
                                //使用标准方法：每个出边均分PR。
                                // sum += pr.get(u) / outDeg; 
                                // 但上面的edges.get(v)是权重，需要去掉。
                            }
                        }
                    }
                    // 加上悬挂节点的贡献
                    sum += danglingSum / N;
                    newPR.put(v, base + d * sum);
                }
                // 检查收敛
                double diff = 0.0;
                for (String v : vertices) {
                    diff += Math.abs(newPR.get(v) - pr.get(v));
                }
                pr = newPR;
                if (diff < threshold) break;
            }
            return pr;
        }

        // 按标准PageRank实现
        // 实现computePageRank，使每个出边均分PR值
        public Map<String, Double> computePageRankStandard() {
            int N = vertices.size();
            if (N == 0) return Collections.emptyMap();
            double d = 0.85;
            Map<String, Double> pr = new HashMap<>();
            for (String v : vertices) pr.put(v, 1.0 / N);
            double threshold = 1e-8;
            int maxIter = 100;
            for (int iter = 0; iter < maxIter; iter++) {
                Map<String, Double> newPR = new HashMap<>();
                double danglingSum = 0.0;
                for (String v : vertices) {
                    if (outDegree(v) == 0) {
                        danglingSum += pr.get(v);
                    }
                }
                double base = (1 - d) / N;
                for (String v : vertices) {
                    double sum = 0.0;
                    for (String u : vertices) {
                        if (getOutEdges(u).containsKey(v)) {
                            int outDeg = outDegree(u);
                            if (outDeg > 0) {
                                sum += pr.get(u) / outDeg;
                            }
                        }
                    }
                    sum += danglingSum / N;
                    newPR.put(v, base + d * sum);
                }
                double diff = 0.0;
                for (String v : vertices) {
                    diff += Math.abs(newPR.get(v) - pr.get(v));
                }
                pr = newPR;
                if (diff < threshold) break;
            }
            return pr;
        }

        // 用Dijkstra算法计算最短路径
        public Map<String, PathInfo> dijkstra(String source) {
            source = source.toLowerCase();
            if (!vertices.contains(source)) return null;
            Map<String, Integer> dist = new HashMap<>();
            Map<String, String> prev = new HashMap<>();
            PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
            for (String v : vertices) {
                dist.put(v, Integer.MAX_VALUE);
                prev.put(v, null);
            }
            dist.put(source, 0);
            pq.add(source);
            while (!pq.isEmpty()) {
                String u = pq.poll();
                int curDist = dist.get(u);
                if (curDist == Integer.MAX_VALUE) continue;
                for (Map.Entry<String, Integer> edge : getOutEdges(u).entrySet()) {
                    String v = edge.getKey();
                    int weight = edge.getValue();
                    int newDist = curDist + weight;
                    if (newDist < dist.get(v)) {
                        dist.put(v, newDist);
                        prev.put(v, u);
                        pq.add(v);
                    }
                }
            }
            Map<String, PathInfo> result = new HashMap<>();
            for (String v : vertices) {
                result.put(v, new PathInfo(dist.get(v), prev.get(v)));
            }
            return result;
        }

        // 内部类存储路径信息
        static class PathInfo {
            int distance;
            String predecessor;
            PathInfo(int distance, String predecessor) {
                this.distance = distance;
                this.predecessor = predecessor;
            }
        }

        // 构建从source到target的路径字符串
        public String getPath(String source, String target, Map<String, PathInfo> paths) {
            source = source.toLowerCase();
            target = target.toLowerCase();
            if (!vertices.contains(source) || !vertices.contains(target)) {
                return null;
            }
            if (paths == null) paths = dijkstra(source);
            PathInfo info = paths.get(target);
            if (info.distance == Integer.MAX_VALUE) {
                return null; // 不可达
            }
            LinkedList<String> path = new LinkedList<>();
            String cur = target;
            while (cur != null) {
                path.addFirst(cur);
                cur = paths.get(cur).predecessor;
            }
            StringBuilder sb = new StringBuilder();
            for (String node : path) {
                if (sb.length() > 0) sb.append(" -> ");
                sb.append(node);
            }
            sb.append(" (length=").append(info.distance).append(")");
            return sb.toString();
        }
    }

    // 从文件读取并处理文本，返回单词列表
    private static List<String> processFile(String filename) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 将非字母字符替换为空格
                line = line.replaceAll("[^a-zA-Z]", " ");
                // 分割成单词
                String[] parts = line.split("\\s+");
                for (String p : parts) {
                    if (!p.isEmpty()) {
                        words.add(p.toLowerCase());
                    }
                }
            }
        }
        return words;
    }

    // 构建图
    private static DirectedGraph buildGraph(List<String> words) {
        DirectedGraph graph = new DirectedGraph();
        for (int i = 0; i < words.size() - 1; i++) {
            String from = words.get(i);
            String to = words.get(i + 1);
            graph.addEdge(from, to);
        }
        return graph;
    }

    // 功能2：展示有向图
    public static void showDirectedGraph(DirectedGraph graph) {
        System.out.println("有向图结构（节点 -> 邻接节点(权重)）：");
        for (String from : graph.getVertices()) {
            System.out.print(from + " -> ");
            Map<String, Integer> edges = graph.getOutEdges(from);
            if (edges.isEmpty()) {
                System.out.println("(无出边)");
            } else {
                List<String> edgeStrs = new ArrayList<>();
                for (Map.Entry<String, Integer> e : edges.entrySet()) {
                    edgeStrs.add(e.getKey() + "(" + e.getValue() + ")");
                }
                System.out.println(String.join(", ", edgeStrs));
            }
        }
    }

    // 功能3：查询桥接词
    public static String queryBridgeWords(DirectedGraph graph, String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        boolean has1 = graph.containsVertex(word1);
        boolean has2 = graph.containsVertex(word2);
        if (!has1 && !has2) {
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        } else if (!has1) {
            return "No \"" + word1 + "\" in the graph!";
        } else if (!has2) {
            return "No \"" + word2 + "\" in the graph!";
        }
        Set<String> bridgeWords = new HashSet<>();
        Map<String, Integer> fromEdges = graph.getOutEdges(word1);
        for (String candidate : fromEdges.keySet()) {
            if (graph.getOutEdges(candidate).containsKey(word2)) {
                bridgeWords.add(candidate);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            List<String> list = new ArrayList<>(bridgeWords);
            if (list.size() == 1) {
                return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: \"" + list.get(0) + "\".";
            } else {
                String last = list.remove(list.size() - 1);
                String joined = String.join(", ", list);
                return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + joined + ", and \"" + last + "\".";
            }
        }
    }

    // 功能4：生成新文本
    public static String generateNewText(DirectedGraph graph, String inputText) {
        // 处理输入文本，得到单词列表
        String processed = inputText.replaceAll("[^a-zA-Z]", " ").toLowerCase();
        String[] words = processed.split("\\s+");
        if (words.length == 0) return "";
        List<String> result = new ArrayList<>();
        result.add(words[0]); // 第一个单词直接加入
        Random rand = new Random();
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];
            // 查找桥接词
            Set<String> bridges = new HashSet<>();
            if (graph.containsVertex(w1) && graph.containsVertex(w2)) {
                Map<String, Integer> fromEdges = graph.getOutEdges(w1);
                for (String cand : fromEdges.keySet()) {
                    if (graph.getOutEdges(cand).containsKey(w2)) {
                        bridges.add(cand);
                    }
                }
            }
            if (!bridges.isEmpty()) {
                // 随机选择一个
                List<String> bridgeList = new ArrayList<>(bridges);
                String chosen = bridgeList.get(rand.nextInt(bridgeList.size()));
                result.add(chosen);
            }
            result.add(w2);
        }
        return String.join(" ", result);
    }

    // 功能5：计算最短路径（两个单词）
    public static String calcShortestPath(DirectedGraph graph, String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!graph.containsVertex(word1) && !graph.containsVertex(word2)) {
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        } else if (!graph.containsVertex(word1)) {
            return "No \"" + word1 + "\" in the graph!";
        } else if (!graph.containsVertex(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }
        Map<String, DirectedGraph.PathInfo> paths = graph.dijkstra(word1);
        String pathStr = graph.getPath(word1, word2, paths);
        if (pathStr == null) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            return "Shortest path: " + pathStr;
        }
    }

    // 功能5（可选）：计算一个单词到所有其他单词的最短路径
    public static String calcShortestPathFromSource(DirectedGraph graph, String source) {
        source = source.toLowerCase();
        if (!graph.containsVertex(source)) {
            return "No \"" + source + "\" in the graph!";
        }
        Map<String, DirectedGraph.PathInfo> paths = graph.dijkstra(source);
        StringBuilder sb = new StringBuilder();
        sb.append("Shortest paths from \"").append(source).append("\":\n");
        for (String target : graph.getVertices()) {
            if (target.equals(source)) continue;
            String path = graph.getPath(source, target, paths);
            if (path != null) {
                sb.append("  ").append(path).append("\n");
            } else {
                sb.append("  ").append(target).append(": unreachable\n");
            }
        }
        return sb.toString();
    }

    // 功能6：计算PageRank
    public static Double calPageRank(DirectedGraph graph, String word) {
        word = word.toLowerCase();
        if (!graph.containsVertex(word)) {
            return null;
        }
        Map<String, Double> pr = graph.computePageRankStandard();
        return pr.get(word);
    }

    // 功能7：随机游走
    public static String randomWalk(DirectedGraph graph) throws IOException {
        List<String> vertices = new ArrayList<>(graph.getVertices());
        if (vertices.isEmpty()) return "";
        Random rand = new Random();
        String current = vertices.get(rand.nextInt(vertices.size()));
        List<String> path = new ArrayList<>();
        path.add(current);
        Set<String> visitedEdges = new HashSet<>(); // 记录走过的边 "from->to"
        Scanner scanner = new Scanner(System.in);
        System.out.println("随机游走开始，当前节点: " + current);
        while (true) {
            Map<String, Integer> edges = graph.getOutEdges(current);
            if (edges.isEmpty()) {
                System.out.println("当前节点无出边，游走结束。");
                break;
            }
            // 随机选择一条出边
            List<String> targets = new ArrayList<>(edges.keySet());
            String next = targets.get(rand.nextInt(targets.size()));
            String edgeKey = current + "->" + next;
            if (visitedEdges.contains(edgeKey)) {
                System.out.println("遇到重复边 " + edgeKey + "，游走结束。");
                break;
            }
            visitedEdges.add(edgeKey);
            path.add(next);
            current = next;
            System.out.println("下一步: " + current);
            System.out.print("是否继续？(y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (!input.equals("y")) {
                System.out.println("用户停止。");
                break;
            }
        }
        String result = String.join(" ", path);
        // 写入文件
        try (FileWriter fw = new FileWriter("random_walk.txt")) {
            fw.write(result);
        }
        System.out.println("随机游走路径已写入 random_walk.txt");
        return result;
    }

    // 主函数
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DirectedGraph graph = null;

        System.out.println("基于大模型的编程实验");
        // 获取文件路径
        System.out.print("请输入文本文件路径: ");
        String filePath = scanner.nextLine().trim();
        try {
            List<String> words = processFile(filePath);
            graph = buildGraph(words);
            System.out.println("图构建成功，节点数: " + graph.getVertices().size());
        } catch (IOException e) {
            System.out.println("文件读取失败: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n请选择功能:");
            System.out.println("1. 展示有向图");
            System.out.println("2. 查询桥接词");
            System.out.println("3. 根据桥接词生成新文本");
            System.out.println("4. 计算最短路径");
            System.out.println("5. 计算单个单词到所有节点的最短路径");
            System.out.println("6. 计算单词PageRank");
            System.out.println("7. 随机游走");
            System.out.println("0. 退出");
            System.out.print("请输入选项: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showDirectedGraph(graph);
                    break;
                case "2":
                    System.out.print("请输入第一个单词: ");
                    String w1 = scanner.nextLine().trim();
                    System.out.print("请输入第二个单词: ");
                    String w2 = scanner.nextLine().trim();
                    String result = queryBridgeWords(graph, w1, w2);
                    System.out.println(result);
                    break;
                case "3":
                    System.out.print("请输入一行新文本: ");
                    String text = scanner.nextLine();
                    String newText = generateNewText(graph, text);
                    System.out.println("生成的新文本: " + newText);
                    break;
                case "4":
                    System.out.print("请输入第一个单词: ");
                    w1 = scanner.nextLine().trim();
                    System.out.print("请输入第二个单词: ");
                    w2 = scanner.nextLine().trim();
                    String path = calcShortestPath(graph, w1, w2);
                    System.out.println(path);
                    break;
                case "5":
                    System.out.print("请输入源单词: ");
                    w1 = scanner.nextLine().trim();
                    String allPaths = calcShortestPathFromSource(graph, w1);
                    System.out.println(allPaths);
                    break;
                case "6":
                    System.out.print("请输入单词: ");
                    w1 = scanner.nextLine().trim();
                    Double pr = calPageRank(graph, w1);
                    if (pr == null) {
                        System.out.println("单词 \"" + w1 + "\" 不在图中！");
                    } else {
                        System.out.printf("单词 \"%s\" 的PageRank值为: %.6f\n", w1, pr);
                    }
                    break;
                case "7":
                    try {
                        randomWalk(graph);
                    } catch (IOException e) {
                        System.out.println("写入文件失败: " + e.getMessage());
                    }
                    break;
                case "0":
                    System.out.println("程序结束。");
                    return;
                default:
                    System.out.println("无效选项，请重新输入。");
            }
        }
    }
}"// This is a new comment" 
"// Another change" 
"// C4 change" 
