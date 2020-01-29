////
//// Created by McQueen, Roy on 2019-11-19.
////
//
#include <jni.h>
//#include <oboe/Oboe.h>
#include <string>
#include <android/log.h>
#include "../../../../../oboe/src/common/OboeDebug.h"
#include <android/asset_manager.h>
#include "AudioEngine.h"

#define JNIEXPORT  __attribute__ ((visibility ("default")))
#define JNICALL
#include <iostream>
#include <ctime>
#include <ratio>
#include <chrono>
#include <iomanip>
#include "unistd.h"
#include <stdio.h>
#include <time.h>
#include <thread>
#include <functional>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <memory>
#include <jni.h>
#include <memory>

#include <android/asset_manager_jni.h>
using namespace std;
//const int NUM_SECONDS = 10;
//AudioEngine engine ;

AudioEngine engine;
extern "C" {

//std::unique_ptr<AudioEngine> engine;
//AudioEngine engine;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_blacksquare_MainActivity_startEngine(JNIEnv *env, jobject instance,
                                                      jobject jAssetManager) {

    AAssetManager *assetManager = AAssetManager_fromJava(env, jAssetManager);
    if (assetManager == nullptr) {
        LOGE("Could not obtain the AAssetManager");
        return;
    }
     //engine(<#initializer#>);
    //AudioEngine engine(*assetManager, <#initializer#>);
  // engine = std::make_unique<AudioEngine>(*assetManager);
    engine.start(*assetManager);

}

//start
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_blacksquare_MainActivity_startEngine() {
////
//////    AAssetManager *assetManager = AAssetManager_fromJava(env, jAssetManager);
//////    if (assetManager == nullptr) {
//////        LOGE("Could not obtain the AAssetManager");
//////        return;
//////    }
////    //engine(<#initializer#>);
////    //AudioEngine engine = AudioEngine(*assetManager);
////
//    engine.start();
////
//}

JNIEXPORT void JNICALL
Java_com_example_blacksquare_MainActivity_setDefaultStreamValues__II(JNIEnv *env,
                                                                                  jclass type,
                                                                                  jint sampleRate,
                                                                                  jint framesPerBurst) {
//    oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
//    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
}





extern "C"
JNIEXPORT void JNICALL

Java_com_example_blacksquare_MainActivity_startPlayEngineFromNative( JNIEnv* env, jobject obj ){

//        isPlaying = true;
    env->MonitorEnter(obj); // same effect as synchronized(thiz) { ...
    // Construct a String
  //  jstring jstr = env->NewStringUTF("This string comes from JNI");
    // First get the class that contains the method you need to call
  //  jclass clazz = env->FindClass("com/example/blacksquare/MainActivity");
    // Get the method that you want to call
 //   jmethodID messageMe = env->GetMethodID(clazz, "messageMe", "(Ljava/lang/String;)V");
    // Call the method on the object
    // env->CallVoidMethod(obj, messageMe, jstr);
    env->MonitorExit(obj);



//    while(isPlaying) {
//
//        //std::this_thread::sleep_for(std::chrono::milliseconds(1));
//
//       // struct timespec ts = {0, 100000000L };
//       //  nanosleep(&ts, NULL);
//
//        int milisec = 100; // length of time to sleep, in miliseconds
//        struct timespec req = {0};
//        req.tv_sec = 0;
//        req.tv_nsec = milisec * 1000000L;
//        nanosleep(&req, (struct timespec *)60000000);
//        env->CallVoidMethod(obj, messageMe, jstr);
//
//    }





    //    // long WAIT_TIME =1000LL;
//    auto a = std::chrono::high_resolution_clock::now();
//    auto now = std::chrono::system_clock::now();
//    auto now_ms = std::chrono::time_point_cast<std::chrono::milliseconds>(now);
//    while ((std::chrono::steady_clock::now() - a) < now_ms){
//
//
//        env->CallVoidMethod(obj, messageMe, jstr);
//
//
//        continue;
//    }

//
//    std::chrono::milliseconds ms (1);
//    std::chrono::time_point<std::chrono::high_resolution_clock> end;
//
//    end = std::chrono::high_resolution_clock.now() + ms; // this is the end point
//
//    while(isPlaying) // still less than the end?
//    {
//
//        if(std::chrono::high_resolution_clock::now() == end){
//            env->CallVoidMethod(obj, messageMe, jstr);
//            //std::cout << "Running" << std::endl;
//            end = std::chrono::high_resolution_clock::now() + ms;
//        } else{
//            end = std::chrono::high_resolution_clock::now() + ms;
//        }
//               // std::cout << "Running" << std::endl;
//    }
//










//    int count = 1;
//
//    double time_counter = 0;
//
//    clock_t this_time = clock();
//    clock_t last_time = this_time;
//
//    printf("Gran = %ld\n", NUM_SECONDS * CLOCKS_PER_SEC);
//
//    while(true)
//    {
//        this_time = clock();
//
//        time_counter += (double)(this_time - last_time);
//
//        last_time = this_time;
//
//        if(time_counter > (double)(NUM_SECONDS * CLOCKS_PER_SEC))
//        {
//            time_counter -= (double)(NUM_SECONDS * CLOCKS_PER_SEC);
//            printf("%d\n", count);
//
////            // Construct a String
////            jstring jstr = env->NewStringUTF("This string comes from JNI");
////            // First get the class that contains the method you need to call
////            jclass clazz = env->FindClass("com/example/blacksquare/MainActivity");
////            // Get the method that you want to call
////            jmethodID messageMe = env->GetMethodID(clazz, "messageMe", "(Ljava/lang/String;)V");
////            // Call the method on the object
//            env->CallVoidMethod(obj, messageMe, jstr);
//
//
//
//            count++;
//        }
//
//        printf("DebugTime = %f\n", time_counter);
//    }


// The function we want to execute on the new thread.





}






}



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

//
extern "C"
JNIEXPORT void JNICALL
Java_com_example_blacksquare_MainActivity_loadAssets(JNIEnv *env, jobject instance,
                                                     jobject jAssetManager) {
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


}



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