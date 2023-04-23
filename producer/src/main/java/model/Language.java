package model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class Language {

    private String name;
    private Integer year;
    private Integer quarter;
    private Integer count;

    public Language(String name, Integer year, Integer quarter, Integer count) {
        this.name= name;
        this.year= year;
        this.quarter= quarter;
        this.count= count;

    }
}
