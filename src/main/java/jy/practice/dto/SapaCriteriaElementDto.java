package jy.practice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SapaCriteriaElementDto {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("achievementRate")
  private Integer achievementRate;
  @JsonProperty("count")
  private Integer count;
  @JsonProperty("value")
  private Integer value; // total value
  @JsonProperty("createdDate") // yyyy-MM-dd
  private String createdDate;
  @JsonProperty("updatedDate") // yyyy-MM-dd
  private String updatedDate;
  @JsonProperty("lastUploadedUser")
  private LastUploadedUserDto lastUploadedUser;
}
