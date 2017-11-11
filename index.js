import { NativeModules, Platform } from 'react-native'

export default class HuaweiPush {
	static init(cb) {
		if (Platform.OS === 'android') {
			NativeModules.HuaweiPush.init(cb)
		}
	}

	static getTocken(cb) {
		if (Platform.OS === 'android') {
			NativeModules.HuaweiPush.getTocken(cb)
		}
	}
}