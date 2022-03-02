package com.example.domain;

import java.io.Serializable;

public record CommandExecutionResult(String stdout, String stderr, int exitCode) implements Serializable {}
