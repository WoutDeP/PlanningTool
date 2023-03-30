package com.example.demo.domain.dto;

import java.util.List;

public class SolvedScheduleDto {
  private List<DeviceUsageDto> deviceUsageDtos;

    public SolvedScheduleDto(List<DeviceUsageDto> deviceUsageDtos) {
        this.deviceUsageDtos = deviceUsageDtos;
    }

    public SolvedScheduleDto() {
    }

    public List<DeviceUsageDto> getDeviceUsageDtos() {
        return deviceUsageDtos;
    }


}
