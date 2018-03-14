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
data class CompanyData(
        var companyId: String = "",
        var compName: String = "",
        var compId: String = "",
        var compLogo: String = "",
        var compLat: String = "",
        var compLng: String = "",
        var compCode: String = "",
        var compEmail: String = "",
        var compNature: String = "",
        var compStatus: String = "",
        var compNatureName: String = "",
        var compTypeName: String = "",
        var compTel: String = "",
        var compType: String = "",
        var compUrl: String = "",
        var copmLic: String = "",
        var legalMan: String = "",
        var compUserId: String = "",
        var copmProfile: String = "",
        var areaId: String = "",
        var areaName: String = "",
        var industryId: String = "",
        var industryName: String = "",
        var address: String = "",
        var regAddress: String = "",
        var linkMan: String = "",
        var regCap: String = "",
        var registerDate: String = "",
        var staffNum: String = "",
        var staffNumName: String = "",
        var remark: String = "",
        var copmImage: String = "",
        var distance: String = "",
        var compService: String = "",
        var collecon: String = "",
        var invoiceTitle: String = "",
        var scope: String = "",

        var gzNum: String = "",
        var llNum: String = "",

        var invoiceId: String = "",
        var bankName: String = "",
        var bankNo: String = "",
        var addressInfo: String = "",
        var telephone: String = "",
        var addressee: String = "",
        var applyUpdate: String = "",
        var vipTypeId: String = "",
        var isGov: String = "",
        var isHot: String = "",

        var productList: List<ProductData>? = ArrayList()
) : Serializable