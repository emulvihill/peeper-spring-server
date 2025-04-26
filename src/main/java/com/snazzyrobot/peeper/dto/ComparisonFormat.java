package com.snazzyrobot.peeper.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ComparisonFormat {
    List<String> comparisons;
    Integer numPersons;
    String pointOfInterestResponse;
}
