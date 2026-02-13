# 宝宝记录 (BabyTracker)

一款简洁实用的母婴记录Android应用，帮助父母轻松记录宝宝的日常喂养和护理情况。

## 功能特点

### 📝 日常记录
- **喂奶记录**: 记录喂奶时间、奶量(ml)、奶类型(母乳/配方奶)
- **拉屎记录**: 记录时间、颜色、稠度
- **拉尿记录**: 记录时间、尿量(少/中/多)
- 支持为每条记录添加备注

### 📊 数据统计
- **今日统计**: 实时显示当天的喂奶次数、总奶量、拉屎和拉尿次数
- **本周趋势**: 柱状图展示最近7天的各项记录趋势
- 直观的图表展示，帮助了解宝宝的日常规律

### 🔍 历史查询
- 按类型筛选(喂奶/拉屎/拉尿)
- 按日期范围查询
- 查看和删除历史记录

## 技术架构

- **开发语言**: Kotlin
- **架构模式**: MVVM (Model-View-ViewModel)
- **数据库**: Room (SQLite)
- **异步处理**: Kotlin Coroutines
- **UI组件**: Material Design Components
- **图表库**: MPAndroidChart

## 项目结构

```
BabyTracker/
├── app/
│   ├── src/main/
│   │   ├── java/com/babytracker/
│   │   │   ├── data/              # 数据层
│   │   │   │   ├── BabyRecord.kt  # 数据模型
│   │   │   │   ├── BabyRecordDao.kt # 数据访问对象
│   │   │   │   ├── BabyDatabase.kt # 数据库
│   │   │   │   ├── BabyRepository.kt # 数据仓库
│   │   │   │   └── Converters.kt  # 类型转换器
│   │   │   ├── viewmodel/         # ViewModel层
│   │   │   │   └── BabyViewModel.kt
│   │   │   ├── adapter/           # 适配器
│   │   │   │   └── RecordAdapter.kt
│   │   │   ├── MainActivity.kt    # 主页面
│   │   │   ├── StatisticsActivity.kt # 统计页面
│   │   │   └── HistoryActivity.kt # 历史页面
│   │   └── res/                   # 资源文件
│   │       ├── layout/            # 布局文件
│   │       ├── values/            # 配置文件
│   │       └── menu/              # 菜单文件
│   └── build.gradle               # 应用级构建配置
└── build.gradle                   # 项目级构建配置
```

## 构建和运行

### 环境要求
- Android Studio Arctic Fox (2020.3.1) 或更高版本
- Android SDK 24 或更高版本
- Kotlin 1.9.0

### 构建步骤

1. 克隆或下载项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮或使用命令:
   ```bash
   ./gradlew installDebug
   ```

## 使用说明

### 添加记录
1. 在主页面点击对应的按钮(喂奶/拉屎/拉尿)
2. 填写相关信息
3. 点击"保存"按钮

### 查看统计
1. 点击底部导航栏的"统计"选项
2. 查看今日统计和本周趋势图表

### 查询历史
1. 点击底部导航栏的"历史"选项
2. 使用筛选器选择记录类型
3. 选择日期范围进行查询

### 删除记录
- 在记录列表中点击记录右侧的删除按钮
- 确认删除操作

## 数据库设计

### BabyRecord 表结构
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键(自增) |
| type | RecordType | 记录类型(FEEDING/POOP/PEE) |
| timestamp | Long | 记录时间戳 |
| milkAmount | Int? | 奶量(ml) |
| milkType | MilkType? | 奶类型(BREAST/FORMULA) |
| poopColor | String? | 大便颜色 |
| poopConsistency | String? | 大便稠度 |
| peeAmount | String? | 尿量 |
| notes | String? | 备注 |

## 依赖库

- AndroidX Core KTX: 1.12.0
- AppCompat: 1.6.1
- Material Components: 1.11.0
- ConstraintLayout: 2.1.4
- Room: 2.6.1
- Lifecycle: 2.7.0
- Coroutines: 1.7.3
- MPAndroidChart: 3.1.0

## 未来计划

- [ ] 添加数据导出功能(CSV/PDF)
- [ ] 支持多宝宝管理
- [ ] 添加提醒功能
- [ ] 云端备份和同步
- [ ] 更多统计图表类型
- [ ] 深色模式支持

## 许可证

本项目仅供学习和个人使用。

## 联系方式

如有问题或建议，欢迎反馈。
