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
#include "led_jni_interface.h"
#include "led_service_interface.h"


const char* g_pJNIREG_CLASS = "com/wizarpos/jni/LEDInterface";


typedef struct led_interface
{
	LED_OPEN					open;
	LED_CLOSE					close;
	LED_ON		                turn_on;
	LED_OFF             		turn_off;
	LED_GET_STATUS			    get_status;
	void*						pHandle;
}LED_INSTANCE;

static LED_INSTANCE led_instance;


static int led_module_init()
{

	char *pError = NULL;

	void* pHandle = dlopen("libwizarposHAL.so", RTLD_LAZY);
	if (!pHandle)
	{
		hal_sys_error("can't open led driver: %s\n", dlerror());
	    return -2;
	}

	led_instance.open = (LED_OPEN)dlsym(pHandle, "led_open");
	if(led_instance.open == NULL)
	{
		hal_sys_error("can't open led_open\n");
		goto led_module_init_clean;
	}

	led_instance.close = (LED_CLOSE)dlsym(pHandle, "led_close");
	if(led_instance.close == NULL)
	{
		
		hal_sys_error("can't open led_close\n");
		goto led_module_init_clean;
	}

	led_instance.turn_on = (LED_ON)dlsym(pHandle, "led_on");
	if(led_instance.turn_on == NULL)
	{
		hal_sys_error("can't open led_on\n");
		goto led_module_init_clean;
	}

	led_instance.turn_off = (LED_OFF)dlsym(pHandle, "led_off");
	if(led_instance.turn_off == NULL)
	{

		hal_sys_error("can't open led_off\n");
		goto led_module_init_clean;
	}

	led_instance.get_status = (LED_GET_STATUS)dlsym(pHandle, "led_get_status");
	if(led_instance.get_status == NULL)
	{

		hal_sys_error("can't open led_get_status\n");
		goto led_module_init_clean;
	}

	led_instance.pHandle = pHandle;
	return 1;

led_module_init_clean:
	return -1;
}

int native_led_open(JNIEnv * env, jclass obj)
{
	int initRs = led_module_init();
	if(initRs == -2 || initRs == -1)
		return initRs;
	int ret = led_instance.open();
	hal_sys_info("led_instance.open() = %d\n", ret);
	return ret;
}

int native_led_close (JNIEnv * env, jclass obj)
{
	led_instance.close();
	hal_sys_info("led_instance.close()\n");
	return dlclose(led_instance.pHandle);
}

int native_led_on(JNIEnv * env, jclass obj, jint index)
{
	int ret = led_instance.turn_on(index);
	hal_sys_info("led_instance.turn_on() = %d\n", ret);
	return ret;
}

int native_led_off(JNIEnv * env, jclass obj, jint index)
{
	int ret = led_instance.turn_off(index);
	hal_sys_info("led_instance.turn_off() = %d\n", ret);
	return ret;
}

int native_led_get_status(JNIEnv * env, jclass obj, jint index)
{
	int ret = led_instance.get_status(index);
	hal_sys_info("led_instance.get_status() = %d\n", ret);
	return ret;
}

static JNINativeMethod g_Methods[] =
{
	{"open",				"()I",		(void*)native_led_open},
	{"close",				"()I",		(void*)native_led_close},
	{"turn_on",				"(I)I",		(void*)native_led_on},
	{"turn_off",			"(I)I",		(void*)native_led_off},
	{"get_status",		    "(I)I",		(void*)native_led_get_status},
};

const char* led_get_class_name()
{
	return g_pJNIREG_CLASS;
}

JNINativeMethod* led_get_methods(int* pCount)
{
	*pCount = sizeof(g_Methods) /sizeof(g_Methods[0]);
	return g_Methods;
}
