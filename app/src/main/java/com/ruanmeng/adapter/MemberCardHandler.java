package com.ruanmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazysunj.cardslideview.CardHandler;
import com.ruanmeng.base.GlideApp;
import com.ruanmeng.model.VipConfigsData;
import com.ruanmeng.model.VipData;
import com.ruanmeng.share.BaseHttp;
import com.ruanmeng.tiger_treasure.PayActivity;
import com.ruanmeng.tiger_treasure.R;

import java.util.List;

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-10-30 20:26
 */

public class MemberCardHandler implements CardHandler<VipData> {
    @SuppressLint("SetTextI18n")
    @Override
    public View onBind(final Context context, final VipData data, final int position, int mode) {
        View view = View.inflate(context, R.layout.item_vip_card, null);
        TextView tv_title = view.findViewById(R.id.item_vip_hui);
        LinearLayout li_vip_list = view.findViewById(R.id.li_vip_list);
        TextView tv_range = view.findViewById(R.id.item_vip_range);
        TextView tv_people = view.findViewById(R.id.item_vip_people);
        TextView tv_count = view.findViewById(R.id.item_vip_count);
        TextView tv_num = view.findViewById(R.id.item_vip_num);
        Button bt_open = view.findViewById(R.id.item_vip_open);
        li_vip_list.removeAllViews();
        List<VipConfigsData> items = data.getVipConfigs();
        for (int i = 0; i < items.size(); i++) {
            View v = View.inflate(context, R.layout.item_text, null);
            TextView item_tv = v.findViewById(R.id.item_tv);
            ImageView item_iv = v.findViewById(R.id.item_iv);
            item_tv.setText(items.get(i).getConfigName());
            GlideApp.with(context)
                    .load(BaseHttp.baseImg + items.get(i).getConfigIcon())
                    .placeholder(R.mipmap.default_product) // 等待时的图片
                    .error(R.mipmap.default_product)       // 加载失败的图片
                    .centerCrop()
                    .dontAnimate()
                    .into(item_iv);
            li_vip_list.addView(v);
        }
        tv_title.setText(data.getVipTypeName() + "特权");

        if (data.getVipTypeId().equals("VIP_SLIVER")) {
            Drawable drawable = context.getResources().getDrawable(R.mipmap.vip_center);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_title.setCompoundDrawables(drawable, null, null, null);
        } else if (data.getVipTypeId().equals("VIP_GOLD")) {
            Drawable drawable = context.getResources().getDrawable(R.mipmap.vip_most);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_title.setCompoundDrawables(drawable, null, null, null);
        }
//        tv_range.setText("可查看企业范围：" + items[0]);
//        tv_people.setText("查看浏览过我的人：" + items[1] + "个");
//        tv_count.setText("可上传产品数量：" + items[2] + "个");
//        tv_num.setText("发布供货采购合作：" + items[3] + "条");

        bt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<VipPriceData> childs = (ArrayList<VipPriceData>) data.getVipPrices();
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("title", data.getVipTypeName());
                intent.putExtra("vipIsPay", data.getVipIsPay());
                intent.putExtra("vipTypeId", data.getVipTypeId());
                context.startActivity(intent);
            }
        });


        //买过的显示续费会员特权 vipIsPay=2的时候按钮变位可点击，文字改为续费
        if (data.getVipIsPay().equals("2")) {
            bt_open.setBackgroundResource(R.mipmap.icon_vip_bt);
            bt_open.setClickable(true);
            bt_open.setText("立即续费");
        } else {
            bt_open.setText("开通会员");
            if (TextUtils.equals("1", data.getVipIsPay())) {
                bt_open.setBackgroundResource(R.drawable.rec_bg_ova_lighter);
                bt_open.setClickable(false);
            } else {
                bt_open.setBackgroundResource(R.mipmap.icon_vip_bt);
                bt_open.setClickable(true);
            }
        }

        return view;
    }
}
