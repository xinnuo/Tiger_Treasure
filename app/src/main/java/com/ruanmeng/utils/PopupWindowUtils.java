package com.ruanmeng.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ruanmeng.model.CityData;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.model.KeyValueData;
import com.ruanmeng.model.ScreenData;
import com.ruanmeng.tiger_treasure.R;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-24 20:39
 */

public class PopupWindowUtils {

    public static void showDistrictPopWindow(
            final Context context,
            View anchor,
            int selected,
            final List<CityData> items,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_discrit, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = view.findViewById(R.id.rg_pop_near_left);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        for (CityData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getAreaName());
            rb.setId(items.indexOf(item));
            if (items.indexOf(item) == selected) rb.setChecked(true);
            rg.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            param.setMargins(DensityUtil.dp2px(10f), 0, DensityUtil.dp2px(10f), 0);
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg.addView(v);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                callBack.doWork(checkedId, items.get(checkedId).getAreaName());
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showDistrictAllPopWindow(
            final Context context,
            View anchor,
            int selected,
            final List<CityData> items,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_discrit, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = view.findViewById(R.id.rg_pop_near_left);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        divider.setVisibility(View.GONE);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        for (CityData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getAreaName());
            rb.setId(items.indexOf(item));
            if (items.indexOf(item) == selected) rb.setChecked(true);
            rg.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            param.setMargins(DensityUtil.dp2px(10f), 0, DensityUtil.dp2px(10f), 0);
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg.addView(v);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                callBack.doWork(checkedId, items.get(checkedId).getAreaName());
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showSinglePopWindow(
            final Context context,
            View anchor,
            int selected,
            final List<CityData> items,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_discrit, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = view.findViewById(R.id.rg_pop_near_left);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        divider.setVisibility(View.GONE);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        for (CityData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getAreaName());
            rb.setId(items.indexOf(item));
            if (items.indexOf(item) == selected) rb.setChecked(true);
            rg.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            param.setMargins(DensityUtil.dp2px(10f), 0, DensityUtil.dp2px(10f), 0);
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg.addView(v);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                callBack.doWork(checkedId, items.get(checkedId).getAreaName());
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showTypePopWindow(
            final Context context,
            View anchor,
            final List<CommonData> items,
            final String id_first,
            final String id_second,
            final PopupWindowTypeCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_enterprise_type, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        View divider = view.findViewById(R.id.v_pop_divider);
        final RadioGroup rg_left = view.findViewById(R.id.rg_pop_left);
        final RadioGroup rg_right = view.findViewById(R.id.rg_pop_right);
        Button bt_ok = view.findViewById(R.id.popu_sure);

        rg_left.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rg_right.removeAllViews();
                rg_right.clearCheck();

                List<CommonData> item_second = items.get(checkedId).getChilds();
                if (item_second == null || item_second.size() == 0) return;

                for (CommonData item : item_second) {
                    RadioButton rb = new RadioButton(context);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                    rb.setLayoutParams(params);
                    rb.setTextAppearance(context, R.style.Font14_selector);
                    rb.setGravity(Gravity.CENTER);
                    rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rb.setText(item.getIndustryName());
                    rb.setId(item_second.indexOf(item));
                    if (item.getIndustryId().equals(id_second)) rb.setChecked(true);
                    rg_right.addView(rb);

                    RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
                    View v = new View(context);
                    v.setLayoutParams(param);
                    v.setBackgroundResource(R.color.colorControlNormal);
                    rg_right.addView(v);
                }
            }
        });

        for (CommonData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getIndustryName());
            rb.setId(items.indexOf(item));
            if (item.getIndustryId().equals(id_first)) rb.setChecked(true);
            rg_left.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg_left.addView(v);
        }

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                List<CommonData> childs = items.get(rg_left.getCheckedRadioButtonId()).getChilds();
                if (childs != null && childs.size() > 0) {

                    if (rg_right.getCheckedRadioButtonId() >= 0) {
                        callBack.doWork(items.get(rg_left.getCheckedRadioButtonId()).getIndustryId(),
                                childs.get(rg_right.getCheckedRadioButtonId()).getIndustryId(),
                                childs.get(rg_right.getCheckedRadioButtonId()).getIndustryName());
                    } else {
                        callBack.doWork(items.get(rg_left.getCheckedRadioButtonId()).getIndustryId(),
                                "",
                                items.get(rg_left.getCheckedRadioButtonId()).getIndustryName());
                    }
                } else {
                    callBack.doWork(items.get(rg_left.getCheckedRadioButtonId()).getIndustryId(),
                            "",
                            items.get(rg_left.getCheckedRadioButtonId()).getIndustryName());
                }
            }
        });

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showAreaPopWindow(
            final Context context,
            View anchor,
            final List<CityData> items,
            final String id_first,
            final String id_second,
            final PopupWindowTypeCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_enterprise_type, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        View divider = view.findViewById(R.id.v_pop_divider);
        final RadioGroup rg_left = view.findViewById(R.id.rg_pop_left);
        final RadioGroup rg_right = view.findViewById(R.id.rg_pop_right);
        Button bt_ok = view.findViewById(R.id.popu_sure);

        rg_left.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rg_right.removeAllViews();
                rg_right.clearCheck();

                List<CityData> item_second = items.get(checkedId).getChilds();
                if (item_second == null || item_second.size() == 0) return;

                for (CityData item : item_second) {
                    RadioButton rb = new RadioButton(context);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                    rb.setLayoutParams(params);
                    rb.setTextAppearance(context, R.style.Font14_selector);
                    rb.setGravity(Gravity.CENTER);
                    rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rb.setText(item.getAreaName());
                    rb.setId(item_second.indexOf(item));
                    if (item.getAreaId().equals(id_second)) rb.setChecked(true);
                    rg_right.addView(rb);

                    RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
                    View v = new View(context);
                    v.setLayoutParams(param);
                    v.setBackgroundResource(R.color.colorControlNormal);
                    rg_right.addView(v);
                }
            }
        });

        for (CityData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getAreaName());
            rb.setId(items.indexOf(item));
            if (item.getAreaId().equals(id_first)) rb.setChecked(true);
            rg_left.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg_left.addView(v);
        }

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                List<CityData> childs = items.get(rg_left.getCheckedRadioButtonId()).getChilds();
                if (childs != null && childs.size() > 0) {

                    if (rg_right.getCheckedRadioButtonId() >= 0) {
                        callBack.doWork(items.get(rg_left.getCheckedRadioButtonId()).getAreaId(),
                                childs.get(rg_right.getCheckedRadioButtonId()).getAreaId(),
                                childs.get(rg_right.getCheckedRadioButtonId()).getAreaName());
                    } else {
                        callBack.doWork(items.get(rg_left.getCheckedRadioButtonId()).getAreaId(),
                                "",
                                items.get(rg_left.getCheckedRadioButtonId()).getAreaName());
                    }
                } else {
                    callBack.doWork(items.get(rg_left.getCheckedRadioButtonId()).getAreaId(),
                            "",
                            items.get(rg_left.getCheckedRadioButtonId()).getAreaName());
                }
            }
        });

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }


    /**
     * 高级筛选弹窗
     *
     * @param context
     * @param anchor
     * @param callBack
     */
    public static void showFilterPopWindow(
            final Context context,
            View anchor,
            final List<ScreenData> list,
            final PopupWindowFilterCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_filter, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        TextView tv_done = view.findViewById(R.id.pop_filter_done);
        TextView tv_reset = view.findViewById(R.id.pop_filter_reset);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        final RecyclerView recyclerView = view.findViewById(R.id.recycle);

        final Map<String, KeyValueData> dataMap = new HashMap<>();
        final SlimAdapter adapter = SlimAdapter.create()
                .register(R.layout.item_screening, new SlimInjector<ScreenData>() {

                    @Override
                    public void onInject(final ScreenData data, final IViewInjector injector) {
                        injector.text(R.id.text_name, data.getName());
                        injector.text(R.id.text_choose, data.getSelected());
                        final RadioGroup group = (RadioGroup) injector.findViewById(R.id.screen_group);
                        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                if (radioGroup.findViewById(i) != null) {
                                    KeyValueData keyValueData = (KeyValueData) radioGroup.findViewById(i).getTag();
                                    injector.text(R.id.text_choose, keyValueData.getName());
                                    dataMap.put(data.getName(), keyValueData);
                                }

                            }
                        });

                        for (KeyValueData value : data.getValue()) {
                            RadioButton radioButton = (RadioButton) LayoutInflater.from(context).inflate(R.layout.screen_radiobutton, group, false);
                            group.addView(radioButton);
                            radioButton.setText(value.getName());
                            radioButton.setTag(value);
                            if (data.getSelected().equals(value.getName()))
                                radioButton.performClick();
                        }
                        injector.clicked(R.id.re_title, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                injector.visibility(R.id.screen_group, group.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                            }
                        });

                    }
                });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapter.updateData(list).notifyDataSetChanged();

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置数据及样式
                recyclerView.removeAllViews();
                for (ScreenData screenData : list) {
                    screenData.setSelected("");
                }
                adapter.updateData(list).notifyDataSetChanged();
                dataMap.clear();
                callBack.reset();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
//                for (String str : dataMap.keySet()) {
//                    Log.e("筛选", str + "," + dataMap.get(str));
//                }
                callBack.doWork(dataMap);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public interface PopupWindowCallBack {
        void doWork(int position, String name);

        void onDismiss();
    }

    public interface PopupWindowTypeCallBack {
        void doWork(String id_left, String id_right, String name);

        void onDismiss();
    }

    public interface PopupWindowFilterCallBack {
        void doWork(Map<String, KeyValueData> map);

        void reset();

        void onDismiss();
    }
}
