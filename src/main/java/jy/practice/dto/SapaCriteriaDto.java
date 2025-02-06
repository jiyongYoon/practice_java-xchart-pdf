package jy.practice.dto;

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
public class SapaCriteriaDto {
  @JsonProperty("sapa_criteria_id")
  private Long id;
  @JsonProperty("sapa_criteria_name")
  private String name;
  @JsonProperty("achievement_rate")
  private Double achievementRate;
  @JsonProperty("count")
  private Integer count;
  @JsonProperty("status")
  private Status status;
}
