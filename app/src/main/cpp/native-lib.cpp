////
//// Created by McQueen, Roy on 2019-11-19.
////
//
//#include <jni.h>
//#include <oboe/Oboe.h>
//#include <string>
//#include <android/log.h>
//#include "../../../../../oboe/src/common/OboeDebug.h"
//
//#include "AudioEngine.h"
//#include "AudioEngine.cpp"
//#define JNIEXPORT  __attribute__ ((visibility ("default")))
//#define JNICALL
//
//
//
//
//
//AudioEngine engine;
//extern "C" {
//
//
//JNIEXPORT void JNICALL
//Java_com_example_blacksquare_MainActivity_startEngine(JNIEnv *env, jobject instance) {
//
//    engine.start(nullptr);
//
//
//}
//JNIEXPORT void JNICALL
//Java_com_example_blacksquare_MainActivity_tap(JNIEnv *env, jobject instance) {
//
//
//}
//
//
//JNIEXPORT void JNICALL
//Java_com_example_blacksquare_MainActivity_frequency(JNIEnv *env, jobject instance) {
//
//
//}
//
//
//
//
//
//
//
//
//
//extern "C" JNIEXPORT jstring JNICALL
//Java_com_example_blacksquare_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//
//    __android_log_write(ANDROID_LOG_DEBUG, "API123", "Debug Log");
//
//    return env->NewStringUTF(hello.c_str());
//}
//
//extern "C"
//JNIEXPORT jint JNICALL
//Java_com_example_blacksquare_MainActivity_add(
//        JNIEnv *pEnv,
//        jobject pThis,
//        jint a,
//        jint b) {
//    return a + b;
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_blacksquare_MainActivity_loadAssets(JNIEnv *env, jobject instance,
//                                                     jobject jAssetManager) {
//
//    AAssetManager *assetManager = AAssetManager_fromJava(env, jAssetManager);
//    if (assetManager == nullptr) {
//        LOGE("Could not obtain the AAssetManager");
//        return;
//    }
//    // Open your file
//    AAsset *file = AAssetManager_open(assetManager, "filename", AASSET_MODE_BUFFER);
//// Get the file length
//    off_t assetSize = AAsset_getLength(file);
//
//    const long maximumDataSizeInBytes = 12 * assetSize * sizeof(float);
//    auto decodedData = new uint8_t[maximumDataSizeInBytes];
//
//   // engine.start(decodedData);
//
//// Allocate memory to read your file
//    char *fileContent = new char[assetSize + 1];
//
//// Read your file
//    AAsset_read(file, fileContent, assetSize);
//// For safety you can add a 0 terminating character at the end of your file ...
//    fileContent[assetSize] = '\0';
//
//// Do whatever you want with the content of the file
//
//// Free the memoery you allocated earlier
//    delete[] fileContent;
//
//
//}
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_blacksquare_MainActivity_returnObject() {
//
//    oboe::AudioStreamBuilder builder;
//    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
//    builder.setSharingMode(oboe::SharingMode::Exclusive);
//
//    oboe::AudioStream *stream = nullptr;
//    oboe::Result result = builder.openStream(&stream);
//
//    if (result != oboe::Result::OK) {
//
//        LOGE("Error opening stream: %s", oboe::convertToText(result));
//
//    }
//
//    result = stream->requestStart();
//    if (result != oboe::Result::OK) {
//
//        LOGE("Error opening stream: %s", oboe::convertToText(result));
//
//    }
//
//
//}
//}