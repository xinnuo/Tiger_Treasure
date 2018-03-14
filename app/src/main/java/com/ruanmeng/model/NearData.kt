/**
 * created by 小卷毛, 2017/10/27
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
 * 创建时间：2017-10-27 16:35
 */
data class NearData(
        var compLogo: String = "",
        var compName: String = "",
        var compNature: String = "",
        var compNatureName: String = "",
        var compStatus: String = "",
        var companyId: String = "",
        var distance: String = "",
        var gzNum: String = "",
        var llNum: String = "",
        var regAddress: String = "",
        var address: String = "",
        var copmImage: String = "",
        var vipTypeId: String = "",
        var compType: String = "",
        var compTypeName: String = ""
) : Serializable