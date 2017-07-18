# YGOMobile
Android编译环境
---------------------
    下载Android Studio和Android sdk
    
ndk编译环境(路径不能太长)
---------------------
    第一次得编译irrlicht\source\Irrlicht\Android
    这个ndk的版本暂时不能换，下面根据自己系统下载（不懂的就下载第一个链接）
    http://dl.google.com/android/ndk/android-ndk-r9d-windows-x86.zip
    http://dl.google.com/android/ndk/android-ndk-r9d-darwin-x86.tar.bz2
    http://dl.google.com/android/ndk/android-ndk-r9d-linux-x86.tar.bz2

    解压并且配置环境变量，cmd窗口，输ndk-build --version，看到一堆数字和英文的版权就是ok了

1.准备游戏资料
--------------------------
    cd mobile\assets\data
    fonts\ygo.ttf
    cards.cdb
    scripts.zip

2.编译ygo的so
-------------------------
    项目根目录运行命令行窗口
    cd libcore
    ndk-build -j4
    成功：libcore\libs\armeabi-v7a\libYGOMobile.so
    大于4.6M

3.包名和签名
---------------------
    一个手机相同包名的app只能同时存在一个，低版本无法覆盖高版本
    包名相同，签名不相同的app是无法覆盖安装。
    （如果你不是组内的开发员，那么你需要改包名和准备一个签名，不然你的app无法覆盖旧版本，或者无法同时存在）

4.如何改包名，制作签名
----------------------------
    如果你需要改包名
    编辑：mobile\build.gradle
    applicationId "cn.garymb.ygomobile"
    cn.garymb.ygomobile改为你的包名，如果不懂，请在后面加.xxxx，例如我是菜菜，改为cn.garymb.ygomobile.caicai
    5.制作签名（仅第一次或者你没有签名文件）
    左边的project标签，选中mobile，点击顶部的菜单Build->Generate Signed Apk->Create New
    key store path: 点第一行的...选择保存位置或者手动输入，例如D:\ygo.jks
    Password:       签名密码，如果不懂，建议直接123456
    Alias:          签名key，随便一个名字，建议直接ygo
    Password:       key密码，如果不懂，建议直接123456
    First and Last name:随便一个名字

6.生成apk文件
-------------------------
    如果是自己电脑，最好勾上Remember passwords
    key store password  签名密码
    key alias           签名key
    key password        key密码
    点Next，第一行就是apk的保存文件夹，下面的V1和V2，如果不懂，请不要勾V2，然后点Finish。