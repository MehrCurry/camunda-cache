package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"tasks"})
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final ManagementService managementService;
    private final CacheManager cacheManager;

    /**
     * This is the normal method to get a single task
     * It is served from the cache if present.
     * @param id
     * @return Camunda Task Object
     */
    @Cacheable
    public Task getTaskById(String id) {
        return taskService.createTaskQuery().taskId(id).singleResult();
    }

    /**
     * This method evicts a single task object from the cache
     * @param id
     */
    @CacheEvict(key="#id")
    public void evict(String id) {
        log.debug("Evict: " + id);
    }

    /**
     * This method does a forced reload from camunda and puts the result in the cache.
     * Used to refresh from the event listener.
     * Sometimes it happens that the event listner fires with a new task id but the
     * API does not find it :(
     * @param id
     * @return Camunda Task Object
     */
    @CachePut
    @Async
    public Task getTaskFromCamundaById(String id) {
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        log.debug(String.format("Updated Task %s: %s",id,task));
        return task;
    }
}
