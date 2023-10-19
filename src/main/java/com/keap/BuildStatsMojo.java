package com.keap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "build-stats")
public class BuildStatsMojo extends AbstractMojo {

  public void execute() throws MojoExecutionException {
    // Get the full command line used to launch the Java process
    String javaCommand = System.getProperty("sun.java.command");

    // Extract the Maven command from the full command line
    String mavenCommand = extractMavenCommand(javaCommand);

    getLog().error("The java command: " + javaCommand);
    try {
      getLog().error(userHardwareJson());
    } catch (Exception e) {
      getLog().error("Unable to run `uname -a`");
    }
  }

  private String extractMavenCommand(String fullCommand) {
    // Split the full command line by spaces to extract individual arguments
    String[] arguments = fullCommand.split(" ");

    // The first argument is typically the Java executable
    // The second argument is the Maven command
    if (arguments.length >= 2) {
      return arguments[1];
    } else {
      return "Maven command not found";
    }
  }

  private String userHardwareJson() throws Exception {
    Runtime runtime = Runtime.getRuntime();
    Process system = runtime.exec("uname -s");
    Process nodeName = runtime.exec("uname -n");
    Process release = runtime.exec("uname -r");
    Process version = runtime.exec("uname -v");
    Process machine = runtime.exec("uname -m");
    Process processor = runtime.exec("uname -p");
    Process os = runtime.exec("uname -o");
    
    system.waitFor();
    nodeName.waitFor();
    release.waitFor();
    version.waitFor();
    machine.waitFor();
    processor.waitFor();
    os.waitFor();

    String systemValue = getCommandValue(system);
    String nodeNameValue = getCommandValue(nodeName);
    String releaseValue = getCommandValue(release);
    String versionValue = getCommandValue(version);
    String machineValue = getCommandValue(machine);
    String processorValue = getCommandValue(processor);
    String osValue = getCommandValue(os);

    Map<String, String> values = ImmutableMap.<String, String>builder()
        .put("system", systemValue)
        .put("nodeName", nodeNameValue)
        .put("release", releaseValue )
        .put("version", versionValue)
        .put("machine", machineValue)
        .put("processor", processorValue)
        .put("os", osValue)
        .build();

    return new ObjectMapper().writeValueAsString(values);
  }

  private static String getCommandValue(Process process) throws IOException {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {

      String line = "";
      StringBuilder returnValue = new StringBuilder();

      while (null != (line = br.readLine())) {
        returnValue.append(line);
      }
      return returnValue.toString();
    }
  }

}