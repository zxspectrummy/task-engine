package com.example.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@With
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

    @Column(name = "stdout", length = 4096)
    private String stdout;

    @Column(name = "stderr", length = 4096)
    private String stderr;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    @Type(type = "state_enum_type")
    private TaskState state;

    @Column(name = "exit_code")
    private int exitCode;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedAt;

    @Column(name = "last_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt;

    public String getCommand() {
        return command;
    }
}