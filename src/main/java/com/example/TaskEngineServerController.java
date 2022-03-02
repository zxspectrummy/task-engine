package com.example;

import com.example.domain.Task;
import com.example.domain.TaskRepository;
import com.example.domain.TaskState;
import com.example.rpc.handlers.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.UUID;

@Configuration
@Profile("pub")
@RestController
@RequestMapping("/api/v1")
public class TaskEngineServerController {
    @Autowired
    private TaskRepository taskRepository;

    private final Publisher publisher;

    public TaskEngineServerController(Publisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task) {
        task.setState(TaskState.QUEUED);
        Task savedTask = taskRepository.saveAndFlush(task);
        publisher.publish(task);
        return savedTask;
    }

    @GetMapping("/tasks")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Task> getAllBooks() {
        Collection<Task> collection = taskRepository.findAll();
        return collection;
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskById(@PathVariable UUID id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entity with id = Not found"));
        return task;
    }
}