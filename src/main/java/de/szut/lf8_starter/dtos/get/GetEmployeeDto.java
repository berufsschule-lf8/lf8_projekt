package de.szut.lf8_starter.dtos.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetEmployeeDto {
    private long id;
    private String lastName;
    private String firstName;
    private String street;
    private String postcode;
    private String city;
    private String phone;
    private List<SkillSetDto> skillSet;

    @Data
    public static class SkillSetDto {
        private long id;
        @JsonProperty("skill")
        private String name;
    }
}
