#define TAG "decrypt_jni"  
  
#include <android/log.h>  
#include "jniUtils.h"  
  
  
  
static const char* const kClassPathName = "com/example/unshell/UnshellApplication";  
  
  
jstring  
Java_com_example_unshell_UnshellApplication_DecryptWrite( JNIEnv* env, jobject thiz,jbyteArray buf,jint size ){   
	  
     const char* filename = "/data/data/com.example.unshell/UnshelledApk.apk";
     
     unsigned char *buf_char = (char*)((*env)->GetByteArrayElements(env,buf, NULL));  
      
     int file_handle=file_open(filename); 
     
     decrypt(buf_char, size);
     
     __android_log_print(ANDROID_LOG_INFO, TAG, "decrypt over"); 
     
     int writesize=file_write(file_handle, buf_char,  size); 
     
     int close=file_close(file_handle);
     
     __android_log_print(ANDROID_LOG_INFO, TAG, "write over");  
     
     if(writesize == -1){
     	     return (*env)->NewStringUTF(env, "fail");  
     }
     else
           return  (*env)->NewStringUTF(env, filename);  
}  

  
/******************************JNI registration.************************************/  
static JNINativeMethod gMethods[] = {  
    {"DecryptWrite",         "([BI)Ljava/lang/String;",        (void *)Java_com_example_unshell_UnshellApplication_DecryptWrite},  
      
};  
  
int register_com_example_unshell_UnshellApplication(JNIEnv *env) {  
    return jniRegisterNativeMethods(env, kClassPathName, gMethods, sizeof(gMethods) / sizeof(gMethods[0]));  
      
}  