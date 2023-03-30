package com.example.demo.domain.dto;

public class TiniestDeviceDto {
  private String entity_id;

  public TiniestDeviceDto() {
  }

  public TiniestDeviceDto(String entity_id) {
    this.entity_id = entity_id;
  }

  public String getEntity_id() {
    return entity_id;
  }

  public void setEntity_id(String entity_id) {
    this.entity_id = entity_id;
  }
}
