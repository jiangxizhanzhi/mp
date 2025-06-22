package cn.cyberpeace.mp.cscene.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class RegionLayoutCalculator {

    // 定义画布总大小
    private static final int CANVAS_WIDTH = 1000;
    private static final int CANVAS_HEIGHT = 800;

    public static List<ObjectNode> calculateLayout(int regionCount, List<Integer> nodesPerRegion) {
        List<ObjectNode> layouts = new ArrayList<>();
        
        // 1. 计算总节点数（用于比例分配）
        int totalNodes = nodesPerRegion.stream().mapToInt(Integer::intValue).sum();
        
        // 2. 确定区域排列的行列数（简单平方根近似）
        int cols = (int) Math.ceil(Math.sqrt(regionCount));
        int rows = (int) Math.ceil((double) regionCount / cols);
        
        // 3. 计算每个单元格的基础大小
        int cellWidth = CANVAS_WIDTH / cols;
        int cellHeight = CANVAS_HEIGHT / rows;
        
        // 4. 为每个区域计算布局
        for (int i = 0; i < regionCount; i++) {
            ObjectNode region = new ObjectMapper().createObjectNode();
            
            // 计算位置（行列索引）
            int row = i / cols;
            int col = i % cols;
            
            // 动态调整区域大小（基于节点数比例）
            double ratio = (double) nodesPerRegion.get(i) / totalNodes;
            int width = (int) (cellWidth * 0.8 + ratio * cellWidth * 0.2);
            int height = (int) (cellHeight * 0.8 + ratio * cellHeight * 0.2);
            
            // 设置原点坐标（居中偏移）
            int x = col * cellWidth + (cellWidth - width) / 2;
            int y = row * cellHeight + (cellHeight - height) / 2;
            
            // 构建JSON
            ObjectNode origin = new ObjectMapper().createObjectNode();
            origin.put("x", x);
            origin.put("y", y);
            
            region.set("origin", origin);
            region.put("width", width);
            region.put("height", height);
            
            layouts.add(region);
        }
        
        return layouts;
    }

    public static void main(String[] args) {
        int regionCount = 3;
        List<Integer> nodesPerRegion = List.of(5, 8, 3);
        
        List<ObjectNode> layouts = calculateLayout(regionCount, nodesPerRegion);
        layouts.forEach(System.out::println);
    }
}