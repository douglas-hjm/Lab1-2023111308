import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Lab1_GraphBlackBoxTest {
    private Lab1_Graph.DirectedGraph graph;

    @Before
    public void setUp() {
        // 构建一个与 Step 1 中假设图一致的图
        graph = new Lab1_Graph.DirectedGraph();
        // 添加边：new -> world, world -> hello, new -> hello, hello -> world
        graph.addEdge("new", "world");
        graph.addEdge("world", "hello");
        graph.addEdge("new", "hello");
        graph.addEdge("hello", "world");
    }

    // BT1: 存在唯一桥接词
    @Test
    public void testQueryBridgeWords_SingleBridge() {
        String result = Lab1_Graph.queryBridgeWords(graph, "new", "hello");
        assertEquals("The bridge words from \"new\" to \"hello\" is: \"world\".", result);
    }

    // BT2: 存在多个桥接词
    @Test
    public void testQueryBridgeWords_MultipleBridges() {
        // 注意：new -> world 的直接边存在，桥接词需要 new->?->world，这里 ?=hello 满足 new->hello 和 hello->world
        // 且 new->world 本身不是桥接词（需要中间词）
        String result = Lab1_Graph.queryBridgeWords(graph, "new", "world");
        // 期望输出：桥接词为 hello
        assertEquals("The bridge words from \"new\" to \"world\" is: \"hello\".", result);
    }

    // BT3: 无桥接词
    @Test
    public void testQueryBridgeWords_NoBridge() {
        String result = Lab1_Graph.queryBridgeWords(graph, "world", "new");
        assertEquals("No bridge words from \"world\" to \"new\"!", result);
    }

    // BT4: word1 不在图中
    @Test
    public void testQueryBridgeWords_Word1NotExist() {
        String result = Lab1_Graph.queryBridgeWords(graph, "abc", "new");
        assertEquals("No \"abc\" in the graph!", result);
    }

    // BT5: word2 不在图中
    @Test
    public void testQueryBridgeWords_Word2NotExist() {
        String result = Lab1_Graph.queryBridgeWords(graph, "new", "xyz");
        assertEquals("No \"xyz\" in the graph!", result);
    }

    // BT6: 两个都不在图中
    @Test
    public void testQueryBridgeWords_BothNotExist() {
        String result = Lab1_Graph.queryBridgeWords(graph, "abc", "xyz");
        assertEquals("No \"abc\" and \"xyz\" in the graph!", result);
    }

    // BT7: 大小写混合
    @Test
    public void testQueryBridgeWords_CaseInsensitive() {
        String result = Lab1_Graph.queryBridgeWords(graph, "NeW", "HeLLo");
        assertEquals("The bridge words from \"new\" to \"hello\" is: \"world\".", result);
    }

    // BT8: 空字符串（边界）
    @Test
    public void testQueryBridgeWords_EmptyString() {
        String result = Lab1_Graph.queryBridgeWords(graph, "", "new");
        assertEquals("No \"\" in the graph!", result);
    }
}
