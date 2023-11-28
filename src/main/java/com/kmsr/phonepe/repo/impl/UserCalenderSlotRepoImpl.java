package com.kmsr.phonepe.repo.impl;

import static java.util.Objects.isNull;

import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.enums.CalenderSlotRefType;
import com.kmsr.phonepe.repo.UserCalenderSlotRepo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class UserCalenderSlotRepoImpl implements UserCalenderSlotRepo {

  private final Map<String, UserCalenderSlot> idMap;
  private final Map<String, UserIdMapVal> userIdTimeMap;

  public UserCalenderSlotRepoImpl() {
    this.idMap = new HashMap<>();
    this.userIdTimeMap = new HashMap<>();
  }

  @Override
  public List<UserCalenderSlot> getAllByUserId(String userId) {
    Collection<Set<String>> values = userIdTimeMap.get(userId).startTimeMap.values();
    return values.stream().flatMap(Collection::stream).map(idMap::get).collect(Collectors.toList());
  }

  @Override
  public List<UserCalenderSlot> getAllInRangeByUserId(String userId, long startTime, long endTime) {
    return getIdsOfAllInRangeByUserId(userId, startTime, endTime).stream().map(idMap::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserCalenderSlot> getAllInRangeByUserIds(List<String> userIds, long startTime,
      long endTime) {
    return userIds.stream().map(id -> getAllInRangeByUserId(id, startTime, endTime))
        .flatMap(List::stream).collect(Collectors.toList());
  }

  @Override
  public List<UserCalenderSlot> addSlots(List<UserCalenderSlot> slots) {
    slots.forEach(slot -> {
      idMap.put(slot.getId(), slot);
      updateUserIdTimeMap(slot);
    });
    return slots;
  }

//  @Override
//  public List<UserCalenderSlot> addForAllUserIds(List<String> userIds, long startTime, long endTime,
//      CalenderSlotRefType refType, String refId) {
//    return userIds.stream().map(u -> addForAUserId(u, startTime, endTime, refType, refId)).collect(
//        Collectors.toList());
//  }

  @Override
  public List<UserCalenderSlot> deleteAllByUserIdsAndRefId(List<String> userIds,
      CalenderSlotRefType refType,
      String refId) {
    List<UserCalenderSlot> deleted = new ArrayList<>();
    for (String userId : userIds) {
      List<UserCalenderSlot> allByUserId = getAllByUserId(userId);
      allByUserId.stream()
        .filter(userCalenderSlot -> userCalenderSlot.getRefType().equals(refType)
            && userCalenderSlot.getRefId().equals(refId))
        .forEach(userCalenderSlot -> {
          idMap.remove(userCalenderSlot.getId());
          UserIdMapVal userIdMapVal = userIdTimeMap.get(userId);
          userIdMapVal.startTimeMap.getOrDefault(userCalenderSlot.getStartTime(), new HashSet<>())
              .remove(userCalenderSlot.getId());
          userIdMapVal.endTimeMap.getOrDefault(userCalenderSlot.getEndTime(), new HashSet<>())
              .remove(userCalenderSlot.getId());
          deleted.add(userCalenderSlot);
        });
    }
    return deleted;
  }

  @Override
  public List<String> getUserIdsWithASlotInRange(List<String> userIds, long startTime,
      long endTime) {
    List<String> bookedUserIds = new ArrayList<>();
    for (String userId : userIds) {
      UserIdMapVal userIdMapVal = userIdTimeMap.get(userId);
      if (isNull(userIdMapVal)) {
        continue;
      }
      NavigableMap<Long, Set<String>> longSetNavigableMap = userIdMapVal.startTimeMap.subMap(
          startTime, true, endTime, false);
      if (longSetNavigableMap.isEmpty()) {
        continue;
      }
      bookedUserIds.add(userId);
    }
    return bookedUserIds;
  }

//  private UserCalenderSlot addForAUserId(String userId, long startTime, long endTime,
//      CalenderSlotRefType refType, String refId) {
//    UserCalenderSlot userCalenderSlot = new UserCalenderSlot(userId, startTime, endTime, refType, refId);
//    idMap.put(userCalenderSlot.getId(), userCalenderSlot);
//    updateUserIdTimeMap(userCalenderSlot);
//    return userCalenderSlot;
//  }

  private void updateUserIdTimeMap(UserCalenderSlot slot) {
    UserIdMapVal userIdMapVal = userIdTimeMap.get(slot.getUserId());
    if (isNull(userIdMapVal)) {
      userIdMapVal = new UserIdMapVal();
      userIdTimeMap.put(slot.getUserId(), userIdMapVal);
    }
    if (!userIdMapVal.startTimeMap.containsKey(slot.getStartTime())) {
      userIdMapVal.startTimeMap.put(slot.getStartTime(), new HashSet<>());
    }
    userIdMapVal.startTimeMap.get(slot.getStartTime()).add(slot.getId());

    if (!userIdMapVal.endTimeMap.containsKey(slot.getEndTime())) {
      userIdMapVal.endTimeMap.put(slot.getEndTime(), new HashSet<>());
    }
    userIdMapVal.endTimeMap.get(slot.getEndTime()).add(slot.getId());
  }

  private Set<String> getIdsOfAllInRangeByUserId(String userId, long startTime, long endTime) {
    UserIdMapVal userIdMapVal = userIdTimeMap.get(userId);
    if (isNull(userIdMapVal)) {
      return Collections.emptySet();
    }
    // Find any booked slot that starts before the endTime we are looking for
    Set<String> startingBefore = userIdMapVal.startTimeMap.headMap(endTime, false)
        .values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    // Find any booked slot that ends after the startTime we are looking for
    Set<String> endingAfter = userIdMapVal.endTimeMap.tailMap(startTime, false)
        .values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    startingBefore.retainAll(endingAfter);
    return startingBefore;
//    startingBefore.addAll(endingAfter);
//    return startingBefore.stream().filter(id -> {
//      UserCalenderSlot slot = idMap.get(id);
//      return startTime < slot.getEndTime() && endTime > slot.getStartTime();
//    }).collect(Collectors.toSet());
  }

  private static class UserIdMapVal {

    public NavigableMap<Long, Set<String>> startTimeMap;
    public NavigableMap<Long, Set<String>> endTimeMap;

    public UserIdMapVal() {
      startTimeMap = new TreeMap<>();
      endTimeMap = new TreeMap<>();
    }
  }

}
