# java高级程序设计 大作业

#### 注：github不支持md文件中以相对路径显示图片。本文中所有引用图片在`assets/README`下。



### 代码运行方法

本项目采用maven构建。推荐使用idea打开。

项目使用了Raylib-J包用于游戏场景绘制，jar包放在`/lib`目录下，如果自动构建失败需要手动引入。

- 开始单人游戏：运行`/src/main/java/game/StartTheme.java`中的main方法
- 开始多人游戏：多人游戏需要有且仅有一个服务端，可选任意个客户端。请注意多人游戏固定使用端口号`11451`，在开始游戏前需要保证端口不被占用。
  - 开启服务端：运行`/src/main/java/game/StartServer.java`。
  - 开启客户端：运行`/src/main/java/game/StartClient.java`。



### 大作业要求

编写一个仿《元气骑士》的java肉鸽类游戏。

以下是原README中的详细要求：

- **并发**：用多线程实现游戏中生物体的自主行为
  - 每个生物体的移动、攻击等行为决策可使用Minimax或其他算法（可参考https://www.baeldung.com/java-minimax-algorithm）
  - 请特别注意线程race condition（两个生物体不能占据同一个tile，对同一生物体的两个攻击行为应该先后发生作用，等）
  - **完成后录屏发小破站**

- **构建**：支持项目自动化构建
  - 使用maven进行所有第三方库的依赖管理和构建
  - **在github actions中运行构建过程**
- **测试**：编写junit单元测试用例
  - 代码测试覆盖率不低于50%（vscode请使用Coverage Gutters扩展，intellij IDEA请run with coverage）
  - **在github actions中运行测试过程**
- **IO**：提供游戏保存功能
  - 地图保存/地图加载
  - 进度保存/进度恢复
  - 游戏过程录制/回放
  - **完成后录屏发小破站**
- **网络通信**：支持网络对战
  - 支持多方（大于两方）对战
  - 要求使用NIO Selector实现
  - 要求通信过程全局状态一致（所有玩家看到的游戏过程完全一样），可通过各方分别录制游戏过程后进行比对验证
  - **完成后录屏发小破站**



---

### 各作业要求实现效果

#### 并发效果展示

游戏中总共分了五个线程，分别是：

- main线程，负责游戏界面渲染；
- server线程，负责接收玩家输入
- enemyFactory线程，负责生成地图中的敌人
- enemyAI线程，负责控制敌人移动
- playerInput线程，负责监听用户输入，并与服务器交互数据。

其中3~5线程都由`server`线程管理，如服务器`server`类的初始化部分代码所示：

![image-20231220234500362](\assets\README\image-20231220234500362.png)





#### 构建展示

使用maven进行包管理。

`pom.xml`中依赖部分如下：

![image-20231220233145825](\assets\README\image-20231220233145825.png)

gson用于存取json文件，commons-io用于其它io处理，junit用于测试，raylib-java则是本项目使用的RaylibJ绘图库。

github actions使用官方推荐的java+maven构筑脚本：

![image-20231220233308084](\assets\README\image-20231220233308084.png)

可以一键完成构筑+执行测试样例。

github上的执行结果：

![image-20231220233402018](D:\code\java_advanced\j05-eyja42\assets\README\image-20231220233402018.png)

对`/src/test`目录下的测试样例的执行结果在"Build with Maven"任务中。

#### 测试样例部分

测试部分代码在`/src/test`目录下。

使用idea 的run with coverage，代码覆盖度如下：

![image-20231220233609298](\assets\README\image-20231220233609298.png)

覆盖率达到80%。

在github actions中配置有样例执行，见上一部分。

#### IO部分

提供游戏保存功能：中途退出游戏时，会将当前游戏信息保存在`src/main/resources`下的`previousGame.json`中。

该文件中主要包含：

1. `valid`，一个表示该文件是否有效的布尔值；
2. `player`字段，存储主玩家信息（关于为什么多人游戏时也只存储服务器所在的主玩家的信息：因为如果存储多名玩家信息，则恢复游戏时需要额外加入各玩家身份验证（即登陆界面）等逻辑，我预留了接口但没空写了）
3. `enemies`列表，存储地图中的敌人信息；
4. `walls`列表，存储地图中的墙。

**地图加载：**在初始化世界时可以选择地图，相关文件存储在`src/main/resources/map`下。目前只有一张地图，但可以仿照格式进行扩展。

#### 网络通信部分

使用NIO实现网络通信，多人游戏时，需要有且只有一个server端和任意client端。

- server端启动：执行`StartServer.java`的main方法。为了演示方便，多人游戏时的窗口需要比单人时小一点，但是是可以放进游戏本体的（毕竟都是static方法）。
- client端启动：执行`StartClient.java`的main方法。
- 由于本地演示的操作难度问题，实例视频里我把联机模式下的怪物ai关掉了，以避免录着视频玩家被怪物打死的情况。但是玩家仍是能正常执行攻击等操作的。

效果见项目根文件夹中的视频文件`网络联机演示/mp4`。

<video src="网络联机演示.mp4"></video>





