package com.codecanvas.algorithm;

import com.codecanvas.model.AlgorithmStep;
import java.util.List;

public interface Algorithm {
    List<AlgorithmStep> run(int[] input);
    String getName();
    String getTheoreticalComplexity();
}