package com.roryhool.videoinfoviewer.views;

import java.io.IOException;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.roryhool.videoinfoviewer.R;

public class VideoPlayerView extends FrameLayout implements SurfaceTextureListener, OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {

   ScaledTextureView mVideoTextureView;

   ImageButton mPlayButton;

   ImageButton mFullscreenButton;

   MediaPlayer mMediaPlayer;

   Uri mVideoUri;

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
      mFullscreenButton = (ImageButton) findViewById( R.id.fullscreen_button );

      mPlayButton.setOnClickListener( mOnPlayClickListener );
   }

   public void setVideoUri( Uri videoUri ) {
      mVideoUri = videoUri;
   }
   
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

   OnClickListener OnFullscreenClickListener = new OnClickListener() {
      @Override
      public void onClick( View view ) {

      }
   };

   public void play() {
      if ( mMediaPlayer != null ) {
         if ( !mMediaPlayer.isPlaying() ) {
            if ( mMediaPlayer.getCurrentPosition() >= mMediaPlayer.getDuration() ) {
               mMediaPlayer.seekTo( 0 );
            }
            mMediaPlayer.start();
            mPlayButton.setImageResource( R.drawable.ic_media_pause );
         }
      }
   }

   public void pause() {
      if ( mMediaPlayer != null ) {
         if ( mMediaPlayer.isPlaying() ) {
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
      mPlayButton.setImageResource( R.drawable.ic_media_play );
   }

   @Override
   public void onBufferingUpdate( MediaPlayer mp, int percent ) {
      // TODO Auto-generated method stub

   }

}
