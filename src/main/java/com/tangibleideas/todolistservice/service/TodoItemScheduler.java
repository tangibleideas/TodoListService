package com.tangibleideas.todolistservice.service;

import static com.tangibleideas.todolistservice.util.Constants.UPDATE_PAST_DUE_STATUS_JOB_DELAY_MS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TodoItemScheduler {

  private final TodoItemService todoItemService;

  @Scheduled(fixedDelay = UPDATE_PAST_DUE_STATUS_JOB_DELAY_MS)
  public void updatePastDueItems() {
    log.info("Running update for past due items");
    List<Long> pastDueItems = todoItemService.getPastDueTodoItemIds();
    log.info("Found {} TodoItems", pastDueItems.size());
    pastDueItems.forEach(todoItemService::setPastDueIfNotDone);
    log.info("Completed update for past due items");
  }
}
