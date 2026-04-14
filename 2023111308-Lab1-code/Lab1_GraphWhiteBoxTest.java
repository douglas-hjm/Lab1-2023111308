import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Lab1_GraphWhiteBoxTest {

    private Lab1_Graph.DirectedGraph graph;

    @Before
    public void setUp() {
        graph = new Lab1_Graph.DirectedGraph();
        // 构建一个基础图，包含节点 new, world, hello，以及一条路径 new->world->hello
        graph.addEdge("new", "world");
        graph.addEdge("world", "hello");
        // 为了测试不可达，再添加一个孤立节点 isolated（只有自己或只作为终点）
        graph.addEdge("isolated", "isolated");  // 自环，但无法从 new 到达
    }

    // P1: 两个单词都不存在
    @Test
    public void testBothNotExist() {
        String result = Lab1_Graph.calcShortestPath(graph, "abc", "xyz");
        assertEquals("No \"abc\" and \"xyz\" in the graph!", result);
    }

    // P2: 只有 word1 不存在
    @Test
    public void testWord1NotExist() {
        String result = Lab1_Graph.calcShortestPath(graph, "abc", "new");
        assertEquals("No \"abc\" in the graph!", result);
    }

    // P3: 只有 word2 不存在
    @Test
    public void testWord2NotExist() {
        String result = Lab1_Graph.calcShortestPath(graph, "new", "xyz");
        assertEquals("No \"xyz\" in the graph!", result);
    }

    // P4: 两者都存在但不可达
    @Test
    public void testUnreachable() {
        String result = Lab1_Graph.calcShortestPath(graph, "new", "isolated");
        assertEquals("No path from \"new\" to \"isolated\"!", result);
    }

    // P5: 可达，返回最短路径字符串
    @Test
    public void testReachable() {
        String result = Lab1_Graph.calcShortestPath(graph, "new", "hello");
        // 注意：路径字符串格式为 "Shortest path: new -> world -> hello (length=2)"
        assertEquals("Shortest path: new -> world -> hello (length=2)", result);
    }
}
