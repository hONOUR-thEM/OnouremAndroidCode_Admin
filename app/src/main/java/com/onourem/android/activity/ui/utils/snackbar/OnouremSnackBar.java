package com.onourem.android.activity.ui.utils.snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.onourem.android.activity.R;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;

public class OnouremSnackBar extends BaseTransientBottomBar<OnouremSnackBar> {

    private static OnouremSnackBar customSnackbar;
    private Callback callback;

    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent   The parent for this transient bottom bar.
     * @param content  The content view for this transient bottom bar.
     * @param callback The content view callback for this transient bottom bar.
     */


    private OnouremSnackBar(ViewGroup parent, View content, ContentViewCallback callback) {
        super(parent, content, callback);
    }

    public static OnouremSnackBar make(@NonNull ViewGroup parent, @Duration int duration) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View content = inflater.inflate(R.layout.snackbar_view, parent, false);
        final ContentViewCallback viewCallback = new ContentViewCallback(content);
        customSnackbar = new OnouremSnackBar(parent, content, viewCallback);

        customSnackbar.getView().setPadding(0, 0, 0, 0);
        customSnackbar.getView().setBackgroundResource(R.color.transparent);
        customSnackbar.setDuration(duration);
        return customSnackbar;
    }

    public OnouremSnackBar setText(CharSequence text) {
        MaterialTextView textView = getView().findViewById(R.id.snackbar_text);
        textView.setText(text);
        return this;
    }

    public OnouremSnackBar setCloseAction(final View.OnClickListener listener) {
        MaterialCardView actionView = getView().findViewById(R.id.cvClose);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(new ViewClickListener(view -> {
            listener.onClick(view);
            // Now dismiss the Snackbar
            dismiss();
        }));
        return this;
    }

    public OnouremSnackBar setAction(CharSequence text, final View.OnClickListener listener, int visibility) {
        MaterialButton actionView = getView().findViewById(R.id.snackbar_action);
        actionView.setText(text);
        actionView.setVisibility(visibility);
        actionView.setOnClickListener(new ViewClickListener(view -> {
            listener.onClick(view);
            // Now dismiss the Snackbar
            dismiss();
        }));
        return this;
    }

    public OnouremSnackBar customAddCallback(Callback callback) {

        Callback callback1 = new Callback();

        return this;
    }

    private static class ContentViewCallback implements BaseTransientBottomBar.ContentViewCallback {

        private final View content;

        public ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            ViewCompat.setScaleY(content, 0f);
            ViewCompat.animate(content).scaleY(1f).setDuration(duration).setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            ViewCompat.setScaleY(content, 1f);
            ViewCompat.animate(content).scaleY(0f).setDuration(duration).setStartDelay(delay);
        }
    }

    /**
     * Callback class for {@link Snackbar} instances.
     *
     * <p>Note: this class is here to provide backwards-compatible way for apps written before the
     * existence of the base {@link BaseTransientBottomBar} class.
     *
     * @see BaseTransientBottomBar#addCallback(BaseCallback)
     */
    public static class Callback extends BaseCallback<Snackbar> {
        /**
         * Indicates that the Snackbar was dismissed via a swipe.
         */
        public static final int DISMISS_EVENT_SWIPE = BaseCallback.DISMISS_EVENT_SWIPE;
        /**
         * Indicates that the Snackbar was dismissed via an action click.
         */
        public static final int DISMISS_EVENT_ACTION = BaseCallback.DISMISS_EVENT_ACTION;
        /**
         * Indicates that the Snackbar was dismissed via a timeout.
         */
        public static final int DISMISS_EVENT_TIMEOUT = BaseCallback.DISMISS_EVENT_TIMEOUT;
        /**
         * Indicates that the Snackbar was dismissed via a call to {@link #dismiss()}.
         */
        public static final int DISMISS_EVENT_MANUAL = BaseCallback.DISMISS_EVENT_MANUAL;
        /**
         * Indicates that the Snackbar was dismissed from a new Snackbar being shown.
         */
        public static final int DISMISS_EVENT_CONSECUTIVE = BaseCallback.DISMISS_EVENT_CONSECUTIVE;

        @Override
        public void onShown(Snackbar sb) {
            // Stub implementation to make API check happy.
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, @DismissEvent int event) {
            // Stub implementation to make API check happy.x
        }
    }
}