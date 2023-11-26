package com.kmsr.phonepe.repo.impl;

import com.kmsr.phonepe.entities.EventParticipant;
import com.kmsr.phonepe.repo.EventParticipantRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EventParticipantRepoImpl implements EventParticipantRepo {

  private final Map<String, Set<String>> eventIdMap;
  private final Map<String, EventParticipant> eventParticipantMap;

  public EventParticipantRepoImpl() {
    eventIdMap = new HashMap<>();
    eventParticipantMap = new HashMap<>();
  }

  @Override
  public List<EventParticipant> saveAllByEventId(String eventId,
      List<EventParticipant> participants) {
    participants.forEach(p -> {
      eventParticipantMap.put(p.getId(), p);
    });
    eventIdMap.put(eventId,
        participants.stream().map(EventParticipant::getId).collect(Collectors.toSet()));
    return participants;
  }

  @Override
  public List<EventParticipant> getAllByEventId(String eventId) {
    Set<String> ids = eventIdMap.get(eventId);
    return ids.stream().map(eventParticipantMap::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<EventParticipant> deleteAllByEventId(String eventId) {
    Set<String> ids = eventIdMap.get(eventId);
    return ids.stream().map(eventParticipantMap::remove)
        .collect(Collectors.toList());
  }
}
