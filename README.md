# cleanyourtv · Android 6.0 fork

[English (upstream)](README.upstream.md) · [中文](README.zh-CN.md) · [日本語](README.ja.md) · [Tiếng Việt](README.vi.md)

这是 [Gathin23/cleanyourtv](https://github.com/Gathin23/cleanyourtv) 的一个 fork,把它的
参考启动器(`launcher/`,包名 `com.gathin.tvhome`)移植到了 **Android 6.0(API 23)**,并在一台
小米电视 4(MiTV4-ANSM0,Android 6.0.1,固件 22.8.15.3312)上作为默认桌面长期使用。
给本仓库写代码的代理请先读 [AGENTS.md](AGENTS.md)(注意其中面向上游的规则);上游英文原版
README 保留在 [README.upstream.md](README.upstream.md)。

上游要求 Android 8.0(API 26)+;按上游规则,降低 `minSdk` 属于 fork 范畴,本分支**不会**向上游提 PR。

## 移植内容

| 提交 | 内容 |
|---|---|
| `6a4a338` | minSdk 26→23:`java.time` 全部重写为 Calendar(`Day`、`RaceWeekend` 毫秒制);字体资源分层(`values/` 系统字体、`values-v26/` Outfit);Android 6 无 `cmd` 的安装路径 |
| `7ef01d0` | lint NewApi 全量修复:`java.util.function` 四类型换成自有小接口,框架 API 加 `SDK_INT` 门槛;体积门上调 12 KB(v1 签名,Android 6 安装必需) |
| `6cb99fb` | `build-and-install.sh` 的 Windows/Git Bash 路径修复,`pm hide` 回退与回滚 |
| `ef84085` | 本机默认开启应用库、顶栏应用库按钮、磁贴菜单(`config.xml`) |
| `51349f5` | `dock_apps` 支持 `pkg/.Activity` 显式组件钉磁贴:小米自家应用无标准启动类别,查询永远列不出 |
| `4a7eb4eb` | 新配置 `top_apps`:顶栏设置旁的图标快捷按钮 |
| `8616f138` | 顶栏按钮名称接入 `app_labels` |
| `67341350` | `top_app_icons`:顶栏按钮使用启动器白色线条图标(新增 `ic_store`) |
| `272b05a4` | 冷启动修复:焦点兜底 + dock 首次停靠硬滚动(消除"第一应用上方的方块") |
| `93e8fffa` `2a501afa` | Android 6 图标 tint 不随焦点的修复:所有顶栏按钮聚焦时图标随文字变黑,网络 pill 换图标保持补染 |

## 本机(小米电视 4)的配置

`launcher/app/src/main/res/values/config.xml` 里为这台电视定制:

- `dock_apps`:银河奇异果(`com.gitvdemo.video`)、电视管家(`com.xiaomi.mitv.tvmanager`)
- `top_apps`:应用中心(`com.xiaomi.mitv.appstore/.MainActivity`)、DTMB 电视(`com.xiaomi.mitv.tvplayer/.dtmb.DTMBActivity`),都在顶栏设置旁
- `top_app_icons` / `app_labels`:白色店面图标 + 电视图标;DTMB pill 显示名「电视」
- `app_library`、`library_button`、`tile_menu` 默认开(这台电视的 sdcard 锁死,主题推送不可用,配置一律进 APK)

## 构建与安装

```bash
cd launcher
JAVA_HOME=<JDK17> ANDROID_HOME=<SDK> ./gradlew assembleRelease lintRelease
# 或直接:TV_HOST=<tv ip> JAVA_HOME=<JDK17> STOCK_LAUNCHER=com.mitv.tvhome bash build-and-install.sh
```

工具链与踩坑记录(国内镜像来源、安装确认弹窗坐标等)见 `work/2026-10-24-android6-fork.md`(本地,不入库)。

## 小米 Android 6 的三道厂锁与对策

1. `pm disable-user` / `pm hide` 均被拒(SecurityException)→ 用 `pm uninstall -k --user 0 com.mitv.tvhome`
   (仅移除 user 0,APK 与数据保留)
2. ADB 安装若干次后弹「安装确认」框,需遥控器/坐标点确定
3. 小米应用商店自动更新原生桌面会把上面的移除覆盖掉(Home 被顶回原生桌面)。恢复:

```bash
TV_HOST=<tv ip> tools/venv/Scripts/python.exe -I tools/tv.py 'pm uninstall -k --user 0 com.mitv.tvhome'
```

按一次遥控器 Home 键即回本桌面;治本可在应用中心设置里关闭自动更新。恢复原生桌面则用
`pm install-existing com.mitv.tvhome`。

## 与上游的关系

上游的电视清单(`examples/`)、工具(`tools/`)、测试(`tests/`)全部保留;测试中指向首页的
路径已改为 `README.upstream.md`。英文原版说明见 [README.upstream.md](README.upstream.md)。
