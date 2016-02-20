/**
 * Copyright (c) 2014 Rory Hool
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.roryhool.videoinfoviewer.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.VideoActivity.CancelFullscreenEvent;
import com.roryhool.videoinfoviewer.VideoInfoViewerApp;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.animation.ResizeAnimation;
import com.roryhool.videoinfoviewer.data.Video;
import com.squareup.otto.Subscribe;

public class VideoPlayerView extends FrameLayout implements SurfaceTextureListener, OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {

   public interface OnFullscreenListener {
      void onFullscreenChanged( boolean fullscreen );
   }

   protected ScaledTextureView mVideoTextureView;
   protected SeekBar           mSeekBar;
   protected ImageButton       mPlayButton;
   protected ImageButton       mFullscreenButton;
   protected RelativeLayout    mVideoControls;
   protected ImageView         mThumbView;
   protected ProgressBar       mPlayableProgress;

   protected SurfaceTexture mSurfaceTexture;

   protected MediaPlayer mMediaPlayer;
   protected Video       mVideo;

   protected boolean mControlsShowing;
   protected boolean mFullscreen;
   protected boolean mAlreadyLoggedPlayAction;
   protected boolean mStartPlayingWhenPrepared;

   protected List<OnFullscreenListener> mFullscreenListeners = new ArrayList<>();

   public VideoPlayerView( Context context ) {
      super( context );
      init( context );
   }

   public VideoPlayerView( Context context, AttributeSet attrs ) {
      super( context, attrs );
      init( context );
   }

   public VideoPlayerView( Context context, AttributeSet attrs, int defStyle ) {
      super( context, attrs, defStyle );
      init( context );
   }

   private void init( Context context ) {
      addView( View.inflate( context, R.layout.video_player, null ) );

      mVideoTextureView = (ScaledTextureView) findViewById( R.id.video_texture_view );

      mVideoTextureView.addSurfaceTextureListener( this );

      mThumbView = (ImageView) findViewById( R.id.video_thumb );

      mPlayableProgress = (ProgressBar) findViewById( R.id.playable_progress );

      mPlayButton = (ImageButton) findViewById( R.id.play_button );

      mVideoControls = (RelativeLayout) findViewById( R.id.video_controls );
      mSeekBar = (SeekBar) findViewById( R.id.seek_bar );
      mFullscreenButton = (ImageButton) findViewById( R.id.fullscreen_button );

      mSeekBar.setOnSeekBarChangeListener( mOnSeekBarChangeListener );
      mPlayButton.setOnClickListener( this::onClickPlay );
      mFullscreenButton.setOnClickListener( this::onClickFullScreen );

      setOnClickListener( this::onClickVideo );

      VideoInfoViewerApp.getBus().register( this );
   }

   public void setVideo( Video video ) {
      mVideo = video;
      mThumbView.setImageURI( Uri.parse( video.getThumbnailFilePath() ) );
   }

   public void addFullscreenListener( OnFullscreenListener listener ) {
      mFullscreenListeners.add( listener );
   }

   protected OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

      boolean mResumePlaying = false;

      @Override
      public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
         if ( fromUser ) {
            if ( mMediaPlayer != null ) {
               mMediaPlayer.seekTo( progress );
            }
         }
      }

      @Override
      public void onStartTrackingTouch( SeekBar seekBar ) {
         if ( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
            pause();
            mResumePlaying = true;
         }
      }

      @Override
      public void onStopTrackingTouch( SeekBar seekBar ) {
         if ( mResumePlaying ) {
            if ( mMediaPlayer != null && !mMediaPlayer.isPlaying() ) {
               play();
            }
         }
         mResumePlaying = false;
      }
   };

   private void onClickPlay( View view ) {
      if ( mMediaPlayer == null ) {
         mStartPlayingWhenPrepared = true;
         mThumbView.setVisibility( View.GONE );
         setupMediaPlayer();
      } else if ( mMediaPlayer.isPlaying() ) {
         pause();
      } else {
         play();
      }
   }

   private void onClickVideo( View view ) {
      if ( mMediaPlayer != null ) {
         if ( !mControlsShowing ) {
            showControls();
         } else if ( mMediaPlayer.isPlaying() ) {
            hideControls();
         }
      }
   }

   private void onClickFullScreen( View view ) {
      toggleFullscreen();
   }

   @Subscribe
   public void onCancelFullscreenEvent( CancelFullscreenEvent event ) {
      if ( mFullscreen ) {
         toggleFullscreen();
      }
   }

   protected void toggleFullscreen() {
      Analytics.logEvent( "App Action", "Toggled Fullscreen Mode" );
      if ( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
         resetTimer();
      }

      int targetHeight = getContext().getResources().getDimensionPixelSize( R.dimen.video_player_size );
      mFullscreen = getHeight() == targetHeight;
      fullscreenChanged( mFullscreen );

      if ( mFullscreen ) {
         WindowManager wm = (WindowManager) getContext().getSystemService( Context.WINDOW_SERVICE );
         Display display = wm.getDefaultDisplay();
         Point size = new Point();
         display.getSize( size );

         targetHeight = size.y;
      }
      ResizeAnimation animation = new ResizeAnimation( VideoPlayerView.this, getHeight(), targetHeight, true );
      animation.setDuration( 200 );
      startAnimation( animation );
   }

   public void shutdownMediaPlayer() {
      if ( mMediaPlayer != null ) {
         pause();
         mMediaPlayer.release();
         mMediaPlayer = null;

         slideOutVideoControls();
         mThumbView.setVisibility( View.VISIBLE );
      }

   }

   protected void setupMediaPlayer() {
      Surface s = new Surface( mSurfaceTexture );

      try {
         mMediaPlayer = new MediaPlayer();
         mMediaPlayer.setDataSource( getContext(), Uri.parse( mVideo.FilePath ) );
         mMediaPlayer.setSurface( s );
         mMediaPlayer.prepare();
         mMediaPlayer.setOnBufferingUpdateListener( this );
         mMediaPlayer.setOnCompletionListener( this );
         mMediaPlayer.setOnPreparedListener( this );
         mMediaPlayer.setOnVideoSizeChangedListener( this );
         mMediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
      } catch ( IllegalArgumentException e ) {
         e.printStackTrace();
      } catch ( SecurityException e ) {
         e.printStackTrace();
      } catch ( IllegalStateException e ) {
         e.printStackTrace();
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   protected void fullscreenChanged( boolean fullscreen ) {
      for ( OnFullscreenListener listener : mFullscreenListeners ) {
         if ( listener != null ) {
            listener.onFullscreenChanged( fullscreen );
         }
      }
   }

   public void handleResize() {
      int currentHeight = getHeight();
      int targetHeight = getContext().getResources().getDimensionPixelSize( R.dimen.video_player_size );

      if ( mFullscreen ) {
         WindowManager wm = (WindowManager) getContext().getSystemService( Context.WINDOW_SERVICE );
         Display display = wm.getDefaultDisplay();
         Point size = new Point();
         display.getSize( size );
         int height = size.y;

         targetHeight = height;

         Log.d( "this", String.format( Locale.US, "handleResize fullscreen, going from %d to %d", currentHeight, targetHeight ) );
         ResizeAnimation animation = new ResizeAnimation( VideoPlayerView.this, currentHeight, targetHeight, true );
         animation.setDuration( 200 );
         startAnimation( animation );
      } else {
         ResizeAnimation animation = new ResizeAnimation( VideoPlayerView.this, currentHeight, targetHeight, true );
         animation.setDuration( 200 );
         startAnimation( animation );
      }
   }

   public void play() {
      if ( mMediaPlayer != null && !mMediaPlayer.isPlaying() ) {
         startTimer();
         if ( mMediaPlayer.getCurrentPosition() >= mMediaPlayer.getDuration() ) {
            mMediaPlayer.seekTo( 0 );
         }
         mMediaPlayer.start();
         mPlayButton.setImageResource( R.drawable.ic_media_pause2 );

         if ( !mAlreadyLoggedPlayAction ) {
            Analytics.logEvent( "App Action", "Played Video" );
            mAlreadyLoggedPlayAction = true;
         }
      }
   }

   public void pause() {
      if ( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
         cancelTimer();
         mMediaPlayer.pause();
         mPlayButton.setImageResource( R.drawable.ic_media_play2 );
      }
   }

   @Override
   public void onSurfaceTextureAvailable( SurfaceTexture surfaceTexture, int width, int height ) {
      mSurfaceTexture = surfaceTexture;

      mPlayableProgress.setVisibility( View.INVISIBLE );
      mPlayButton.setVisibility( View.VISIBLE );
   }

   @Override
   public boolean onSurfaceTextureDestroyed( SurfaceTexture surfaceTexture ) {
      return false;
   }

   @Override
   public void onSurfaceTextureSizeChanged( SurfaceTexture surfaceTexture, int width, int height ) {
   }

   @Override
   public void onSurfaceTextureUpdated( SurfaceTexture surfaceTexture ) {
   }

   @Override
   public void onVideoSizeChanged( MediaPlayer mediaPlayer, int width, int height ) {
   }

   @Override
   public void onPrepared( MediaPlayer mediaPlayer ) {
      if ( mMediaPlayer != null ) {
         mSeekBar.setProgress( 0 );
         mSeekBar.setMax( mMediaPlayer.getDuration() );

         if ( mStartPlayingWhenPrepared ) {
            play();
         }
         mVideoTextureView.setAspectRatio( mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight() );
         slideInVideoControls();
         resetTimer();
      }
   }

   @Override
   public void onCompletion( MediaPlayer mediaPlayer ) {
      showControls();
      cancelTimer();
      mPlayButton.setImageResource( R.drawable.ic_media_play2 );
      mSeekBar.setMax( mMediaPlayer.getDuration() );
   }

   @Override
   public void onBufferingUpdate( MediaPlayer mp, int percent ) {
   }

   protected CountDownTimer mTimer = new CountDownTimer( 3000, 300 ) {

      public void onTick( long millisUntilFinished ) {
         updateProgress();
      }

      public void onFinish() {
         hideControls();
      }
   };

   private void resetTimer() {
      cancelTimer();
      startTimer();
   }

   private void cancelTimer() {
      mTimer.cancel();
   }

   private void startTimer() {
      mTimer.start();
   }

   private void updateProgress() {
      if ( mMediaPlayer != null && mMediaPlayer.getDuration() > 0 ) {
         mSeekBar.setProgress( mMediaPlayer.getCurrentPosition() );
      }
   }

   private void hideControls() {
      if ( !mControlsShowing ) {
         return;
      }
      mControlsShowing = false;

      AlphaAnimation alphaAnim = new AlphaAnimation( 1.0f, 0.0f );
      alphaAnim.setDuration( 200 );
      alphaAnim.setFillAfter( true );
      mPlayButton.startAnimation( alphaAnim );
      mPlayButton.setClickable( false );

      slideOutVideoControls();
   }

   private void showControls() {
      if ( mControlsShowing ) {
         return;
      }
      updateProgress();
      mControlsShowing = true;

      fadeInPlayButton();
      slideInVideoControls();

      resetTimer();
   }

   private void fadeInPlayButton() {
      AlphaAnimation alphaAnim = new AlphaAnimation( 0.0f, 1.0f );
      alphaAnim.setDuration( 200 );
      alphaAnim.setFillAfter( true );
      mPlayButton.startAnimation( alphaAnim );
      mPlayButton.setClickable( true );
   }

   private void slideInVideoControls() {
      slideVideoControls( getResources().getDimensionPixelSize( R.dimen.min_touch ), 0 );
   }

   private void slideOutVideoControls() {
      slideVideoControls( 0, getResources().getDimensionPixelSize( R.dimen.min_touch ) );
   }

   private void slideVideoControls( int fromYDelta, int toYDelta ) {
      TranslateAnimation translateAnim = new TranslateAnimation( 0, 0, fromYDelta, toYDelta );
      translateAnim.setDuration( 200 );
      translateAnim.setFillAfter( true );
      mVideoControls.startAnimation( translateAnim );
   }
}
