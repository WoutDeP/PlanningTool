package com.example.demo.domain;

import java.time.LocalTime;

public class Device {
  private String id;

  private String friendly_name;

  private int importance;

  private int duration; //in hours, can be null

  private int consumption;

  private boolean splittable;

  private String state;

  private boolean obligatory;

  private LocalTime startTime;

  private LocalTime endTime;

  public Device() {
  }

  public Device(String id, String friendly_name, int importance, int duration, int consumption, boolean splittable, LocalTime startTime, LocalTime endTime) {
    this.friendly_name = friendly_name;
    this.id = id;
    this.importance = importance; //check
    this.duration = duration;
    this.consumption = consumption;
    this.splittable = splittable;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Device(String id, String friendly_name, int importance, int duration, int consumption, boolean splittable, LocalTime startTime, LocalTime endTime, boolean obligatory) {
    this.friendly_name = friendly_name;
    this.id = id;
    this.importance = importance; //check
    this.duration = duration;
    this.consumption = consumption;
    this.splittable = splittable;
    this.startTime = startTime;
    this.endTime = endTime;
    this.obligatory = obligatory;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFriendly_name() {
    return friendly_name;
  }

  public void setFriendly_name(String friendly_name) {
    this.friendly_name = friendly_name;
  }

  public int getImportance() {
    return importance;
  }

  public void setImportance(int importance) {
    this.importance = importance;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getConsumption() {
    return consumption;
  }

  public void setConsumption(int consumption) {
    this.consumption = consumption;
  }

  public boolean isSplittable() {
    return splittable;
  }

  public void setSplittable(boolean splittable) {
    this.splittable = splittable;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public boolean isObligatory() {
    return obligatory;
  }

  public void setObligatory(boolean obligatory) {
    this.obligatory = obligatory;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  @Override
  public String toString() {
    var starttimeString = startTime == null ? "" : startTime.toString();
    var endTimeString = endTime == null ? "" : endTime.toString();
    return "{" +
        "\"id\": \"" + id +
        "\",\"friendly_name\": \"" + friendly_name +
        "\", \"importance\": " + importance +
        ", \"duration\": " + duration +
        ", \"consumption\": " + consumption +
        ", \"splitable\": " + splittable +
        ", \"obligatory\": " + obligatory +
        ", \"startTime\": \"" + starttimeString +
        "\", \"endTime\": \"" + endTimeString +
        "\"}";
  }
}