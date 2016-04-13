package com.qunar.flight.extension.plugin.bridges;

import com.google.common.collect.Lists;
import com.qunar.flight.java.bytecode.visitor.ResourceMapping;
import com.qunar.flight.rules.CycleBetweenPackagesCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.design.Dependency;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Directory;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.TimeProfiler;
import org.sonar.graph.Cycle;
import org.sonar.graph.DirectedGraph;
import org.sonar.graph.Dsm;
import org.sonar.graph.DsmTopologicalSorter;
import org.sonar.graph.Edge;
import org.sonar.graph.IncrementalCyclesAndFESSolver;
import org.sonar.graph.MinimumFeedbackEdgeSetSolver;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by ping.miao on 2016/4/12.
 */
public class DesignBridge {
    private static final Logger LOG = LoggerFactory.getLogger(DesignBridge.class);

    private final SensorContext context;
    private final DirectedGraph<Resource, Dependency> graph;
    private final ResourceMapping resourceMapping;
    private final ResourcePerspectives resourcePerspectives;

    public DesignBridge(SensorContext context, DirectedGraph<Resource, Dependency> graph,
            ResourceMapping resourceMapping, ResourcePerspectives resourcePerspectives) {
        this.context = context;
        this.graph = graph;
        this.resourceMapping = resourceMapping;
        this.resourcePerspectives = resourcePerspectives;
    }

    public void saveDesign(Project sonarProject) {
        Collection<Resource> directories = resourceMapping.directories();
        TimeProfiler profiler = new TimeProfiler(LOG).start("Package design analysis");
        LOG.debug("{} packages to analyze", directories.size());

        IncrementalCyclesAndFESSolver<Resource> cyclesAndFESSolver = new IncrementalCyclesAndFESSolver<>(graph,
                directories);
        LOG.debug("{} cycles", cyclesAndFESSolver.getCycles().size());

        Set<Edge> feedbackEdges = cyclesAndFESSolver.getFeedbackEdgeSet();
        LOG.debug("{} feedback edges", feedbackEdges.size());
        int tangles = cyclesAndFESSolver.getWeightOfFeedbackEdgeSet();

        saveIssues(feedbackEdges);
        saveDependencies();
        savePositiveMeasure(sonarProject, CoreMetrics.PACKAGE_CYCLES, cyclesAndFESSolver.getCycles().size());
        savePositiveMeasure(sonarProject, CoreMetrics.PACKAGE_FEEDBACK_EDGES, feedbackEdges.size());
        savePositiveMeasure(sonarProject, CoreMetrics.PACKAGE_TANGLES, tangles);
        savePositiveMeasure(sonarProject, CoreMetrics.PACKAGE_EDGES_WEIGHT, getEdgesWeight(directories));

        String dsmJson = serializeDsm(graph, directories, feedbackEdges);
        Measure dsmMeasure = new Measure(CoreMetrics.DEPENDENCY_MATRIX, dsmJson)
                .setPersistenceMode(PersistenceMode.DATABASE);
        context.saveMeasure(sonarProject, dsmMeasure);

        profiler.stop();

        for (Resource sonarPackage : directories) {
            onPackage(sonarPackage);
        }
    }

    private void savePositiveMeasure(Resource sonarResource, Metric metric, double value) {
        if (value >= 0.0) {
            context.saveMeasure(sonarResource, metric, value);
        }
    }

    private void onPackage(Resource sonarPackage) {
        Collection<Resource> squidFiles = getResourcesForDirectory(sonarPackage);
        if (squidFiles != null && !squidFiles.isEmpty()) {
            IncrementalCyclesAndFESSolver<Resource> cycleDetector = new IncrementalCyclesAndFESSolver<>(graph,
                    squidFiles);
            Set<Cycle> cycles = cycleDetector.getCycles();

            MinimumFeedbackEdgeSetSolver solver = new MinimumFeedbackEdgeSetSolver(cycles);
            Set<Edge> feedbackEdges = solver.getEdges();
            int tangles = solver.getWeightOfFeedbackEdgeSet();

            savePositiveMeasure(sonarPackage, CoreMetrics.FILE_CYCLES, cycles.size());
            savePositiveMeasure(sonarPackage, CoreMetrics.FILE_FEEDBACK_EDGES, feedbackEdges.size());
            savePositiveMeasure(sonarPackage, CoreMetrics.FILE_TANGLES, tangles);
            savePositiveMeasure(sonarPackage, CoreMetrics.FILE_EDGES_WEIGHT, getEdgesWeight(squidFiles));
            String dsmJson = serializeDsm(graph, squidFiles, feedbackEdges);
            context.saveMeasure(sonarPackage, new Measure(CoreMetrics.DEPENDENCY_MATRIX, dsmJson));
        }
    }

    private Collection<Resource> getResourcesForDirectory(Resource sonarPackage) {
        List<Resource> result = Lists.newArrayList();
        for (Resource resource : resourceMapping.files((Directory) sonarPackage)) {
            result.add(context.getResource(resource));
        }
        return result;
    }

    private double getEdgesWeight(Collection<Resource> resources) {
        List<Dependency> edges = graph.getEdges(resources);
        double total = 0.0;
        for (Dependency edge : edges) {
            total += edge.getWeight();
        }
        return total;
    }

    private static String serializeDsm(DirectedGraph<Resource, Dependency> graph, Collection<Resource> sources,
            Set<Edge> feedbackEdges) {
        Dsm<Resource> dsm = new Dsm<>(graph, sources, feedbackEdges);
        DsmTopologicalSorter.sort(dsm);
        return DsmSerializer.serialize(dsm);
    }

    private void saveIssues(Set<Edge> feedbackEdges) {
        for (Edge feedbackEdge : feedbackEdges) {
            for (Dependency subDependency : resourceMapping.getSubDependencies((Dependency) feedbackEdge)) {
                Resource fromFile = subDependency.getFrom();
                Resource toFile = subDependency.getTo();
                Issuable issuable = resourcePerspectives.as(Issuable.class, fromFile);
                if (issuable != null) {
                    issuable.addIssue(issuable
                            .newIssueBuilder()
                            .ruleKey(CycleBetweenPackagesCheck.RULE_KEY)
                            .message(
                                    "Remove the dependency on the source file \"" + toFile.getLongName()
                                            + "\" to break a package cycle.").build());
                }
            }
        }
    }

    private void saveDependencies() {
        for (Resource resource : graph.getVertices()) {
            for (Dependency dependency : graph.getOutgoingEdges(resource)) {
                context.saveDependency(dependency);
            }
        }
    }
}