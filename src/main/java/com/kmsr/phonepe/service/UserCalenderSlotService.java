package com.kmsr.phonepe.service;

import com.kmsr.phonepe.entities.TimeSlot;
import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.enums.CalenderSlotRefType;
import java.util.List;

public interface UserCalenderSlotService {

  void blockCalenderSlot(List<String> userIds, CalenderSlotRefType refType, String refId,
      long startTime, long endTime);

  TimeSlot getCommonAvailableSlot(List<String> userIds, long startTime, long duration);

  @SuppressWarnings("UnusedReturnValue")
  List<UserCalenderSlot> deleteAllSlotsOnEventDeletion(String eventId, List<String> userIds);

  @SuppressWarnings("UnusedReturnValue")
  List<UserCalenderSlot> insertScheduledSlots(List<UserCalenderSlot> slots);

  List<UserCalenderSlot> getConflicts(String userId, long startTime, long endTime);
}
