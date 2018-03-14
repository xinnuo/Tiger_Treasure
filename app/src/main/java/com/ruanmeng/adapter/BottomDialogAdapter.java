package com.ruanmeng.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.ruanmeng.tiger_treasure.R;
import com.ruanmeng.utils.DialogHelper;

import java.util.List;

/**
 * Created by PYM2017 on 2018/1/15.
 */

public class BottomDialogAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mList;
    private DialogHelper.ItemCallBack callBack;
    private BottomBaseDialog dialog;

    public BottomDialogAdapter(Context context, List<String> list, DialogHelper.ItemCallBack callBack, BottomBaseDialog dialog) {
        mContext = context;
        mList = list;
        this.callBack = callBack;
        this.dialog = dialog;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_bottom_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Holder holder1 = (Holder) holder;
        holder1.tvName.setText(mList.get(position));
        holder1.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.doWork(position, mList.get(position));
                dialog.dismiss();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public Holder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
