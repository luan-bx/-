package com.shark.aio.base.information;

import lombok.Data;

/**
 * @author lbx
 * @date 2023/4/12 - 17:36
 **/
@Data
public class InformationEntity {
    private int id;
    private String company;
    private String industry;
    private String description;
    private String location;
    private String telephone;
}
