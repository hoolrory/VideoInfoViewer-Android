/**
   Copyright (c) 2014 Rory Hool
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.animation.ResizeAnimation;

public class VideoPlayerView extends FrameLayout implements SurfaceTextureListener, OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {

   public interface OnFullscreenListener {
      public void onFullscreenChanged( boolean fullscreen );
   }

   ScaledTextureView mVideoTextureView;

   SeekBar mSeekBar;

   ImageButton mPlayButton;

   ImageButton mFullscreenButton;

   MediaPlayer mMediaPlayer;

   RelativeLayout mVideoControls;

   Uri mVideoUri;

   boolean mControlsShowing = true;

   boolean mFullscreen = false;

   boolean mAlreadyLoggedPlayAction = false;

   List<OnFullscreenListener> mFullscreenListeners = new ArrayList<OnFullscreenListener>();

   View mFullscreenFillView;

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

      mPlayButton = (ImageButton) findViewById( R.id.play_button );

      mVideoControls = (RelativeLayout) findViewById( R.id.video_controls );
      mSeekBar = (SeekBar) findViewById( R.id.seek_bar );
      mFullscreenButton = (ImageButton) findViewById( R.id.fullscreen_button );

      mSeekBar.setOnSeekBarChangeListener( mOnSeekBarChangeListener );
      mPlayButton.setOnClickListener( mOnPlayClickListener );
      mFullscreenButton.setOnClickListener( mOnFullscreenClickListener );

      this.setOnClickListener( mOnVideoPlayerViewClickListener );
   }

   public void setVideoUri( Uri videoUri ) {
      mVideoUri = videoUri;
   }
   
   public void addFullscreenListener( OnFullscreenListener listener ) {
      mFullscreenListeners.add( listener );
   }

   public void setFullscreenFillView( View view ) {
      mFullscreenFillView = view;
   }

   OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

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
         if ( mMediaPlayer != null ) {
            if ( mMediaPlayer.isPlaying() ) {
               pause();
               mResumePlaying = true;
            }
         }
      }

      @Override
      public void onStopTrackingTouch( SeekBar seekBar ) {
         if ( mResumePlaying ) {
            if ( mMediaPlayer != null ) {
               if ( !mMediaPlayer.isPlaying() ) {
                  play();
               }
            }
         }
         mResumePlaying = false;
      }

   };

   OnClickListener mOnPlayClickListener = new OnClickListener() {
      @Override
      public void onClick( View view ) {
         if ( mMediaPlayer != null ) {
            if ( mMediaPlayer.isPlaying() ) {
               pause();
            } else {
               play();
            }
         }
      }
   };

   OnClickListener mOnVideoPlayerViewClickListener = new OnClickListener() {
      @Override
      public void onClick( View view ) {
         if ( mControlsShowing ) {
            if ( mMediaPlayer.isPlaying() ) {
               hideControls();
            }
         } else {
            showControls();
         }
      }
   };

   OnClickListener mOnFullscreenClickListener = new OnClickListener() {
      @Override
      public void onClick( View view ) {

         Analytics.Instance( getContext() ).LogEvent( "App Action", "Toggled Fullscreen Mode" );

         if ( mMediaPlayer.isPlaying() ) {
            resetTimer();
         }

         ResizeAnimation animation = null;

         int currentHeight = getHeight();
         int targetHeight = getContext().getResources().getDimensionPixelSize( R.dimen.video_player_size );

         mFullscreen = currentHeight == targetHeight;
         fullscreenChanged( mFullscreen );

         if ( mFullscreen ) {

            WindowManager wm = (WindowManager) getContext().getSystemService( Context.WINDOW_SERVICE );
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize( size );
            int height = size.y;

            View fullscreenTargetView = mFullscreenFillView;

            if ( fullscreenTargetView == null ) {
               fullscreenTargetView = (View) getParent();
            }
            targetHeight = height;
         }
         animation = new ResizeAnimation( VideoPlayerView.this, currentHeight, targetHeight, true );
         animation.setDuration( 200 );
         startAnimation( animation );
      }
   };

   private void fullscreenChanged( boolean fullscreen ) {
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

         View fullscreenTargetView = mFullscreenFillView;

         if ( fullscreenTargetView == null ) {
            fullscreenTargetView = (View) getParent();
         }

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
      if ( mMediaPlayer != null ) {
         if ( !mMediaPlayer.isPlaying() ) {
            startTimer();
            if ( mMediaPlayer.getCurrentPosition() >= mMediaPlayer.getDuration() ) {
               mMediaPlayer.seekTo( 0 );
            }
            mMediaPlayer.start();
            mPlayButton.setImageResource( R.drawable.ic_media_pause );

            if ( !mAlreadyLoggedPlayAction ) {
               Analytics.Instance( getContext() ).LogEvent( "App Action", "Played Video" );
               mAlreadyLoggedPlayAction = true;
            }
         }
      }
   }

   public void pause() {
      if ( mMediaPlayer != null ) {
         if ( mMediaPlayer.isPlaying() ) {
            cancelTimer();
            mMediaPlayer.pause();
            mPlayButton.setImageResource( R.drawable.ic_media_play );
         }
      }
   }

   @Override
   public void onSurfaceTextureAvailable( SurfaceTexture surfaceTexture, int width, int height ) {

      Surface s = new Surface( surfaceTexture );

      try {
         mMediaPlayer = new MediaPlayer();
         mMediaPlayer.setDataSource( getContext(), mVideoUri );
         mMediaPlayer.setSurface( s );
         mMediaPlayer.prepare();
         mMediaPlayer.setOnBufferingUpdateListener( this );
         mMediaPlayer.setOnCompletionListener( this );
         mMediaPlayer.setOnPreparedListener( this );
         mMediaPlayer.setOnVideoSizeChangedListener( this );
         mMediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
         mMediaPlayer.seekTo( 0 );
      } catch ( IllegalArgumentException e ) {
         e.printStackTrace();
      } catch ( SecurityException e ) {
         e.printStackTrace();
      } catch ( IllegalStateException e ) {
         e.printStackTrace();
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      if ( mMediaPlayer != null ) {
         mSeekBar.setProgress( 0 );
         mSeekBar.setMax( mMediaPlayer.getDuration() );
         mVideoTextureView.SetVideoSize( mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight() );
      }
   }

   @Override
   public boolean onSurfaceTextureDestroyed( SurfaceTexture surfaceTexture ) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void onSurfaceTextureSizeChanged( SurfaceTexture surfaceTexture, int width, int height ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onSurfaceTextureUpdated( SurfaceTexture surfaceTexture ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onVideoSizeChanged( MediaPlayer mediaPlayer, int width, int height ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onPrepared( MediaPlayer mediaPlayer ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onCompletion( MediaPlayer mediaPlayer ) {
      showControls();
      cancelTimer();
      mPlayButton.setImageResource( R.drawable.ic_media_play );
   }

   @Override
   public void onBufferingUpdate( MediaPlayer mp, int percent ) {
      // TODO Auto-generated method stub

   }
   
   CountDownTimer mTimer = new CountDownTimer( 3000, 300 ) {

      public void onTick(long millisUntilFinished) {
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

      int y = getResources().getDimensionPixelSize( R.dimen.min_touch );

      TranslateAnimation translateAnim = new TranslateAnimation( 0, 0, 0, y );
      translateAnim.setDuration( 200 );
      translateAnim.setFillAfter( true );
      mVideoControls.startAnimation( translateAnim );
   }

   private void showControls() {

      if ( mControlsShowing ) {
         return;
      }
      updateProgress();
      mControlsShowing = true;

      AlphaAnimation alphaAnim = new AlphaAnimation( 0.0f, 1.0f );
      alphaAnim.setDuration( 200 );
      alphaAnim.setFillAfter( true );
      mPlayButton.startAnimation( alphaAnim );
      mPlayButton.setClickable( true );

      int y = getResources().getDimensionPixelSize( R.dimen.min_touch );

      TranslateAnimation anim = new TranslateAnimation( 0, 0, y, 0 );
      anim.setDuration( 200 );
      anim.setFillAfter( true );
      mVideoControls.startAnimation( anim );

      resetTimer();
   }

}
