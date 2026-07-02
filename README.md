# HideSearchIME

小米平板 HyperOS 3.x 全局搜索自动弹出键盘的解决方案。

打开 dock 栏全局搜索时**不再自动弹出输入法**，点击搜索框后才弹出。

## ✨ 功能

- 打开全局搜索时自动隐藏键盘
- 点击搜索框后正常弹出键盘
- 零后台开销，纯 hook 实现

## 📋 适用环境

| 项目 | 要求 |
|------|------|
| 设备 | 小米平板（HyperOS 3.x） |
| Root | KernelSU |
| 框架 | LSPosed 2.0+ (Vector) |
| 目标应用 | `com.android.quicksearchbox` |

## 📦 安装

1. 从 [Releases](../../releases/latest) 下载最新 APK
2. 安装 APK
3. 打开 **LSPosed 管理器** → 模块 → **HideSearchIME**
4. 启用模块，作用域勾选 `com.android.quicksearchbox`
5. **重启设备**

## 🔧 构建

### 环境要求

- JDK 17
- Android SDK (API 34)
- Xposed API 82

### 重要注意事项

⚠️ LSPosed 2.0 (Vector) 使用 R8 混淆了 Xposed API 类名，构建时需注意：

1. **必须使用 `dx` 工具**生成 DEX（不要用 `d8`，其生成的 DEX 039 格式不兼容 LSPosed verifier）
2. **不要将 Xposed API stub 类打包进 DEX**，只保留 `HookEntry` 相关类
3. `xposed_init` 文件必须放在 `assets/` 目录下

### 手动构建步骤

```bash
# 1. 编译 Java
javac -encoding UTF-8 -source 8 -target 8 \
    -classpath "android.jar:xposed-api.jar" \
    -d build-classes/ \
    app/src/main/java/com/hide/searchime/HookEntry.java

# 2. 用 dx 生成 DEX（关键！不要用 d8）
dx --dex --output=classes.dex build-classes/

# 3. 用 aapt2 打包
aapt2 link -o base.apk --manifest AndroidManifest.xml \
    -I android.jar --version-code 1 --version-name 1.0 \
    --auto-add-overlay resources.zip

# 4. 将 classes.dex 和 assets/xposed_init 加入 APK
# 5. zipalign + apksigner 签名
```

## 🔍 原理

通过 Xposed hook `SearchActivity.onCreate()`，设置窗口标志：

```java
activity.getWindow().setSoftInputMode(
    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
```

阻止系统在搜索 Activity 创建时自动弹出软键盘。

## 📝 开发日志

- 首先尝试了纯 KernelSU 模块方案（后台脚本监控），键盘会闪一下
- 转向 LSPosed 方案，经历了多个问题：
  - `assets/xposed_init` 放错位置（放在了 APK 根目录）
  - `versionCode`/`versionName` 为空导致 LSPosed 无法识别
  - d8 生成的 DEX 039 格式不兼容 LSPosed verifier，必须用 dx 生成 DEX 035
  - LSPosed 2.0 混淆了 Xposed API 类名，不能用反射加载 `XposedBridge`
  - 最终直接调用 `XposedBridge.hookMethod()` 解决

## License

MIT
