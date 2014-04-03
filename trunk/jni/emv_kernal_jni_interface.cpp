#include <fcntl.h>
#include <dlfcn.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include "emv_kernal_interface.h"
#include "emv_kernal_jni_interface.h"
#include <android/log.h>
#include "hal_sys_log.h"


EMV_KERNEL_INSTANCE* g_emv_kernel_instance = NULL;

JavaVM *g_jvm2 = NULL;
jobject g_obj2 = NULL;

int native_load(JNIEnv * env, jclass obj)
{

	char *pError = NULL;

	if(g_emv_kernel_instance == NULL)
	{

		void* pHandle = dlopen("/data/data/com.wizarpos.cuppos/lib/libEMVKernal.so", RTLD_LAZY);
		if (!pHandle)
		{
			hal_sys_error("can't open emv kernel: %s\n", dlerror());
			return -2;
		}
		g_emv_kernel_instance = new EMV_KERNEL_INSTANCE();

		g_emv_kernel_instance->pHandle = pHandle;

		// 1.1
		g_emv_kernel_instance->EMV_IsTagPresent = (EMV_IS_TAG_PRESENT)dlsym(pHandle, "EMV_IsTagPresent");
		if(g_emv_kernel_instance->EMV_IsTagPresent == NULL)
		{
			hal_sys_error("can't open EMV_IsTagPresent: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 1.2
		g_emv_kernel_instance->EMV_GetTagData = (EMV_GET_TAG_DATA)dlsym(pHandle, "EMV_GetTagData");
		if(g_emv_kernel_instance->EMV_GetTagData == NULL)
		{
			hal_sys_error("can't open EMV_GetTagData: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 1.3
		g_emv_kernel_instance->EMV_SetTagData = (EMV_SET_TAG_DATA)dlsym(pHandle, "EMV_SetTagData");
		if(g_emv_kernel_instance->EMV_SetTagData == NULL)
		{
			hal_sys_error("can't open EMV_SetTagData: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 2.1
		g_emv_kernel_instance->EMV_Initialize = (EMV_INITIALIZE)dlsym(pHandle, "EMV_Initialize");
		if(g_emv_kernel_instance->EMV_Initialize == NULL)
		{
			hal_sys_error("can't open EMV_Initialize: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 2.2
		g_emv_kernel_instance->EMV_ProcessNext = (EMV_PROCESS_NEXT)dlsym(pHandle, "EMV_ProcessNext");
		if(g_emv_kernel_instance->EMV_ProcessNext == NULL)
		{
			hal_sys_error("can't open EMV_ProcessNext: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.1
		g_emv_kernel_instance->EMV_GetVersionString = (EMV_GET_VERSION_STRING)dlsym(pHandle, "EMV_GetVersionString");
		if(g_emv_kernel_instance->EMV_GetVersionString == NULL)
		{
			hal_sys_info("can't open EMV_GetVersionString: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.2
		g_emv_kernel_instance->EMV_SetTransAmount = (EMV_SET_TRANS_AMOUNT)dlsym(pHandle, "EMV_SetTransAmount");
		if(g_emv_kernel_instance->EMV_SetTransAmount == NULL)
		{
			hal_sys_info("can't open EMV_SetTransAmount: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.3
		g_emv_kernel_instance->EMV_SetTransType = (EMV_SET_TRANS_TYPE)dlsym(pHandle, "EMV_SetTransType");
		if(g_emv_kernel_instance->EMV_SetTransType == NULL)
		{
			hal_sys_info("can't open EMV_SetTransType: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.4
		g_emv_kernel_instance->EMV_SetOtherAmount = (EMV_SET_OTHER_AMOUNT)dlsym(pHandle, "EMV_SetOtherAmount");
		if(g_emv_kernel_instance->EMV_SetOtherAmount == NULL)
		{
			hal_sys_info("can't open EMV_SetOtherAmount: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.5
		g_emv_kernel_instance->EMV_AIDPARAM_Clear = (EMV_AIDPARAM_CLEAR)dlsym(pHandle, "EMV_AIDPARAM_Clear");
		if(g_emv_kernel_instance->EMV_AIDPARAM_Clear == NULL)
		{
			hal_sys_info("can't open EMV_AIDPARAM_Clear: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.6
		g_emv_kernel_instance->EMV_AIDPARAM_Add = (EMV_AIDPARAM_ADD)dlsym(pHandle, "EMV_AIDPARAM_Add");
		if(g_emv_kernel_instance->EMV_AIDPARAM_Add == NULL)
		{
			hal_sys_info("can't open EMV_AIDPARAM_Add: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.7
		g_emv_kernel_instance->EMV_CAPKPARAM_Clear = (EMV_CAPKPARAM_CLEAR)dlsym(pHandle, "EMV_CAPKPARAM_Clear");
		if(g_emv_kernel_instance->EMV_CAPKPARAM_Clear == NULL)
		{
			hal_sys_info("can't open EMV_CAPKPARAM_Clear: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.8
		g_emv_kernel_instance->EMV_CAPKPARAM_Add = (EMV_CAPKPARAM_ADD)dlsym(pHandle, "EMV_CAPKPARAM_Add");
		if(g_emv_kernel_instance->EMV_CAPKPARAM_Add == NULL)
		{
			hal_sys_info("can't open EMV_CAPKPARAM_Add: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.9
		g_emv_kernel_instance->EMV_TerminalPARAM_Set = (EMV_TERMINAL_PARAM_SET)dlsym(pHandle, "EMV_TerminalPARAM_Set");
		if(g_emv_kernel_instance->EMV_TerminalPARAM_Set == NULL)
		{
			hal_sys_info("can't open EMV_TerminalPARAM_Set: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.10
		g_emv_kernel_instance->EMV_BlackCard_Clear = (EMV_BLACK_CARD_CLEAR)dlsym(pHandle, "EMV_BlackCard_Clear");
		if(g_emv_kernel_instance->EMV_BlackCard_Clear == NULL)
		{
			hal_sys_info("can't open EMV_BlackCard_Clear: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.11
		g_emv_kernel_instance->EMV_BlackCard_Add = (EMV_BLACK_CARD_ADD)dlsym(pHandle, "EMV_BlackCard_Add");
		if(g_emv_kernel_instance->EMV_BlackCard_Add == NULL)
		{
			hal_sys_info("can't open EMV_BlackCard_Add: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.12
		g_emv_kernel_instance->EMV_BlackCert_Clear = (EMV_BLACK_CERT_CLEAR)dlsym(pHandle, "EMV_BlackCert_Clear");
		if(g_emv_kernel_instance->EMV_BlackCert_Clear == NULL)
		{
			hal_sys_info("can't open EMV_BlackCert_Clear: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.13
		g_emv_kernel_instance->EMV_BlackCert_Add = (EMV_BLACK_CERT_ADD)dlsym(pHandle, "EMV_BlackCert_Add");
		if(g_emv_kernel_instance->EMV_BlackCert_Add == NULL)
		{
			hal_sys_info("can't open EMV_BlackCert_Add: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.14
		g_emv_kernel_instance->isNeedAdvice = (IS_NEED_ADVICE)dlsym(pHandle, "isNeedAdvice");
		if(g_emv_kernel_instance->isNeedAdvice == NULL)
		{
			hal_sys_info("can't open isNeedAdvice: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.15
		g_emv_kernel_instance->isNeedSignature = (IS_NEED_SIGNATURE)dlsym(pHandle, "isNeedSignature");
		if(g_emv_kernel_instance->isNeedSignature == NULL)
		{
			hal_sys_info("can't open isNeedSignature: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.16
		g_emv_kernel_instance->EMV_SetForceOnline = (EMV_SET_FORCE_ONLINE)dlsym(pHandle, "EMV_SetForceOnline");
		if(g_emv_kernel_instance->EMV_SetForceOnline == NULL)
		{
			hal_sys_info("can't open EMV_SetForceOnline: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.17
		g_emv_kernel_instance->EMV_GetCardRecord = (EMV_GET_CARD_RECORD)dlsym(pHandle, "EMV_GetCardRecord");
		if(g_emv_kernel_instance->EMV_GetCardRecord == NULL)
		{
			hal_sys_info("can't open EMV_GetCardRecord: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.18
		g_emv_kernel_instance->EMV_GetCandidateList = (EMV_GET_CANDIDATE_LIST)dlsym(pHandle, "EMV_GetCandidateList");
		if(g_emv_kernel_instance->EMV_GetCandidateList == NULL)
		{
			hal_sys_info("can't open EMV_GetCandidateList: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.19
		g_emv_kernel_instance->EMV_SetCandidateListResult = (EMV_SET_CANDIDATE_LIST_RESULT)dlsym(pHandle, "EMV_SetCandidateListResult");
		if(g_emv_kernel_instance->EMV_SetCandidateListResult == NULL)
		{
			hal_sys_info("can't open EMV_SetCandidateListResult: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.20
		g_emv_kernel_instance->EMV_SetIDCheckResult = (EMV_SET_ID_CHECK_RESULT)dlsym(pHandle, "EMV_SetIDCheckResult");
		if(g_emv_kernel_instance->EMV_SetIDCheckResult == NULL)
		{
			hal_sys_info("can't open EMV_SetIDCheckResult: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.21
		g_emv_kernel_instance->EMV_SetOnlinePinEntered = (EMV_SET_ONLINE_PIN_ENTERED)dlsym(pHandle, "EMV_SetOnlinePINEntered");
		if(g_emv_kernel_instance->EMV_SetOnlinePinEntered == NULL)
		{
			hal_sys_info("can't open EMV_SetOnlinePinEntered: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.22
		g_emv_kernel_instance->EMV_SetPinBypassConfirmed = (EMV_SET_PINBYPASS_CONFIRMED)dlsym(pHandle, "EMV_SetPINBypassConfirmed");
		if(g_emv_kernel_instance->EMV_SetPinBypassConfirmed == NULL)
		{
			hal_sys_info("can't open EMV_SetPinBypassConfirmed: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}

		// 3.23
		g_emv_kernel_instance->EMV_SetOnlineResult = (EMV_SET_ONLINE_RESULT)dlsym(pHandle, "EMV_SetOnlineResult");
		if(g_emv_kernel_instance->EMV_SetOnlineResult == NULL)
		{
			hal_sys_info("can't open EMV_SetOnlineResult: %s\n", pError);
			goto emv_kernel_module_init_clean;
		}
	}
	g_emv_kernel_instance->g_jni_env = env;
	g_emv_kernel_instance->g_jni_obj = obj;
	env->GetJavaVM(&g_jvm2);
	g_obj2 = env->NewGlobalRef(obj);

	return 0;

	emv_kernel_module_init_clean:
	return -1;
}

jbyte native_close(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_close.....");
	if(g_emv_kernel_instance == NULL)
		return -1;
	dlclose(g_emv_kernel_instance->pHandle);
	delete g_emv_kernel_instance;
	g_emv_kernel_instance = NULL;
	return 0;
}

// 回调函数
static void EMV_ProcessNextCompleted(uchar status, uchar info)
{
	hal_sys_info("jni invoke EMV_ProcessNextCompleted.....");
	JNIEnv *env;
	jclass cls;

	if (g_jvm2->AttachCurrentThread(&env, NULL) != JNI_OK)
	{
		hal_sys_error("%s: AttachCurrentThread() failed", __FUNCTION__);
		return;
	}
	cls = env->FindClass("com/wizarpos/cuppos/activity/FuncActivity");
	if (cls == NULL)
	{
		hal_sys_error("FindClass() Error.....");
		return;
	}


	jmethodID method = env->GetStaticMethodID(cls, "emvProcessNextCompleted", "(BB)V");
	if (env->ExceptionCheck()) {
		hal_sys_error("jni can't find java processNextCompleted");
	   return;
	}
	env->CallStaticVoidMethod( cls, method,status,info);
	if (env->ExceptionCheck()) {
		hal_sys_error("jni can't call java processNextCompleted");
	}
}
static int EMV_CardTransmit(int nHandle, unsigned char* pAPDU, unsigned int nAPDULength, unsigned char* pResponse, unsigned int *pResponseLength)
{
	hal_sys_info("Enter EMV_CardTransmit().....\n");
	JNIEnv *env;
	jclass cls;

	if (g_jvm2->AttachCurrentThread(&env, NULL) != JNI_OK)
	{
		hal_sys_error("%s: AttachCurrentThread() failed", __FUNCTION__);
		return -1;
	}
	cls = env->FindClass("com/wizarpos/cuppos/activity/FuncActivity");
	if (cls == NULL)
	{
		hal_sys_error("FindClass() Error.....");
		return -1;
	}

	int iResult = -1;
	jmethodID method = env->GetStaticMethodID(cls, "emvCardTransmit", "(I[BI[B)I");
	if (env->ExceptionCheck())
	{
		hal_sys_error("jni can't find java cardTransmit");
		return -1;
	}
	jbyteArray apduData = env->NewByteArray(nAPDULength);
	env->SetByteArrayRegion(apduData, 0, nAPDULength, (jbyte*)pAPDU);

	jbyteArray responseData = env->NewByteArray(255);
	iResult = env->CallStaticIntMethod( cls, method,nHandle,apduData,nAPDULength,responseData);
	if (env->ExceptionCheck())
	{
		hal_sys_error("jni can't call java cardTransmit");
		return -1;
	}
	if(iResult >= 0)
	{
		jbyte* tmpBytes = env->GetByteArrayElements(responseData,0);
		memcpy(pResponse,tmpBytes,iResult);
		env->ReleaseByteArrayElements(responseData,tmpBytes,0);
		*pResponseLength = iResult;
	}
	hal_sys_info("Leave EMV_CardTransmit().....\n");
	return iResult;
}

// 1.1
jbyte native_isTagPresent(JNIEnv * env, jclass obj, jshort tag)
{
	hal_sys_info("jni invoke native_isTagPresent.....");

	jbyte bResult  = g_emv_kernel_instance->EMV_IsTagPresent(tag);
	
	return bResult;
}

// 1.2
jbyte native_getTagData(JNIEnv * env, jclass obj, jbyteArray data, jshortArray dataLen, jshort tag)
{
	hal_sys_info("jni invoke native_getTagData.....");

	jbyte iResult = 0;
	jshort* sDataLen = env->GetShortArrayElements(dataLen,0);

	jbyte* bData = env->GetByteArrayElements(data, 0);
	iResult = g_emv_kernel_instance->EMV_GetTagData((uchar*)bData,( ushort *)sDataLen,tag);
	env->ReleaseByteArrayElements(data, bData, 0);

	env->ReleaseShortArrayElements(dataLen,sDataLen,0);

	return iResult;
}

// 1.3
jbyte native_setTagData(JNIEnv * env, jclass obj, jbyteArray data, jshort dataLen,jshort tag)
{
	hal_sys_info("jni invoke native_setTagData.....");

	jbyte iResult = 0;

	jbyte* bData = env->GetByteArrayElements(data, 0);
	
	hal_sys_info("jni invoke g_emv_kernel_instance->EMV_SetTagData,data len: %d, tag is: %d.....",dataLen,tag);

	iResult = g_emv_kernel_instance->EMV_SetTagData((uchar*)bData,dataLen,tag);	

	hal_sys_info("jni invoke g_emv_kernel_instance->EMV_SetTagData successful.....");

	env->ReleaseByteArrayElements(data, bData, 0);

	return iResult;
}

// 2.1
jbyte native_init(JNIEnv * env, jclass obj, jbyte kernelType, jobject emvFunParam)
{
	hal_sys_info("jni invoke native_init.....");

	jbyte bResult = 0;
	jint transType = 0;

	EMV_INIT_PARAM initParam;
	memset(&initParam,0,sizeof(initParam));

	jclass clazz = env->GetObjectClass(emvFunParam);
	if(0 == clazz)
	{
		hal_sys_info("native_init: can't find emvFunParam type");
		return 0;
	}

	jfieldID fid = env->GetFieldID(clazz,"TransType","I");
	transType = env->GetIntField(emvFunParam,fid);
	hal_sys_info("native_init: transType is %d", transType);

	fid = env->GetFieldID(clazz,"ReaderHandle","I");
	initParam.pReaderHandle = env->GetIntField(emvFunParam,fid);
	hal_sys_info("native_init: pReaderHandle is %d", initParam.pReaderHandle );

	fid = env->GetFieldID(clazz,"CardType","B");
	initParam.cardType = (uchar)env->GetByteField(emvFunParam,fid);
	hal_sys_info("native_init: cardType is %d", initParam.cardType );

	fid = env->GetFieldID(clazz,"ATRLength","S");
	initParam.ATRLength = (uint)env->GetShortField(emvFunParam,fid);
	hal_sys_info("native_init: ATRLength is %d", initParam.ATRLength );

	fid = env->GetFieldID(clazz,"ATR","[B");
	jbyteArray tmpByteArray =  (jbyteArray)env->GetObjectField(emvFunParam,fid);
	jbyte* tmpBytes = env->GetByteArrayElements(tmpByteArray,0);
	memcpy(initParam.ATR,tmpBytes,env->GetArrayLength(tmpByteArray));
	env->ReleaseByteArrayElements(tmpByteArray,tmpBytes,0);

	// 初始化回调函数
	initParam.pTransmitPointer = (SMART_CARD_TRANSMIT)EMV_CardTransmit;
	initParam.pEMV_ProcessNextCompleted = (EMV_PROCESS_NEXT_COMPLETED)EMV_ProcessNextCompleted;

	g_emv_kernel_instance->EMV_Initialize(kernelType, &initParam);
	return 0;
}

// 2.2
jbyte native_processNext(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_processNext.....");
	return g_emv_kernel_instance->EMV_ProcessNext();
}

// 3.1
void native_getVersionString(JNIEnv * env, jclass obj, jbyteArray versionStr)
{
	hal_sys_info("jni invoke native_getVersionString.....");

	jbyte* strVersion = env->GetByteArrayElements(versionStr, 0);
	g_emv_kernel_instance->EMV_GetVersionString((uchar*)strVersion);
	env->ReleaseByteArrayElements(versionStr, strVersion, 0);
}

// 3.2
void native_setTransAmount(JNIEnv * env, jclass obj, jint amount)
{
	hal_sys_info("jni invoke native_setTransAmount.....");

	g_emv_kernel_instance->EMV_SetTransAmount(amount);
}

// 3.3
void native_setTransType(JNIEnv * env, jclass obj, jbyte transType)
{
	hal_sys_info("jni invoke native_setTransType.....");
	g_emv_kernel_instance->EMV_SetTransType(transType);
}

// 3.4
void native_setOtherAmount(JNIEnv * env, jclass obj, jint amount)
{
	hal_sys_info("jni invoke native_setOtherAmount.....");
	g_emv_kernel_instance->EMV_SetOtherAmount(amount);
}

// 3.5
jbyte native_clearAID(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_clearAIDs.....");
	return g_emv_kernel_instance->EMV_AIDPARAM_Clear();
}

// 3.6
jbyte native_addAID(JNIEnv * env, jclass obj, jbyteArray aidData)
{
	//hal_sys_info("jni invoke native_addAID.....");
	jbyte iResult = 0;
	jbyte* aid = env->GetByteArrayElements(aidData, NULL);

	iResult = g_emv_kernel_instance->EMV_AIDPARAM_Add((uchar *)aid);
	env->ReleaseByteArrayElements(aidData, aid, 0);
	return iResult;
}

// 3.7
jbyte native_clearCAPK(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_clearCAPK.....");
	return g_emv_kernel_instance->EMV_CAPKPARAM_Clear();
}

// 3.8
jbyte native_addCAPK(JNIEnv * env, jclass obj, jbyteArray capkData)
{
	//hal_sys_info("jni invoke native_addCAPK.....");
	jbyte iResult = 0;
	jbyte* capk = env->GetByteArrayElements(capkData, NULL);
	iResult = g_emv_kernel_instance->EMV_CAPKPARAM_Add((uchar *)capk);
	env->ReleaseByteArrayElements(capkData, capk, 0);
	return iResult;
}

// 3.9
jbyte native_setTerminalParam(JNIEnv * env, jclass obj, jbyteArray termParam)
{
	hal_sys_info("jni invoke native_setTerminalParam.....");
	jbyte iResult = 0;
	jbyte* term = env->GetByteArrayElements(termParam, NULL);
	iResult = g_emv_kernel_instance->EMV_TerminalPARAM_Set((uchar *)term);
	env->ReleaseByteArrayElements(termParam, term, 0);
	return iResult;
}

// 3.10
jbyte native_clearBlackCard(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_clearBlackCard.....");
	return g_emv_kernel_instance->EMV_BlackCard_Clear();
}

// 3.11
jbyte native_addBlackCard(JNIEnv * env, jclass obj, jbyteArray blackCardData)
{
	hal_sys_info("jni invoke native_addBlackCard.....");
	jbyte iResult = 0;
	jbyte* blackCard = env->GetByteArrayElements(blackCardData, NULL);
	iResult = g_emv_kernel_instance->EMV_BlackCard_Add((uchar *)blackCard);
	env->ReleaseByteArrayElements(blackCardData, blackCard, 0);
	return iResult;
}

// 3.12
jbyte native_clearBlackCert(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_clearBlackCert.....");
	return g_emv_kernel_instance->EMV_BlackCert_Clear();
}

// 3.13
jbyte native_addBlackCert(JNIEnv * env, jclass obj, jbyteArray blackCertData)
{
	hal_sys_info("jni invoke native_addBlackCert.....");
	jbyte iResult = 0;
	jbyte* blackCert = env->GetByteArrayElements(blackCertData, NULL);
	iResult = g_emv_kernel_instance->EMV_BlackCert_Add((uchar *)blackCert);
	env->ReleaseByteArrayElements(blackCertData, blackCert, 0);
	return iResult;
}

// 3.14
jbyte native_isNeedAdvice(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_isNeedAdvice.....");
	return g_emv_kernel_instance->isNeedAdvice();
}

// 3.15
jbyte native_isNeedSignature(JNIEnv * env, jclass obj)
{
	hal_sys_info("jni invoke native_isNeedSignature.....");
	return g_emv_kernel_instance->isNeedSignature();
}

// 3.16
void native_setForceOnline(JNIEnv * env, jclass obj, jbyte flag)
{
	hal_sys_info("jni invoke native_setForceOnline.....");
	g_emv_kernel_instance->EMV_SetForceOnline(flag);
}

// 3.17
jbyte native_getCardRecord(JNIEnv * env, jclass obj, jbyteArray dataLen, jbyteArray data)
{
	hal_sys_info("jni invoke native_getCardRecord.....");
	jbyte bResult = 0;
	jbyte* sDataLen = env->GetByteArrayElements(dataLen,0);

	jbyte* tmpBytes = env->GetByteArrayElements(data,0);
	bResult = g_emv_kernel_instance->EMV_GetCardRecord((uchar*)sDataLen,(uchar*)tmpBytes);
	env->ReleaseByteArrayElements(data,tmpBytes,0);

	env->ReleaseByteArrayElements(dataLen,sDataLen,0);

	return bResult;
}

// 3.18
jbyte native_getCandidateList(JNIEnv * env, jclass obj, jbyteArray aidNumber, jbyteArray aidList)
{
	hal_sys_info("jni invoke native_GetCandidateList.....");
	jbyte bResult = 0;
	jbyte* sAidNumber = env->GetByteArrayElements(aidNumber,0);

	jbyte* tmpBytes = env->GetByteArrayElements(aidList,0);
	bResult = g_emv_kernel_instance->EMV_GetCandidateList((uchar*)sAidNumber,(uchar*)tmpBytes);
	env->ReleaseByteArrayElements(aidList,tmpBytes,0);

	env->ReleaseByteArrayElements(aidNumber,sAidNumber,0);

	return bResult;
}

// 3.19
jbyte native_setCandidateListResult(JNIEnv * env, jclass obj, jbyte index)
{
	hal_sys_info("jni invoke native_setCandidateListResult.....");
	jbyte bResult = 0;
	bResult = g_emv_kernel_instance->EMV_SetCandidateListResult(index);
	return bResult;
}

// 3.20
jbyte native_setIDCheckResult(JNIEnv * env, jclass obj, jbyte result)
{
	hal_sys_info("jni invoke native_setIDCheckResult.....");
	jbyte bResult = 0;
	bResult = g_emv_kernel_instance->EMV_SetIDCheckResult(result);
	return bResult;
}

// 3.21
jbyte native_setOnlinePINEntered(JNIEnv * env, jclass obj, jbyte result)
{
	hal_sys_info("jni invoke native_setOnlinePINEntered.....");
	jbyte bResult = 0;
	bResult = g_emv_kernel_instance->EMV_SetOnlinePinEntered(result);
	return bResult;
}

// 3.22
jbyte native_SetPINBypassConfirmed(JNIEnv * env, jclass obj, jbyte result)
{
	hal_sys_info("jni invoke native_SetPINBypassConfirmed.....");
	jbyte bResult = 0;
	bResult = g_emv_kernel_instance->EMV_SetPinBypassConfirmed(result);
	return bResult;
}

// 3.23
jbyte native_setOnlineResult(JNIEnv * env, jclass obj, jbyte result, jbyteArray issuerRespData, jbyte issuerRespDataLength)
{
	hal_sys_info("jni invoke native_setOnlineResult.....");
	jbyte iResult = 0;
	jbyte* respData = env->GetByteArrayElements(issuerRespData, NULL);
	iResult = g_emv_kernel_instance->EMV_SetOnlineResult(result, (uchar *)respData, issuerRespDataLength);
	env->ReleaseByteArrayElements(issuerRespData, respData, 0);
	return iResult;
}

const char* g_pJNIREG_CLASS = "com/wizarpos/cuppos/activity/FuncActivity";
static JNINativeMethod g_Methods[] =
{
	{"loadEMVKernel",				"()B",											(void*)native_load},
	{"exitEMVKernel",				"()B",											(void*)native_close},
	{"isEMVTagPresent",				"(S)B",											(void*)native_isTagPresent},		// 1.1
	{"getEMVTagData",				"([B[SS)B",										(void*)native_getTagData},			// 1.2
	{"setEMVTagData",				"([BSS)B",										(void*)native_setTagData},			// 1.3
	{"initEMVKernel",				"(BLcom/wizarpos/EMVKernel/EMVInitParam;)B",	(void*)native_init},                // 2.1
	{"emvProcessNext",				"()B",	                                        (void*)native_processNext},         // 2.2
	{"getEMVKernelVersion",			"([B)V",										(void*)native_getVersionString},    // 3.1
	{"setEMVTransAmount",			"(I)V",											(void*)native_setTransAmount},      // 3.2
	{"setEMVTransType",				"(B)V",											(void*)native_setTransType},        // 3.3
	{"setEMVTransOtherAmount",		"(I)V",											(void*)native_setOtherAmount},      // 3.4
	{"clearEMVAID",					"()B",											(void*)native_clearAID},            // 3.5
	{"addEMVAID",					"([B)B",				                        (void*)native_addAID},              // 3.6
	{"clearEMVCAPK",				"()B",											(void*)native_clearCAPK},           // 3.7
	{"addEMVCAPK",					"([B)B",			                            (void*)native_addCAPK},             // 3.8
	{"setEMVTerminalParam",			"([B)B",		                                (void*)native_setTerminalParam},    // 3.9
	{"clearEMVExceptionFile",		"()B",											(void*)native_clearBlackCard},      // 3.10
	{"addEMVExceptionFile",			"([B)B",		                                (void*)native_addBlackCard},        // 3.11
	{"clearEMVRevokedCert",			"()B",									        (void*)native_clearBlackCert},      // 3.12
	{"addEMVRevokedCert",			"([B)B",		                                (void*)native_addBlackCert},        // 3.13
	{"isEMVTransNeedAdvice",		"()B",											(void*)native_isNeedAdvice},        // 3.14
	{"isEMVTransNeedSignature",		"()B",											(void*)native_isNeedSignature},     // 3.15
	{"setEMVTransForceOnline",		"(B)V",											(void*)native_setForceOnline},      // 3.16
	{"getEMVCardTransRecord",		"([B[B)B",										(void*)native_getCardRecord},       // 3.17
	{"getEMVCandidateList",	    	"([B[B)B",	                                    (void*)native_getCandidateList},    // 3.18
	{"setEMVCandidateListResult",	"(B)B",	                                        (void*)native_setCandidateListResult},  // 3.19
	{"setEMVIDCheckResult",		    "(B)B",											(void*)native_setIDCheckResult},        // 3.20
	{"setEMVOnlinePINEntered",	    "(B)B",	                                    	(void*)native_setOnlinePINEntered},     // 3.21
	{"SetEMVPINBypassConfirmed",	"(B)B",	                                        (void*)native_SetPINBypassConfirmed},   // 3.22
	{"setEMVOnlineResult",	        "(B[BB)B",	                                    (void*)native_setOnlineResult},         // 3.23
};

const char* emv_kernal_get_class_name()
{
	return g_pJNIREG_CLASS;
}

JNINativeMethod* emv_kernal_get_methods(int* pCount)
{
	*pCount = sizeof(g_Methods) /sizeof(g_Methods[0]);
	return g_Methods;
}
