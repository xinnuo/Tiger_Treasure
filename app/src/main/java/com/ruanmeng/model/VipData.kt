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

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-20 12:52
 */
data class VipData(
        var vipIsPay: String = "",
        var vipTypeId: String = "",
        var vipTypeCode: String = "",
        var vipTypeName: String = "",
        var vipPrices: List<VipPriceData> ?= ArrayList(),
        var vipConfigs: List<VipConfigsData> ?= ArrayList(),
        var vipTypeStandard: String = "",
        var vipTip: String = "",
        var configCode: String = "",
        var configIcon: String = "",
        var configName: String = "",
        var configVal: String = "",
        var vipConfigId: String = "",
        var expiryDay: String = "",
        var price: String = "",
        var priceUnit: String = "",
        var vippriceId: String = ""
): Serializable