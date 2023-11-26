package com.kmsr.phonepe.service;

import com.kmsr.phonepe.entities.Event;
import com.kmsr.phonepe.entities.EventParticipant;
import com.kmsr.phonepe.entities.TimeSlot;
import com.kmsr.phonepe.entities.UserCalenderSlot;
import java.util.List;

public interface EventService {

  Event createEvent(String organiser, List<String> participants, long startTime, long endTime);

  Event getById(String id);

  List<EventParticipant> getParticipantsById(String eventId);

  Event deleteEvent(String eventId);

  TimeSlot getNextAvailableSlot(List<String> userIds, long startTime, long duration);

  List<UserCalenderSlot> getConflicts(String userId, long startTime, long endTime);
}
