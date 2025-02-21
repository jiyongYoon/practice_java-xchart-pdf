package jy.practice.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoundDto {

  @JsonProperty("id")
  private Long id;
  @JsonProperty("sequence")
  private Long sequence;
  @JsonProperty("start_date") // yyyy-MM-dd
  private String startDate;
  @JsonProperty("end_date") // yyyy-MM-dd
  private String endDate;
  @JsonProperty("achievement_rate")
  private Integer achievementRate;
  @JsonProperty("status")
  private Status status;
  @JsonProperty("created_date") // yyyy-MM-dd
  private String createdDate;

}
