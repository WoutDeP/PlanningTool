package com.example.demo.domain.dto;

import java.time.LocalDateTime;

public class SolarWattageDto {
  private LocalDateTime timeForWattage;

  private int wattage;

  public SolarWattageDto(LocalDateTime timeForWattage, int wattage) {
    this.timeForWattage = timeForWattage;
    this.wattage = wattage;
  }

  public LocalDateTime getTimeForWattage() {
    return timeForWattage;
  }

  public int getWattage() {
    return wattage;
  }

  @Override
  public String toString() {
    return "{" +
        "\"timeForWattage\": \"" + timeForWattage +
        "\", \"wattage\": " + wattage +
        "}";
  }
}
