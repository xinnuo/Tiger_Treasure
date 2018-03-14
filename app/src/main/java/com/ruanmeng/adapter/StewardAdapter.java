package com.ruanmeng.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ruanmeng.model.StewardData;
import com.ruanmeng.tiger_treasure.R;

import java.util.List;

/**
 * Created by PYM2017 on 2018/1/16.
 */

public class StewardAdapter extends RecyclerView.Adapter<StewardAdapter.Holder> {
    private List<StewardData> mList;
    private Context mContext;

    public StewardAdapter(Context context, List<StewardData> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_addsteward, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.edName.setTag(position);
        holder.edPhone.setTag(position);
        holder.title.setTag(position);
        holder.edName.setText(mList.get(position).getName());
        holder.edPhone.setText(mList.get(position).getPhone());
        holder.title.setText(mList.get(position).getTitle());

        holder.edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!holder.edName.getText().toString().trim().isEmpty()
//                        && !holder.edPhone.getText().toString().trim().isEmpty()
//                        && !holder.title.getText().toString().trim().isEmpty()) {
//                    fillListener.onFill(true);
//                } else {
//                    fillListener.onFill(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (((int) holder.edName.getTag()) == position)
                    edittextFillListener.onEdittextFill(position, "name", editable.toString().trim());

            }
        });
        holder.edPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!holder.edName.getText().toString().trim().isEmpty()
//                        && !holder.edPhone.getText().toString().trim().isEmpty()
//                        && !holder.title.getText().toString().trim().isEmpty()) {
//                    fillListener.onFill(true);
//                } else {
//                    fillListener.onFill(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (((int) holder.edPhone.getTag()) == position)
                    edittextFillListener.onEdittextFill(position, "phone", editable.toString().trim());

            }
        });

        holder.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!holder.edName.getText().toString().trim().isEmpty()
//                        && !holder.edPhone.getText().toString().trim().isEmpty()
//                        && !holder.title.getText().toString().trim().isEmpty()) {
//                    fillListener.onFill(true);
//                } else {
//                    fillListener.onFill(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (((int) holder.title.getTag()) == position)
                    edittextFillListener.onEdittextFill(position, "title", editable.toString().trim());

            }
        });

        holder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder {
        private EditText edName, edPhone, title;
        private ImageView imgDel;

        public Holder(View itemView) {
            super(itemView);
            edName = itemView.findViewById(R.id.edit_name);
            edPhone = itemView.findViewById(R.id.edit_phone);
            title = itemView.findViewById(R.id.edit_title);
            imgDel = itemView.findViewById(R.id.del_steward);
        }
    }

    private OnEdittextFillListener edittextFillListener;

    public void setEdittextFillListener(OnEdittextFillListener edittextFillListener) {
        this.edittextFillListener = edittextFillListener;
    }

    public interface OnEdittextFillListener {
        void onEdittextFill(int position, String type, String text);
    }


//    private OnFillListener fillListener;
//
//    public void setFillListener(OnFillListener fillListener) {
//        this.fillListener = fillListener;
//    }
//
//    public interface OnFillListener {
//        void onFill(boolean isFill);
//    }

}
