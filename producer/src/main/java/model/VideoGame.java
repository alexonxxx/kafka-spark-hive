package model;

import lombok.*;

@Getter
@Setter
@ToString
public class VideoGame {

    private Integer id;
    private String name;
    private String platform;
    private Integer year;
    private String publisher;
    private Float naSales;
    private Float euSales;
    private Float jpSales;
    private Float otherSales;


    public VideoGame(){}

    public VideoGame(Integer id, String name, String platform, Integer year,
                     String publisher, Float naSales, Float euSales, Float jpSales, Float otherSales) {
        this.id=id;
        this.name= name;
        this.platform= platform;
        this.year= year;
        this.publisher= publisher;
        this.naSales= naSales;
        this.euSales= euSales;
        this.jpSales= jpSales;
        this.otherSales= otherSales;
    }
}
