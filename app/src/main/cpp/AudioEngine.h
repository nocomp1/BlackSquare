////
//// Created by McQueen, Roy on 2019-12-19.
////

#ifndef BLACKSQUARE_AUDIOENGINE_H
#define BLACKSQUARE_AUDIOENGINE_H
#include <array>
#include <oboe/Oboe.h>
#include <android/asset_manager.h>
#include <math.h>
#include <unistd.h>
#include <future>

using namespace oboe;
// must be established by someone else...
static AAssetManager *android_asset_manager = NULL;

class AudioEngine : public AudioStreamCallback {
public:

    void renderAudio(AudioStream *oboeStream, float *audioData, int32_t numFrames) {

        // Open your file
    AAsset *asset = AAssetManager_open(android_asset_manager, "sound1.wav", AASSET_MODE_BUFFER);


        // Get the length of the track (we assume it is stereo 48kHz)
        off_t trackSizeInBytes = AAsset_getLength(asset);


        auto numSamples = static_cast<int32_t>(trackSizeInBytes / sizeof(int32_t));
//        auto numberFrames = static_cast<int32_t>(numSamples / 2);

        // Load it into memory (we assume it is 16 bit signed integers)
        auto *sourceBuffer = static_cast<const int16_t*>(AAsset_getBuffer(asset));


        auto outputBuffer = std::make_unique<float[]>(numSamples);
         oboe::convertPcm16ToFloat(sourceBuffer, outputBuffer.get(), numSamples);
//        float kScaleI16ToFloat = (1.0f / 32768.0f);

//        for (int i = 0; i < numberFrames; i++) {
//            audioData[i] = static_cast<int16_t>(sourceBuffer[i] * kScaleI16ToFloat);
//
//        }



        auto *outputData = static_cast<float *>(audioData);
        // Generate random numbers (white noise) centered around zero.
        const float amplitude = 0.2f;
        for (int i = 0; i < numFrames; ++i){
            outputData[i] = ((float)drand48() - 0.5f) * 2 * amplitude;
        }




        AAsset_close(asset);


    }

    void start(AAssetManager &manager) {

        android_asset_manager = &manager;

        oboe::AudioStreamBuilder builder;
        // The builder set methods can be chained for convenience.
        builder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setChannelCount(kChannelCount)
                ->setSampleRate(kSampleRate)
                ->setFormat(oboe::AudioFormat::Float)
                ->setCallback(this)
                ->openManagedStream(outStream);
        // Typically, start the stream after querying some stream information, as well as some input from the user
        outStream->requestStart();

    }

    // void stop();
    // int ReadAndWriteWave();
    DataCallbackResult
    onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) override {

        renderAudio(oboeStream, static_cast<float *>(audioData), numFrames);

//
//        float *floatData = (float *) audioData;
//        for (int i = 0; i < numFrames; ++i) {
//            float sampleValue = kAmplitude * sinf(mPhase);
//            for (int j = 0; j < kChannelCount; j++) {
//                floatData[i * kChannelCount + j] = sampleValue;
//            }
//            mPhase += mPhaseIncrement;
//            if (mPhase >= kTwoPi) mPhase -= kTwoPi;
//        }



        return oboe::DataCallbackResult::Continue;
    }

private:
    oboe::ManagedStream outStream;
    // Stream params
    static int constexpr kChannelCount = 2;
    static int constexpr kSampleRate = 48000;

    // Wave params, these could be instance variables in order to modify at runtime
   // static float constexpr kAmplitude = 0.5f;

    static float constexpr kFrequency = 440;
    static float constexpr kPI = M_PI;
    static float constexpr kTwoPi = kPI * 2;

    static double constexpr mPhaseIncrement = kFrequency * kTwoPi / (double) kSampleRate;

    // Keeps track of where the wave is
//    float mPhase = 0.0;

    // AAssetManager &aAssetManager;
};


#endif //BLACKSQUARE_AUDIOENGINE_H
