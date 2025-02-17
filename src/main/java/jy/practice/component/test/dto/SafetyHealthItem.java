package jy.practice.component.test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SafetyHealthItem {
    private int id;
    private String name;
    private int count;
    
    @JsonProperty("achievement_rate")
    private double achievementRate;
    
    @JsonProperty("created_date")
    private String createdDate;
    
    @JsonProperty("updated_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String updatedDate;
    
    private String status;
    private List<Element> elements;

}
