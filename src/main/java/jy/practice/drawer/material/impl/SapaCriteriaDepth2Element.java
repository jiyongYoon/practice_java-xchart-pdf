package jy.practice.drawer.material.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SapaCriteriaDepth2Element {
    private String name;
    private String period;
    private int value;
    private int count;
    private double achievementRate;
}