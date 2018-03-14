/**
 * created by 小卷毛, 2017/11/20
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.ruanmeng.model

import java.io.Serializable
import java.util.*

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-20 12:53
 */
data class VipModel(
        var total: String = "",
        var page: String = "",
        var pageCount: String = "",
        var pages: String = "",
        var compName: String = "",
        var vipTypeName: String = "",
        var vipTypeId: String = "",
        var expiryDate: String = "",
        var rows: List<VipData>? = ArrayList(),
        var vipConfigs: List<VipConfigs>? = ArrayList(),
        var vipPrices: List<VipPriceData>? = ArrayList(),
        var params:VipPriceParams
) : Serializable