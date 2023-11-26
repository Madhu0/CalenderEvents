package com.kmsr.phonepe.entities;

import java.util.UUID;

public class EventParticipant {

  private String id;
  private String userId;
  private String eventId;

  public EventParticipant(String userId, String eventId) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
    this.eventId = eventId;
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

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  @Override
  public String toString() {
    return "EventParticipant{" +
        "id='" + id + '\'' +
        ", userId='" + userId + '\'' +
        ", eventId='" + eventId + '\'' +
        '}';
  }
}
