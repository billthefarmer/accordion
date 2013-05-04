#include <jni.h>
#include <assert.h>
#include <pthread.h>

// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

// for EAS midi
#include "eas.h"

// determines how many EAS buffers to fill a host buffer
#define NUM_BUFFERS 8

// EAS data
static EAS_DATA_HANDLE easData;
const S_EAS_LIB_CONFIG *pLibConfig;
static void *buffer;
static EAS_RESULT result;
static EAS_I32 bufferSize;

static EAS_I32 polyphony;

// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

// output mix interfaces
static SLObjectItf outputMixObject = NULL;
static SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;

// buffer queue player interfaces
static SLObjectItf playerObject = NULL;
static SLPlayItf playerPlay;
static SLAndroidSimpleBufferQueueItf playerBufferQueue;
static SLEffectSendItf playerEffectSend;
static SLMuteSoloItf playerMuteSolo;
static SLVolumeItf playerVolume;

// aux effect on the output mix, used by the buffer queue player
static const SLEnvironmentalReverbSettings reverbSettings =
    SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

// pointer and size of the next player buffer to enqueue, and number
// of remaining buffers
static short *nextBuffer;
static unsigned nextSize;
static int nextCount;

// setup EAS midi
void setupEAS()
{
    // get the library configuration
    pLibConfig = EAS_Config();
    assert(NULL != pLibConfig);
    assert(LIB_VERSION == pLibConfig->libVersion);

    polyphony = pLibConfig->maxVoices;

    // calculate buffer size
    bufferSize = pLibConfig->mixBufferSize * pLibConfig->numChannels *
	(EAS_I32)sizeof(EAS_PCM) * NUM_BUFFERS;

    // allocate output buffer memory
    buffer = malloc((EAS_U32)bufferSize);
    assert(NULL != buffer);

    if ((result = EAS_Init(&easData)) != EAS_SUCCESS)
    {
        free(buffer);
        return;
    }
}

// this callback handler is called every time a buffer finishes playing
void playerCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
{
    assert(bq == playerBufferQueue);
    assert(NULL == context);
    // for streaming playback, replace this test by logic to find and fill the next buffer
    if (--nextCount > 0 && NULL != nextBuffer && 0 != nextSize) {
        SLresult result;
        // enqueue another buffer
        result = (*playerBufferQueue)->Enqueue(playerBufferQueue, nextBuffer, nextSize);
        // the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
        // which for this code example would indicate a programming error
        assert(SL_RESULT_SUCCESS == result);
    }
}

// create the engine and output mix objects
void createEngine()
{
    SLresult result;

    // create engine
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    assert(SL_RESULT_SUCCESS == result);

    // realize the engine
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);

    // get the engine interface, which is needed in order to create
    // other objects
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE,
					   &engineEngine);
    assert(SL_RESULT_SUCCESS == result);

    // create output mix, with environmental reverb specified as a
    // non-required interface
    const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean req[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject,
					      1, ids, req);
    assert(SL_RESULT_SUCCESS == result);

    // realize the output mix
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);

    // get the environmental reverb interface
    // this could fail if the environmental reverb effect is not
    // available, either because the feature is not present, excessive
    // CPU load, or the required MODIFY_AUDIO_SETTINGS permission was
    // not requested and granted
    result = (*outputMixObject)->GetInterface(outputMixObject,
					      SL_IID_ENVIRONMENTALREVERB,
					      &outputMixEnvironmentalReverb);
    if (SL_RESULT_SUCCESS == result)
    {
        result =
	    (*outputMixEnvironmentalReverb)->
	    SetEnvironmentalReverbProperties(outputMixEnvironmentalReverb,
					     &reverbSettings);
    }
    // ignore unsuccessful result codes for environmental reverb, as
    // it is optional for this example

}


// create buffer queue audio player
void createBufferQueueAudioPlayer()
{
    SLresult result;

    // configure audio source
    SLDataLocator_AndroidSimpleBufferQueue loc_bufq =
	{SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
				   SL_PCMSAMPLEFORMAT_FIXED_16,
				   SL_PCMSAMPLEFORMAT_FIXED_16,
				   SL_SPEAKER_FRONT_CENTER,
				   SL_BYTEORDER_LITTLEENDIAN};
    SLDataSource audioSrc = {&loc_bufq, &format_pcm};

    // configure audio sink
    SLDataLocator_OutputMix loc_outmix =
	{SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&loc_outmix, NULL};

    // create audio player
    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND,
				  /*SL_IID_MUTESOLO,*/ SL_IID_VOLUME};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,
			      /*SL_BOOLEAN_TRUE,*/ SL_BOOLEAN_TRUE};
    result = (*engineEngine)->CreateAudioPlayer(engineEngine, &playerObject,
						&audioSrc, &audioSnk,
						3, ids, req);
    assert(SL_RESULT_SUCCESS == result);

    // realize the player
    result = (*playerObject)->Realize(playerObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);

    // get the play interface
    result = (*playerObject)->GetInterface(playerObject, SL_IID_PLAY,
					   &playerPlay);
    assert(SL_RESULT_SUCCESS == result);

    // get the buffer queue interface
    result = (*playerObject)->GetInterface(playerObject, SL_IID_BUFFERQUEUE,
					   &playerBufferQueue);
    assert(SL_RESULT_SUCCESS == result);

    // register callback on the buffer queue
    result = (*playerBufferQueue)->RegisterCallback(playerBufferQueue,
						    playerCallback, NULL);
    assert(SL_RESULT_SUCCESS == result);

    // get the effect send interface
    result = (*playerObject)->GetInterface(playerObject, SL_IID_EFFECTSEND,
					   &playerEffectSend);
    assert(SL_RESULT_SUCCESS == result);

    // mute/solo is not supported for sources that are known to be
    // mono, as this is
    // get the mute/solo interface
    result = (*playerObject)->GetInterface(playerObject, SL_IID_MUTESOLO,
					   &playerMuteSolo);
    assert(SL_RESULT_SUCCESS == result);

    // get the volume interface
    result = (*playerObject)->GetInterface(playerObject, SL_IID_VOLUME,
					   &playerVolume);
    assert(SL_RESULT_SUCCESS == result);

    // set the player's state to playing
    result = (*playerPlay)->SetPlayState(playerPlay, SL_PLAYSTATE_PLAYING);
    assert(SL_RESULT_SUCCESS == result);

}

// enable reverb on the buffer queue player
SLboolean enableReverb(SLboolean enabled)
{
    SLresult result;

    // we might not have been able to add environmental reverb to the
    // output mix
    if (NULL == outputMixEnvironmentalReverb)
    {
	return JNI_FALSE;
    }

    result = (*playerEffectSend)->EnableEffectSend(playerEffectSend,
						   outputMixEnvironmentalReverb,
						   (SLboolean) enabled,
						   (SLmillibel) 0);
    // and even if environmental reverb was present, it might no
    // longer be available
    if (SL_RESULT_SUCCESS != result)
    {
	return JNI_FALSE;
    }

    return JNI_TRUE;
}

// shut down the native audio system
void shutdown()
{

    // destroy buffer queue audio player object, and invalidate all
    // associated interfaces
    if (playerObject != NULL)
    {
	(*playerObject)->Destroy(playerObject);
	playerObject = NULL;
	playerPlay = NULL;
	playerBufferQueue = NULL;
	playerEffectSend = NULL;
	playerMuteSolo = NULL;
	playerVolume = NULL;
    }

    // destroy output mix object, and invalidate all associated interfaces
    if (outputMixObject != NULL)
    {
	(*outputMixObject)->Destroy(outputMixObject);
	outputMixObject = NULL;
	outputMixEnvironmentalReverb = NULL;
    }

    // destroy engine object, and invalidate all associated interfaces
    if (engineObject != NULL)
    {
	(*engineObject)->Destroy(engineObject);
	engineObject = NULL;
	engineEngine = NULL;
    }

}
