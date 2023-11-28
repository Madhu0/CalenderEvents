package com.kmsr.phonepe.service.impl;

import static com.kmsr.phonepe.utils.Constants.MAX_TIME_TO_LOOK_FOR_SLOTS;
import static java.util.Objects.isNull;

import com.kmsr.phonepe.Exceptions.InvalidInputException;
import com.kmsr.phonepe.Exceptions.UnableToFindAvailableSlot;
import com.kmsr.phonepe.entities.TimeSlot;
import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.enums.CalenderSlotRefType;
import com.kmsr.phonepe.repo.UserCalenderSlotRepo;
import com.kmsr.phonepe.service.UserCalenderSlotService;
import com.kmsr.phonepe.utils.PreConditions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserCalenderSlotServiceImpl implements UserCalenderSlotService {

  private final UserCalenderSlotRepo userCalenderSlotRepo;

  public UserCalenderSlotServiceImpl(UserCalenderSlotRepo userCalenderSlotRepo) {
    this.userCalenderSlotRepo = userCalenderSlotRepo;
  }

  @Override
  public void blockCalenderSlot(List<String> userIds, CalenderSlotRefType refType, String refId,
      long startTime, long endTime) {
    PreConditions.notEmpty(userIds);
    PreConditions.nonNull(refType);
    PreConditions.notEmpty(refId);
    Map<String, List<UserCalenderSlot>> conflictingSlots = getConflictingSlots(userIds, startTime,
        endTime);
    List<UserCalenderSlot> newSlots = new ArrayList<>();
    for (String userId : userIds) {
      UserCalenderSlot newSlot = new UserCalenderSlot(userId, startTime, endTime, refType, refId);
      if (conflictingSlots.containsKey(userId)) {
        newSlot.setConflict(true);
        conflictingSlots.get(userId).forEach(t -> t.setConflict(true));
      }
      newSlots.add(newSlot);
    }
    userCalenderSlotRepo.addSlots(newSlots);
  }

  @Override
  public TimeSlot getCommonAvailableSlot(List<String> userIds, long startTime, long duration) {
    PreConditions.notEmpty(userIds);
    validateDuration(duration);
    List<UserCalenderSlot> allInRangeByUserIds = userCalenderSlotRepo.getAllInRangeByUserIds(
        userIds, startTime, startTime + MAX_TIME_TO_LOOK_FOR_SLOTS);
    if (isNull(allInRangeByUserIds) || allInRangeByUserIds.isEmpty()) {
      return new TimeSlot(startTime, startTime + duration);
    }
    return findAvailableSlot(allInRangeByUserIds, startTime, duration);
  }

  @Override
  public List<UserCalenderSlot> deleteAllSlotsOnEventDeletion(String eventId,
      List<String> userIds) {
    PreConditions.notEmpty(eventId);
    PreConditions.notEmpty(userIds);
    List<UserCalenderSlot> calenderSlots = userCalenderSlotRepo.deleteAllByUserIdsAndRefId(userIds,
        CalenderSlotRefType.EVENT,
        eventId);
    /**
     * Refreshing the conflicts by the user is very costly, as it goes through every slot and checks for
     * conflicts. Few optimisations to consider
     * 1. Reduce the range in which conflicts are affected. For every slot, get the conflicting ones
     * from query, which goes through the startTime and endTime indices so may instead O(n2) it
     * would be O(nlogn)
     * 2. If we can maintain the conflicts for which slot with a reference, then figuring out which
     * conflicts the event deletion would affect is very easy. Since this requires changing a lot of
     * places, ignoring for now.
     */
    refreshConflictsByUsers(userIds);
    return calenderSlots;
  }

  @Override
  @SuppressWarnings("unused-return-types")
  public List<UserCalenderSlot> insertScheduledSlots(List<UserCalenderSlot> slots) {
    PreConditions.notEmpty(slots);
    return userCalenderSlotRepo.addSlots(slots);
  }

  @Override
  public List<UserCalenderSlot> getConflicts(String userId, long startTime, long endTime) {
    PreConditions.notEmpty(userId);
    List<UserCalenderSlot> slots = userCalenderSlotRepo.getAllInRangeByUserId(userId,
        startTime, endTime);
    return slots.stream().filter(UserCalenderSlot::isConflict).collect(Collectors.toList());
  }

  private TimeSlot findAvailableSlot(List<UserCalenderSlot> slots, long startTime, long duration) {
    PreConditions.notEmpty(slots);
    long currEnd = startTime;
    List<UserCalenderSlot> sortedSlots = slots.stream()
        .sorted(Comparator.comparing(UserCalenderSlot::getStartTime)).collect(Collectors.toList());
    for (UserCalenderSlot slot : sortedSlots) {
      if (currEnd + duration <= slot.getStartTime()) {
        return new TimeSlot(currEnd, currEnd + duration);
      }
      if (currEnd < slot.getEndTime()) {
        currEnd = slot.getEndTime();
        ;
      }
    }
    if (currEnd < startTime + MAX_TIME_TO_LOOK_FOR_SLOTS) {
      return new TimeSlot(currEnd, currEnd + duration);
    }
    throw new UnableToFindAvailableSlot();
  }

  private void validateDuration(long duration) {
    long days = duration / TimeUnit.HOURS.toMillis(24);
    if (days > 1) {
      throw new InvalidInputException();
    }
  }

//  private Map<String, Boolean> getUserIdMapWithConflict(List<String> userIds, long startTime,
//      long endTime) {
//    List<String> bookedUserIds = userCalenderSlotRepo.getUserIdsWithASlotInRange(userIds, startTime,
//        endTime);
//    return bookedUserIds.stream().collect(Collectors.toMap(k -> k, k -> true));
//  }

  private Map<String, List<UserCalenderSlot>> getConflictingSlots(List<String> userIds, long startTime,
      long endTime) {
    List<UserCalenderSlot> conflictingSlots = userCalenderSlotRepo.getAllInRangeByUserIds(
        userIds, startTime, endTime);
    return conflictingSlots.stream()
        .collect(Collectors.groupingBy(UserCalenderSlot::getUserId));
  }

  /**
   * Too costly of an operation to compare each slot of a user with another
   * 1. The conflicts will only effect
   * @param userIds
   */
  private void refreshConflictsByUsers(List<String> userIds) {
    for (String userId:userIds) {
      List<UserCalenderSlot> allByUserId = userCalenderSlotRepo.getAllByUserId(userId);
      markConflicts(allByUserId);
    }
  }

  private void markConflicts(List<UserCalenderSlot> slots) {
    if (isNull(slots) || slots.isEmpty()) {
      return;
    }
    slots.forEach(s -> s.setConflict(false));
    for (UserCalenderSlot slot1: slots) {
      for (UserCalenderSlot slot2: slots) {
        if (!slot1.getId().equals(slot2.getId()) && areMeetingsConflicting(slot1, slot2)) {
          slot1.setConflict(true);
          slot2.setConflict(true);
        }
      }
    }
  }

  private boolean areMeetingsConflicting(UserCalenderSlot slot1, UserCalenderSlot slot2) {
    return slot1.getStartTime() < slot2.getEndTime() && slot1.getEndTime() > slot2.getStartTime();
  }
}
