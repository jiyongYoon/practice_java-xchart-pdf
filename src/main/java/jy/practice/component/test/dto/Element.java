package jy.practice.component.test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Element {
    private int id;
    private String name;
    private String period;
    private int value;
    private int count;
    
    @JsonProperty("achievement_rate")
    private double achievementRate;
    
    @JsonProperty("created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String createdDate;
    
    @JsonProperty("updated_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String updatedDate;

}