package com.example.demo.domain;

import jakarta.annotation.Nullable;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@PlanningSolution
public class Schedule {

  @ProblemFactCollectionProperty
  private final List<Device> obligatoryDeviceList;

  @Nullable
  @ProblemFactCollectionProperty
  private final List<Device> nonObligatoryDevices;
  @PlanningScore
  private HardMediumSoftScore score;

  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private List<TimeSlot> timeSlotList;

  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private List<SolarWattage> solarWattage;

  @PlanningEntityCollectionProperty
  private List<ObligatoryDeviceUsage> obligatoryDeviceUsageList;

  @Nullable
  @PlanningEntityCollectionProperty
  private List<NonObligatoryDeviceUsage> nonObligatoryDeviceUsages;
  private LocalTime endTime;

  public Schedule(List<Device> obligatoryDeviceList, List<Device> nonObligatoryDevices, List<SolarWattage> solarWattage, List<TimeSlot> timeSlotList, List<ObligatoryDeviceUsage> obligatoryDeviceUsageList, LocalTime endTime, List<NonObligatoryDeviceUsage> nonObligatoryDeviceUsages) {
    this.obligatoryDeviceList = obligatoryDeviceList;
    this.solarWattage = solarWattage;
    this.timeSlotList = timeSlotList;
    this.obligatoryDeviceUsageList = obligatoryDeviceUsageList;
    this.nonObligatoryDeviceUsages = nonObligatoryDeviceUsages;
    this.endTime = endTime;
    this.nonObligatoryDevices = nonObligatoryDevices;
  }

  public Schedule(List<Device> obligatoryDeviceList, List<Device> nonObligatoryDevices, List<SolarWattage> solarWattage, List<TimeSlot> timeSlotList, List<ObligatoryDeviceUsage> obligatoryDeviceUsageList, List<NonObligatoryDeviceUsage> nonObligatoryDeviceUsages) {
    this.obligatoryDeviceList = obligatoryDeviceList;
    this.solarWattage = solarWattage;
    this.timeSlotList = timeSlotList;
    this.obligatoryDeviceUsageList = obligatoryDeviceUsageList;
    this.nonObligatoryDeviceUsages = nonObligatoryDeviceUsages;
    this.endTime = LocalTime.MIDNIGHT;
    this.nonObligatoryDevices = nonObligatoryDevices;
  }

  public Schedule(List<Device> obligatoryDeviceList, List<Device> nonObligatoryDevices, List<SolarWattage> solarWattage, List<TimeSlot> timeSlotList, List<ObligatoryDeviceUsage> obligatoryDeviceUsageList, List<NonObligatoryDeviceUsage> nonObligatoryDeviceUsages, LocalTime endTime) {
    this.obligatoryDeviceList = obligatoryDeviceList;
    this.solarWattage = solarWattage;
    this.timeSlotList = timeSlotList;
    this.obligatoryDeviceUsageList = obligatoryDeviceUsageList;
    this.nonObligatoryDeviceUsages = nonObligatoryDeviceUsages;
    this.endTime = endTime;
    this.nonObligatoryDevices = nonObligatoryDevices;
  }

  public Schedule() {
    obligatoryDeviceList = new ArrayList<>();
    solarWattage = new ArrayList<>();
    nonObligatoryDevices = new ArrayList<>();
  }

  public List<SolarWattage> getSolarWattage() {
    return solarWattage;
  }

  public List<Device> getObligatoryDevices() {
    return obligatoryDeviceList;
  }

  public HardMediumSoftScore getScore() {
    return score;
  }

  public List<TimeSlot> getTimeSlotList() {
    return timeSlotList;
  }

  public List<ObligatoryDeviceUsage> getObligatoryDeviceUsageList() {
    return obligatoryDeviceUsageList;
  }

  public List<NonObligatoryDeviceUsage> getNonObligatoryDeviceUsages() {
    return nonObligatoryDeviceUsages;
  }

  public void setSolarWattage(List<SolarWattage> solarWattage) {
    this.solarWattage = solarWattage;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }
}
