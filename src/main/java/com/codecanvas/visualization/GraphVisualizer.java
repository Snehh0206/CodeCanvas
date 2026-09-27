package com.codecanvas.visualization;

import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;
import com.codecanvas.model.GraphStep;
import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GraphVisualizer {

    private static final double NODE_RADIUS = 22;
    private static final Duration ANIMATION_DURATION = Duration.millis(400);

    private static final Color COLOR_UNVISITED = Color.web("#7f8c8d");
    private static final Color COLOR_CURRENT   = Color.web("#f4d03f");
    private static final Color COLOR_SETTLED   = Color.web("#1abc9c");
    private static final Color COLOR_SOURCE    = Color.web("#3498db");
    private static final Color COLOR_FINAL     = Color.web("#2ecc71");
    private static final Color COLOR_EDGE_NORMAL = Color.web("#999999");
    private static final Color COLOR_EDGE_ACTIVE = Color.web("#e67e22");
    private static final Color COLOR_EDGE_FINAL  = Color.web("#27ae60");

    private Graph graph;
    private Map<String, Circle> nodeCircles;
    private Map<String, Label> distanceLabels;
    private Map<GraphEdge, Line> edgeLines;
    private String sourceNodeId;

    public void initialize(Pane pane, Graph targetGraph) {
        this.graph = targetGraph;
        this.sourceNodeId = null;
        pane.getChildren().clear();
        nodeCircles = new HashMap<>();
        distanceLabels = new HashMap<>();
        edgeLines = new HashMap<>();

        for (GraphEdge edge : graph.getEdges()) {
            GraphNode from = findNode(edge.getFrom());
            GraphNode to = findNode(edge.getTo());

            Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
            line.setStroke(COLOR_EDGE_NORMAL);
            line.setStrokeWidth(2.5);
            edgeLines.put(edge, line);
            pane.getChildren().add(line);

            Label weightLabel = new Label(String.valueOf(edge.getWeight()));
            weightLabel.setStyle("-fx-font-weight: bold; -fx-background-color: white;");
            weightLabel.setLayoutX((from.getX() + to.getX()) / 2);
            weightLabel.setLayoutY((from.getY() + to.getY()) / 2);
            pane.getChildren().add(weightLabel);
        }

        for (GraphNode node : graph.getNodes()) {
            Circle circle = new Circle(node.getX(), node.getY(), NODE_RADIUS);
            circle.setFill(COLOR_UNVISITED);
            circle.setStroke(Color.web("#2c3e50"));
            circle.setStrokeWidth(1.5);
            nodeCircles.put(node.getId(), circle);
            pane.getChildren().add(circle);

            Label idLabel = new Label(node.getId());
            idLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            idLabel.setLayoutX(node.getX() - 5);
            idLabel.setLayoutY(node.getY() - 9);
            pane.getChildren().add(idLabel);

            Label distLabel = new Label("∞");
            distLabel.setStyle("-fx-font-weight: bold;");
            distLabel.setLayoutX(node.getX() - 8);
            distLabel.setLayoutY(node.getY() + NODE_RADIUS + 2);
            distanceLabels.put(node.getId(), distLabel);
            pane.getChildren().add(distLabel);
        }
    }

    // Draws the graph all-gray and lets the user click a node to choose the source
    public void enableSourceSelection(Pane pane, Graph targetGraph, Consumer<String> onSelect) {
        initialize(pane, targetGraph);
        for (Map.Entry<String, Circle> entry : nodeCircles.entrySet()) {
            String nodeId = entry.getKey();
            Circle circle = entry.getValue();
            circle.setCursor(Cursor.HAND);
            circle.setOnMouseClicked(e -> onSelect.accept(nodeId));
        }
    }

    public void markSource(String nodeId) {
        this.sourceNodeId = nodeId;
        Circle circle = nodeCircles.get(nodeId);
        if (circle != null) {
            circle.setFill(COLOR_SOURCE);
            circle.setCursor(Cursor.DEFAULT);
        }
        for (Circle c : nodeCircles.values()) {
            c.setOnMouseClicked(null);
        }
    }

    public void animateToStep(GraphStep step) {
        animateToStep(step, false);
    }

    public void animateToStep(GraphStep step, boolean isFinal) {
        for (GraphEdge edge : graph.getEdges()) {
            boolean isActive = edge.getFrom().equals(step.getActiveEdgeFrom())
                    && edge.getTo().equals(step.getActiveEdgeTo());
            Line line = edgeLines.get(edge);
            line.setStroke(isActive ? COLOR_EDGE_ACTIVE : COLOR_EDGE_NORMAL);
            line.setStrokeWidth(isActive ? 4 : 2.5);
        }

        for (GraphNode node : graph.getNodes()) {
            Circle circle = nodeCircles.get(node.getId());
            Color targetColor = colorFor(node.getId(), step);
            new FillTransition(ANIMATION_DURATION, circle, (Color) circle.getFill(), targetColor).play();

            if (node.getId().equals(step.getCurrentNodeId())) {
                ScaleTransition pulse = new ScaleTransition(Duration.millis(200), circle);
                pulse.setToX(1.2);
                pulse.setToY(1.2);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(2);
                pulse.play();
            }

            Integer dist = step.getDistances().get(node.getId());
            String distText = (dist == null || dist == Integer.MAX_VALUE) ? "∞" : String.valueOf(dist);
            distanceLabels.get(node.getId()).setText(distText);
        }

        if (isFinal) {
            Map<String, String> predecessors = step.getPredecessors();

            for (GraphEdge edge : graph.getEdges()) {
                String pred = predecessors.get(edge.getTo());
                if (pred != null && pred.equals(edge.getFrom())) {
                    Line line = edgeLines.get(edge);
                    line.setStroke(COLOR_EDGE_FINAL);
                    line.setStrokeWidth(6);
                }
            }

            for (GraphNode node : graph.getNodes()) {
                boolean reachable = node.getId().equals(sourceNodeId) || predecessors.containsKey(node.getId());
                if (reachable) {
                    Circle circle = nodeCircles.get(node.getId());
                    new FillTransition(ANIMATION_DURATION, circle, (Color) circle.getFill(), COLOR_FINAL).play();
                }
            }
        }
    }

    private Color colorFor(String nodeId, GraphStep step) {
        if (nodeId.equals(step.getCurrentNodeId())) return COLOR_CURRENT;
        if (step.getVisitedNodes().contains(nodeId)) return COLOR_SETTLED;
        if (nodeId.equals(sourceNodeId)) return COLOR_SOURCE;
        return COLOR_UNVISITED;
    }

    private GraphNode findNode(String id) {
        for (GraphNode node : graph.getNodes()) {
            if (node.getId().equals(id)) return node;
        }
        throw new IllegalArgumentException("Node not found: " + id);
    }
}