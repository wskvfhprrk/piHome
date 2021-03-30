# 数据库更改为sqlite

> SQLite 是一个软件库，实现了自给自足的、无服务器的、零配置的、事务性的 SQL 数据库引擎。SQLite 是在世界上最广泛部署的 SQL 数据库引擎。SQLite 源代码不受版权限制。一般多用于安卓和mircopython开发，非常小巧的数据库，它可以直接使用spring boot+mybatis+sqlite，基本上于mysql用法一致。

## 不知道的问题

到现在我都没有弄明白一个问题：就是不能junit进行测试，直接可以在项目中可以直接引用，不知道项目的原因还是其它的原因，目前我直接使用sqlite更换了redis，在实用redis过程中zero w有几次重启时找不到所有数据，原因可能是redis太重了，目前使用sqlite后一切正常。没有出现查询不了数据的原因。

## 项目准备

#### 1、安装sqlite3

```shell
sudo apt-get update
sudo apt-get install sqlite sqlite3 -y
```

#### 2、建立数据库

请在`/home/pi`下建立数据库`piHome.db`命令,项目有schema.sql文件：

```shell
cd /home/pi
sqite3 piHome.db
```

#### 3、运行脚本建表——两张表（控制命令表piHome和缓存表Temp），创建表后命令不要退出，直接输入建表命令：

```sql
DROP TABLE IF EXISTS piHome;
CREATE TABLE piHome
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    openInHex   TEXT NOT NULL,
    closeInHex  TEXT NOT NULL,
    groupName   TEXT NOT NULL,
    switchName  TEXT NOT NULL,
    status      INT DEFAULT 1,
    openOutHex  TEXT NOT NULL,
    closeOutHex TEXT NOT NULL
);



INSERT INTO piHome (openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex)
VALUES ('123', '123', '123', 'ALI.123', 1, '123', '123', '123');
INSERT INTO piHome (openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex)
VALUES ('321', '321', '123', 'ALI.123', 1, '123', '123', '123');



UPDATE piHome
SET switchName = '00000'
WHERE piHome.openInHex = '123';

DELETE
FROM piHome
WHERE openInHex = '321';

select *
from piHome;
DROP TABLE IF EXISTS temp;
CREATE TABLE temp( outHex TEXT);
insert into temp (outHex)values ('222');
insert into temp (outHex)values ('123');
delete from temp where outHex='123';
select outHex from temp where outHex='123';
select * from temp;
```

#### 4、添加配置文件——使用vim才能修改：

```yaml
  #sqlite连接
  datasource:
    #url连接根据数据库实际地址建立
#    url: jdbc:sqlite:C:/sqlite/piHome.db
    #注意jdbc:sqlite:后跟的实际路径和数据库名称，根据实际位置可以更改
    url: jdbc:sqlite:/home/pi/piHome.db
    driver-class-name: org.sqlite.JDBC

# MyBatis配置
mybatis:
  mapper-locations: classpath*:io/parcelx/**/mapper/xml/**.xml
  type-aliases-package: io.parcelx
  configuration:
    use-generated-keys: true
    map-underscore-to-camel-case: true

```

把redis配置去了

#### 5、重启服务再批量添加命令即可