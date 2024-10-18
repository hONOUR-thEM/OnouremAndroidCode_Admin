package com.onourem.android.activity.ui.utils.listners;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MessagePaginationListener extends RecyclerView.OnScrollListener {

    /**
     * Set scrolling threshold here (for now i'm
     * assuming 10 item in one page)
     */
    public static final int PAGE_ITEM_SIZE = 20;
    @NonNull
    private final LinearLayoutManager layoutManager;

    /**
     * Supporting only LinearLayoutManager for now.
     */
    public MessagePaginationListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_ITEM_SIZE) {
                loadMoreItems();
            }
        }


    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}