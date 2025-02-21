package jy.practice.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StatusDepth1Dto {

  @JsonProperty("sapa_criteria")
  private SapaCriteriaDto sapaCriteriaDto;

  @JsonProperty("sapa_criteria_elements")
  private List<SapaCriteriaElementDto> sapaCriteriaElementDtoList;

}
