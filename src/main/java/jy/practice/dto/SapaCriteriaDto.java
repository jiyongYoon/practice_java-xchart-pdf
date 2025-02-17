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
  @JsonProperty("id")
  private Long id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("achievement_rate")
  private Double achievementRate;
  @JsonProperty("count")
  private Integer count;
  @JsonProperty("status")
  private Status status;
  @JsonProperty("created_date") // yyyy-MM-dd
  private String createdDate;
  @JsonProperty("updated_date") // yyyy-MM-dd
  private String updatedDate;
}
