package com.example.demo.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class ObligatoryDeviceUsage {
    private Device device;
    private TimeSlot timeSlot;

    public ObligatoryDeviceUsage(Device device) {
        this.device = device;
    }

    public ObligatoryDeviceUsage() {
    }

    public Device getDevice() {
        return device;
    }

    @PlanningVariable
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
