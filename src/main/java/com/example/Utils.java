package com.example;

import com.example.domain.CommandExecutionResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Utils {
    public static CommandExecutionResult shellExec(String command) {
        final StringBuilder stdOut = new StringBuilder();
        final StringBuilder stdErr = new StringBuilder();
        int exitCode;
        try {
            final Process process = Runtime.getRuntime().exec(buildPlatformDependentCommand(command).toArray(String[]::new));
            process.waitFor();
            exitCode = process.exitValue();
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            outputReader.lines().forEach(stdOut::append);
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            errorReader.lines().forEach(stdErr::append);
        } catch (Exception e) {
            return new CommandExecutionResult("",e.getMessage(), 1);
        }
        return new CommandExecutionResult(stdOut.toString(), stdErr.toString(), exitCode);
    }
    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
    private static List<String> buildPlatformDependentCommand(String command) {
        if (isWindows()) {
            return List.of("cmd","/c",command);
        } else {
            return List.of("sh","-c",command);
        }
    }
}