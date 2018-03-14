package com.ruanmeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.ruanmeng.base.GlideApp;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.share.BaseHttp;
import com.ruanmeng.tiger_treasure.EnterpriseDetailActivity;
import com.ruanmeng.tiger_treasure.R;

import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private Context mContext;
    private List<CommonData> mList;

    public HorizontalAdapter(Context context, List<CommonData> mList) {
        mContext = context;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_first_horizontal, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CommonData data = mList.get(position);
        holder.name.setText(data.getCompName());

        GlideApp.with(mContext)
                .load(BaseHttp.baseImg + data.getCompLogo())
                .placeholder(R.mipmap.default_product) //等待时的图片
                .error(R.mipmap.default_product)       //加载失败的图片
                .dontAnimate()
                .into(holder.iv_img);

        holder.divider.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.GONE);

        holder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EnterpriseDetailActivity.class);
                intent.putExtra("companyId", data.getCompanyId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RoundedImageView iv_img;
        private View divider;

        ViewHolder(final View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.item_horizontal_name);
            this.iv_img = itemView.findViewById(R.id.item_horizontal_img);
            this.divider = itemView.findViewById(R.id.item_horizontal_divider);
        }
    }
}