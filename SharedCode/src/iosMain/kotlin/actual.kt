package jeremywsherman.com.handson.kmp

import platform.UIKit.UIDevice

actual fun platformName() =
    UIDevice.currentDevice.run { "$systemName $systemVersion" }
