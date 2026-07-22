# 🛡️ FilterYou

**FilterYou** 是一款基于 Jetpack Compose 构建的简约、清新、美观的 Android 电话拦截应用。它不仅能帮助你免受骚扰电话的打扰，更通过现代化的设计语言为你提供愉悦的使用体验。

---

## ✨ 核心特性

- **🌿 清新设计**：采用 Material 3 设计规范，薄荷绿主题色彩，大圆角卡片布局，充满“呼吸感”。
- **🚀 性能卓越**：基于 Hilt 依赖注入架构，响应迅速，逻辑解耦。
- **🛡️ 强大拦截**：
  - **白名单系统**：支持优先级放行，确保重要号码永不丢失。
  - **多维度规则**：支持包含、开头、结尾、**正则表达式**匹配。
  - **地区识别**：集成 `libphonenumber`，支持根据 **ISO 地区代码**（如 HK, CN）进行智能拦截。
- **🤌 极致交互**：支持侧滑删除拦截规则，并提供一键撤销（Undo）功能，防止误操作。
- **🌓 完美适配**：完整支持浅色/深色模式，全屏沉浸式（Edge-to-Edge）体验。

---

## 🛠️ 技术栈

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room
- **Networking/Tools**: libphonenumber, Coroutines, Flow

---

## 📈 项目路线图 (Roadmap)

我们对 FilterYou 有长远的规划，包括：
- [ ] 通讯录保护模式（仅允许联系人）
- [ ] 拦截反馈系统（静默通知）
- [ ] 可视化拦截报表
- [ ] AI 语音代接（长期目标）

详细规划请查看 [ROADMAP.md](./ROADMAP.md)。

---

## 📥 安装

1. 前往 [Releases](https://github.com/wceng/FilterYou/releases) 页面。
2. 下载最新的 `app-release.apk`。
3. 在 Android 设备上安装，并按照引导授予 **“默认来电显示与骚扰拦截应用”** 权限。

---

## 🤝 贡献与反馈

FilterYou 是一个开源项目，欢迎提交 Issue 或 Pull Request！

如果你喜欢这个项目，请给一个 **⭐ Star**，让更多人发现它！

---

## 📜 视觉风格指南

为了保持 UI 的一致性，项目遵循 [UI_STYLE_GUIDE.md](./UI_STYLE_GUIDE.md)。
