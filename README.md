Infinity 项目介绍

一、项目概述
Infinity 是一个 Android 应用项目，其功能涵盖了用户登录、文件管理、绘图、图片分享等多个方面。项目使用了 Kotlin 语言进行开发，并集成了众多 Android 开发常用的库和插件，遵循 Apache 2.0 开源协议。

二、项目结构
主要模块
1. app 模块：包含了应用的主要代码和资源，如界面布局、视图模型、文件管理等功能的实现。
2. .idea 文件夹：包含了 IntelliJ IDEA 开发环境的配置文件。
3. gradle 文件夹：包含了 Gradle 构建工具的配置文件和依赖版本管理文件。

关键文件和目录
1. LICENSE：项目使用的 Apache 2.0 开源协议文件。
2. settings.gradle.kts：Gradle 项目的设置文件，定义了项目的插件管理和依赖解析配置。
3. app/build.gradle.kts：应用模块的构建脚本，定义了应用的编译配置和依赖项。
4. app/src/main/java：Java 代码的主要目录，包含了各个功能模块的实现类。
5. app/src/main/res：应用的资源目录，包含了布局文件、字符串资源、图片资源等。

三、功能特性
1. 用户登录与文件管理
- 多方式登录：支持图案登录和密码登录，用户登录后会创建对应的用户文件夹和缩略图、原图文件夹。
- 文件操作：可以保存、读取、删除图片文件，图片分为缩略图和原图，分别存储在不同的文件夹中。

2. 绘图功能
- 文本更新：支持在绘图层更新文本内容，能够动态修改绘制的文本。
- 文本尺寸处理：自动处理文本的尺寸，根据文本内容调整绘制区域的大小。

3. 图片分享
- 分享绘图：可以将绘图视图转化为图片并分享到其他应用。
- 分享相册图片：支持分享相册中的图片到其他应用。

4. 动画引导
- 欢迎动画：应用启动时会播放 Lottie 动画，动画结束后根据用户登录状态和有效期进行页面跳转。

四、开发环境
1. 开发工具
- Android Studio：建议使用最新版本，以确保对项目的良好支持。

2. 开发语言和版本
- Kotlin：项目使用 Kotlin 进行开发，Kotlin 版本在 `gradle/libs.versions.toml` 中定义。
- Java：Java 版本为 1.8。

3. Android 配置
- compileSdk：34
- minSdk：24
- targetSdk：34

五、依赖库
项目使用了多个 Android 开发常用的依赖库，包括但不限于：
- AndroidX 系列：如 `androidx.core.ktx`、`androidx.appcompat`、`androidx.lifecycle` 等。
- Kotlin 协程：用于异步操作，如 `kotlinx.coroutines.core` 和 `kotlinx.coroutines.android`。
- Room：用于数据库操作，如 `androidx.room.runtime` 和 `androidx.room.ktx`。
- Navigation：用于实现应用的导航功能，如 `androidx.navigation.fragment.ktx` 和 `androidx.navigation.ui.ktx`。
- 其他：如 `glide` 用于图片加载，`airbnb.lottie` 用于播放 Lottie 动画等。

具体的依赖版本在 `gradle/libs.versions.toml` 和 `app/build.gradle.kts` 中定义。

六、快速开始
1. 克隆项目
```bash
git clone [项目仓库地址]
```

2. 打开项目
使用 Android Studio 打开克隆后的项目，等待 Gradle 同步完成。

3. 运行项目
连接 Android 设备或启动模拟器，点击 Android Studio 中的运行按钮，选择目标设备运行应用。

七、代码示例
1. 文件管理初始化
```kotlin
// 在 MyApplication 中初始化 FileManager
override fun onCreate() {
    super.onCreate()
    FileManager.init(this)
}
```

2. 绘图文本更新
```kotlin
// 在 LayerManager 中更新绘制的文本
fun updateText(text: String){
    getCurrentLayer()?.updateText(text)
}
```

3. 图片分享
```kotlin
// 在 HomeFragment 中分享绘图图片
private fun shareImage() {
    lifecycleScope.launch {
        mBinding.drawView.getBitmap().collect{ bitmap ->
            val externalDir = requireContext().getExternalFilesDir(null)
            val file = File(externalDir,"infinity.jpg")
            saveImageToExternalPath(file,bitmap)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "com.baidu.infinity.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_STREAM,uri)
            requireContext().startActivity(Intent.createChooser(intent,"分享图片"))
        }
    }
}
```

八、贡献指南
如果您想为该项目做出贡献，请遵循以下步骤：
1.  Fork 项目到自己的仓库。
2. 创建新的分支进行开发。
3. 提交代码并创建 Pull Request。

在提交代码时，请确保代码风格一致，并添加必要的注释和测试。

九、许可证
本项目使用 Apache 2.0 开源许可证，详细内容请参阅 [LICENSE](Infinity/LICENSE) 文件。
