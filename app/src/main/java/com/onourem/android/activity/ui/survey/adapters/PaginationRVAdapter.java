package com.onourem.android.activity.ui.survey.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onourem.android.activity.R;
import com.onourem.android.activity.ui.survey.BaseViewHolder;
import com.onourem.android.activity.ui.survey.FooterViewHolder;
import com.onourem.android.activity.ui.survey.ProgressViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PaginationRVAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private final T emptyLoadingItem;
    private final T emptyFooterItem;
    protected List<T> items;
    private boolean isLoaderVisible = false;
    private boolean isFooterVisible = false;
    private String footerMessage = "";

    protected PaginationRVAdapter(@NonNull List<T> items) {
        this.items = items;
        emptyLoadingItem = emptyLoadingItem();
        emptyFooterItem = emptyFooterItem();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == items.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else if (isFooterVisible) {
            return position == items.size() - 1 ? VIEW_TYPE_FOOTER : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public List<T> getItems() {
        return items;
    }


    @NotNull
    @Override
    public final BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return onCreateItemViewHolder(parent, viewType);
            case VIEW_TYPE_LOADING:
                return new ProgressViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false), footerMessage);
            default:
                throw new IllegalArgumentException("VIEW TYPE is invalid");
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public final int getItemCount() {
        return items.size();
    }

    public void addItems(List<T> postItems) {
        if (isLoaderVisible) {
            removeLoading();
            notifyDataSetChanged();
        }

        if (isFooterVisible) {
            removeFooter();
            notifyDataSetChanged();
        }

        items.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        items.add(emptyLoadingItem);
        notifyItemInserted(items.size() - 1);
    }

    public void removeLoading() {
        if (!isLoaderVisible) return;

        isLoaderVisible = false;
        int position = items.indexOf(emptyLoadingItem);
        if (position >= 0) {
            items.remove(emptyLoadingItem);
            notifyItemRemoved(position);
        }
    }

    public void addFooter(String footerMessage) {
        this.footerMessage = "";
        this.footerMessage = footerMessage;
        if (isLoaderVisible) {
            removeLoading();
        }

        isFooterVisible = true;
        if (!items.contains(emptyFooterItem)) {
            items.add(emptyFooterItem);
            notifyItemInserted(items.size() - 1);
        } else {
            notifyItemChanged(items.size() - 1);
        }

    }

    public void removeFooter() {
        //if (!isFooterVisible) return;
        isFooterVisible = false;
        int position = items.indexOf(emptyFooterItem);
        if (position >= 0) {
            items.remove(emptyFooterItem);
            notifyItemRemoved(position);
        }
    }


    protected abstract T emptyLoadingItem();

    protected abstract T emptyFooterItem();

    protected abstract BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public void removeItem(T item) {
        if (items != null && !items.isEmpty()) {
            int position = items.indexOf(item);
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public int updateItem(T item) {
        int position = 0;
        if (items != null && !items.isEmpty()) {
            position = items.indexOf(item);
            notifyItemChanged(position);
        }
        return position;
    }

    public void clearData() {
        items.clear();
        notifyDataSetChanged();
    }

    public void resetData(List<T> newData) {
        isLoaderVisible = false;
        isFooterVisible = false;
        items.clear();
        notifyDataSetChanged();
        items.addAll(newData);
        notifyDataSetChanged();
    }

    public boolean hasValidItems() {
        return !(items.contains(emptyLoadingItem) || items.contains(emptyFooterItem));
    }

}
