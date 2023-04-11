package com.shark.aio.data.video.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CarRecordsEntity {
    private int id;
    private String pictureUrl;
    private String result;
    private double score;
    private Timestamp time;
}
