package com.onourem.android.activity.network

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.onourem.android.activity.ui.utils.listners.CheckNullProcessable
import java.io.IOException

class CheckNullValuesEnabler : TypeAdapterFactory {
      override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
          val delegate = gson.getDelegateAdapter(this, type)

          return object : TypeAdapter<T>() {
              @Throws(IOException::class)
              override fun write(out: JsonWriter, value: T) {
                  delegate.write(out, value)
              }

              @Throws(IOException::class)
              override fun read(`in`: JsonReader): T {
                  val obj = delegate.read(`in`)
                  if (obj is CheckNullProcessable) {
                      (obj as CheckNullProcessable).checkNullValues()
                  }
                  return obj
              }
          }
      }
  }