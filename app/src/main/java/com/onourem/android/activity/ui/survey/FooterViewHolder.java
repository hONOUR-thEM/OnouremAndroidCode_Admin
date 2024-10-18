package com.onourem.android.activity.ui.survey;

import android.view.View;

import com.onourem.android.activity.databinding.ItemFooterBinding;

public class FooterViewHolder extends BaseViewHolder {

    private final ItemFooterBinding binding;
    private String footerMessage = "";

    public FooterViewHolder(View view, String footerMessage) {
        super(view);
        this.footerMessage = footerMessage;
        binding = ItemFooterBinding.bind(view);
    }

    @Override
    public void onBind(int position) {
        // empty
        binding.tvFooterMessage.setText(footerMessage);
    }
}
