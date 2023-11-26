package com.kmsr.phonepe.service.impl;

import com.kmsr.phonepe.entities.EventParticipant;
import com.kmsr.phonepe.repo.EventParticipantRepo;
import com.kmsr.phonepe.service.EventParticipantService;
import com.kmsr.phonepe.utils.PreConditions;
import java.util.List;

public class EventParticipantServiceImpl implements EventParticipantService {

  private final EventParticipantRepo eventParticipantRepo;

  public EventParticipantServiceImpl(EventParticipantRepo eventParticipantRepo) {
    this.eventParticipantRepo = eventParticipantRepo;
  }

  @Override
  public List<EventParticipant> saveEventParticipants(String eventId,
      List<EventParticipant> participants) {
    PreConditions.notEmpty(eventId);
    PreConditions.notEmpty(participants);
    return eventParticipantRepo.saveAllByEventId(eventId, participants);
  }

  @Override
  public List<EventParticipant> getAllByEventId(String eventId) {
    PreConditions.notEmpty(eventId);
    return eventParticipantRepo.getAllByEventId(eventId);
  }

  @Override
  public List<EventParticipant> deleteAllByEventId(String eventId) {
    PreConditions.notEmpty(eventId);
    return eventParticipantRepo.deleteAllByEventId(eventId);
  }
}
