package com.example.demo;

import com.example.demo.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TestScoreCalculator {
  @Autowired
  private ScheduleService scheduleService;

  private List<Device> getObligatoryDevices() {
    var obligatoryDevices = new ArrayList<Device>(3);
    obligatoryDevices.add(new Device("1L", "Wasmachine", 1, 30, 20, false, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 90, 40, false, null, null));
    obligatoryDevices.add(new Device("3L", "Afwasmachine", 3, 30, 20, false, null, null));
    return obligatoryDevices;
  }

  private List<Device> getObligatoryDevicesSplit() {
    var obligatoryDevices = new ArrayList<Device>(5);
    obligatoryDevices.add(new Device("1L", "Wasmachine", 1, 30, 20, false, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 30, 40, true, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 30, 40, true, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 30, 40, true, null, null));
    obligatoryDevices.add(new Device("3L", "Afwasmachine", 3, 30, 40, true, null, null));
    return obligatoryDevices;
  }

  private List<TimeSlot> getTimeSlots() {
    var timeSlotList = new ArrayList<TimeSlot>(480);
    var idCounter = 0;
    for (int i = 8; i < 17; i++) {
      for (int j = 0; j < 60; j++) {
        timeSlotList.add(new TimeSlot(idCounter, LocalTime.of(i, j)));
        idCounter++;
      }
    }
    return timeSlotList;
  }

  @Test
  void testCalculateObligatoryNoSun() {
    var obligatoryDevices = getObligatoryDevices();
    for (var obligatoryDevice : obligatoryDevices) {
      obligatoryDevice.setObligatory(true);
    }
    var obligatoryDeviceUsageList = scheduleService.getObligatoryDeviceUsage(obligatoryDevices);
    var timeSlotList = getTimeSlots();
    List<SolarWattage> solarWattageList = new ArrayList<>(480);
    for (var timeSlot : timeSlotList) {
      solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
    }

    var solution = scheduleService.solveProblem(new Schedule(obligatoryDevices, new ArrayList<>(), solarWattageList, timeSlotList, obligatoryDeviceUsageList, new ArrayList<>()),20);

    for (var obligatoryDeviceUsage : solution.getObligatoryDeviceUsageList()) {
      for (var obligatoryDeviceUsage2 : solution.getObligatoryDeviceUsageList()) {
        if (!Objects.equals(obligatoryDeviceUsage.getDevice().getFriendly_name(), obligatoryDeviceUsage2.getDevice().getFriendly_name())) {
          assertNotEquals(obligatoryDeviceUsage.getTimeSlot().toString(), obligatoryDeviceUsage2.getTimeSlot().toString());
        }
      }
    }
  }

  @Test
  void testCalculateObligatorySun() {
    var obligatoryDevices = new ArrayList<Device>(3);
    obligatoryDevices.add(new Device("1L", "Wasmachine", 1, 60, 20, false, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 60, 40, false, null, null));
    obligatoryDevices.add(new Device("3L", "Afwasmachine", 3, 60, 20, false, null, null));

    var obligatoryDeviceUsageList = scheduleService.getObligatoryDeviceUsage(obligatoryDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(480);
    for (var timeSlot : timeSlotList) {
      if (timeSlot.getStart().isBefore(LocalTime.of(9, 0))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 100, timeSlot));
      } else {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      }
    }

    var solution = scheduleService.solveProblem(new Schedule(obligatoryDevices, new ArrayList<>(), solarWattageList, timeSlotList, obligatoryDeviceUsageList, new ArrayList<>()), 20);

    assertEquals(solution.getObligatoryDeviceUsageList().get(0).getTimeSlot().toString(), "08:00");
    assertEquals(solution.getObligatoryDeviceUsageList().get(1).getTimeSlot().toString(), "08:00");
    assertEquals(solution.getObligatoryDeviceUsageList().get(2).getTimeSlot().toString(), "08:00");
  }

  @Test
  void testCalculateObligatorySunLate() {
    var obligatoryDevices = new ArrayList<Device>(3);
    obligatoryDevices.add(new Device("1L", "Wasmachine", 1, 60, 20, false, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 60, 40, false, null, null));
    obligatoryDevices.add(new Device("3L", "Afwasmachine", 3, 60, 20, false, null, null));

    var obligatoryDeviceUsageList = scheduleService.getObligatoryDeviceUsage(obligatoryDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>();
    for (var timeSlot : timeSlotList) {
      if ((timeSlot.getStart().isAfter(LocalTime.of(9, 0)) || timeSlot.getStart().equals(LocalTime.of(9, 0))) && timeSlot.getStart().isBefore(LocalTime.of(10, 0)) ) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 100, timeSlot));
      } else if (timeSlot.getStart().isAfter(LocalTime.of(10, 0))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      } else {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      }
    }

    var solution = scheduleService.solveProblem(new Schedule(obligatoryDevices, new ArrayList<>(), solarWattageList, timeSlotList, obligatoryDeviceUsageList, new ArrayList<>()), 20);

    assertEquals(solution.getObligatoryDeviceUsageList().get(0).getTimeSlot().toString(), "09:00");
    assertEquals(solution.getObligatoryDeviceUsageList().get(1).getTimeSlot().toString(), "09:00");
    assertEquals(solution.getObligatoryDeviceUsageList().get(2).getTimeSlot().toString(), "09:00");
  }

  @Test
  void testCalculateOptionalSun() {
    var optionalDevices = getObligatoryDevices();
    var optionalDeviceUsage = scheduleService.getOptionalDeviceUsage(optionalDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(480);
    for (var timeSlot : timeSlotList) {
      if (timeSlot.getStart().isBefore(LocalTime.of(9, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 80, timeSlot));
      } else {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      }
    }

    var solution = scheduleService.solveProblem(new Schedule(new ArrayList<>(), optionalDevices, solarWattageList, timeSlotList, new ArrayList<>(), optionalDeviceUsage), 20);

    assertEquals(solution.getNonObligatoryDeviceUsages().get(0).getTimeSlot().toString(), "08:00");
    assertEquals(solution.getNonObligatoryDeviceUsages().get(1).getTimeSlot().toString(), "08:00");
    assertEquals(solution.getNonObligatoryDeviceUsages().get(2).getTimeSlot().toString(), "08:00");
  }

  @Test
  void testCalculateOptionalNoSun() {
    var optionalDevices = getObligatoryDevices();
    var optionalDeviceUsage = scheduleService.getOptionalDeviceUsage(optionalDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(480);
    for (var timeSlot : timeSlotList) {
      solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
    }

    var solution = scheduleService.solveProblem(new Schedule(new ArrayList<>(), optionalDevices, solarWattageList, timeSlotList, new ArrayList<>(), optionalDeviceUsage), 20);

    assertNull(solution.getNonObligatoryDeviceUsages().get(0).getTimeSlot());
    assertNull(solution.getNonObligatoryDeviceUsages().get(1).getTimeSlot());
    assertNull(solution.getNonObligatoryDeviceUsages().get(2).getTimeSlot());
  }

  @Test
  void testCalculateSplitableObligatory() {
    var obligatoryDevices = getObligatoryDevicesSplit();
    var obligatoryDeviceUsage = scheduleService.getObligatoryDeviceUsage(obligatoryDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(480);
    for (var timeSlot : timeSlotList) {
      //
      if (timeSlot.getStart().isBefore(LocalTime.of(8, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 2, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(9, 0))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(9, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 2, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(10, 0))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(10, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 2, timeSlot));
      } else {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      }
    }

    var solution = scheduleService.solveProblem(new Schedule(obligatoryDevices, new ArrayList<>(), solarWattageList, timeSlotList, obligatoryDeviceUsage, new ArrayList<>()), 20);

    for (var deviceUsage : solution.getObligatoryDeviceUsageList()) {
      for (var solarwattsAtHour : solution.getSolarWattage()) {
        if (deviceUsage.getTimeSlot() == solarwattsAtHour.getTimeSlot()) {
          solarwattsAtHour.setWattage(solarwattsAtHour.getWattage() - deviceUsage.getDevice().getConsumption());
        }
      }
    }

    var solarWattageListSum = solution.getSolarWattage().stream().mapToDouble(SolarWattage::getWattage).sum();
    assertEquals(solarWattageListSum, 0);
  }

  @Test
  void testCalculateSplitableOptionalSunny() {
    var optionalDevices = getObligatoryDevicesSplit();

    var optionalDeviceUsage = scheduleService.getOptionalDeviceUsage(optionalDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(480);
    for (var timeSlot : timeSlotList) {

      if (timeSlot.getStart().isBefore(LocalTime.of(8, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 1, timeSlot));
      } else {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      }
    }

    var solution = scheduleService.solveProblem(new Schedule(new ArrayList<>(), optionalDevices, solarWattageList, timeSlotList, new ArrayList<>(), optionalDeviceUsage), 20);

    for (var deviceUsage : solution.getNonObligatoryDeviceUsages()) {
      for (var solarwattsAtHour : solution.getSolarWattage()) {
        if (deviceUsage.getTimeSlot() == solarwattsAtHour.getTimeSlot()) {
          solarwattsAtHour.setWattage(solarwattsAtHour.getWattage() - deviceUsage.getDevice().getConsumption());
        }
      }
    }

    var solarWattageListSum = solution.getSolarWattage().stream().mapToDouble(SolarWattage::getWattage).sum();
    assertEquals(solarWattageListSum, 10);
  }

  @Test
  void testCalculateStartAfter() {
    var obligatoryDevices = getObligatoryDevices();
    obligatoryDevices.stream().filter(device -> device.getFriendly_name().equals("Wasmachine")).findFirst().get().setStartTime(LocalTime.of(10, 0));
    var obligatoryDeviceUsageList = scheduleService.getObligatoryDeviceUsage(obligatoryDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(10);
    solarWattageList.add(new SolarWattage(0, 40, timeSlotList.get(0)));
    solarWattageList.add(new SolarWattage(1, 0, timeSlotList.get(1)));
    solarWattageList.add(new SolarWattage(2, 40, timeSlotList.get(2)));
    solarWattageList.add(new SolarWattage(3, 0, timeSlotList.get(3)));
    solarWattageList.add(new SolarWattage(4, 40, timeSlotList.get(4)));
    solarWattageList.add(new SolarWattage(5, 0, timeSlotList.get(5)));
    solarWattageList.add(new SolarWattage(6, 40, timeSlotList.get(6)));
    solarWattageList.add(new SolarWattage(7, 0, timeSlotList.get(7)));
    solarWattageList.add(new SolarWattage(8, 0, timeSlotList.get(8)));
    solarWattageList.add(new SolarWattage(9, 0, timeSlotList.get(9)));

    var solution = scheduleService.solveProblem(new Schedule(obligatoryDevices, new ArrayList<>(), solarWattageList, timeSlotList, obligatoryDeviceUsageList, new ArrayList<>()), 20);
    var obligatoryWasmachine = solution.getObligatoryDeviceUsageList().stream().filter(deviceUsage -> deviceUsage.getDevice().getFriendly_name().equals("Wasmachine")).findFirst().get();
    assertTrue(obligatoryWasmachine.getTimeSlot().getStart().isAfter(LocalTime.of(10, 0)) || obligatoryWasmachine.getDevice().getStartTime().equals(obligatoryWasmachine.getTimeSlot().getStart()));
  }

  @Test
  void testCalculateEndBefore() {
    var obligatoryDevices = getObligatoryDevices();
    obligatoryDevices.get(0).setEndTime(LocalTime.of(9, 0));
    var obligatoryDeviceUsageList = scheduleService.getObligatoryDeviceUsage(obligatoryDevices);
    var timeSlotList = getTimeSlots();

    List<SolarWattage> solarWattageList = new ArrayList<>(10);
    for (var timeSlot : timeSlotList) {

      if (timeSlot.getStart().isBefore(LocalTime.of(8, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 2, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(9, 0))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(9, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 2, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(10, 0))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      } else if (timeSlot.getStart().isBefore(LocalTime.of(10, 30))) {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 2, timeSlot));
      } else {
        solarWattageList.add(new SolarWattage(timeSlot.getTimeSlotId(), 0, timeSlot));
      }
    }
    var solution = scheduleService.solveProblem(new Schedule(obligatoryDevices, new ArrayList<>(), solarWattageList, timeSlotList, obligatoryDeviceUsageList, new ArrayList<>()), 20);
    var obligatoryWasmachine = solution.getObligatoryDeviceUsageList().stream().filter(deviceUsage -> deviceUsage.getDevice().getFriendly_name().equals("Wasmachine")).findFirst().get();
    assertTrue(obligatoryWasmachine.getTimeSlot().getStart().isBefore(LocalTime.of(9, 0)) || obligatoryWasmachine.getDevice().getFriendly_name().equals(obligatoryWasmachine.getTimeSlot().getStart()));
  }

}
