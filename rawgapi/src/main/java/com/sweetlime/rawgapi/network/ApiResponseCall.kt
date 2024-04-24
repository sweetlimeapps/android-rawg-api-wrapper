/*
 * Copyright 2024 Sweet Lime Apps
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.sweetlime.rawgapi.network

import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.io.IOException

abstract class CallDelegate<TIn, TOut>(protected val proxy: Call<TIn>) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    final override fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

class ApiResponseCall<T>(proxy: Call<T>) : CallDelegate<T, ApiResponse<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<ApiResponse<T>>) = proxy.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()
                ApiResponse.Success(body)
            } else {
                ApiResponse.ApiError(code)
            }

            callback.onResponse(this@ApiResponseCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = if (t is IOException) {
                ApiResponse.NetworkError(t)
            } else {
                ApiResponse.ApiError(null)
            }

            callback.onResponse(this@ApiResponseCall, Response.success(result))
        }
    })

    override fun cloneImpl() = ApiResponseCall(proxy.clone())

    override fun timeout(): Timeout = proxy.timeout()
}

