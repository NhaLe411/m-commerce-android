// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Khai báo plugin Android Application, sẽ được áp dụng trong mô-đun app
    alias(libs.plugins.android.application) apply false
    // Khai báo plugin Kotlin Android, sẽ được áp dụng trong mô-đun app (nếu bạn dùng Kotlin)
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    // Sử dụng alias nếu bạn đã định nghĩa trong libs.versions.toml
    // Nếu không, bạn có thể dùng: id("com.google.gms.google-services") version "LATEST_VERSION" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}