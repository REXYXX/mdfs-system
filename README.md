## Architecting a Distributed File System with Microservices
### 功能要求实现 </br>
###### 1.基于Spring Boot实现NameNode和DataNode两个服务</br>
- mdfs-client-server文件夹下运行 mvn spring-boot:run -Dserver.port = 8011 开启一个数据节点
- mdfs-master-server文件夹下运行 mvn spring-boot:run 启动主节点
###### 2.NameNode提供REST风格接口与用户交互，实现用户文件上传、下载、删除。因为没有实现前端，所以使用postman进行测试。</br>
- 上传
![avatar](/home/picture/1.png)
- 下载
![avatar](/home/picture/1.png)
- 删除
![avatar](/home/picture/1.png)
###### 3.NameNode将用户上传文件文件拆为固定大小的存储块，分散存储在各个DataNode上，每个块保存若干副本
- 启动三个数据节点 ，每个块两个备份
![avatar](/home/picture/1.png)

### 非功能要求实现 </br>
###### 1.DataNode服务可弹性扩展，每次启动一个DataNode服务NameNode可发现并将其纳入整个系统</br>
###### 2.NameNode在管理数据块存储和迁移过程中应实现一定策略尽量保持各DataNode的负载均衡</br>
###### 3.NameNode在管理数据块存储和迁移过程中应实现一定策略尽量保持各DataNode的负载均衡</br>