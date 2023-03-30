package com.example.demo.domain.dto;

import java.time.LocalTime;

public class DeviceUsageDto {
  private String name;
  private String entityId;
  private int wattage;
  private LocalTime start;
  private LocalTime end;

  public DeviceUsageDto(String name, String entityId, int wattage, LocalTime start, LocalTime end) {
    this.name = name;
    this.entityId = entityId;
    this.wattage = wattage;
    this.start = start;
    this.end = end;
  }

  public DeviceUsageDto() {
  }

  public String getName() {
    return name;
  }

  public int getWattage() {
    return wattage;
  }

  public LocalTime getStart() {
    return start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWattage(int wattage) {
    this.wattage = wattage;
  }

  public void setStart(LocalTime start) {
    this.start = start;
  }

  public void setEnd(LocalTime end) {
    this.end = end;
  }
}
