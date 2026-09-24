package com.codecanvas.algorithm;

import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphStep;
import java.util.List;

public interface GraphAlgorithm {
    List<GraphStep> run(Graph graph, String startNodeId);
    String getName();
    String getTheoreticalComplexity();
}