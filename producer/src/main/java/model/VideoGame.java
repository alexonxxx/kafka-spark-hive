package model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
public class VideoGame {

    private Integer cid;
    private String cname;
    private String platform;
    private Integer year;
    private String publisher;
    private Float naSales;
    private Float euSales;
    private Float jpSales;
    private Float otherSales;
    private Long timestamp;


    public VideoGame(){}

    public VideoGame(Integer cid, String cname, String platform, Integer year,
                     String publisher, Float naSales, Float euSales, Float jpSales, Float otherSales) {
        this.cid=cid;
        this.cname= cname;
        this.platform= platform;
        this.year= year;
        this.publisher= publisher;
        this.naSales= naSales;
        this.euSales= euSales;
        this.jpSales= jpSales;
        this.otherSales= otherSales;
        Date currentDate = new Date();
        this.timestamp = currentDate.getTime() / 1000;

    }
}
