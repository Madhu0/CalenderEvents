package com.kmsr.phonepe.entities;

import com.kmsr.phonepe.utils.TimeUtil;
import java.util.UUID;

public class Event {

  private String id;
  private String organiserId;
  private long startTime;
  private long endTime;

  public Event(String organiserId, long startTime, long endTime) {
    this.id = UUID.randomUUID().toString();
    this.organiserId = organiserId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOrganiserId() {
    return organiserId;
  }

  public void setOrganiserId(String organiserId) {
    this.organiserId = organiserId;
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
    return "Event{" +
        "id='" + id + '\'' +
        ", organiserId='" + organiserId + '\'' +
        ", startTime=" + TimeUtil.getHumanReadableTimeFromEpoch(startTime) +
        ", endTime=" + TimeUtil.getHumanReadableTimeFromEpoch(endTime) +
        '}';
  }
}
