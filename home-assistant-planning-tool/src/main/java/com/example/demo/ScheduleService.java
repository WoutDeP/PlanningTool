package com.example.demo;

import com.example.demo.domain.*;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {
  public List<ObligatoryDeviceUsage> getObligatoryDeviceUsage(List<Device> obligatoryDevices) {
    var obligatoryDeviceUsageList = new ArrayList<ObligatoryDeviceUsage>(obligatoryDevices.size());
    for (var device : obligatoryDevices) {
      var deviceUsage = new ObligatoryDeviceUsage(device);
      obligatoryDeviceUsageList.add(deviceUsage);
    }
    return obligatoryDeviceUsageList;
  }

  public List<NonObligatoryDeviceUsage> getOptionalDeviceUsage(List<Device> obligatoryDevices) {
    var obligatoryDeviceUsageList = new ArrayList<NonObligatoryDeviceUsage>(obligatoryDevices.size());
    for (var device : obligatoryDevices) {
      var deviceUsage = new NonObligatoryDeviceUsage(device);
      obligatoryDeviceUsageList.add(deviceUsage);
    }
    return obligatoryDeviceUsageList;
  }

  public Schedule solveProblem(Schedule schedule, int scheduleTime) {

    SolverFactory<Schedule> solverFactory = SolverFactory.create(new SolverConfig()
        .withSolutionClass(Schedule.class)
        .withEntityClasses(ObligatoryDeviceUsage.class, NonObligatoryDeviceUsage.class)
        .withEasyScoreCalculatorClass(ScoreCalculator.class)
        .withTerminationSpentLimit(Duration.ofSeconds(scheduleTime)));
    Solver<Schedule> solver = solverFactory.buildSolver();
    return solver.solve(schedule);
  }
}
