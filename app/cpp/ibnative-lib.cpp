////
//// Created by anhnd on 21/10/2020.
////
//
//#include <jni.h>
//#include <string>
//
//std::string getData(int x) {
//    std::string app_secret = "Null";
//
//    if (x == 1) app_secret = "123456789"; //The first key that you want to protect against decompilation
//    if (x == 2) app_secret = "abcdefg";   //The second key that you want to protect against decompilation
//
//    // The number of parameters to be protected can be increased.
//
//    return app_secret;
//}
//
//
//extern "C"
//JNIEXPORT jstring
//
//extern "C" JNICALL
//Java_com_solar_dev_tiktok_app_MyApp_getApiKey(
//        JNIEnv *env,
//        jobject /* this */,
//        jint x) {
//    std::string app_secret = "Null";
//    app_secret = getData(x);
//    return env->NewStringUTF(app_secret.c_str());
//}