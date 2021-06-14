package com.dev.anhnd.mybaselibrary.viewmodel.auto

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException


private val hashMapSingleton by lazy {
    HashMap<String, Any?>()
}

@Synchronized
fun Class<*>.getSingleton(): Any {
    if (hashMapSingleton[this.simpleName] == null)
        hashMapSingleton[this.simpleName] = try {
            this.newInstance()
        } catch (e: InstantiationException) {
            getDefaultValue(this.name)
        }
    /**
     * If your class use this function not have empty constructor, it will cause error
     */
    return hashMapSingleton[this.simpleName]!!
}

fun getDefaultValue(typeName: String): Any? {
    return when (typeName) {
        Int::class.java.name
        -> 0
        String::class.java.name
        -> ""
        Float::class.java.name
        -> 0f
        Long::class.java.name
        -> 0L
        Boolean::class.java.name
        -> false
        else -> null
    }
}

fun Class<*>.createInstanceWithAuto(): Any? {
    val constructors = this.declaredConstructors
    val params = ArrayList<Any?>()
    var resultConstructor: Constructor<*>? = null
    var maxParamCount = 0
    var isSingleton = true
    for (constructor in constructors) {
        val paramCount = constructor.parameterTypes.size
        val annotations = constructor.declaredAnnotations
        for (annotation in annotations) {
            if (annotation is Auto && paramCount >= maxParamCount) {
                isSingleton = annotation.singleton
                resultConstructor = constructor
                maxParamCount = paramCount
            }
        }
    }

    resultConstructor?.let { resConstructor ->
        val paramTypes = resConstructor.parameterTypes
        for (i in 0 until maxParamCount) {
            val defValue = getDefaultValue(paramTypes[i].name)
            val param = try {
                if (isSingleton) {
                    val checkIfPrimitiveDataType = (defValue != null)
                    if (checkIfPrimitiveDataType) {
                        defValue
                    } else{
                        paramTypes[i].getSingleton()
                    }
                } else{
                    paramTypes[i].createInstanceWithAuto()
                }
            } catch (e: Exception) {
                defValue
            }
            params.add(param)
        }
    }
    if (resultConstructor ==null) {
        return try {
            this.newInstance()
        } catch (e: InvocationTargetException){
            getDefaultValue(this.name)
        }
    }

    return resultConstructor.newInstance(*params.toArray())
}


@MainThread
inline fun <reified VM : ViewModel> Fragment.autoViewModels() = createViewModelLazy(
    VM::class,
    { this.viewModelStore },
    { AutoFactory() }
)
