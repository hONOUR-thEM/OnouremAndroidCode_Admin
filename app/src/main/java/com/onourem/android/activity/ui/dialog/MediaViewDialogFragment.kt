package com.onourem.android.activity.ui.dialog

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDefaultBindingDialogFragment
import com.onourem.android.activity.databinding.DialogMediaViewBinding
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.io.IOException

//https://developer.android.com/codelabs/exoplayer-intro#2
class MediaViewDialogFragment : AbstractBaseDefaultBindingDialogFragment<DialogMediaViewBinding>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    private var player: ExoPlayer? = null


    override fun layoutResource(): Int {
        return R.layout.dialog_media_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (MediaViewDialogFragmentArgs.fromBundle(requireArguments()).mediaType == 1) {
            setupImageView(MediaViewDialogFragmentArgs.fromBundle(requireArguments()).mediaUri)
        } else if (MediaViewDialogFragmentArgs.fromBundle(requireArguments()).mediaType == 2) {
            setupVideoView(MediaViewDialogFragmentArgs.fromBundle(requireArguments()).mediaUri)
        }
        binding.cvClose.setOnClickListener(ViewClickListener { v: View? -> dismiss() })
        binding.ivClose.setOnClickListener(ViewClickListener { v: View? -> dismiss() })
    }

    private fun setupImageView(url: String) {
        try {
            if (url.contains("://")) {
                binding.vvSelectedMedia.visibility = View.GONE
                binding.ivSelectedMedia.visibility = View.VISIBLE
                Glide.with(requireActivity())
                    .load(url)
                    .apply(options)
                    .into(binding.ivSelectedMedia)
            } else {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.parse(url))
                binding.vvSelectedMedia.visibility = View.GONE
                binding.ivSelectedMedia.visibility = View.VISIBLE
                Glide.with(requireActivity())
                    .load(bitmap)
                    .apply(options)
                    .into(binding.ivSelectedMedia)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @OptIn(UnstableApi::class)
    private fun setupVideoView(url: String) {
        if (this.activity is DashboardActivity) {
            (fragmentContext as DashboardActivity).exoPlayerPause(true)
        }

        player = ExoPlayer.Builder(fragmentContext)
            .build()
            .also { exoPlayer ->
                binding.vvSelectedMedia.player = exoPlayer
            }

//        val mediaItem = MediaItem.fromUri(url)
//        player?.setMediaItem(mediaItem)
//        player?.playWhenReady = true
//        player?.play()

        val dataSourceFactory = DefaultDataSource.Factory(fragmentContext)

        val mediaItem: MediaItem = MediaItem.Builder().setUri(url).build()
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)
        player?.playWhenReady = true
        player?.prepare()

        binding.vvSelectedMedia.setShowNextButton(false)
        binding.vvSelectedMedia.setShowPreviousButton(false)

        binding.vvSelectedMedia.visibility = View.VISIBLE
        binding.ivSelectedMedia.visibility = View.GONE
//        val extraHeaders = HashMap<String, String>()
//        binding.vvSelectedMedia.setSource(url, extraHeaders)
    }

    override fun onStop() {
        super.onStop()
        //        binding.vvSelectedMedia.stopPlayer();
    }

    override fun onPause() {
        super.onPause()
        //binding.vvSelectedMedia.pausePlayer();
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (player != null) player?.release()
    }
}