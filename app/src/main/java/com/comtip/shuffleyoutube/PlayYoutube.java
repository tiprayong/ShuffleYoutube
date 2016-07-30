package com.comtip.shuffleyoutube;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

/**
 * Created by TipRayong on 20/7/2559.
 */
public class PlayYoutube extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    final String YOUTUBE_API_KEY = "Youtube API Key";
    YouTubePlayerView youtubeView;
    ArrayList<String> shuffle = new ArrayList<>();
    TextView headerVideo;
    Button finishBT;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.youtube_layout);
        youtubeView = (YouTubePlayerView) findViewById(R.id.youtubeView);
        headerVideo = (TextView) findViewById(R.id.headerVideo);
        finishBT = (Button) findViewById(R.id.finishBT);

        finishBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle  bundle  =  getIntent().getExtras();
        shuffle = bundle.getStringArrayList("shuffle");


        if (shuffle.isEmpty())  {
            //nothing
        }else {

            youtubeView.initialize(YOUTUBE_API_KEY,this);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setFullscreen(true);
        youTubePlayer.loadVideos(shuffle);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            Toast.makeText(this, "Unknow Error", Toast.LENGTH_LONG).show();
        }
    }


}
