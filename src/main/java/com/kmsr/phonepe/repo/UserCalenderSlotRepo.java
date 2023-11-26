package com.kmsr.phonepe.repo;

import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.enums.CalenderSlotRefType;
import java.util.List;

public interface UserCalenderSlotRepo {

  List<UserCalenderSlot> getAllByUserId(String userId);

  List<UserCalenderSlot> getAllInRangeByUserId(String userId, long startTime, long endTime);

  List<UserCalenderSlot> getAllInRangeByUserIds(List<String> userIds, long startTime, long endTime);

  List<UserCalenderSlot> addSlots(List<UserCalenderSlot> slots);

  List<UserCalenderSlot> deleteAllByUserIdsAndRefId(List<String> userIds,
      CalenderSlotRefType refType, String refId);

  List<String> getUserIdsWithASlotInRange(List<String> userIds, long startTime, long endTime);
}
