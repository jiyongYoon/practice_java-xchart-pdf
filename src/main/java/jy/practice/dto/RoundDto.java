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
public class RoundDto {

  @JsonProperty("sequence")
  private Integer sequence;
  @JsonProperty("start_date")
  private String startDate;
  @JsonProperty("end_date")
  private String endDate;

}
