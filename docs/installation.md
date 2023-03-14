---
layout: default
title: Installation
nav_order: 2
---

# Installation

## Requirements

- Android 7.0 (API level 24) and above

## Configuration

Add `afterpay-android-button` to your `build.gradle` dependencies.

``` gradle
dependencies {
    implementation 'com.afterpay:afterpay-android-button:4.3.0'
}
```
ui
## ProGuard

If you are using R8 the shrinking and obfuscation rules are included automatically.

Proguard users will need to manually apply the rules defined in [`consumer-rules.pro`][proguard-rules]{:target="_blank"}.

[proguard-rules]: https://github.com/afterpay/sdk-android-button/blob/master/afterpay/consumer-rules.pro
