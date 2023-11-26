package com.kmsr.phonepe.service;

import com.kmsr.phonepe.entities.EventParticipant;
import java.util.List;

public interface EventParticipantService {

  List<EventParticipant> saveEventParticipants(String eventId, List<EventParticipant> participants);

  List<EventParticipant> getAllByEventId(String eventId);

  List<EventParticipant> deleteAllByEventId(String eventId);
}
