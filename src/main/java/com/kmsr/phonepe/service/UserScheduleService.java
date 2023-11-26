package com.kmsr.phonepe.service;

import com.kmsr.phonepe.entities.UserSchedule;
import java.util.List;

public interface UserScheduleService {

  List<UserSchedule> getByUserId(String userId);

  UserSchedule getById(String id);

  UserSchedule addShift(String userId, long startTime, long endTime);

  UserSchedule updateShift(String id, long startTime, long endTime);

  UserSchedule addBusySlot(String userId, long startTime, long endTime);

  UserSchedule updateBusySlot(String userId, long startTime, long endTime);

  void ensureUsersSchedulesAreCreatedInCalender(List<String> userIds, long startTime, long endTime);
}
