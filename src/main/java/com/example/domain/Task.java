package com.example.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "command")
    private String command;

    @Column(name = "stdout")
    private String stdout;

    @Column(name = "stderr")
    private String stderr;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    @Type(type = "state_enum_type")
    private TaskState state;

    @Column(name = "exitCode")
    private int exitCode;

    public Task() {

    }

    public Task(UUID id, String command) {
        this.id = id;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}