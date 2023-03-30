package com.example.demo.domain;

public class SolarWattage {
  private int solarId;
  private double wattage;

  private TimeSlot timeSlot;

  public SolarWattage(int solarId, double wattage, TimeSlot timeSlot) {
    this.wattage = wattage;
    this.timeSlot = timeSlot;
    this.solarId = solarId;
  }

  public SolarWattage() {
  }

  public SolarWattage clone() {
    SolarWattage s = new SolarWattage();
    s.solarId = solarId;
    s.wattage = wattage;
    s.timeSlot = timeSlot;
    return s;
  }

  public double getWattage() {
    return wattage;
  }

  public TimeSlot getTimeSlot() {
    return timeSlot;
  }

  public void setWattage(double wattage) {
    this.wattage = wattage;
  }

  public int getSolarId() {
    return solarId;
  }

  public void setSolarId(int solarId) {
    this.solarId = solarId;
  }

  @Override
  public String toString() {
    return "SolarWattage{" +
        "solarId=" + solarId +
        ", wattage=" + wattage +
        ", timeSlot=" + timeSlot +
        '}';
  }
}
