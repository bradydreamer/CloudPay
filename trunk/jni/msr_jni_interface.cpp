#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <dlfcn.h>
#include <semaphore.h>
#include <unistd.h>
#include <errno.h>
#include <pthread.h>
#include <jni.h>

#include <time.h>

#include "hal_sys_log.h"
#include "msr_jni_interface.h"
#include "msr_interface.h"


const char* g_pJNIREG_CLASS = "com/wizarpos/jni/MsrInterface";


typedef struct msr_interface
{
	MSR_OPEN					open;
	MSR_CLOSE					close;
	MSR_REGISTER_NOTIFIER		register_notifier;
	MSR_UNREGISTER_NOTIFIER		unregister_notifier;
	MSR_GET_TRACK_ERROR			get_track_error;
	MSR_GET_TRACK_DATA_LENGTH	get_track_data_length;
	MSR_GET_TRACK_DATA			get_track_data;
	void*						pHandle;
	bool                        cancelPoll;
	int                         pThread;
}MSR_INSTANCE;

static MSR_INSTANCE msr_instance;


static int msr_module_init()
{

	char *pError = NULL;

	void* pHandle = dlopen("libwizarposHAL.so", RTLD_LAZY);
	if (!pHandle)
	{
		hal_sys_error("can't open msr driver: %s\n", dlerror());
	    return -2;
	}

	msr_instance.open = (MSR_OPEN)dlsym(pHandle, "msr_open");
	if(msr_instance.open == NULL)
	{
		hal_sys_info("test....");
		hal_sys_info(strerror(errno));
		hal_sys_error("can't open msr_open: %s\n", pError);
		goto msr_module_init_clean;
	}

	msr_instance.close = (MSR_CLOSE)dlsym(pHandle, "msr_close");
	if(msr_instance.close == NULL)
	{
		
		hal_sys_error("can't open msr_close\n");
		goto msr_module_init_clean;
	}

	msr_instance.register_notifier = (MSR_REGISTER_NOTIFIER)dlsym(pHandle, "msr_register_notifier");
	if(msr_instance.register_notifier == NULL)
	{
		hal_sys_error("can't open msr_register_notifier\n");
		goto msr_module_init_clean;
	}

	msr_instance.unregister_notifier = (MSR_UNREGISTER_NOTIFIER)dlsym(pHandle, "msr_unregister_notifier");
	if(msr_instance.unregister_notifier == NULL)
	{
		hal_sys_error("can't open msr_unregister_notifier\n");
		goto msr_module_init_clean;
	}

	msr_instance.get_track_error = (MSR_GET_TRACK_ERROR)dlsym(pHandle, "msr_get_track_error");
	if(msr_instance.get_track_error == NULL)
	{
		hal_sys_error("can't open msr_get_track_error\n");
		goto msr_module_init_clean;
	}

	msr_instance.get_track_data_length = (MSR_GET_TRACK_DATA_LENGTH)dlsym(pHandle, "msr_get_track_data_length");
	if(msr_instance.get_track_data_length == NULL)
	{
		hal_sys_error("can't open msr_get_track_data_length\n");
		goto msr_module_init_clean;
	}

	msr_instance.get_track_data = (MSR_GET_TRACK_DATA)dlsym(pHandle, "msr_get_track_data");
	if(msr_instance.get_track_data == NULL)
	{
		hal_sys_error("can't open msr_get_track_data\n");
		goto msr_module_init_clean;
	}
	msr_instance.pHandle = pHandle;
	return 1;

msr_module_init_clean:
	return -1;
}

typedef struct msr_call_back_info
{
	sem_t m_sem;

}MSR_CALL_BACK_INFO;

static MSR_CALL_BACK_INFO g_MsrCallbackInfo;

static void msr_call_back(void* pUserData)
{
	MSR_CALL_BACK_INFO * pCallbackInfo = (MSR_CALL_BACK_INFO*)pUserData;
	hal_sys_info("enter c msr_call_back\n");
	sem_post(&(pCallbackInfo->m_sem));
	hal_sys_info("leave c msr_call_back\n");
	return;
}

int native_msr_open(JNIEnv * env, jclass obj)
{
	int initRs = msr_module_init();
	if(initRs == -2 || initRs == -1)
		return initRs;
	memset(&g_MsrCallbackInfo, 0, sizeof(MSR_CALL_BACK_INFO));
	sem_init(&(g_MsrCallbackInfo.m_sem), 0, 0);
	int ret = msr_instance.open();
	hal_sys_info("msr_instance.open() = %d\n", ret);
	return ret;

}


int native_msr_close (JNIEnv * env, jclass obj)
{
	sem_destroy(&(g_MsrCallbackInfo.m_sem));
	msr_instance.close();
	return dlclose(msr_instance.pHandle);

}

static void sig_usr(int signo)
{
	if(signo == SIGUSR1)
		hal_sys_info("received SIGUSR1\n");
	else if(signo == SIGUSR2)
		hal_sys_info("received SIGUSR2\n");
	else
		hal_sys_info("received signal %d\n", signo);
}


int native_msr_cancelPoll(JNIEnv * env, jclass obj)
{
	hal_sys_info("enter msr_cancelPoll\n");
	msr_instance.cancelPoll = true;
	pthread_kill(msr_instance.pThread, SIGUSR2);
	return 0;
}

int native_msr_poll (JNIEnv * env, jclass obj , jint nTimeout_MS)
{
	int nReturn = -1;
	int nTimeout_Sec = 0;
	struct timespec ts;

	msr_instance.cancelPoll = false;
	msr_instance.pThread = pthread_self();
	hal_sys_info("pThread = %d\n", msr_instance.pThread);
	struct sigaction actions;
	memset(&actions, 0, sizeof(actions));
	sigemptyset(&actions.sa_mask);
	actions.sa_flags = SA_NODEFER;
	actions.sa_handler = sig_usr;
	int rc = sigaction(SIGUSR2,&actions,NULL);

	nReturn = msr_instance.register_notifier(msr_call_back, &g_MsrCallbackInfo);
	if(nReturn < 0)
		return nReturn;
	if(nTimeout_MS < 0)
		nTimeout_Sec = -1;
	else
	{
		nTimeout_Sec = nTimeout_MS % 1000 ? (nTimeout_MS / 1000 + 1) : nTimeout_MS / 1000;
		clock_gettime(CLOCK_REALTIME, &ts);
		ts.tv_sec += nTimeout_Sec;
	}
	while(1)
	{
		nReturn = nTimeout_Sec >= 0 ? sem_timedwait(&(g_MsrCallbackInfo.m_sem), &ts)
				:sem_wait(&(g_MsrCallbackInfo.m_sem));
		hal_sys_info("sem_wait returned\n");
		if(msr_instance.cancelPoll == true)
		{
			nReturn = -1;
			break;
		}
		else if(nReturn == -1 && errno == EINTR)
			continue;
		else
			break;
	}
	msr_instance.unregister_notifier();
	
	if(nReturn == -1)
		return -1;
	return 0;

}


int native_msr_getTrackError (JNIEnv * env, jclass obj, jint nTrackIndex)
{
	return msr_instance.get_track_error(nTrackIndex);

}

int native_msr_getTrackDataLength (JNIEnv * env, jclass obj, jint nTrackIndex)
{
	return msr_instance.get_track_data_length(nTrackIndex);
}

int native_msr_getTrackData (JNIEnv * env, jclass obj, jint nTrackIndex, jbyteArray byteArray, jint nLength)
{
	jint nResult = 0;

	jbyte* arrayBody = env->GetByteArrayElements(byteArray, NULL);
	nResult = msr_instance.get_track_data(nTrackIndex, (unsigned char*)arrayBody, nLength);
	if(nResult > 0)
	{
		arrayBody[nResult] = '\0';
	}
	env->ReleaseByteArrayElements(byteArray, arrayBody, 0);
	return nResult;
}

static JNINativeMethod g_Methods[] =
{
	{"open",				"()I",		(void*)native_msr_open},
	{"close",				"()I",		(void*)native_msr_close},
	{"poll",				"(I)I",		(void*)native_msr_poll},
	{"cancelPoll",			"()I",		(void*)native_msr_cancelPoll},
	{"getTrackError",		"(I)I",		(void*)native_msr_getTrackError},
	{"getTrackDataLength",	"(I)I",		(void*)native_msr_getTrackDataLength},
	{"getTrackData",		"(I[BI)I",	(void*)native_msr_getTrackData},
};

const char* msr_get_class_name()
{
	return g_pJNIREG_CLASS;
}

JNINativeMethod* msr_get_methods(int* pCount)
{
	*pCount = sizeof(g_Methods) /sizeof(g_Methods[0]);
	return g_Methods;
}
