import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Lab1_GraphWhiteBoxTest {
    private Lab1_Graph.DirectedGraph graph;

    // 构建一个基础图，供多个测试使用
    @Before
    public void setUp() {
        graph = new Lab1_Graph.DirectedGraph();
        graph.addEdge("new", "world");
        graph.addEdge("world", "hello");
        graph.addEdge("new", "hello");
        graph.addEdge("hello", "world");
    }

    // WT1: 两个单词都不存在
    @Test
    public void testBothNotExist() {
        String result = Lab1_Graph.queryBridgeWords(graph, "abc", "xyz");
        assertEquals("No \"abc\" and \"xyz\" in the graph!", result);
    }

    // WT2: word1 不存在
    @Test
    public void testWord1NotExist() {
        String result = Lab1_Graph.queryBridgeWords(graph, "abc", "new");
        assertEquals("No \"abc\" in the graph!", result);
    }

    // WT3: word2 不存在
    @Test
    public void testWord2NotExist() {
        String result = Lab1_Graph.queryBridgeWords(graph, "new", "xyz");
        assertEquals("No \"xyz\" in the graph!", result);
    }

    // WT4: 无桥接词（需要特殊图）
    @Test
    public void testNoBridgeWord() {
        // 构建一个没有桥接词的图：new -> world, world -> hello, 但没有 new -> ? -> world 的中间词
        Lab1_Graph.DirectedGraph g2 = new Lab1_Graph.DirectedGraph();
        g2.addEdge("new", "world");
        g2.addEdge("world", "hello");
        // 没有 new->hello，所以 new->world 没有桥接词
        String result = Lab1_Graph.queryBridgeWords(g2, "new", "world");
        assertEquals("No bridge words from \"new\" to \"world\"!", result);
    }

    // WT5: 单个桥接词
    @Test
    public void testSingleBridgeWord() {
        String result = Lab1_Graph.queryBridgeWords(graph, "new", "hello");
        assertEquals("The bridge words from \"new\" to \"hello\" is: \"world\".", result);
    }

    // WT6: 多个桥接词
    @Test
    public void testMultipleBridgeWords() {
        // 添加另一条桥接路径：new -> hi -> world
        graph.addEdge("new", "hi");
        graph.addEdge("hi", "world");
        // 现在 new->world 的桥接词有 hello 和 hi
        String result = Lab1_Graph.queryBridgeWords(graph, "new", "world");
        // 期望格式："The bridge words from \"new\" to \"world\" are: hello, and \"hi\"." 或 "hi, and \"hello\"." 顺序不定
        assertTrue(result.startsWith("The bridge words from \"new\" to \"world\" are: "));
        assertTrue(result.contains("hello") && result.contains("hi"));
    }
}