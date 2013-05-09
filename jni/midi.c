////////////////////////////////////////////////////////////////////////////////
//
//  Accordion - An Android Accordion written in C and Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

#include <jni.h>
#include <assert.h>

// for EAS midi
#include "eas.h"

// determines how many EAS buffers to fill a host buffer
#define NUM_BUFFERS 8

// EAS data
static EAS_DATA_HANDLE pEASData;
const S_EAS_LIB_CONFIG *pLibConfig;
static EAS_PCM *buffer;
static EAS_RESULT result;
static EAS_I32 bufferSize;
static EAS_HANDLE midiHandle;
static EAS_I32 polyphony;

// init EAS midi
jint
Java_org_billthefarmer_accordion_MainActivity_midiInit(JNIEnv *env,
						       jobject clazz)
{
    // get the library configuration
    pLibConfig = EAS_Config();
    assert(NULL != pLibConfig);
    assert(LIB_VERSION == pLibConfig->libVersion);

    polyphony = pLibConfig->maxVoices;

    // calculate buffer size
    bufferSize = pLibConfig->mixBufferSize * pLibConfig->numChannels *
	(EAS_I32)sizeof(EAS_PCM) * NUM_BUFFERS;

    // init library
    if ((result = EAS_Init(&pEASData)) != EAS_SUCCESS)
    {
        return 0;
    }

    // open midi stream
    if (result = EAS_OpenMIDIStream(pEASData, &midiHandle, NULL) !=
	EAS_SUCCESS)
    {
	EAS_Shutdown(pEASData);
	return 0;
    }

    // set volume
    // if (result = EAS_SetVolume(pEASData, midiHandle, 90) !=
    // 	 EAS_SUCCESS)
    // {
    // 	EAS_CloseMIDIStream(pEASData, midiHandle);
    // 	EAS_Shutdown(pEASData);
    // 	return 0;
    // }

    return bufferSize;
}

// midi render
jint
Java_org_billthefarmer_accordion_MainActivity_midiRender(JNIEnv *env,
							 jobject clazz,
							 jbyteArray byteArray)
{
    jboolean isCopy;
    EAS_I32 numGenerated;
    EAS_I32 count;
    jsize size;

    buffer =
	(EAS_PCM *)(*env)->GetByteArrayElements(env, byteArray, &isCopy);

    size = (*env)->GetArrayLength(env, byteArray);

    count = 0;
    while (count < size)
    {
    	result = EAS_Render(pEASData, buffer + count / sizeof(EAS_PCM),
    			    pLibConfig->mixBufferSize, &numGenerated);
    	if (result != EAS_SUCCESS)
    	    break;

    	count += numGenerated * pLibConfig->numChannels * sizeof(EAS_PCM);
    }

    buffer[0] = 127;
    buffer[1] = 127;
    buffer[2] = 127;
    buffer[3] = 127;
    buffer[4] = 127;
    buffer[128] = -127;
    buffer[129] = -127;
    buffer[130] = -127;
    buffer[131] = -127;
    buffer[132] = -127;

    (*env)->ReleaseByteArrayElements(env, byteArray, (jbyte *)buffer, 0);

    return count;
}

// midi write
jboolean
Java_org_billthefarmer_accordion_MainActivity_midiWrite(JNIEnv *env,
							jobject clazz,
							jint s, jint n,
							jint v)
{
static EAS_U8 buf[3];

    buf[0] = s;
    buf[1] = n;
    buf[2] = v;

    result = EAS_WriteMIDIStream(pEASData, midiHandle, buf, sizeof(buf));

    if (result != EAS_SUCCESS)
	return 0;

    return 1;
}

// shutdown EAS midi
jboolean
Java_org_billthefarmer_accordion_MainActivity_midiShutdown(JNIEnv *env,
							   jobject clazz)
{

    EAS_CloseMIDIStream(pEASData, midiHandle);
    EAS_Shutdown(pEASData);

    return 1;
}
