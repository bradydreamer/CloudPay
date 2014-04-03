#ifndef LED_JNI_INTERFACE_H_
#define LED_JNI_INTERFACE_H_

const char* led_get_class_name();
JNINativeMethod* led_get_methods(int* pCount);

#endif /* LED_JNI_INTERFACE_H_ */
