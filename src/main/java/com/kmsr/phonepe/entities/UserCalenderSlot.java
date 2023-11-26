package com.kmsr.phonepe.entities;

import com.kmsr.phonepe.enums.CalenderSlotRefType;
import com.kmsr.phonepe.utils.TimeUtil;
import java.util.UUID;

public class UserCalenderSlot {

  private String id;
  private String userId;
  private long startTime;
  private long endTime;
  private boolean conflict;
  private CalenderSlotRefType refType;
  private String refId;

  public UserCalenderSlot(String userId, long startTime, long endTime, CalenderSlotRefType refType,
      String refId) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.refType = refType;
    this.refId = refId;
  }

  public UserCalenderSlot offSetTimes(long offset) {
    return new UserCalenderSlot(userId, offset + startTime, offset + endTime, refType, refId);
  }

  public CalenderSlotRefType getRefType() {
    return refType;
  }

  public void setRefType(CalenderSlotRefType refType) {
    this.refType = refType;
  }

  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId;
  }

  public boolean isConflict() {
    return conflict;
  }

  public void setConflict(boolean conflict) {
    this.conflict = conflict;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  @Override
  public String toString() {
    return "UserCalenderSlot{" +
        "id='" + id + '\'' +
        ", userId='" + userId + '\'' +
        ", startTime=" + TimeUtil.getHumanReadableTimeFromEpoch(startTime) +
        ", endTime=" + TimeUtil.getHumanReadableTimeFromEpoch(endTime) +
        ", conflict=" + conflict +
        ", refType=" + refType +
        ", refId='" + refId + '\'' +
        '}';
  }
}
