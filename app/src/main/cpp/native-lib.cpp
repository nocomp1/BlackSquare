//
// Created by McQueen, Roy on 2019-11-19.
//

#include <jni.h>
#include <oboe/Oboe.h>
#include <string>
#include <android/log.h>
#include "../../../../../oboe/src/common/OboeDebug.h"

#include "AudioEngine.h"
#include "AudioEngine.cpp"
#define JNIEXPORT  __attribute__ ((visibility ("default")))
#define JNICALL


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_blacksquare_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    __android_log_write(ANDROID_LOG_DEBUG, "API123", "Debug Log");

    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_blacksquare_MainActivity_add(
        JNIEnv *pEnv,
        jobject pThis,
        jint a,
        jint b) {
    return a + b;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_blacksquare_MainActivity_passObject(
        JNIEnv *pEnv,
        jobject pThis,
        jobject pObject) {

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_blacksquare_MainActivity_returnObject() {

    oboe::AudioStreamBuilder builder;
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setSharingMode(oboe::SharingMode::Exclusive);

    oboe::AudioStream *stream = nullptr;
    oboe::Result result = builder.openStream(&stream);

    if (result != oboe::Result::OK) {

        LOGE("Error opening stream: %s", oboe::convertToText(result));

    }

    result = stream->requestStart();
    if (result != oboe::Result::OK) {

        LOGE("Error opening stream: %s", oboe::convertToText(result));

    }



}