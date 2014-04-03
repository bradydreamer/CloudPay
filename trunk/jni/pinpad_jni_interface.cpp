#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <dlfcn.h>
#include <semaphore.h>
#include <unistd.h>
#include <errno.h>

#include <jni.h>

#include "hal_sys_log.h"
#include "pinpad_jni_interface.h"
#include "pinpad_interface.h"

const char* g_pJNIREG_CLASS = "com/wizarpos/jni/PinPadInterface";

typedef struct pinpad_hal_interface
{
	pinpad_open                open;
	pinpad_close               close;
	pinpad_show_text           set_text;
	pinpad_select_key          set_key;
	pinpad_set_pin_length      set_pin_length;
	pinpad_encrypt_string      encrypt;
	pinpad_calculate_pin_block input_pin;
	pinpad_calculate_mac       calculate_mac;
	pinpad_update_user_key     update_user_key;
	pinpad_update_master_key   update_master_key;
	void* pSoHandle;
}PINPAD_HAL_INSTANCE;

static PINPAD_HAL_INSTANCE* g_pPinpadInstance = NULL;

int native_pinpad_open(JNIEnv* env, jclass obj)
{
	int nResult = 0;
	hal_sys_info("native_pinpad_open() is called");
	if(g_pPinpadInstance == NULL)
	{
		void* pHandle = dlopen("libwizarposHAL.so", RTLD_LAZY);
		if (!pHandle)
		{
			hal_sys_error("%s\n", dlerror());
			return -1;
		}

		g_pPinpadInstance = new PINPAD_HAL_INSTANCE();

		g_pPinpadInstance->open = (pinpad_open)dlsym(pHandle, "pinpad_open");
		if(g_pPinpadInstance->open == NULL)
		{
			hal_sys_error("can't find pinpad_open");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->close = (pinpad_close)dlsym(pHandle, "pinpad_close");
		if(g_pPinpadInstance->close == NULL)
		{
			hal_sys_error("can't find pinpad_close");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->set_text = (pinpad_show_text)dlsym(pHandle, "pinpad_show_text");
		if(g_pPinpadInstance->set_text == NULL)
		{
			hal_sys_error("can't find pinpad_show_text");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->set_key = (pinpad_select_key)dlsym(pHandle, "pinpad_select_key");
		if(g_pPinpadInstance->set_key == NULL)
		{
			hal_sys_error("can't find pinpad_select_key");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->encrypt = (pinpad_encrypt_string)dlsym(pHandle, "pinpad_encrypt_string");
		if(g_pPinpadInstance->encrypt == NULL)
		{
			hal_sys_error("can't find pinpad_encrypt_string");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->input_pin = (pinpad_calculate_pin_block)dlsym(pHandle, "pinpad_calculate_pin_block");
		if(g_pPinpadInstance->input_pin == NULL)
		{
			hal_sys_error("can't find pinpad_calculate_pin_block");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->calculate_mac = (pinpad_calculate_mac)dlsym(pHandle, "pinpad_calculate_mac");
		if(g_pPinpadInstance->calculate_mac == NULL)
		{
			hal_sys_error("can't find pinpad_calculate_mac");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->update_user_key = (pinpad_update_user_key)dlsym(pHandle, "pinpad_update_user_key");
		if(g_pPinpadInstance->update_user_key == NULL)
		{
			hal_sys_error("can't find pinpad_update_user_key");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->update_master_key = (pinpad_update_master_key)dlsym(pHandle, "pinpad_update_master_key");
		if(g_pPinpadInstance->update_master_key == NULL)
		{
			hal_sys_error("can't find pinpad_update_master_key");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->set_pin_length = (pinpad_set_pin_length)dlsym(pHandle, "pinpad_set_pin_length");
		if(g_pPinpadInstance->set_pin_length == NULL)
		{
			hal_sys_error("can't find pinpad_set_pin_length");
			goto pinpad_init_clean;
		}

		g_pPinpadInstance->pSoHandle = pHandle;
		nResult = g_pPinpadInstance->open();
	}
	return nResult;
pinpad_init_clean:
	if(g_pPinpadInstance != NULL)
	{
		delete g_pPinpadInstance;
		g_pPinpadInstance = NULL;
	}
	return -1;
}

int native_pinpad_close(JNIEnv* env, jclass obj)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;
	nResult = g_pPinpadInstance->close();
	dlclose(g_pPinpadInstance->pSoHandle);
	delete g_pPinpadInstance;
	g_pPinpadInstance = NULL;

	return nResult;
}

int native_pinpad_show_text(JNIEnv* env, jclass obj, jint nLineIndex, jbyteArray arryText, jint nLength, jint nFlagSound)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	//typedef int (*pinpad_show_text)(int nLineIndex, char* strText, int nLength, int nFlagSound);
	if(arryText == NULL)
		nResult = g_pPinpadInstance->set_text(nLineIndex, NULL, 0, nFlagSound);
	else
	{
		jbyte* pText = env->GetByteArrayElements(arryText, 0);
		nResult = g_pPinpadInstance->set_text(nLineIndex, (char*)pText, nLength, nFlagSound);
		env->ReleaseByteArrayElements(arryText, pText, 0);
	}
	return nResult;
}

int native_pinpad_select_key(JNIEnv* env, jclass obj, jint nKeyType, jint nMasterKeyID, jint nUserKeyID, jint nAlgorith)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	nResult = g_pPinpadInstance->set_key(nKeyType, nMasterKeyID, nUserKeyID, nAlgorith);
	return nResult;
}

int native_pinpad_encrypt_string(JNIEnv* env, jclass obj, jbyteArray arryPlainText, jint nTextLength, jbyteArray arryCipherTextBuffer)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	if(arryPlainText == NULL || arryCipherTextBuffer == NULL)
		return -1;

	//typedef int (*pinpad_encrypt_string)(unsigned char* pPlainText, int nTextLength, unsigned char* pCipherTextBuffer, int nCipherTextBufferLength);

	jbyte* pPlainText = env->GetByteArrayElements(arryPlainText, 0);
	jbyte* pCipherTextBuffer = env->GetByteArrayElements(arryCipherTextBuffer, 0);
	jint nCipherTextBufferLength = env->GetArrayLength(arryCipherTextBuffer);

	nResult = g_pPinpadInstance->encrypt((unsigned char*)pPlainText, nTextLength, (unsigned char*)pCipherTextBuffer, nCipherTextBufferLength);

	env->ReleaseByteArrayElements(arryPlainText, pPlainText, 0);
	env->ReleaseByteArrayElements(arryCipherTextBuffer, pCipherTextBuffer, 0);
	return nResult;
}

int native_pinpad_calculate_pin_block(JNIEnv* env, jclass obj, jbyteArray arryASCIICardNumber, jint nCardNumberLength, jbyteArray arryPinBlockBuffer, jint nTimeout_MS, jint nFlagSound)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	if(arryASCIICardNumber == NULL || arryPinBlockBuffer == NULL)
		return -1;

	jbyte* pASCIICardNumber = env->GetByteArrayElements(arryASCIICardNumber, 0);
	jbyte* pPinBlockBuffer = env->GetByteArrayElements(arryPinBlockBuffer, 0);
	jint nPinBlockBufferLength = env->GetArrayLength(arryPinBlockBuffer);

	nResult = g_pPinpadInstance->input_pin((unsigned char*)pASCIICardNumber, nCardNumberLength,
			(unsigned char*)pPinBlockBuffer, nPinBlockBufferLength, nTimeout_MS, nFlagSound);

	env->ReleaseByteArrayElements(arryASCIICardNumber, pASCIICardNumber, 0);
	env->ReleaseByteArrayElements(arryPinBlockBuffer, pPinBlockBuffer, 0);

	return nResult;
}

int native_pinpad_calculate_mac(JNIEnv* env, jclass obj, jbyteArray arryData, jint nDataLength, jint nMACFlag, jbyteArray arryMACOutBuffer)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	if(arryData == NULL || arryMACOutBuffer == NULL)
		return -1;

	//typedef int (*pinpad_calculate_mac)(unsigned char* pData, int nDataLength, int nMACFlag, unsigned char* pMACOutBuffer, int nMACOutBufferLength);

	jbyte* pData = env->GetByteArrayElements(arryData, 0);
	jbyte* pMACOutBuffer = env->GetByteArrayElements(arryMACOutBuffer, 0);
	int nMACOutBufferLength = env->GetArrayLength(arryMACOutBuffer);

	nResult = g_pPinpadInstance->calculate_mac((unsigned char*)pData, nDataLength, nMACFlag, (unsigned char*)pMACOutBuffer, nMACOutBufferLength);

	env->ReleaseByteArrayElements(arryData, pData, 0);
	env->ReleaseByteArrayElements(arryMACOutBuffer, pMACOutBuffer, 0);

	return nResult;
}

int native_pinpad_update_user_key(JNIEnv* env, jclass obj, jint nMasterKeyID, jint nUserKeyID, jbyteArray arryCipherNewUserKey, jint nCipherNewUserKeyLength)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	if(arryCipherNewUserKey == NULL)
		return -1;

	//typedef int (*pinpad_update_user_key)(int nMasterKeyID, int nUserKeyID, unsigned char* pCipherNewUserKey, int nCipherNewUserKeyLength);
	jbyte* pCipherNewUserKey = env->GetByteArrayElements(arryCipherNewUserKey, 0);
	nResult = g_pPinpadInstance->update_user_key(nMasterKeyID, nUserKeyID, (unsigned char*)pCipherNewUserKey, nCipherNewUserKeyLength);
	env->ReleaseByteArrayElements(arryCipherNewUserKey, pCipherNewUserKey, 0);

	return nResult;
}

int native_pinpad_update_master_key(JNIEnv* env, jclass obj, jint nMasterKeyID, jbyteArray arrayOldKey, jint nOldKeyLength, jbyteArray arrayNewKey, jint nNewKeyLength)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;

	jbyte* pOldKey = env->GetByteArrayElements(arrayOldKey, 0);
	jbyte* pNewKey = env->GetByteArrayElements(arrayNewKey, 0);

	hal_sys_error("Do update_master_key\n");
	nResult = g_pPinpadInstance->update_master_key(nMasterKeyID, (unsigned char*)pOldKey, nOldKeyLength, (unsigned char*)pNewKey, nNewKeyLength);

	env->ReleaseByteArrayElements(arrayOldKey, pOldKey, 0);
	env->ReleaseByteArrayElements(arrayNewKey, pNewKey, 0);
	return nResult;
}

int native_pinpad_set_pin_length(JNIEnv* env, jclass obj, jint nLength, jint nFlag)
{
	int nResult = -1;
	if(g_pPinpadInstance == NULL)
		return -1;
	nResult = g_pPinpadInstance->set_pin_length(nLength, nFlag);
	return nResult;
}

static JNINativeMethod g_Methods[] =
{
	{"open",			"()I",									(void*)native_pinpad_open},
	{"close",			"()I",									(void*)native_pinpad_close},
	{"setText",			"(I[BII)I",								(void*)native_pinpad_show_text},
	{"setKey",			"(IIII)I",								(void*)native_pinpad_select_key},
	{"setPinLength",	"(II)I",								(void*)native_pinpad_set_pin_length},
	{"encrypt",			"([BI[B)I",								(void*)native_pinpad_encrypt_string},
	{"inputPIN",		"([BI[BII)I",							(void*)native_pinpad_calculate_pin_block},
	{"calculateMac",	"([BII[B)I",							(void*)native_pinpad_calculate_mac},
	{"updateUserKey",	"(II[BI)I",								(void*)native_pinpad_update_user_key},
	{"updateMasterKey",	"(I[BI[BI)I",							(void*)native_pinpad_update_master_key}
};

const char* pinpad_get_class_name()
{
	return g_pJNIREG_CLASS;
}

JNINativeMethod* pinpad_get_methods(int* pCount)
{
	*pCount = sizeof(g_Methods) /sizeof(g_Methods[0]);
	return g_Methods;
}
