package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TaskScheduler {
    private final TaskService taskService;
    private final ManagementService managementService;
    private final TaskController taskController;

    @Scheduled(fixedDelay = 5000l)
    public void getMyTasks() {
        List<Task> result = taskService.createNativeTaskQuery()
                .sql("SELECT ID_ FROM " + managementService.getTableName(Task.class) + " T WHERE T.assignee_ = #{assignee}")
                .parameter("assignee", "kermit").list();
        result.forEach( it -> {
            // Get Task from cache (in 99% of all cases)
            Task task = taskController.getTaskById(it.getId());
            log.debug(ReflectionToStringBuilder.toString(task));
        });
    }


}
