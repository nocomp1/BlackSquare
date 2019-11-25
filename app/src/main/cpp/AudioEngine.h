//
// Created by McQueen, Roy on 2019-11-19.
//

#ifndef BLACKSQUARE_AUDIOENGINE_H
#define BLACKSQUARE_AUDIOENGINE_H

#include <oboe/Oboe.h>
#include "Synthesizer.h"

class AudioEngine : public AudioStreamCallback{

    void start();

private:
    Synthesizer synth;



};


#endif //BLACKSQUARE_AUDIOENGINE_H
