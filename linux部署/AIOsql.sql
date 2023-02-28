create table alarm_records
(
    id            int auto_increment comment '主键'
        primary key,
    alarm_time    timestamp    not null comment '报警时间',
    monitor       varchar(255) not null comment '监测点',
    monitor_class varchar(255) not null comment '监测类型',
    monitor_value varchar(255) not null comment '监测值',
    monitor_data  varchar(255) not null comment '监测数据',
    message       varchar(255) not null comment '告警信息'
)
    comment '报警记录表';

create table alarm_settings
(
    id            int auto_increment comment '主键',
    monitor_class varchar(255) not null comment '监测类型',
    monitor_value varchar(255) not null comment '监测值',
    lower_limit   double       not null comment '下限阈值',
    upper_limit   double       not null comment '上限阈值',
    message       varchar(255) not null comment '告警信息',
    constraint id
        unique (id),
    constraint monitor_value
        unique (monitor_value)
)
    comment '预警设置';

create table announcement
(
    id           int auto_increment comment '主键'
        primary key,
    title        varchar(255) not null comment '公告标题',
    publish_time timestamp    not null comment '发布时间',
    publisher    varchar(255) not null comment '发布人',
    context      varchar(500) not null comment '公告内容'
)
    comment '公告表';

create table monitor
(
    id   int auto_increment comment '主键（自增长）'
        primary key,
    name varchar(100) not null comment '监测点名称'
)
    comment '监测点名称表';

create table monitor_class
(
    id   int auto_increment comment '主键'
        primary key,
    name varchar(255) not null comment '监测类型',
    constraint name
        unique (name)
)
    comment '监测类型表';

create table pollution
(
    id   int auto_increment comment '主键'
        primary key,
    name varchar(255) not null comment '污染物名称',
    constraint name
        unique (name)
)
    comment '污染物种类表';

create table pollution_monitor
(
    id           int auto_increment comment '主键（自增长）'
        primary key,
    monitor_name varchar(100) not null comment '监测点名称',
    device_id    varchar(100) not null comment '设备id'
)
    comment '监测点和数采仪设备绑定表';

create table user
(
    id        int auto_increment comment '主键（自增长）'
        primary key,
    user_name varchar(50)                         not null comment '用户名',
    password  varchar(32)                         not null comment '密码，加密存储',
    openId    varchar(100)                        null comment '用户openid',
    phone     varchar(20)                         not null comment '手机号',
    email     varchar(50)                         not null comment '邮箱',
    gender    int                                 not null comment '性别，0 未知，1 女性，2 男性',
    icon      varchar(500)                        null comment '头像地址',
    number    varchar(20)                         not null comment '工号',
    post_id   int                                 null comment '岗位id',
    wx_id     int                                 null comment '关联的微信用户表id',
    created   timestamp default CURRENT_TIMESTAMP not null comment '自动插入，创建时间',
    updated   timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '自动插入，修改时间'
);

create table video
(
    id           int auto_increment comment '主键'
        primary key,
    monitor_name varchar(255) not null comment '监测点名称',
    rtsp         varchar(255) null comment 'rtsp地址',
    description  varchar(255) null comment '描述',
    stream       varchar(255) not null comment 'ffmpeg推流后的stream',
    constraint rtsp
        unique (rtsp),
    constraint stream
        unique (stream)
);

