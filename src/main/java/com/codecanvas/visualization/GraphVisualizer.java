package com.codecanvas.visualization;

import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;
import com.codecanvas.model.GraphStep;
import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import com.codecanvas.service.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class GraphVisualizer {

    private static final double NODE_RADIUS = 20;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private static final Color COLOR_UNVISITED = Color.web("#6c8ebf");
    private static final Color COLOR_CURRENT = Color.web("#f4d03f");
    private static final Color COLOR_VISITED = Color.web("#2ecc71");
    private static final Color COLOR_EDGE_NORMAL = Color.web("#999999");
    private static final Color COLOR_EDGE_ACTIVE = Color.web("#e74c3c");

    private Graph graph;
    private Map<String, Circle> nodeCircles;
    private Map<String, Label> distanceLabels;
    private Map<GraphEdge, Line> edgeLines;

    public void initialize(Pane pane, Graph targetGraph) {
        this.graph = targetGraph;
        pane.getChildren().clear();
        nodeCircles = new HashMap<>();
        distanceLabels = new HashMap<>();
        edgeLines = new HashMap<>();

        for (GraphEdge edge : graph.getEdges()) {
            GraphNode from = findNode(edge.getFrom());
            GraphNode to = findNode(edge.getTo());

            Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
            line.setStroke(COLOR_EDGE_NORMAL);
            line.setStrokeWidth(1.5);
            edgeLines.put(edge, line);
            pane.getChildren().add(line);

            Label weightLabel = new Label(String.valueOf(edge.getWeight()));
            weightLabel.setLayoutX((from.getX() + to.getX()) / 2);
            weightLabel.setLayoutY((from.getY() + to.getY()) / 2);
            pane.getChildren().add(weightLabel);
        }

        for (GraphNode node : graph.getNodes()) {
            Circle circle = new Circle(node.getX(), node.getY(), NODE_RADIUS);
            circle.setFill(COLOR_UNVISITED);
            nodeCircles.put(node.getId(), circle);
            pane.getChildren().add(circle);

            Label idLabel = new Label(node.getId());
            idLabel.setLayoutX(node.getX() - 5);
            idLabel.setLayoutY(node.getY() - 8);
            pane.getChildren().add(idLabel);

            Label distLabel = new Label("∞");
            distLabel.setLayoutX(node.getX() - 5);
            distLabel.setLayoutY(node.getY() + NODE_RADIUS + 2);
            distanceLabels.put(node.getId(), distLabel);
            pane.getChildren().add(distLabel);
        }
    }

    public void animateToStep(GraphStep step) {
        for (GraphEdge edge : graph.getEdges()) {
            boolean isActive = edge.getFrom().equals(step.getActiveEdgeFrom())
                    && edge.getTo().equals(step.getActiveEdgeTo());
            Line line = edgeLines.get(edge);
            line.setStroke(isActive ? COLOR_EDGE_ACTIVE : COLOR_EDGE_NORMAL);
            line.setStrokeWidth(isActive ? 3 : 1.5);
        }

        for (GraphNode node : graph.getNodes()) {
            Circle circle = nodeCircles.get(node.getId());
            Color targetColor = colorFor(node.getId(), step);
            new FillTransition(ANIMATION_DURATION, circle, (Color) circle.getFill(), targetColor).play();

            if (node.getId().equals(step.getCurrentNodeId())) {
                ScaleTransition pulse = new ScaleTransition(Duration.millis(150), circle);
                pulse.setToX(1.15);
                pulse.setToY(1.15);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(2);
                pulse.play();
            }

            Integer dist = step.getDistances().get(node.getId());
            String distText = (dist == null || dist == Integer.MAX_VALUE) ? "∞" : String.valueOf(dist);
            distanceLabels.get(node.getId()).setText(distText);
        }
        if (step.getActiveEdgeFrom() != null) {
            SoundManager.playCompare();
        }
    }

    private Color colorFor(String nodeId, GraphStep step) {
        if (nodeId.equals(step.getCurrentNodeId())) return COLOR_CURRENT;
        if (step.getVisitedNodes().contains(nodeId)) return COLOR_VISITED;
        return COLOR_UNVISITED;
    }

    private GraphNode findNode(String id) {
        for (GraphNode node : graph.getNodes()) {
            if (node.getId().equals(id)) return node;
        }
        throw new IllegalArgumentException("Node not found: " + id);
    }
}