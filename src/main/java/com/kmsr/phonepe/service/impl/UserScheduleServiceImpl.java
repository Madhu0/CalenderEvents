package com.kmsr.phonepe.service.impl;

import static java.util.Objects.isNull;

import com.kmsr.phonepe.Exceptions.InvalidInputException;
import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.entities.UserSchedule;
import com.kmsr.phonepe.enums.CalenderSlotRefType;
import com.kmsr.phonepe.enums.Status;
import com.kmsr.phonepe.repo.UserScheduleRepo;
import com.kmsr.phonepe.service.UserCalenderSlotService;
import com.kmsr.phonepe.service.UserScheduleService;
import com.kmsr.phonepe.utils.PreConditions;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserScheduleServiceImpl implements UserScheduleService {

  private static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

  private final UserScheduleRepo userScheduleRepo;
  private final UserCalenderSlotService userCalenderSlotService;
  private final Map<String, Boolean> userScheduleSlotInsertTracker;

  public UserScheduleServiceImpl(UserScheduleRepo userScheduleRepo,
      UserCalenderSlotService userCalenderSlotService) {
    this.userScheduleRepo = userScheduleRepo;
    this.userCalenderSlotService = userCalenderSlotService;
    this.userScheduleSlotInsertTracker = new HashMap<>();
  }

  @Override
  public List<UserSchedule> getByUserId(String userId) {
    PreConditions.notEmpty(userId);
    return userScheduleRepo.getAllByUserId(userId);
  }

  @Override
  public UserSchedule getById(String id) {
    PreConditions.notEmpty(id);
    return userScheduleRepo.getById(id);
  }

  @Override
  public UserSchedule addShift(String userId, long startTime, long endTime) {
    return createUserSchedule(userId, startTime, endTime, Status.AVAILABLE);
  }

  @Override
  public UserSchedule updateShift(String id, long startTime, long endTime) {
    /*
    1. Delete the existing slots with refId as id
    2. recompute the slot for all the days where the slots are already created
     */
    throw new UnsupportedOperationException();
  }

  @Override
  public UserSchedule addBusySlot(String userId, long startTime, long endTime) {
    return createUserSchedule(userId, startTime, endTime, Status.BUSY);
  }

  @Override
  public UserSchedule updateBusySlot(String userId, long startTime, long endTime) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void ensureUsersSchedulesAreCreatedInCalender(List<String> userIds, long startTime,
      long endTime) {
    List<UserCalenderSlot> finalCalenderSlots = new ArrayList<>();
    for (String userId : userIds) {
      List<UserCalenderSlot> schedules = getUserScheduleCalenderSlots(userId);
      finalCalenderSlots.addAll(
          offSetSchedulesWithEpochForUser(userId, schedules, startTime, endTime));
    }
    userCalenderSlotService.insertScheduledSlots(finalCalenderSlots);
  }

  private List<UserCalenderSlot> getUserScheduleCalenderSlots(String userId) {
    List<UserSchedule> userSchedules = getByUserId(userId);
    return userSchedules.stream().map(this::toCalenderSlot)
        .flatMap(Collection::stream).collect(Collectors.toList());
    // Do we have to merge duplicate schedules?
  }

  private List<UserCalenderSlot> offSetSchedulesWithEpochForUser(String userId,
      List<UserCalenderSlot> slots, long startTime, long endTime) {
    if (isNull(slots) || slots.isEmpty()) {
      return slots;
    }
    LocalDate curr = Instant.ofEpochMilli(startTime).atOffset(ZoneOffset.UTC).toLocalDate();
    LocalDate end = Instant.ofEpochMilli(endTime).atOffset(ZoneOffset.UTC).toLocalDate();
    List<UserCalenderSlot> offSetSlots = new ArrayList<>();
    while (curr.compareTo(end) <= 0) {
      String scheduleSlotTrackerId = getScheduleSlotTrackerId(userId, curr);
      if (!userScheduleSlotInsertTracker.containsKey(scheduleSlotTrackerId)) {
        long currEpoch = curr.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        offSetSlots.addAll(
            slots.stream().map(s -> s.offSetTimes(currEpoch)).collect(Collectors.toList()));
        userScheduleSlotInsertTracker.put(scheduleSlotTrackerId, true);
      }
      curr = curr.plusDays(1);
    }
    return offSetSlots;
  }

  private List<UserCalenderSlot> toCalenderSlot(UserSchedule schedule) {
    List<UserCalenderSlot> calenderSlots = new ArrayList<>();
    if (schedule.getStatus().equals(Status.BUSY)) {
      calenderSlots.add(new UserCalenderSlot(schedule.getUserId(), schedule.getStartTime(),
          schedule.getEndTime(), CalenderSlotRefType.USER_SCHEDULE, schedule.getId()));
      return calenderSlots;
    }
    if (0 != schedule.getStartTime()) {
      calenderSlots.add(
          new UserCalenderSlot(schedule.getUserId(), 0, schedule.getStartTime(),
              CalenderSlotRefType.USER_SCHEDULE, schedule.getId()));
    }
    if (schedule.getEndTime() != DAY_IN_MILLIS) {
      calenderSlots.add(
          new UserCalenderSlot(schedule.getUserId(), schedule.getEndTime(), DAY_IN_MILLIS,
              CalenderSlotRefType.USER_SCHEDULE, schedule.getId()));
    }
    return calenderSlots;
  }

  private void validateScheduleTimeRange(long startTime, long endTime) {
    if (startTime > endTime) {
      throw new InvalidInputException();
    }
    if (startTime > DAY_IN_MILLIS || endTime > DAY_IN_MILLIS) {
      throw new InvalidInputException();
    }
  }

  private UserSchedule createUserSchedule(String userId, long startTime, long endTime,
      Status status) {
    PreConditions.notEmpty(userId);
    validateScheduleTimeRange(startTime, endTime);
    UserSchedule schedule = new UserSchedule(userId, startTime, endTime);
    schedule.setStatus(status);
    return userScheduleRepo.save(schedule);
  }

  private String getScheduleSlotTrackerId(String userId, LocalDate day) {
    return String.format("%s_%s", userId, day.toString());
  }
}
