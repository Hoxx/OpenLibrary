package com.open.monitor.core.thread

/**
 * xingxiu.hou
 * 2021/5/30
 */
class IThread {

    internal var threadNum: Int = 0
    internal var threadNames: List<String> = mutableListOf()

    fun result(): String {
        val result = StringBuilder()
        if (threadNum > 0) {
            result.append("线程总数:$threadNum")
        }
        return result.toString()
    }

}