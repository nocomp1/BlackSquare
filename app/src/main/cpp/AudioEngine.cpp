////
//// Created by McQueen, Roy on 2019-12-19.
////
//
//#include "AudioEngine.h"
//#include "../../../../../oboe/include/oboe/Definitions.h"
//#include "../../../../../oboe/include/oboe/AudioStream.h"
//#include "android/asset_manager.h"
//#include <android/asset_manager_jni.h>
//#include <__hash_table>
//#include <__tree>
//#include <valarray>
//#include <deque>
//
//using namespace oboe;
//
//void AudioEngine::start() {
//
//
//
//    oboe::AudioStreamBuilder builder;
//
//   // builder.setCallback(this);
//    builder.setFormat(AudioFormat::Float);
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
//
//    auto setBufferSizeResults = stream->setBufferSizeInFrames(stream->getFramesPerBurst() * 2);
//
//    if (setBufferSizeResults) {
//
//        LOGE("Error opening stream: %s", setBufferSizeResults.value());
//    }
//
//
//    result = stream->requestStart();
//
//    if (result != oboe::Result::OK) {
//
//        LOGE("Error opening stream: %s", oboe::convertToText(result));
//
//    }
//
//
//}
//
//
////DataCallbackResult
////AudioEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {
////
////
////    return DataCallbackResult::Continue;
////}
//
