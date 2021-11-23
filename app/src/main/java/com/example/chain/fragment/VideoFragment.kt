package com.example.chain.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chain.R
import com.example.chain.databinding.FragmentVideoBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.goal_config_dialog.view.*
import kotlinx.android.synthetic.main.video_datepicker_dialog.*
import kotlinx.android.synthetic.main.video_datepicker_dialog.view.*

class VideoFragment : Fragment(){

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        val view = binding.root

        if (Util.SDK_INT >= 24) {
            initializePlayer(1)
            initializePlayer(2)
            initializePlayer(3)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        lateinit var alertDialog: AlertDialog

        val button :Button = button
        button.setOnClickListener{

            val inflater: LayoutInflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.video_datepicker_dialog, null)

            val datePicker: DatePicker = dialogView.datePicker
            val textView:TextView = textView

            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            dialogBuilder.setView(dialogView)
                .setPositiveButton("確定", DialogInterface.OnClickListener { dialog, id ->

                    val date :String = datePicker.year.toString() +"年"+ (datePicker.month +1 ).toString()+"月"+datePicker.dayOfMonth.toString()+"日"
                    textView.text = date

                })

            alertDialog = dialogBuilder.create();
            //alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
            alertDialog.show()

        }

    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun initializePlayer(num : Int) {
        player = SimpleExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                when(num){
                    1 -> binding.playerView1.player = exoPlayer
                    2 -> binding.playerView2.player = exoPlayer
                    3 -> binding.playerView3.player = exoPlayer
                    else -> binding.playerView1.player = exoPlayer
                }

                val mediaItem = MediaItem.fromUri(getString(R.string.sample_mp4))
                exoPlayer.setMediaItem(mediaItem)

                exoPlayer.playWhenReady = false
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()


            }

    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }








}