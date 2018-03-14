/**
 * created by 小卷毛, 2017/10/26
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
 * 创建时间：2017-10-26 17:16
 */
data class CooperateData(
        var companyId: String = "",
        var address: String = "",
        var compLogo: String = "",
        var compName: String = "",
        var expiryDate: String = "",
        var retailPrice: String = "",
        var tradePrice: String = "",
        var supplyDescribe: String = "",
        var supplyId: String = "",
        var supplyNum: String = "",
        var supplyPic: String = "",
        var supplyTitle: String = "",

        var purchasingDescribe: String = "",
        var purchasingId: String = "",
        var purchasingNum: String = "",

        var cooperateCondition: String = "",
        var cooperateId: String = "",
        var cooperateTitle: String = "",

        var productId: String = "",
        var createDate: String = "",
        var productDescribe: String = "",
        var productName: String = "",
        var productPic: String = "",
        var productPrice: String = ""
) : Serializable