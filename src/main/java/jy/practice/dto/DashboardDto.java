package jy.practice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
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
public class DashboardDto {

  @JsonProperty("round")
  private RoundDto roundDto;
  @JsonProperty("all_achievement_rate")
  private Double allAchievementRate;
  @JsonProperty("status")
  private Status status;
  @JsonProperty("sapa_criterias")
  private List<SapaCriteriaDto> sapaCriteriaList;
}
