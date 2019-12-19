//
// Created by McQueen, Roy on 2019-12-19.
//

#include "AudioEngine.h"
#include "../../../../../oboe/include/oboe/Definitions.h"
#include "../../../../../oboe/include/oboe/AudioStream.h"


void AudioEngine ::start() {

    oboe::AudioStreamBuilder builder;

    builder.setCallback(this);
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setSharingMode(oboe::SharingMode::Exclusive);

    oboe::AudioStream *stream = nullptr;
    oboe::Result result = builder.openStream(&stream);

    if (result != oboe::Result::OK) {

        LOGE("Error opening stream: %s", oboe::convertToText(result));

    }


    auto setBufferSizeResults = stream->setBufferSizeInFrames(stream->getFramesPerBurst());
    if(setBufferSizeResults){

        LOGE("Error opening stream: %s", setBufferSizeResults.value());
    }


    result = stream->requestStart();

    if (result != oboe::Result::OK) {

        LOGE("Error opening stream: %s", oboe::convertToText(result));

    }





}


DataCallbackResult
AudioEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {


    return DataCallbackResult ::Continue;
}
