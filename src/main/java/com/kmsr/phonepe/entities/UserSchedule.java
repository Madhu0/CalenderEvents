package com.kmsr.phonepe.entities;

import com.kmsr.phonepe.enums.Status;
import com.kmsr.phonepe.utils.TimeUtil;
import java.util.UUID;

public class UserSchedule {

  private String id;
  private String userId;
  private long startTime;
  private long endTime;
  private Status status;

  public UserSchedule(String userId, long startTime, long endTime) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
    this.startTime = startTime;
    this.endTime = endTime;
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
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

  @Override
  public String toString() {
    return "UserSchedule{" +
        "id='" + id + '\'' +
        ", userId='" + userId + '\'' +
        ", startTime=" + TimeUtil.getHumanReadableTimeFromEpoch(startTime) +
        ", endTime=" + TimeUtil.getHumanReadableTimeFromEpoch(endTime) +
        ", status=" + status +
        '}';
  }
}
