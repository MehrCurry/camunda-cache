package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.history.event.HistoricDetailEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CacheEvictController implements HistoryEventHandler {
    private final CacheManager cacheManger;

    @Lazy
    @Autowired
    private TaskController taskController;

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
        log.debug(historyEvent.toString());

        String taskId = null;

        if (historyEvent instanceof HistoricDetailEventEntity) {
            taskId = (((HistoricDetailEventEntity) historyEvent).getTaskId());
        }

        if (historyEvent instanceof HistoricTaskInstanceEventEntity) {
            taskId = (((HistoricTaskInstanceEventEntity) historyEvent).getTaskId());
        }

        // add more events as needed

        if (taskId!=null) {
            taskController.getTaskFromCamundaById(taskId);
        }
    }

    @Override
    public void handleEvents(List<HistoryEvent> historyEvents) {
        historyEvents.forEach(e -> handleEvent(e));
    }
}
