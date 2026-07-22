# FilterYou UI/UX 视觉风格指南 (Visual Style Guide)

本文档定义了 FilterYou 项目的核心视觉语言，旨在为开发者提供统一的设计标准，确保应用始终保持“简约、清新、美观”的特质。

---

## 🎨 1. 设计哲学 (Design Philosophy)

*   **简约 (Simplicity)**：消除不必要的视觉干扰。功能优先，不为了装饰而装饰。
*   **清新 (Freshness)**：采用明亮、富有活力的薄荷绿系列配色。利用大面积的留白和柔和的光感营造通透感。
*   **美观 (Aesthetics)**：遵循 Material Design 3 (M3) 的黄金法则，强调大圆角、富有表现力的排版和流畅的动效。

---

## 🌈 2. 色彩系统 (Color Palette)

我们基于 **Mint Green (薄荷绿)** 构建了动态色彩方案。

### 核心角色
*   **Primary (主色)**：`#006B54` (Light) / `#73DBB7` (Dark)。代表安全与生机。
*   **Surface (表面色)**：采用略带色相的浅绿/深灰，而非纯白或纯黑，以减少视觉疲劳。
*   **Status Colors**：
    *   **Success (拦截激活)**：Primary 系列。
    *   **Error (已拦截/异常)**：`#BA1A1A` 系列，采用柔和的淡红容器色。

### 使用规范
> [!IMPORTANT]
> 严禁在界面上使用高饱和度的纯色。所有的容器色应优先使用 `Color.copy(alpha = 0.1f)` 的半透明叠加方案，以增加层次感。

---

## 🔤 3. 排版规范 (Typography)

| 样式 | 字体大小 | 字重 (Weight) | 用途 |
| :--- | :--- | :--- | :--- |
| **Headline Large** | 32.sp | **ExtraBold (800)** | 核心状态标题 (如：Protection Active) |
| **Headline Medium**| 28.sp | **Bold (700)** | 页面主要标题 |
| **Title Large** | 22.sp | **Bold (700)** | 卡片标题、部分二级标题 |
| **Body Large** | 16.sp | Normal (400) | 正文内容、规则名称 |
| **Label Medium** | 12.sp | Medium (500) | 辅助信息、时间戳、说明文字 |

---

## 📐 4. 形状与阴影 (Shapes & Elevation)

FilterYou 采用 **“全方位圆角”** 策略：

*   **Extra Large (28dp)**：用于首页状态卡片、底部抽屉、主要操作按钮。
*   **Large (16dp)**：用于普通的列表卡片（日志项、规则项）。
*   **Medium (12dp)**：用于输入框、小图标容器。

### 阴影 (Elevation)
> [!TIP]
> 避免使用厚重的投影。优先使用 `BorderStroke(1.dp, color.copy(alpha = 0.1f))` 或 `TonalElevation` 来替代传统的阴影，以保持界面的“轻盈”感。

---

## 📱 5. 沉浸式体验 (Edge-to-Edge)

*   **状态栏 & 导航栏**：必须完全透明。
*   **内容避让**：所有的列表底部必须增加 `WindowInsets.navigationBars` 的 padding，确保内容不被系统手势栏遮挡。

---

## 🏗️ 6. 技术实现建议 (Implementation)

1.  **组件管理**：优先使用 `ComponentStyles` 集中定义样式，通过 `Modifier.styleable` 应用到组件。
2.  **动画原则**：状态切换必须有过渡动画（如 `animateColorAsState`），持续时间建议为 **500ms**，曲线采用 `tween`。
3.  **预览驱动**：每个 UI 组件必须提供 `@Preview` 并在浅色/深色模式下通过验证。

---

> [!NOTE]
> 本指南由 Agent 根据 Material 3 设计标准生成，是 FilterYou 视觉演进的基石。修改时请确保不违背“清新”的核心理念。
