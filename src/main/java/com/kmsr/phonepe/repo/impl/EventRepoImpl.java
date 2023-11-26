package com.kmsr.phonepe.repo.impl;

import com.kmsr.phonepe.entities.Event;
import com.kmsr.phonepe.repo.EventRepo;
import java.util.HashMap;
import java.util.Map;

public class EventRepoImpl implements EventRepo {

  private final Map<String, Event> eventIdMap;

  public EventRepoImpl() {
    eventIdMap = new HashMap<>();
  }

  public Event save(Event event) {
    eventIdMap.put(event.getId(), event);
    return event;
  }

  public Event getById(String id) {
    return eventIdMap.get(id);
  }

  @Override
  public Event deleteById(String id) {
    return eventIdMap.remove(id);
  }
}
