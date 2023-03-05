package com.shark.aio.video.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FaceRecordsEntity {

    private int id;
    private String pictureUrl;
    private String result;
    private double score;
    private Timestamp time;
}
