package org.trilgar.medimage.ssl.radiology.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trilgar.medimage.ssl.radiology.entity.ExaminationTask;
import org.trilgar.medimage.ssl.radiology.service.api.ExaminationTaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/radiology/tasks")
@RequiredArgsConstructor
public class ExaminationTaskController {
    private final ExaminationTaskService taskService;

    @GetMapping
    public List<ExaminationTask> getWorklist() {
        return taskService.getPendingTasks();
    }

    @GetMapping("/{id}")
    public ExaminationTask getTask(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelTask(@PathVariable UUID id) {
        taskService.cancelTask(id);
        return ResponseEntity.ok("Task cancelled");
    }
}
