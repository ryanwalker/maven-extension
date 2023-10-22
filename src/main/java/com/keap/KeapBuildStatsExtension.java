package com.keap;


import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class KeapBuildStatsExtension extends AbstractEventSpy {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeapBuildStatsExtension.class);

  Map<String, Instant> projectStartTimes = new HashMap<>();
  Map<String, Long> projectTotalTimes = new HashMap<>();

  @Override
  public void onEvent(Object event) throws Exception {
    try {
      if (event instanceof ExecutionEvent) {
        ExecutionEvent executionEvent = (ExecutionEvent) event;
        Type executionEventType = executionEvent.getType();
        String projectName =
            executionEvent.getProject() != null ? executionEvent.getProject().getName() : "na";
        if (executionEventType == Type.ProjectStarted) {
          LOGGER.error("Project Started: " + projectName + " " + Instant.now());
          projectStartTimes.put(projectName, Instant.now());
        } else if (executionEventType == Type.ProjectSucceeded ||
            executionEventType == Type.ProjectFailed) {
          Instant projectStarted = projectStartTimes.get(projectName);
          Instant projectCompleted = Instant.now();
          LOGGER.error("Project Succeeded: " + projectName + " " + Instant.now());
          long projectDurationMillis = Duration.between(projectStarted, projectCompleted)
              .toMillis();
          LOGGER.error("Project Time: " + projectDurationMillis);
          projectTotalTimes.put(projectName, projectDurationMillis);
        } else {
//          LOGGER.error("ExecutionEvent is: " + executionEvent.getType());
        }
      } else if (event instanceof org.eclipse.aether.RepositoryEvent) {
        RepositoryEvent repositoryEvent = (RepositoryEvent) event;
        if (repositoryEvent.getType() != EventType.ARTIFACT_RESOLVED &&
            repositoryEvent.getType() != EventType.ARTIFACT_RESOLVING &&
            repositoryEvent.getType() != EventType.ARTIFACT_INSTALLED &&
            repositoryEvent.getType() != EventType.ARTIFACT_INSTALLED) {
//          LOGGER.error("RepositoryEvent.Type: " + repositoryEvent.getType());
        }
      } else if (event instanceof MavenExecutionRequest) {
        LOGGER.error("MavenExecutionRequestE.ExecutionListener: "
            + ((MavenExecutionRequest) event).getExecutionListener());
      } else if (event instanceof MavenExecutionResult) {
        MavenExecutionResult mavenExecutionResult = (MavenExecutionResult) event;
        LOGGER.error(
            "MavenExecutionResult: ");// + (mavenExecutionResult.getBuildSummary(event.getTopologicallySortedProjects()));
        for (Entry<String, Long> entry : projectTotalTimes.entrySet()) {
          LOGGER.error(entry.getKey() + " ..... " + entry.getValue());
        }
      } else if (event instanceof DependencyResolutionRequest) {
//        LOGGER.error("ExecutionEvent.Type: " + ((ExecutionEvent) event).getType());
      } else if (event instanceof DependencyResolutionResult) {
//        LOGGER.error("ExecutionEvent.Type: " + ((ExecutionEvent) event).getType());
      }
      // The following event type is available since Maven 3.3.1+
      // else if ( event instanceof DefaultSettingsBuildingRequest) {
      // DefaultSettingsBuildingRequest r = null;
      // r.getGlobalSettingsFile();
      // r.getGlobalSettingsSource();
      // r.getSystemProperties();
      // r.getUserSettingsFile();
      // r.getUserSettingsSource();
      //
      // r.setGlobalSettingsFile( globalSettingsFile );
      // r.setGlobalSettingsSource( globalSettingsSource );
      // r.setSystemProperties( systemProperties );
      // r.setUserProperties( userProperties );
      // r.setUserSettingsFile( userSettingsFile );
      // r.setUserSettingsSource( userSettingsSource );
      // }
      // The following event type is available since Maven 3.3.1+
      // else if (event instanceof DefaultSettingsBuildingRequest) {
      //
      // DefaultSettingsBuildingRequest r = null;
      // r.getGlobalSettingsSource().getLocation()
      // }
      // The following event type is available since Maven 3.3.1+
      // else if (event instanceof DefaultToolchainsBuildingRequest) {
      // DefaultToolchainsBuildingRequest r = null;
      // r.getGlobalToolchainsSource().
      // }
      // The following event type is available since Maven 3.3.1+
      // else if (event instanceof DefaultToolchainsBuildingResult) {
      // DefaultToolchainsBuildingResult r = null;
      // r.getEffectiveToolchains();
      // r.getProblems();
      // }
      else {
        // TODO: What kind of event we haven't considered?
        LOGGER.debug("MBTP: Event {}", event.getClass().getCanonicalName());
      }
    } catch (Exception e) {
      LOGGER.error("MBTP: Exception", e);
    }
  }
}
