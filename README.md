# HideSearchIME

小米平板 HyperOS 3.x 全局搜索自动弹出键盘的解决方案。

打开 dock 栏全局搜索时不再自动弹出输入法，点击搜索框后才弹出。

## 适用环境

- 设备：小米平板（HyperOS 3.x）
- Root：KernelSU
- 框架：LSPosed 2.0+ (Vector)
- 目标应用：`com.android.quicksearchbox`

## 安装

1. 从 [Releases](../../releases) 下载最新 APK
2. 安装 APK
3. 打开 LSPosed 管理器 → 模块 → HideSearchIME
4. 启用模块，作用域勾选 `com.android.quicksearchbox`
5. 重启设备

## 构建

项目使用标准 Xposed API，可以用 Android Studio 打开编译。

### 依赖

- JDK 17
- Android SDK (API 34)
- Xposed API (`de.robv.android.xposed:api:82`)

### 注意事项

- LSPosed 2.0 (Vector) 使用 R8 混淆了 Xposed API 类名
- 必须使用 `dx` 工具生成 DEX（d8 生成的 DEX 039 格式不兼容 LSPosed 的 verifier）
- 不要将 Xposed API stub 类打包进 DEX

## 原理

通过 Xposed hook `SearchActivity.onCreate()`，设置 `SOFT_INPUT_STATE_ALWAYS_HIDDEN` 窗口标志，阻止系统自动弹出软键盘。

## License

MIT
