# react-native-android-hwpush

react-native-android-hwpush是一个集成了华为push sdk的react-native模块(android)：具体使用步骤如下。

## 准备集成所需内容

1. 到[华为开发者联盟](http://developer.huawei.com/consumer/cn/devunion/openPlatform/html/memberCenter.html#/appManager)注册账号。
2. 创建产品，完善信息，并添加`push服务`。
3. 查看产品的`App ID`并记录。

## 项目中引入华为push

1 install
```
yarn add jykun/react-native-android-hwpush.git --save

or

npm install jykun/react-native-android-hwpush.git --save
```
2 check `package.json`
```
 "react-native-android-hwpush": "jykun/react-native-android-hwpush.git"
```
3 set `android/build.gradlew`
```
allprojects {
    repositories {
        ...
        maven {url 'http://developer.huawei.com/repo/'}
    }
}
```
4 set `android/settings.gradle`
```
include ':react-native-android-hwpush'
project(':react-native-android-hwpush').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-android-hwpush/android')
```
5 set `android/app/build.gradlew`
```
android {
	defaultConfig{
		manifestPlaceholders [
			HW_APPID: "准备工作中获取的App ID"
		]
	}
}

dependencies {
	 compile project(':react-native-android-hwpush')
}
```
6 set `MainAoolication.java`
```
@Override
protected List<ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
  		...
  		new HuaweiPushPackage()
	)
}
```
## 在js文件中使用
```js
// js中监听事件所需内容
import { DeviceEventEmitter } from 'react-native'

// 引入HuaweiPush
import HuaweiPush from 'react-native-android-hwpush'
```
```js
// 初始化
HuaweiPush.init((res) => {
	if(res) {
		// 进行了初始化操作
	} else {
		// 未能进行初始化操作
		// 可能因为不是华为手机；或者版本较低；
	}
})
```
```js
// 创建监听事件方法 获取返回的token
function getHWToken(token) {
	// 可将token发送服务器备用
	
	// 移除获取token监听事件 视情况使用
  	DeviceEventEmitter.removeListener('HWToken', getHWToken)
}

// 添加监听事件
DeviceEventEmitter.addListener('HWToken', getHWToken)
```
```js
// 获取token
HuaweiPush.getToken((res) => {
  if (!res) {
  	// 获取token失败
  	// 可能原因：签名信息与appid不匹配
  	
  	// 获取token结束
  	DeviceEventEmitter.removeListener('HWToken', getHWToken)
  } else {
  	// token结果在 getHWToken方法中查看
  }
})
```
## PS
待补充 receiver data



