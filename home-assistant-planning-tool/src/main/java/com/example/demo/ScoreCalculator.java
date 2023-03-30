package com.example.demo;

import com.example.demo.domain.ObligatoryDeviceUsage;
import com.example.demo.domain.Schedule;
import com.example.demo.domain.SolarWattage;
import com.example.demo.domain.TimeSlot;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import java.util.*;

public class ScoreCalculator implements EasyScoreCalculator<Schedule, HardMediumSoftScore> {

  public record ScheduleReturn(int softScore, int mediumScore, int hardScore, List<SolarWattage> solarWattsAtTimes) {
  }

  private ScheduleReturn calculateNonObligatoryDevices(int softScore, int mediumScore, int hardScore, Schedule schedule, List<SolarWattage> solarWattsAtTimes) {
    var nonObligatoryDeviceUsage = schedule.getNonObligatoryDeviceUsages().stream().sorted(Comparator.comparing(deviceUsage -> deviceUsage.getDevice().getImportance())).toList();
    //only schedule nonObligatoryDevices if there is any solarWattage
    if (solarWattsAtTimes.stream().mapToDouble(SolarWattage::getWattage).sum() <= 0) {
      for (var deviceUsage : nonObligatoryDeviceUsage) {
        if (!deviceUsage.getDevice().isSplittable()) {
          if (deviceUsage.getTimeSlot() != null)
            hardScore -= 1;
        } else {
          var timeSlotList = schedule.getNonObligatoryDeviceUsages().stream().filter(device -> Objects.equals(device.getDevice().getFriendly_name(), deviceUsage.getDevice().getFriendly_name())).toList();
          if (!timeSlotList.isEmpty()) {
            hardScore -= 1;
          }
        }
      }
    } else {
      for (var deviceUsage : nonObligatoryDeviceUsage) {
        //check if device has enough watts for scheduling
        var numberOfTimeFrames = deviceUsage.getDevice().getDuration();
        var orderWatts = 0;
        for (var solarWattage : solarWattsAtTimes) {
          if (orderWatts != deviceUsage.getDevice().getDuration()) {
            if (solarWattage.getWattage() >= deviceUsage.getDevice().getConsumption() / (double) numberOfTimeFrames) {
              orderWatts++;
            } else {
              orderWatts = 0;
            }
          }
        }
        //if there is not enough watts, the device should not be scheduled. If there is enough, the device should be scheduled
        if ((orderWatts == numberOfTimeFrames && deviceUsage.getTimeSlot() == null) || (orderWatts != numberOfTimeFrames && deviceUsage.getTimeSlot() != null)) {
          hardScore -= 1;
        } else if (orderWatts == numberOfTimeFrames && deviceUsage.getTimeSlot() != null) {
          var timeSlotList = calculateTimeSlotList(numberOfTimeFrames, deviceUsage.getTimeSlot(), schedule);
          var solarWattageListSum = solarWattsAtTimes.stream().filter(solarWattage -> timeSlotList.contains(solarWattage.getTimeSlot())).filter(solarWattage -> solarWattage.getWattage() >= deviceUsage.getDevice().getConsumption() / (double) numberOfTimeFrames).toList();
          //if there is not enough watts, the device should not be scheduled during these timeframes
          if (solarWattageListSum.size() < numberOfTimeFrames) {
            mediumScore -= 1;
          }
        }
        if (deviceUsage.getDevice().getStartTime() != null) {
          if (deviceUsage.getTimeSlot() != null) {
            if (deviceUsage.getDevice().getStartTime().isBefore(deviceUsage.getTimeSlot().getStart())) {
              mediumScore -= 1;
            }
          }
        }
        if (deviceUsage.getDevice().getEndTime() != null) {
          if (deviceUsage.getTimeSlot() != null) {
            if (deviceUsage.getTimeSlot().getStart().plusMinutes(numberOfTimeFrames).isAfter(deviceUsage.getTimeSlot().getStart())) {
              mediumScore -= 1;
            }
          }
        }
        if (deviceUsage.getTimeSlot() != null) {
          //subtract the energy used by the device from the solarWattsAtTimes, so that the next device can account for already used energy
          var timeSlotList = calculateTimeSlotList(numberOfTimeFrames, deviceUsage.getTimeSlot(), schedule);
          for (var solarWattage : solarWattsAtTimes) {
            if (timeSlotList.contains(solarWattage.getTimeSlot())) {
              solarWattsAtTimes.get(solarWattage.getSolarId()).setWattage(solarWattage.getWattage() - (deviceUsage.getDevice().getConsumption() / (double) numberOfTimeFrames));
            }
          }
        }
      }
    }
    return new ScheduleReturn(softScore, mediumScore, hardScore, solarWattsAtTimes);
  }

  private HardMediumSoftScore accountForToLittleSun(int softScore, int mediumScore, int hardScore, Schedule schedule, List<ObligatoryDeviceUsage> obligatoryDeviceUsage, ObligatoryDeviceUsage deviceUsage, List<TimeSlot> timeSlotList) {
    if (obligatoryDeviceUsage.size() > 1) {
      for (var deviceUsage2 : obligatoryDeviceUsage) {
        //all obligatory devices should be scheduled
        if (deviceUsage2.getTimeSlot() == null)
          hardScore -= 10;
        else {
          if (!deviceUsage.getDevice().getId().equals(deviceUsage2.getDevice().getId())) {
            //TODO splitable devices
            //calculate other device timeframes
            var numberOfTimeFrames2 = deviceUsage.getDevice().getDuration();
            var timeSlotList2 = calculateTimeSlotList(numberOfTimeFrames2, deviceUsage2.getTimeSlot(), schedule);
            //if the timeframes overlap, the devices should be scheduled at different times
            for (var timeSlot : timeSlotList2) {
              if (timeSlotList.contains(timeSlot)) {
                mediumScore -= 1;
              }
            }
          }
        }
      }
    }
    return HardMediumSoftScore.of(hardScore, mediumScore, softScore);
  }

  private List<TimeSlot> calculateTimeSlotList(double numberOfTimeFrames, TimeSlot timeSlot, Schedule schedule) {
    var timeSlotList = new ArrayList<TimeSlot>();
    for (int i = 0; i < numberOfTimeFrames; i++) {
      if (timeSlot.getTimeSlotId() + i < schedule.getTimeSlotList().size())
        timeSlotList.add(schedule.getTimeSlotList().get(timeSlot.getTimeSlotId() + i));
    }
    return timeSlotList;
  }

  private ScheduleReturn calculateObligatoryDevices(int softScore, int mediumScore, int hardScore, Schedule schedule, List<SolarWattage> solarWattsAtTimes) {
    List<ObligatoryDeviceUsage> obligatoryDeviceUsage = new ArrayList<>();
    Collections.shuffle(schedule.getObligatoryDeviceUsageList(), new Random());
    obligatoryDeviceUsage = schedule.getObligatoryDeviceUsageList();

    for (var deviceUsage : obligatoryDeviceUsage) {
      //obligatory devices have to be assigned a timeslot
      if (deviceUsage.getTimeSlot() == null) {
        hardScore -= 10;
      } else {
        double numberOfTimeFrames;
        List<TimeSlot> timeSlotList;
        if (!deviceUsage.getDevice().isSplittable()) {
          //timeslots can't be later than the last timeslot of the day
          if (deviceUsage.getTimeSlot().getStart().plusMinutes(deviceUsage.getDevice().getDuration()).isAfter(schedule.getEndTime())) {
            hardScore -= 1;
          }
          numberOfTimeFrames = deviceUsage.getDevice().getDuration();
          timeSlotList = calculateTimeSlotList(numberOfTimeFrames, deviceUsage.getTimeSlot(), schedule);
        } else {
          var currentDevice = schedule.getObligatoryDeviceUsageList().stream().filter(device -> Objects.equals(device.getDevice().getFriendly_name(), deviceUsage.getDevice().getFriendly_name())).toList();
          for (var devicePart : currentDevice) {
            if (devicePart.getTimeSlot() == deviceUsage.getTimeSlot()) {
              hardScore -= 1;
            }
          }
          numberOfTimeFrames = currentDevice.size();
          timeSlotList = currentDevice.stream().map(ObligatoryDeviceUsage::getTimeSlot).toList();
        }
        //timeslots should be scheduled when the sun is generating enough energy
        var solarWattageListSum = solarWattsAtTimes
            .stream()
            .filter(solarWattage -> timeSlotList.contains(solarWattage.getTimeSlot()))
            .filter(solarWattage -> solarWattage.getWattage() >= deviceUsage.getDevice().getConsumption() / numberOfTimeFrames && solarWattage.getWattage() > 0)
            .toList();
        //if there is not enough energy, the device should be scheduled another time
        if (solarWattageListSum.size() < numberOfTimeFrames) {
          var solarWattageListSum2 = solarWattsAtTimes
              .stream()
              .filter(solarWattage -> solarWattage.getWattage() >= deviceUsage.getDevice().getConsumption() / numberOfTimeFrames)
              .toList();
          if (solarWattageListSum2.size() > numberOfTimeFrames) {
            mediumScore -= 1;
          } else {
            var hardMediumSoft = accountForToLittleSun(softScore, mediumScore, hardScore, schedule, obligatoryDeviceUsage, deviceUsage, timeSlotList);
            mediumScore = hardMediumSoft.getMediumScore();
            hardScore = hardMediumSoft.getHardScore();
          }
        }

        if (deviceUsage.getTimeSlot() != null) {
          if (deviceUsage.getDevice().getStartTime() != null) {
            if (!deviceUsage.getDevice().getStartTime().isBefore(deviceUsage.getTimeSlot().getStart())) {
              mediumScore -= 1;
            }
          }
          if (deviceUsage.getDevice().getEndTime() != null) {
            if (deviceUsage.getTimeSlot().getStart().plusMinutes(deviceUsage.getDevice().getDuration()).isAfter(deviceUsage.getDevice().getEndTime())) {
              hardScore -= 1;
            }
          }
        }

        //subtract the energy used by the device from the solarWattsAtTimes, so that the next device can account for already used energy
        for (var solarWattage : solarWattsAtTimes) {
          if (timeSlotList.contains(solarWattage.getTimeSlot())) {
            softScore += (solarWattage.getWattage() - deviceUsage.getDevice().getConsumption()/numberOfTimeFrames);
            solarWattsAtTimes.get(solarWattage.getSolarId()).setWattage(solarWattage.getWattage() - deviceUsage.getDevice().getConsumption()/numberOfTimeFrames);
          }
        }
      }
    }
    return new ScheduleReturn(softScore, mediumScore, hardScore, solarWattsAtTimes);
  }

  @Override
  public HardMediumSoftScore calculateScore(Schedule schedule) {
    int softScore = 0;
    int mediumScore = 0;
    int hardScore = 0;
    var solarWattsAtTimes = new ArrayList<SolarWattage>();
    for (var solarWattage : schedule.getSolarWattage()) {
      solarWattsAtTimes.add(solarWattage.clone());
    }
    var scheduleReturn = calculateObligatoryDevices(softScore, mediumScore, hardScore, schedule, solarWattsAtTimes);
    scheduleReturn = calculateNonObligatoryDevices(scheduleReturn.softScore ,scheduleReturn.mediumScore, scheduleReturn.hardScore, schedule, scheduleReturn.solarWattsAtTimes);

    softScore = scheduleReturn.softScore();
    mediumScore = scheduleReturn.mediumScore();
    hardScore = scheduleReturn.hardScore();

    var solarSum = solarWattsAtTimes.stream().mapToDouble(SolarWattage::getWattage).sum();
    if (solarSum < 0) {
      mediumScore -= 1;
    }

    return HardMediumSoftScore.of(hardScore, mediumScore, softScore);
  }
}
