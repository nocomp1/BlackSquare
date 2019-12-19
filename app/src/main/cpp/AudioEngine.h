//
// Created by McQueen, Roy on 2019-12-19.
//

#ifndef BLACKSQUARE_AUDIOENGINE_H
#define BLACKSQUARE_AUDIOENGINE_H

#include <oboe/Oboe.h>
using namespace oboe;
class AudioEngine : public AudioStreamCallback {
public:
    DataCallbackResult
    onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) override;

private:
    void start();

private:Synthesizer synth;

};


#endif //BLACKSQUARE_AUDIOENGINE_H
