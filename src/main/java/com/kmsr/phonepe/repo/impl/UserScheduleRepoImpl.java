package com.kmsr.phonepe.repo.impl;

import static java.util.Objects.isNull;

import com.kmsr.phonepe.entities.UserSchedule;
import com.kmsr.phonepe.repo.UserScheduleRepo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserScheduleRepoImpl implements UserScheduleRepo {

  private final Map<String, UserSchedule> scheduleMap;
  private final Map<String, Set<String>> userIdMap;

  public UserScheduleRepoImpl() {
    scheduleMap = new HashMap<>();
    userIdMap = new HashMap<>();
  }

  @Override
  public UserSchedule save(UserSchedule userSchedule) {
    scheduleMap.put(userSchedule.getId(), userSchedule);
    updateUserIdMap(userSchedule);
    return userSchedule;
  }

  @Override
  public UserSchedule getById(String id) {
    return scheduleMap.get(id);
  }

  @Override
  public List<UserSchedule> getAllByUserId(String userId) {
    Set<String> strings = userIdMap.getOrDefault(userId, new HashSet<>());
    return strings.stream().map(scheduleMap::get).collect(Collectors.toList());
  }

  @Override
  public UserSchedule deleteById(String id) {
    UserSchedule schedule = scheduleMap.get(id);
    userIdMap.get(schedule.getUserId()).remove(schedule.getId());
    return schedule;
  }

  private void updateUserIdMap(UserSchedule userSchedule) {
    Set<String> strings = userIdMap.get(userSchedule.getUserId());
    if (isNull(strings)) {
      strings = new HashSet<>();
      userIdMap.put(userSchedule.getUserId(), strings);
    }
    strings.add(userSchedule.getId());
  }
}
