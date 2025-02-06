package jy.practice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
  @JsonProperty("매우 위험")
  DANGER_2("매우 위험"),
  @JsonProperty("위험")
  DANGER_1("위험"),
  @JsonProperty("보통")
  NORMAL("보통"),
  @JsonProperty("좋음")
  GOOD_1("좋음"),
  @JsonProperty("아주 좋음")
  GOOD_2("아주 좋음"),
  ;

  private final String value;
}
