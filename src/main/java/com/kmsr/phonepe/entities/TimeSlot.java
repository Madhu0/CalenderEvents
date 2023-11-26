package com.kmsr.phonepe.entities;

import com.kmsr.phonepe.utils.TimeUtil;

public class TimeSlot {

  private long startTime;
  private long endTime;

  public TimeSlot(long startTime, long endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  @Override
  public String toString() {
    return "TimeSlot{" +
        "startTime=" + TimeUtil.getHumanReadableTimeFromEpoch(startTime) +
        ", endTime=" + TimeUtil.getHumanReadableTimeFromEpoch(endTime) +
        '}';
  }
}
