# bupt-subway
2024BUPT数据结构大作业 地铁系统

## 写在最前的一些吐槽

​	这大作业像是出题组没有经过任何思考拍拍脑子就定下来的。

​	地铁的发车时刻表只有图片，没办法只能一张一张ocr然后再整理数据...最后还要把数据格式化之后导入到数据库。光是进行数据整理就花了近十个小时，读写数据导入库的IO函数写了两百多行...最后得到的纯文本格式的数据快4M。众所周知按照字符算的话1M大概等于50W个字符...

​	最后算上写前后端，测试数据，~~修bug~~ ，总共花了得60h+吧，这个工作量感觉对于大作业可能纸面上感觉不是很多，但是其中有1/3 ~ 1/2的时间是花在数据处理上的。

​	因为去年比赛用过Neo4j做知识图谱生成，所以还算简单地系统学习了一下~~(虽然早就忘光了)~~  ，所以今年一看到这个大作业题目首先想到的就是Neo4j。现在看来选Neo4j是个很正确的决定，Neo4j的图数据科学库的最短路实现帮了大忙，以至于我基本上没在算法层面上有过多少犹豫，也没有在这上面浪费多少找bug的时间~~(和我用幼教vue水平写前端的时间差不多)~~。![image-20240423173051952](C:\Users\16780\AppData\Roaming\Typora\typora-user-images\image-20240423173051952.png)

![image-20240423173125693](C:\Users\16780\AppData\Roaming\Typora\typora-user-images\image-20240423173125693.png)

## 项目依赖

- Java 17
- Mysql 8.0.0
- Neo4j community 5.17.0 windows

## 使用方法

- 将四个csv文件导入到数据库（库名默认为subway）
- 安装Neo4j并配置环境变量

- 安装Neo4j插件 `neo4j-graph-data-science-2.6.5`和 `apoc-5.17.0-core`
- 启动Neo4j于默认端口(7474)

```shell
.\neo4j-admin.bat server  console
```

- 启动Springboot项目于默认端口(11450)

第一次启动Springboot可能会很慢，，，因为要加载图数据

## 一些项目图片

> 主页面

![image-20240423184908775](C:\Users\16780\AppData\Roaming\Typora\typora-user-images\image-20240423184908775.png)

> 路线查询
>
> 某地铁软件推荐 10 -> 19 -> 2 -> 1这样换乘，毕竟正经人谁在平安里19换4啊(笑)
>
> ~~恭喜平安里打败西直门成为北京最地狱的换乘站~~

![image-20240423184934417](C:\Users\16780\AppData\Roaming\Typora\typora-user-images\image-20240423184934417.png)

> 长距离路线查询

![image-20240423185031639](C:\Users\16780\AppData\Roaming\Typora\typora-user-images\image-20240423185031639.png)
