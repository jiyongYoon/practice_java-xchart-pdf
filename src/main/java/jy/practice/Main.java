package jy.practice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import jy.practice.dto.DashboardDto;

public class Main {

  public static void main(String[] args) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();

    BufferedReader br = new BufferedReader(new FileReader("./data.json"));
    StringBuilder sb = new StringBuilder();
    String data;
    while ((data = br.readLine()) != null) {
      sb.append(data);
    }

    DashboardDto dashboardDto = objectMapper.readValue(sb.toString(), DashboardDto.class);
    System.out.println(dashboardDto);
  }
}