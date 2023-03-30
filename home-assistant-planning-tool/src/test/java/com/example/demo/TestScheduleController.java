package com.example.demo;

import com.example.demo.domain.Device;
import com.example.demo.domain.dto.ScheduleDto;
import com.example.demo.domain.dto.SolarWattageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestScheduleController {
  @Autowired
  private MockMvc mockMvc;
  @Test
  void testPostSchedule() throws Exception {
    var obligatoryDevices = new ArrayList<Device>(3);
    obligatoryDevices.add(new Device("1L", "Wasmachine", 1, 30, 20, false, null, null));
    obligatoryDevices.add(new Device("2L", "Koffiemachine", 2, 90, 40, false, null, null));
    obligatoryDevices.add(new Device("3L", "Afwasmachine", 3, 30, 20, true, null, null));
    var solarWattageList = new ArrayList<SolarWattageDto>();
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(8,0)), 1000));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(9,0)), 1000));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(10,0)), 1000));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(11,0)), 1000));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(12,0)), 1000));

    var scheduleDto = new ScheduleDto(obligatoryDevices, LocalTime.of(8,0),LocalTime.NOON, solarWattageList, "test", 20);
    System.out.println(scheduleDto);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule")
      .contentType(MediaType.APPLICATION_JSON)
      .content(scheduleDto.toString()))
      .andExpect(status().isOk());
  }

  @Test
  void testPostSchedule2() throws Exception {
    var obligatoryDevices = new ArrayList<Device>(3);
    obligatoryDevices.add(new Device("switch.dishwasher", "Dishwasher", 1, 90, 2400, false, LocalTime.of(7,0), LocalTime.of(18,0)));
    obligatoryDevices.add(new Device("switch.adguard_home_protection", "Adguard Home Protection", 1, 60, 1400, false, LocalTime.of(7,0), LocalTime.of(18,0)));
    obligatoryDevices.add(new Device("switch.adguard_home_safe_search", "Adguard Home Safe search", 1, 75, 2000, true, LocalTime.of(7,0), LocalTime.of(18,0)));
    var solarWattageList = new ArrayList<SolarWattageDto>();
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(6,41)), 0));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(7,0)), 19));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(8,0)), 179));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(9,0)), 286));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(10,0)), 360));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(11,0)), 419));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(12,0)), 488));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(13,0)), 859));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(14,0)), 1372));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(15,0)), 1515));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(16,0)), 1398));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(17,0)), 1111));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(18,0)), 685));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(18,57)), 219));

    var scheduleDto = new ScheduleDto(obligatoryDevices, LocalTime.of(7,0),LocalTime.of(18,0), solarWattageList, "test", 20);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule")
            .contentType(MediaType.APPLICATION_JSON)
            .content(scheduleDto.toString()))
        .andExpect(status().isOk());
  }

  @Test
  void testPostSchedule3() throws Exception {
    var obligatoryDevices = new ArrayList<Device>(3);
    obligatoryDevices.add(new Device("switch.dishwasher", "Dishwasher", 1, 90, 2400, false, LocalTime.of(10,52), LocalTime.of(13,0),true));
    obligatoryDevices.add(new Device("switch.adguard_home_protection", "Adguard Home Protection", 2, 60, 2000, false, LocalTime.of(10,52), LocalTime.of(18,0),true));
    var solarWattageList = new ArrayList<SolarWattageDto>();
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(6,38)), 0));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(7,0)), 19));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(8,0)), 169));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(9,0)), 292));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(10,0)), 384));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(11,0)), 484));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(12,0)), 587));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(13,0)), 601));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(14,0)), 568));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(15,0)), 535));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(16,0)), 470));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(17,0)), 384));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(18,0)), 273));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(19,0)), 133));
    solarWattageList.add(new SolarWattageDto(LocalDateTime.of(LocalDate.now(),LocalTime.of(18,57)), 1));

    var scheduleDto = new ScheduleDto(obligatoryDevices, LocalTime.of(7,0),LocalTime.of(18,0), solarWattageList, "test", 20);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule")
            .contentType(MediaType.APPLICATION_JSON)
            .content(scheduleDto.toString()))
        .andExpect(status().isOk());
  }
}
