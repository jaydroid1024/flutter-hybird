package com.jay.flutter_hybrid_android.utils

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.Reader
import java.lang.reflect.Type

object GsonUtils {

    /**
     * json字符串回数组 example:Type listType = new
     * TypeToken<List></List><Order>>(){}.getType();将listType传到方法里
    </Order> */
    fun <T> fromJson(strjson: String?, tp: Type?): List<T>? {
        return try {
            val gs = Gson()
            // Type listType = new TypeToken<List<T>>(){}.getType();
            gs.fromJson(strjson, tp)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * json字符串转map
     * val type = object : TypeToken<HashMap></HashMap><String></String>, String>>() {}.type
     * 将 type 传到方法里
     */
    fun <K, V> fromJsonToMap(strJson: String?, tp: Type?): Map<K, V>? {
        return try {
            val gs = Gson()
            gs.fromJson(strJson, tp)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * json字符串回数组 example:Type listType = new
     * TypeToken<List></List><Order>>(){}.getType();将listType传到方法里
    </Order> */
    fun <T> fromJson(reader: Reader?, tp: Type?): List<T>? {
        return try {
            val gs = Gson()
            // Type listType = new TypeToken<List<T>>(){}.getType();
            gs.fromJson(reader, tp)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * json字符串返回对象
     *
     * @param strjson json字符串
     * @param cls     对象类型
     * @param <T>     泛型对象
     * @return 对象
    </T> */
    fun <T> fromJson(strjson: String?, cls: Class<T>?): T? {
        return try {
            val gs = Gson()
            // Type listType = new TypeToken<List<T>>(){}.getType();
            gs.fromJson(strjson, cls)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * json字符串返回对象
     *
     * @param reader 数据流
     * @param cls    对象类型
     * @param <T>    泛型对象
     * @return 对象
    </T> */
    fun <T> fromJson(reader: Reader?, cls: Class<T>?): T? {
        return try {
            val gs = Gson()
            // Type listType = new TypeToken<List<T>>(){}.getType();
            gs.fromJson(reader, cls)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * json字符串返回对象
     *
     * @param reader 数据流
     * @param cls    对象类型
     * @param <T>    泛型对象
     * @return 对象
    </T> */
    fun <T> fromJson(reader: JsonReader?, cls: Class<T>?): T? {
        return try {
            val gs = Gson()
            // Type listType = new TypeToken<List<T>>(){}.getType();
            gs.fromJson(reader, cls)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /***
     * 将对象转换成json字符串
     *
     * @param object 需要转换的对象
     * @return json字符串
     */
    fun toJson(`object`: Any?): String {
        return Gson().toJson(`object`)
    }
}