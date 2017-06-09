package com.dpt.itnews.api.converter

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by dupengtao on 17/6/7.
 */
class QualifiedTypeConverterFactory(val jsonFactory: Converter.Factory, val xmlFactory: Converter.Factory) : Converter.Factory() {

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Json

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Xml


    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *> {

        if (annotations != null) {
            for (a in annotations) {
                if (a is Json) {
                    return jsonFactory.responseBodyConverter(type, annotations, retrofit)
                }

                if (a is Xml) {
                    return xmlFactory.responseBodyConverter(type, annotations, retrofit)
                }
            }

        }

        return super.responseBodyConverter(type, annotations, retrofit)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody> {

        for (a in parameterAnnotations!!) {
            if (a is Json) {
                return jsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
            }
            if (a is Xml) {
                return xmlFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
            }
        }

        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }
}