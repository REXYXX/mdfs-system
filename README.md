## Architecting a Distributed File System with Microservices
### 功能要求实现 </br>
###### 1.基于Spring Boot实现NameNode和DataNode两个服务</br>
- mdfs-client-server文件夹下运行 mvn spring-boot:run -Dserver.port = ? 开启一个数据节点
- mdfs-master-server文件夹下运行 mvn spring-boot:run 启动主节点
###### 2.NameNode提供REST风格接口与用户交互，实现用户文件上传、下载、删除。因为没有实现前端，所以使用postman进行测试。</br>
- 上传</br>
![avatar](/shot/1.png)
- 下载(可以在本地正常打开，postman显示的是Resourse类型)</br>
![avatar](/shot/3.png)
- 删除</br>
![avatar](/shot/2.png)
###### 3.NameNode将用户上传文件文件拆为固定大小的存储块，分散存储在各个DataNode上，每个块保存若干副本
- 启动三个数据节点 ，每个块两个备份</br>
![avatar](/shot/4.png)

### 非功能要求实现 </br>
###### 1.DataNode服务可弹性扩展，每次启动一个DataNode服务NameNode可发现并将其纳入整个系统</br>
- 启动三个数据节点8011,8012,8013, 主节点进行监听并将数据节点纳入系统</br>
![avatar](/shot/5.png)
###### 2.NameNode负责检查各DataNode健康状态，需模拟某个DataNode下线时NameNode自动在其他DataNode上复制（迁移）该下线服务原本保存的数据块</br>
- 8011节点下线， 主节点监听事件</br>
![avatar](/shot/6.png)
- 数据迁移</br>
![avatar](/shot/7.png)
###### 3.NameNode在管理数据块存储和迁移过程中应实现一定策略尽量保持各DataNode的负载均衡</br>
- 对每一个存储Block及其备份，主节点对个数据节点按存储的Block数进行排序。取存储Block数最少的数据节点进行存储。
