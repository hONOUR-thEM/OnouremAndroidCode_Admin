package com.onourem.android.activity.arch.helper

class Resource<T>(val status: Status, val data: T?, val message: String?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val resource = other as Resource<*>
        if (status != resource.status) {
            return false
        }
        return if (message != resource.message) {
            false
        } else data == resource.data
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }

    companion object {
        fun <T> success(data: T?): Resource<T?> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String?, data: T?): Resource<T?> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T?> {
            return Resource(Status.LOADING, data, null)
        }
    }
}