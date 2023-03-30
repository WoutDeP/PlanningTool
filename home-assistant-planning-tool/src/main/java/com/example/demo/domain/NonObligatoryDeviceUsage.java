package com.example.demo.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class NonObligatoryDeviceUsage {
  private Device device;

  @PlanningVariable(nullable = true)
  private TimeSlot timeSlot;

  public NonObligatoryDeviceUsage(Device device) {
    this.device = device;
  }

  public NonObligatoryDeviceUsage() {
  }

  public Device getDevice() {
    return device;
  }

  public TimeSlot getTimeSlot() {
    return timeSlot;
  }

  public void setDevice(Device device) {
    this.device = device;
  }

  public void setTimeSlot(TimeSlot timeSlot) {
    this.timeSlot = timeSlot;
  }
}
