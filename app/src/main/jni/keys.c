//
// Created by Himanshu on 10/20/2021.
//

#include <jni.h>



JNIEXPORT jstring JNICALL
Java_project_superuniqueit_fastbuy_services_network_api_NetworkModule_getBaseURL(JNIEnv *env,
                                                                                        jobject thiz) {
    return (*env)->NewStringUTF(env, "https://www.fastbuy.lk");
}

JNIEXPORT jstring JNICALL
Java_project_superuniqueit_fastbuy_services_network_api_NetworkModule_getContumerKry(
        JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "ck_fd87f58d45ea89429ea6ee658555d466bcff5359");
}

JNIEXPORT jstring JNICALL
Java_project_superuniqueit_fastbuy_services_network_api_NetworkModule_getContumerSecret(
        JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "cs_da1498081ad0dc356e10781e0dfb7eeeb1404dff");
}


JNIEXPORT jstring JNICALL
Java_project_superuniqueit_fastbuy_ui_activity_MainActivity_getMerchant(JNIEnv *env,
                                                                               jobject thiz) {
    return (*env)->NewStringUTF(env, "214383");
}

JNIEXPORT jstring JNICALL
Java_project_superuniqueit_fastbuy_ui_activity_MainActivity_getMerchantSecret(JNIEnv *env,
                                                                                     jobject thiz) {
    return (*env)->NewStringUTF(env, "MS55bnNuZ2tqOWJh");
}