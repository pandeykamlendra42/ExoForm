package com.form.android.adaptors;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.model.Option;

import java.util.List;

/**
 * @OptionAdapter used for managing option menu of the multiOptions.
 *
 */


public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ItemHolder> {

    private Context mContext;
    private List<Option> list;
    private OnItemClickListener onItemClickListener;
    private String mDefaultValue;

    public OptionAdapter(Context context, List<Option> list) {
        this.mContext = context;
        this.list = list;
    }

    /**
     * Getter for on item click listener
     *
     * @return
     */
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * Setter for on item click listener
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * Setting default value for selected option.
     *
     * @param defaultValue
     */
    public void setDefaultKey(String defaultValue) {
        mDefaultValue = defaultValue;
    }

    @Override
    public OptionAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_menu_item, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final OptionAdapter.ItemHolder holder, int position) {
        final Option item = list.get(position);
        if (item.getName() != null) {
            holder.tvMenuTitle.setText(item.getName());
        }

        // Marking the selected option with primary color
        if (mContext != null && mDefaultValue != null
                && mDefaultValue.equals(item.getKey())) {
            holder.tvMenuTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            holder.tvMenuTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final OnItemClickListener listener = holder.adapter.getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(item, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * @OnItemClickListener interface for handling on option change event.
     */
    public interface OnItemClickListener {
        void onItemClick(Option value, int position);
    }

    /**
     * @ItemHolder for handling adaptor views
     */
    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvMenuTitle, tvMenuIcon;
        View mainView, viewContainer;
        private OptionAdapter adapter;

        public ItemHolder(View itemView, OptionAdapter parent) {
            super(itemView);
            mainView = itemView;
            this.adapter = parent;
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvMenuTitle.setGravity(Gravity.CENTER_VERTICAL);
            tvMenuIcon = itemView.findViewById(R.id.tvMenuIcon);
            viewContainer = itemView.findViewById(R.id.view_container);

        }
    }
}
