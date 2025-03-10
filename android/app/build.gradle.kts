plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "triviyou.michal.com"
    compileSdk = 34

    defaultConfig {
        applicationId = "triviyou.michal.com"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.games)
    implementation(libs.firebase.firestore) // אם lib לא עובד, הוסף את השורות הבאות
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.android.material:material:1.7.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.material:material:1.10.0")
   // implementation("com.google.android.youtube:youtube-android-player:1.2.2")

    // Firebase product dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.firebase:firebase-firestore:24.7.1")

}
