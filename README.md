# `lib_dlt`使用说明

## 零、配置

### 1. allprojects.repositories
```Gradle
maven {
    url 'https://dl.bintray.com/ztone/maven'
}
```

### 2. build.gradle:
```Gradle
implementation 'com.xrj:lib-dlt:0.3'
```

## 一、设备定义

### 1. 板载资源

```Java
public enum Units {
        /** 补光灯 */
        LED("/dev/led_en", false),
        /** 5V电源 */
        VCC5V("/dev/vcc5v_out", false),
        /** 12V电源 */
        VCC12V("/dev/vcc12v_out", false),
        /** 继电器 */
        RELAY("/dev/relay", false),

        /**
         * GPIO
         */
        GPIO8_A7("/dev/gpio8_a7", false),
        GPIO8_B1("/dev/gpio8_b1", false),
        GPIO8_B0("/dev/gpio8_b0", true),
        GPIO8_A6("/dev/gpio8_a6", true),

        PIR_INT("/dev/pir_int", false),
        CDS_INT("/dev/cds_int", false);
        
        /** 单元模块路径 */
        public final String path;
        /** 是否可读，用于标记io口输入输出模式，2种模式不可混用 */
        public final boolean readable;
}

```

- _path_ : 单元模块在Android系统中的文件路径；
- _readable_ : 是否可读，用于标记io口输入输出模式，2种模式不可混用;

> 当Units.readable=true，表明该IO处于输入模式时，如果执行了状态输出，将会改变IO工作模式，从而改变IO输入状态。

### 2. 操作符

```Java
public enum Levers {
    ON("1"), OFF("0");
}

```

## 二、示例

```Java
// 读取`GPIO8_B0` IO口状态
if(GPIO8_B0.readable){
	Levers lever = Board.Impl.getStatus(GPIO8_B0);
}

// 打开补光灯
Board.Impl.setStatus(LED, ON);

// 关闭补光灯
Board.Impl.setStatus(LED, OFF);

// 打开继电器
Board.Impl.setStatus(RELAY, ON);

// 关闭继电器
Board.Impl.setStatus(RELAY, OFF);

// 模拟长闭型出门开关按钮动作，默认模拟50ms脉冲高电平
Board.Impl.pulse(RELAY, ON);

// 带回调接口
Board.Impl.pulse(RELAY, ON, 50, new OnHandlePulseListener(){

        @MainThread
        void onPulsed(Units u, Levers l){
            // do something
        }
    });

```