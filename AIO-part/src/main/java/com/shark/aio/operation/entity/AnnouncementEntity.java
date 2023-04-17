package com.shark.aio.operation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnnouncementEntity {

    private int id;
    private String title;
    private Timestamp publishTime;
    private String publisher;
    private String context;
}
