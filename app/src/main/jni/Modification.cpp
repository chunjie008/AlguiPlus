#include <jni.h>
#include <MemoryTool.h>

extern "C" 
{
	
	//清空搜索结果
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_clearResult(JNIEnv *env, jclass cls) {
	   clearResult();                                                     
	}
	
	//设置内存范围
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_setRange(JNIEnv *env, jclass cls,jint memory) {
	   setRange(memory);                                                 
	}
	
	//设置包名
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_setPackageName(JNIEnv *env, jclass cls,jstring packageName) {
    const char* str = env->GetStringUTFChars(packageName, NULL);
     char* newStr = strdup(str);
     env->ReleaseStringUTFChars(packageName, str);
	   setPackageName(newStr);                
       free(newStr);
	}
	
	//内存搜索
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_RangeMemorySearch(JNIEnv *env, jclass cls,jstring value, jint type) {
    const char* str = env->GetStringUTFChars(value, NULL);                 
     char* newStr = strdup(str);
     env->ReleaseStringUTFChars(value, str);
     RangeMemorySearch(newStr,type);     
     free(newStr);
     
	}
	
	//内存偏移搜索
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_MemoryOffset(JNIEnv *env, jclass cls,jstring value, jint type, jlong offset) {
	const char* str = env->GetStringUTFChars(value, NULL);
    char* newStr = strdup(str);
     env->ReleaseStringUTFChars(value, str);
       MemoryOffset(newStr, type, offset);     
       free(newStr);
       
	}
	
	//内存写入
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_MemoryWrite(JNIEnv *env, jclass cls,jstring value, jint type, jlong offset) {
	const char* str = env->GetStringUTFChars(value, NULL);
    char* newStr = strdup(str);
     env->ReleaseStringUTFChars(value, str);
	   MemoryWrite(newStr,type,offset);     
       free(newStr);
      
	}
	
	//获取包名pid
	JNIEXPORT int JNICALL Java_com_irene_algui_AlguiMemory_getPackageNamePid(JNIEnv *env, jclass cls,jstring packageName) {
    const char* str = env->GetStringUTFChars(packageName, NULL);
     char* newStr = strdup(str);
     env->ReleaseStringUTFChars(packageName, str);
	   int pid = getPackageNamePid(newStr);                
       free(newStr);
       return pid;
	}
	
	//获取搜索结果数量
	JNIEXPORT int JNICALL Java_com_irene_algui_AlguiMemory_getResultCount(JNIEnv *env, jclass cls) {
       return getResultCount();
	}
	
	//开启冻结线程
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_startFreeze(JNIEnv *env, jclass cls) {
       startFreeze();
	}
	
	//关闭冻结线程
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_stopFreeze(JNIEnv *env, jclass cls) {
       stopFreeze();
	}
	
	//获取基址
	JNIEXPORT jlong JNICALL Java_com_irene_algui_AlguiMemory_getModuleAddress(JNIEnv *env, jclass cls, jstring name) {
	   const char* str = env->GetStringUTFChars(name, NULL);
       char* newStr = strdup(str);
       env->ReleaseStringUTFChars(name, str);
	   jlong value = getModuleAddress(newStr);                
       free(newStr);
       
       return value;
	}

    //读取指针
    JNIEXPORT jlong JNICALL Java_com_irene_algui_AlguiMemory_readLong(JNIEnv *env, jclass cls, jlong value)   {
       /*unsigned long ul = static_cast<unsigned long>(value);
       unsigned long result = readLong(ul);
       return static_cast<jlong>(result);*/
       return readLong(value);
	}
	
	//设置数值
	JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_setValue(JNIEnv *env, jclass cls, jstring value, jlong addr,jint type)   
    {
       const char* str = env->GetStringUTFChars(value, NULL);
       char* newStr = strdup(str);
       env->ReleaseStringUTFChars(value, str);
	   setValue(newStr,addr,type);                
       free(newStr);
	}
}
