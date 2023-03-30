package com.example.demo;

import com.example.demo.domain.dto.SolvedScheduleDto;
import com.example.demo.domain.dto.TiniestDeviceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Objects;

@Component
public class ScheduleTasks {
  private SolvedScheduleDto schedule = null;
  private String token = "";

  private final Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);

  @Scheduled(fixedRate = 60000)
  public void turnOnDevice() throws JsonProcessingException {
    if (schedule != null) {
      for (var device : schedule.getDeviceUsageDtos()) {
        if (device.getStart().isBefore(LocalTime.now()) && device.getEnd().isAfter(LocalTime.now())) {
          changeDeviceState(device.getEntityId(), "turn_on");
        } else {
          var counter = 0;
          for (var device2 : schedule.getDeviceUsageDtos()) {
            if (Objects.equals(device.getEntityId(), device2.getEntityId())) {
              counter++;
            }
          }
          if (counter > 1) {
            changeDeviceState(device.getEntityId(), "turn_off");
          }
        }
      }
    }
  }

  private void changeDeviceState(String entityId, String connectionString) throws JsonProcessingException {
    var headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("Authorization","Bearer " + token);
    var tinyDevice = new TiniestDeviceDto(entityId);
    var objectMapper = new ObjectMapper();
    var playerJson = objectMapper.writeValueAsString(tinyDevice);
    var request = new HttpEntity<>(playerJson, headers);

    ResponseEntity<String> response;
    var restTemplate = new RestTemplate();

    logger.info("bearer token: " + token);
    logger.info("entity: " + playerJson);
    logger.info("action: " + connectionString);
    try {
      response = restTemplate.exchange("http://homeassistant.local:8123/api/services/switch/" + connectionString, HttpMethod.POST, request, String.class);
        logger.info(response.toString());
    } catch (Exception e) {
      System.out.println("Bad request: " + e.getMessage() );
      new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  public void setSchedule(SolvedScheduleDto schedule) {
    this.schedule = schedule;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
