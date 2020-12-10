<p align="center"><img src="/pic/BlackSpider.png"></p>

# BlackSpider

## 简介
java 实现“红蜘蛛网络教室”协议,兼容原版红蜘蛛。跨平台运行，同时支持Windows，Linux，Mac OS。

## 基本原理

1. 分析原版红蜘蛛通信协议
2. java接收通信数据包，解析数据，还原原始视频信号
3. 通过GUI技术显示在屏幕上

## 使用说明

`java -jar BlackSpider.jar`

## 进度

- [x] 协议分析
- [x] 接收UDP广播
- [x] 图像数据还原
- [x] swing GUI
- [x] 至此完成基础功能
- [ ] GraalVM本地化编译，脱离jre
- [ ] 代码重构
- [ ] 编写教师端:eyes:

## 细节

#### 协议分析
抓包工具[Wireshark](https://www.wireshark.org/)

#### jpeg解码



#### lzo解压



#### GUI呈现

swing

#### 本地化编译

