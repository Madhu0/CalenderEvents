package com.kmsr.phonepe.service.impl;

import static com.kmsr.phonepe.utils.Constants.MAX_TIME_TO_LOOK_FOR_SLOTS;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.kmsr.phonepe.Exceptions.AssetNotFoundException;
import com.kmsr.phonepe.Exceptions.InvalidInputException;
import com.kmsr.phonepe.entities.Event;
import com.kmsr.phonepe.entities.EventParticipant;
import com.kmsr.phonepe.entities.TimeSlot;
import com.kmsr.phonepe.entities.User;
import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.enums.CalenderSlotRefType;
import com.kmsr.phonepe.repo.EventRepo;
import com.kmsr.phonepe.service.EventParticipantService;
import com.kmsr.phonepe.service.EventService;
import com.kmsr.phonepe.service.UserCalenderSlotService;
import com.kmsr.phonepe.service.UserScheduleService;
import com.kmsr.phonepe.service.UserService;
import com.kmsr.phonepe.utils.PreConditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService {

  private final EventRepo eventRepo;
  private final UserService userService;
  private final UserCalenderSlotService userCalenderSlotService;
  private final EventParticipantService eventParticipantService;
  private final UserScheduleService userScheduleService;

  public EventServiceImpl(EventRepo eventRepo, UserService userService,
      UserCalenderSlotService userCalenderSlotService,
      EventParticipantService eventParticipantService,
      UserScheduleService userScheduleService) {
    this.eventRepo = eventRepo;
    this.userService = userService;
    this.userCalenderSlotService = userCalenderSlotService;
    this.eventParticipantService = eventParticipantService;
    this.userScheduleService = userScheduleService;
  }

  @Override
  public Event createEvent(String organiser, List<String> participantIds, long startTime,
      long endTime) {
    validateCreateEventInput(organiser, participantIds, startTime, endTime);
    List<String> bookedUserIds = new ArrayList<>(participantIds);
    userScheduleService.ensureUsersSchedulesAreCreatedInCalender(bookedUserIds, startTime, endTime);
    bookedUserIds.add(organiser);
    Event event = new Event(organiser, startTime, endTime);
    eventRepo.save(event);
    List<EventParticipant> participants = bookedUserIds.stream()
        .map(p -> new EventParticipant(p, event.getId())).collect(Collectors.toList());
    eventParticipantService.saveEventParticipants(event.getId(), participants);
    userCalenderSlotService.blockCalenderSlot(bookedUserIds, CalenderSlotRefType.EVENT,
        event.getId(),
        startTime, endTime);
    return event;
  }

  @Override
  public Event getById(String id) {
    PreConditions.notEmpty(id);
    return eventRepo.getById(id);
  }

  @Override
  public List<EventParticipant> getParticipantsById(String eventId) {
    PreConditions.notEmpty(eventId);
    return eventParticipantService.getAllByEventId(eventId);
  }

  @Override
  public Event deleteEvent(String eventId) {
    PreConditions.notEmpty(eventId);
    Event event = eventRepo.deleteById(eventId);
    if (isNull(event)) {
      throw new AssetNotFoundException();
    }
    List<EventParticipant> eventParticipants = eventParticipantService.deleteAllByEventId(eventId);
    userCalenderSlotService.deleteAllSlotsOnEventDeletion(event.getId(),
        eventParticipants.stream().map(EventParticipant::getUserId).collect(
            Collectors.toList()));
    return event;
  }

  @Override
  public TimeSlot getNextAvailableSlot(List<String> userIds, long startTime, long duration) {
    PreConditions.notEmpty(userIds);
    userScheduleService.ensureUsersSchedulesAreCreatedInCalender(userIds, startTime,
        startTime + MAX_TIME_TO_LOOK_FOR_SLOTS);
    return userCalenderSlotService.getCommonAvailableSlot(userIds, startTime, duration);
  }

  @Override
  public List<UserCalenderSlot> getConflicts(String userId, long startTime, long endTime) {
    PreConditions.notEmpty(userId);
    userScheduleService.ensureUsersSchedulesAreCreatedInCalender(singletonList(userId), startTime,
        endTime);
    return userCalenderSlotService.getConflicts(userId, startTime, endTime);
  }

  private void validateCreateEventInput(String organiser, List<String> participantIds, long start,
      long end) {
    if (isNull(organiser) || isNull(participantIds)) {
      throw new InvalidInputException();
    }
    validateCreateEventTimes(start, end);
    ArrayList<String> userIds = new ArrayList<>(participantIds);
    userIds.add(organiser);
    validateUserIds(userIds);
  }

  private void validateUserIds(List<String> ids) {
    List<User> allByIds = userService.getAllByIds(ids);
    if (nonNull(allByIds) && allByIds.size() == ids.size()) {
      return;
    }
    throw new InvalidInputException();
  }

  private void validateCreateEventTimes(long start, long end) {
    if (end <= start) {
      throw new InvalidInputException();
    }
  }
}
