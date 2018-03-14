package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-20 12:52
 */
data class ManagerData(
        var managerId: String = "",
        var status: String = "",
        var userName: String = "",
        var telephone: String = "",
        var userhead: String = "",
        var positionName: String = ""
): Serializable