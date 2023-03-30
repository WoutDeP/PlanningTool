package com.example.demo;

import com.example.demo.domain.*;
import com.example.demo.domain.dto.DeviceUsageDto;
import com.example.demo.domain.dto.ScheduleDto;
import com.example.demo.domain.dto.SolarWattageDto;
import com.example.demo.domain.dto.SolvedScheduleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(path = "api/schedule")
@CrossOrigin(origins = "http://homeassistant.local:8123")
public class ScheduleController {
  private final ScheduleService scheduleService;

  private final ScheduleTasks scheduleTasks;

  private final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

  public ScheduleController(ScheduleService scheduleService, ScheduleTasks scheduleTasks) {
    this.scheduleService = scheduleService;
    this.scheduleTasks = scheduleTasks;
  }

  @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SolvedScheduleDto> postSchedule(@RequestBody ScheduleDto scheduleDto) {
    var obligatoryDeviceList = scheduleDto.getDeviceList().stream().filter(Device::isObligatory).toList();
    var optionalDeviceList = scheduleDto.getDeviceList().stream().filter(device -> !device.isObligatory()).toList();

    //generate timeslotList
    List<TimeSlot> timeSlotList = new ArrayList<>();
    var slotCounter = 0;
    var solarWattageList = new ArrayList<SolarWattage>();
    while (!timeSlotList.stream().map(TimeSlot::getStart).toList().contains(scheduleDto.getEndTime())) {
      var currentTimeSlot = new TimeSlot(slotCounter, scheduleDto.getStartTime().plusMinutes(slotCounter));
      timeSlotList.add(currentTimeSlot);
      if (scheduleDto.getSolarWattageList().size()-1 > 0) {
        scheduleDto.getSolarWattageList().sort(Comparator.comparing(SolarWattageDto::getTimeForWattage));
        var currentWattage = new SolarWattage();
        for (int i = 0; i < scheduleDto.getSolarWattageList().size(); i++) {
          int timeForWattage;
          var solarWattageDto = scheduleDto.getSolarWattageList().get(i);
          if (i + 1 != scheduleDto.getSolarWattageList().size()) {
            var nextSolarWattageDto = scheduleDto.getSolarWattageList().get(i+1);
            timeForWattage = (int) (solarWattageDto.getTimeForWattage().until(nextSolarWattageDto.getTimeForWattage(), java.time.temporal.ChronoUnit.MINUTES));

            if (solarWattageDto.getTimeForWattage().toLocalTime().equals(currentTimeSlot.getStart()) || (currentTimeSlot.getStart().isAfter(solarWattageDto.getTimeForWattage().toLocalTime())) && (currentTimeSlot.getStart().isBefore(scheduleDto.getSolarWattageList().get(i+1).getTimeForWattage().toLocalTime()))) {
              currentWattage = new SolarWattage(currentTimeSlot.getTimeSlotId(), (double) nextSolarWattageDto.getWattage() / timeForWattage, currentTimeSlot);
            }
          }
        }
        solarWattageList.add(currentWattage);
      }
      slotCounter++;
    }
    var schedule = new Schedule(obligatoryDeviceList, optionalDeviceList, solarWattageList, timeSlotList, scheduleService.getObligatoryDeviceUsage(obligatoryDeviceList), scheduleDto.getEndTime(),scheduleService.getOptionalDeviceUsage(optionalDeviceList));
    schedule = scheduleService.solveProblem(schedule, scheduleDto.getScheduleTime());
    List<DeviceUsageDto> deviceUsageDtos = new ArrayList<>();
    for (
        var obligatoryDeviceUsage : schedule.getObligatoryDeviceUsageList()) {
      if (obligatoryDeviceUsage.getTimeSlot() != null) {
        deviceUsageDtos.add(new DeviceUsageDto(obligatoryDeviceUsage.getDevice().getFriendly_name(), obligatoryDeviceUsage.getDevice().getId(), obligatoryDeviceUsage.getDevice().getConsumption(), obligatoryDeviceUsage.getTimeSlot().getStart(), obligatoryDeviceUsage.getTimeSlot().getStart().plusMinutes(obligatoryDeviceUsage.getDevice().getDuration())));
      } else {
        deviceUsageDtos.add(new DeviceUsageDto(obligatoryDeviceUsage.getDevice().getFriendly_name(), obligatoryDeviceUsage.getDevice().getId(), obligatoryDeviceUsage.getDevice().getConsumption(), null, null));
      }
    }
    for (
        var optionalDeviceUsage : schedule.getNonObligatoryDeviceUsages()) {
      if (optionalDeviceUsage.getTimeSlot() != null) {
        deviceUsageDtos.add(new DeviceUsageDto(optionalDeviceUsage.getDevice().getFriendly_name(), optionalDeviceUsage.getDevice().getId(), optionalDeviceUsage.getDevice().getConsumption(), optionalDeviceUsage.getTimeSlot().getStart(), optionalDeviceUsage.getTimeSlot().getStart().plusMinutes(optionalDeviceUsage.getDevice().getDuration())));
      } else {
        deviceUsageDtos.add(new DeviceUsageDto(optionalDeviceUsage.getDevice().getFriendly_name(), optionalDeviceUsage.getDevice().getId(), optionalDeviceUsage.getDevice().getConsumption(), null, null));
      }
    }

    var solvedScheduleDto = new SolvedScheduleDto(deviceUsageDtos);
    scheduleTasks.setSchedule(solvedScheduleDto);
    scheduleTasks.setToken(scheduleDto.getToken());
    return ResponseEntity.ok(solvedScheduleDto);
  }
}
