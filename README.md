## 概述
如果你的业务场景的流程处理非常复杂，并且每个节点的顺序会任意组合调整，并且又想要集成简单一点，那这款轻量级的流程引擎框架能解决你的问题

## 场景举例
1. 部门员工请年假场景，只需要主管审批；部门员工请病假场景，不仅需要主管审批外，还需要Hr，老板审批。

2. 电商交易中心算价格的时候，会涉及到优惠券抵扣，促销满减，会员抵扣，运费等等价格计算逻辑，会跟着上游业务场景会任意组合

3. 跑步-->吃早饭-->看新闻；某一天不爽了，换成“跑步-->看新闻-->吃早饭”

## 特性

* 流程配置引擎
    * 文件格式：支持xml,json,yml三种规则
    
    * 文件来源：本地文件配置源，zk配置源
    
    * 流程节点支持串行和并行2种模式
    
    * 提供无级嵌套的显式子流程模式，隐式子流程模式
    
* 流程执行引擎    
    * 组件可以支持重试，重试次数可配置化
    
    * 数据槽隔离机制，在多并发下上下文独立而稳定

## 相比于Flowable和Activiti

Flowable和Activiti都是极为优秀的流程引擎框架，其中Flowable的底层也是Activiti，

功能上：能做基于任务角色的流程，也能做基于逻辑的流程，

协议上：基于BPM协议，很多基于BPM协议的编辑工具都能为他们可视化编辑流程。

缺点是这2款集成起来比较重

Mars相比，优势是比较轻量级，正常的基于逻辑流程的场景都能够支持

## 模块原理说明
1. 解析模块：负责读取文件里面的流程配置，支持多种文件格式和文件来源，多数据源的实现使用了策略模式

2. 组件加载模块：spring容器启动后负责扫描实现了NodeComponent的bean，集中管理。

3. FLowBus模块：将解析模块和组件加载模块的内容转化成Chain和Node元信息

4. 执行模块：根据传入的chanid找到FLowBus里面的元信息，执行对应的Component方法；Component跟Component之间使用Threadlocal共享上下文

## 怎么使用
#### 依赖
```
<dependency>
  <groupId>com.qianzhang</groupId>
  <artifactId>mars-spring-boot-starter</artifactId>
  <version>{version}</version>
</dependency>
```

#### 配置文件
```
<?xml version="1.0" encoding="UTF-8"?>
<flow>
    <chain name="chain1">
        <then value="ComponentA,ComponentB,ComponentC"/>
        <when value="ComponentD,ComponentE"/>
    </chain>
</flow>
```
#### 组件定义
```
@Component("ComponentA")
public class ACmp extends NodeComponent {

    @Override
    public void process() {
        //do your business
    }
}
```
#### 执行
```
@Component
public class MainTest{
    
    @Resource
    private FlowExecutor flowExecutor;
    
    @Test
    public void testConfig(){
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain1", "arg");
    }
}
```

