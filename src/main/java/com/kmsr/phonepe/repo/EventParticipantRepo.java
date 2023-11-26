package com.kmsr.phonepe.repo;

import com.kmsr.phonepe.entities.EventParticipant;
import java.util.List;

public interface EventParticipantRepo {

  List<EventParticipant> saveAllByEventId(String eventId, List<EventParticipant> participants);

  List<EventParticipant> getAllByEventId(String eventId);

  List<EventParticipant> deleteAllByEventId(String eventId);
}
