package com.keap;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;

  @Mojo(name = "report-summary", inheritByDefault = false, aggregator = true)
  public class BuildStatsFailMojo extends AbstractMojo {
    @Parameter(defaultValue = "${session}")
    private MavenSession session;

    public void execute() throws MojoExecutionException {
      ProjectDependencyGraph projectGraph = session.getProjectDependencyGraph();
      for (MavenProject project : projectGraph.getSortedProjects()) {
        String artifactId = project.getArtifactId();
        getLog().error("I am artifact: " + artifactId);

        MavenProject executionProject = project.getExecutionProject();
        getLog().error(executionProject.toString());
        getLog().error(executionProject.getBuild().toString());

//        String status = project.getExecutionProject() == project ?
//            project.getBuildSummary(session).getTopLevelBuildState() :
//            project.getBuildSummary(session).getBuildState();
        // Capture the reactor summary information for each module and convert it to JSON
        // Write the JSON report to a file
        // You can use a library like Gson for JSON conversion
      }
    }
  }