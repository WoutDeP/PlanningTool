package com.example.demo.domain.dto;

import com.example.demo.domain.Device;

import java.time.LocalTime;
import java.util.List;

public class ScheduleDto {
  private List<Device> deviceList;
  private LocalTime startTime;
  private LocalTime endTime;
  private String token;

  private int scheduleTime;

  private List<SolarWattageDto> solarWattageList;

  public ScheduleDto() {
  }

  public ScheduleDto(List<Device> deviceList, LocalTime startTime, LocalTime endTime, List<SolarWattageDto> solarWattageList, String token, int scheduleTime) {
    this.deviceList = deviceList;
    this.startTime = startTime;
    this.endTime = endTime;
    this.solarWattageList = solarWattageList;
    this.token = token;
    this.scheduleTime = scheduleTime;
  }

  public List<Device> getDeviceList() {
    return deviceList;
  }

  public List<SolarWattageDto> getSolarWattageList() {
    return solarWattageList;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getScheduleTime() {
    return scheduleTime;
  }

  public void setScheduleTime(int scheduleTime) {
    this.scheduleTime = scheduleTime;
  }
/*
  @Override
  public String toString() {
    return "{" +
        "\"deviceList\": " + deviceList +
        ", \"startTime\": " + startTime +
        ", \"endTime\": " + endTime +
        ", \"solarWattageList\": " + solarWattageList +
        "}";
  }*/

  @Override
  public String toString() {
    return "{" +
        "\"deviceList\": " + deviceList +
        ",\"startTime\": \"" + startTime.toString() +
        "\", \"endTime\": \"" + endTime.toString() +
        "\", \"solarWattageList\": " + solarWattageList +
        ", \"token\": \"" + token +
        "\", \"scheduleTime\": \"" + scheduleTime +
        "\"}";
  }
}
