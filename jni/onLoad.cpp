#define TAG "jni_onLoad"  
  
#include <android/log.h> 
#include <sys/ptrace.h> 
#include "jniUtils.h"  
  
extern "C" {  
  
extern int register_com_example_unshell_UnshellApplication(JNIEnv *env);  
  
}  
  
static JavaVM *sVm;  
  
/* 
 * Throw an exception with the specified class and an optional message. 
 */  
int jniThrowException(JNIEnv* env, const char* className, const char* msg) {  
    jclass exceptionClass = env->FindClass(className);  
    if (exceptionClass == NULL) {  
        __android_log_print(ANDROID_LOG_ERROR,  
                TAG,  
                "Unable to find exception class %s",  
                        className);  
        return -1;  
    }  
  
    if (env->ThrowNew(exceptionClass, msg) != JNI_OK) {  
        __android_log_print(ANDROID_LOG_ERROR,  
                TAG,  
                "Failed throwing '%s' '%s'",  
                className, msg);  
    }  
    return 0;  
}  
  
JNIEnv* getJNIEnv() {  
    JNIEnv* env = NULL;  
    if (sVm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {  
        __android_log_print(ANDROID_LOG_ERROR,  
                            TAG,  
                            "Failed to obtain JNIEnv");  
        return NULL;  
    }  
    return env;  
}  
  
/* 
 * Register native JNI-callable methods. 
 * 
 * "className" looks like "java/lang/String". 
 */  
int jniRegisterNativeMethods(JNIEnv* env,  
                             const char* className,  
                             const JNINativeMethod* gMethods,  
                             int numMethods)  
{  
    jclass clazz;  
  
    __android_log_print(ANDROID_LOG_INFO, TAG, "Registering %s natives\n", className);  
    clazz = env->FindClass(className);  
    if (clazz == NULL) {  
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Native registration unable to find class '%s'\n", className);  
        return -1;  
    }  
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {  
        __android_log_print(ANDROID_LOG_ERROR, TAG, "RegisterNatives failed for '%s'\n", className);  
        return -1;  
    }  
    return 0;  
}  
//Dalvik���������C��ʱ����һ�����ǵ���JNI_OnLoad()����  
jint JNI_OnLoad(JavaVM* vm, void* reserved) {  
	
    int res = ptrace(PTRACE_TRACEME,0 ,0 ,0);
    if(res != 0){
    exit(0);
    }
    
    JNIEnv* env = NULL;  
    jint result = JNI_ERR;  
    sVm = vm;  
  
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {  
        __android_log_print(ANDROID_LOG_ERROR, TAG, "GetEnv failed!");  
        return result;  
    }  
  
    __android_log_print(ANDROID_LOG_INFO, TAG, "loading . . .");  
  
  
    if(register_com_example_unshell_UnshellApplication(env) != JNI_OK) {  
        __android_log_print(ANDROID_LOG_ERROR, TAG, "can't load register_com_example_unshell_UnshellApplication");  
        return -1;  
    }  
    
    

  
    __android_log_print(ANDROID_LOG_INFO, TAG, "loaded");  
  
    result = JNI_VERSION_1_4;  
  
	     
    return result;  
}  