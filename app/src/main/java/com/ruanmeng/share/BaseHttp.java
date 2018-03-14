package com.ruanmeng.share;

import com.ruanmeng.tiger_treasure.BuildConfig;

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-09 17:20
 */
public class BaseHttp {
    public static String baseUrl = BuildConfig.API_HOST;

    public static String baseIp = baseUrl + "/api";

    public static String baseImg = baseUrl + "/";

    public static String get_smscode = baseIp + "/get_smscode.rm";           //获取验证码√
    public static String account_reglogin = baseIp + "/account_reglogin.rm"; //登录(验证码方式)√
    public static String account_login = baseIp + "/account_login.rm"; //(登录密码方式)√
    public static String company_cheeck = baseIp + "/company_cheeck.rm";     //检测企业是否注册√
    public static String mobile_update = baseIp + "/mobile_update.rm";       //修改绑定手机号√
    public static String company_reg = baseIp + "/company_reg.rm";           //企业注册√
    public static String manager_apply = baseIp + "/manager_apply.rm";       //企业管家申请√
    public static String area_list = baseIp + "/area_list.rm";               //根据城市或代码获取区域√
    public static String pro_list = baseIp + "/pro_list.rm";               //获取省份√
    public static String html = baseIp + "/html.rm";                         //服务协议相关√

    public static String index = baseIp + "/index.rm";                         //首页√
    public static String city_list = baseIp + "/city_list.rm";                 //获取城市√
    public static String financial_list = baseIp + "/financial_list.rm";       //金融圈√
    public static String financial_details = baseIp + "/financial_details.rm"; //金融圈详细√
    public static String company_details = baseIp + "/company_details.rm";     //企业详情√
    public static String compnature_list = baseIp + "/comptype_list.rm";     //企业类型√
    public static String industry_list = baseIp + "/industry_list.rm";         //行业列表√
    public static String company_list = baseIp + "/company_list.rm";           //企业列表√
    public static String message_list = baseIp + "/message_list.rm";           //消息公告列表√
    public static String message_details = baseIp + "/message_details.rm";     //消息详细√
    public static String message_delete = baseIp + "/message_del.rm";             //删除公告√
    public static String viptype_list = baseIp + "/viptype_list.rm";           //会员特权√

    public static String slider_list = baseIp + "/slider_list.rm";               //轮播图√
    public static String supply_list = baseIp + "/supply_list.rm";               //供货列表√
    public static String purchasing_list = baseIp + "/purchasing_list.rm";       //采购列表√
    public static String cooperate_list = baseIp + "/cooperate_list.rm";         //合作列表√
    public static String supply_details = baseIp + "/supply_details.rm";         //供货详情√
    public static String purchasing_details = baseIp + "/purchasing_details.rm"; //采购详情√
    public static String cooperate_details = baseIp + "/cooperate_details.rm";   //合作详情√
    public static String supply_add = baseIp + "/supply_add.rm";                 //发布供货信息√
    public static String purchasing_add = baseIp + "/purchasing_add.rm";         //发布采购信息√
    public static String cooperate_add = baseIp + "/cooperate_add.rm";           //发布合作信息√
    public static String purchasing_del = baseIp + "/purchasing_del.rm";         //发布采购删除√
    public static String cooperate_del = baseIp + "/cooperate_del.rm";           //发布合作删除√
    public static String supply_company = baseIp + "/supply_company.rm";         //企业产品列表√

    public static String collecon_sc_sub = baseIp + "/collecon_sc_sub.rm";                     //(添加/取消)收藏√
    public static String collecon_gz_sub = baseIp + "/collecon_gz_sub.rm";                     //(添加/取消)关注√
    public static String enterpriser2_list = baseIp + "/enterpriser2_list.rm";                 //通讯录√
    public static String government_list = baseIp + "/gov_list.rm";                             //政府号√
    public static String release_government_list = baseIp + "/message_add.rm";                   //发布公告√
    public static String chatroom_list = baseIp + "/chatroom_list.rm";                         //聊天室列表√
    public static String chatroom_mine = baseIp + "/chatroom_mine.rm";                         //我的聊天室√
    public static String chatroom_users = baseIp + "/chatroom_users.rm";                       //查询用户信息√
    public static String circle_list = baseIp + "/circle_list.rm";                             //企业圈列表√
    public static String circle_thumb = baseIp + "/circle_thumb.rm";                           //点赞√
    public static String circle_reply = baseIp + "/circle_reply.rm";                           //回复√
    public static String circle_add = baseIp + "/circle_add.rm";                               //发布√

    public static String other_accountinfo_details = baseIp + "/other_accountinfo_details.rm"; //查看其他人资料√
    public static String findinvoice_by_company = baseIp + "/findinvoice_by_company.rm";       //根据企业查询发票信息√
    public static String invoice_add = baseIp + "/invoice_add.rm";                             //添加/修改发票信息√
    public static String pay_sub = baseIp + "/pay_sub.rm";                                     //购买会员√
    public static String vip_pay = baseIp + "/vip_pay.rm";                                          // 支付界面获取数据接口
    public static String company_manager_list = baseIp + "/company_manager_list.rm";           //企业管家列表√
    public static String index_search = baseIp + "/index_search.rm";                           //首页搜索√

    public static String accountinfo_details = baseIp + "/accountinfo_details.rm"; //个人中心√
    public static String accountinfo_update = baseIp + "/accountinfo_update.rm";   //修改个人资料√
    public static String company_mine = baseIp + "/company_mine.rm";               //我的企业√
    public static String company_update = baseIp + "/company_update.rm";           //企业资料更新√
    public static String company_collect = baseIp + "/collecon_sc_companys.rm";    //我的收藏--企业√
    public static String products_collect = baseIp + "/collecon_sc_products.rm";   //我的收藏--产品√
    public static String myattention = baseIp + "/collecon_gz_accounts.rm";        //我的关注-用户√
    public static String attentionme = baseIp + "/collecon_gz_mine.rm";            //关注我的用户√
    public static String managerlist = baseIp + "/manager_list.rm";                //企业管家列表√
    public static String managerdel = baseIp + "/manager_del.rm";                  //删除企业管家√
    public static String manageraccept = baseIp + "/manager_accept.rm";            //企业管家审核√
    public static String productmine = baseIp + "/supply_mine.rm";                 //我的产品列表√
    public static String productdel = baseIp + "/supply_del.rm";                   //供货(产品)删除√
    public static String scanlist = baseIp + "/footprint_mine.rm";                 //浏览过我的人员列表√
    public static String stepcompany = baseIp + "/footprint_company_list.rm";      //我的足迹-企业√
    public static String stepaccount = baseIp + "/footprint_account_list.rm";      //我的足迹-用户√
    public static String allinteresting = baseIp + "/chatroom_list.rm";      //聊天室列表√
    public static String saveinteresting = baseIp + "/chatroom_select.rm";      //选择聊天室√
    public static String feedback = baseIp + "/feedback_sub.rm";      //系统反馈√
    public static String Changepsw = baseIp + "/update_pwd2.rm";      //修改密码(旧密码方式)
    public static String Forgetpsw = baseIp + "/update_pwd.rm";      //找回密码(验证码方式)
    public static String vip_info = baseIp + "/vip_info.rm";      //
    public static String company_info = baseIp + "/company_info.rm";      //拉取企业信息√
    public static String area_mine_list = baseIp + "/area_mine_list.rm";      //我的区域范围√ √
    public static String supply_search = baseIp + "/supply_search.rm";     //供货采购搜索√
    public static String version = baseIp + "/version.rm";     //版本更新√
}
