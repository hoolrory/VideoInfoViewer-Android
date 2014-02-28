package com.roryhool.videoinfoviewer;

import java.io.File;
import java.io.IOException;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.FontManager;
import com.roryhool.videoinfoviewer.utils.RecentVideosManager;
import com.roryhool.videoinfoviewer.views.RobotoTextView;
import com.roryhool.videoinfoviewer.views.ScaledTextureView;

@SuppressWarnings( "deprecation" )
@Fullscreen
@EActivity( R.layout.activity_video )
@NoTitle
public class VideoActivity extends Activity implements TextureView.SurfaceTextureListener, OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {
   
   public static final String EXTRA_VIDEO_JSON = "EXTRA_VIDEO_JSON";

   @ViewById( R.id.video_texture_view )
   ScaledTextureView mVideoTextureView;
   
   MediaPlayer mMediaPlayer;

   Uri mVideoUri;

   RetrieveVideoDetailsTask mRetrieveVideoDetailsTask;

   Video mVideo;

   @Override
   public void onStart() {
      super.onStart();
      
      Bundle extras = getIntent().getExtras();

      Video video = null;

      if ( extras != null ) {
         if ( extras.containsKey( EXTRA_VIDEO_JSON ) ) {
            String videoJSON = extras.getString( EXTRA_VIDEO_JSON );
            Gson gson = new Gson();
            video = gson.fromJson( videoJSON, Video.class );
         }
      }

      if ( video != null ) {
         mVideoUri = Uri.parse( video.FilePath );
         LoadVideo( video );
      } else {
         mVideoUri = FindVideoUri();
         if ( mVideoUri != null ) {
            mRetrieveVideoDetailsTask = new RetrieveVideoDetailsTask();
            mRetrieveVideoDetailsTask.execute( mVideoUri );
         }
      }
   }

   @Override
   public void onPause() {
      super.onPause();

      if ( mMediaPlayer != null ) {
         if ( mMediaPlayer.isPlaying() ) {
            mMediaPlayer.pause();
         }
      }
   }

   @Override
   public boolean onCreateOptionsMenu( Menu menu ) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate( R.menu.main, menu );
      return true;
   }

   @Override
   public void onSurfaceTextureAvailable( SurfaceTexture surface, int width, int height ) {
      Surface s = new Surface( surface );

      try {
         mMediaPlayer = new MediaPlayer();
         mMediaPlayer.setDataSource( this, mVideoUri );
         mMediaPlayer.setSurface( s );
         mMediaPlayer.prepare();
         mMediaPlayer.setOnBufferingUpdateListener( this );
         mMediaPlayer.setOnCompletionListener( this );
         mMediaPlayer.setOnPreparedListener( this );
         mMediaPlayer.setOnVideoSizeChangedListener( this );
         mMediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
         mMediaPlayer.start();
      } catch ( IllegalArgumentException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch ( SecurityException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch ( IllegalStateException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch ( IOException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void addKeyValueField( int linearLayoutId, int keyStringId, String value ) {
      LinearLayout layout = (LinearLayout) findViewById( linearLayoutId );
      
      LinearLayout keyLayout = (LinearLayout) layout.getChildAt( 0 );
      LinearLayout valueLayout = (LinearLayout) layout.getChildAt( 1 );

      RobotoTextView keyView = new RobotoTextView( new ContextThemeWrapper( this, R.style.CardKey ) );
      // FontManager.get( this ).setRobotoBold( keyView );

      RobotoTextView valueView = new RobotoTextView( new ContextThemeWrapper( this, R.style.CardValue ) );
      FontManager.get( this ).setRobotoLight( valueView );
      
      keyView.setText( keyStringId );
      valueView.setText( value );

      keyLayout.addView( keyView );
      valueLayout.addView( valueView );
   }
   
   /*
   private void logBoxes( Box box ) {

      for ( Box subBox : box.getBoxes() ) {
         Log.d( "This", String.format( "Box - %s", box.getType() ) );

      }

   }
   */
   
   private Uri FindVideoUri() {
      Intent intent = getIntent();

      if ( intent != null ) {

         Uri uri = intent.getData();

         if ( uri != null ) {
            return uri;
         }

         Parcelable p = intent.getParcelableExtra( Intent.EXTRA_STREAM );

         if ( p != null && p instanceof Uri ) {
            return (Uri) p;
         }
      }

      return null;
   }

   private String GetFilePathFromUri( Uri uri ) {
      String filePath = null;

      if ( uri.getPath() != null ) {
         File f = new File( uri.getPath() );
         if ( f.exists() ) {
            filePath = uri.getPath();
         }
      }

      if ( ( filePath == null ) && uri.getScheme().equals( "content" ) ) {
         String[] projection = { MediaStore.Video.Media.DATA };
         Cursor cursor = getContentResolver().query( uri, projection, null, null, null );

         int dataColumn = cursor.getColumnIndex( MediaStore.Video.Media.DATA );

         if ( dataColumn >= 0 && cursor.moveToFirst() ) {

            filePath = cursor.getString( dataColumn );
         }

         cursor.close();
      }

      return filePath;
   }

   private void LoadVideo( Video video ) {

      mVideo = video;

      RecentVideosManager.Instance( VideoActivity.this ).addRecentVideo( mVideo );

      addKeyValueField( R.id.video_properties_layout, R.string.key_file_name, video.FileName );
      // addKeyValueField( R.id.video_properties_layout, R.string.key_file_path, video.FilePath );
      addKeyValueField( R.id.video_properties_layout, R.string.key_resolution, String.format( "%dx%d", video.VideoWidth, video.VideoHeight ) );
      addKeyValueField( R.id.video_properties_layout, R.string.key_format, video.Format );
      addKeyValueField( R.id.video_properties_layout, R.string.key_format_profile, video.FormatProfile );
      addKeyValueField( R.id.video_properties_layout, R.string.key_codec_id, video.CodecID );
      addKeyValueField( R.id.video_properties_layout, R.string.key_file_size, video.FileSize );
      addKeyValueField( R.id.video_properties_layout, R.string.key_duration, video.Duration );
      addKeyValueField( R.id.video_properties_layout, R.string.key_overall_bit_rate, video.OverallBitRate );
      addKeyValueField( R.id.video_properties_layout, R.string.key_encoded_date, video.EncodedDate );
      addKeyValueField( R.id.video_properties_layout, R.string.key_tagged_date, video.TaggedDate );

      mVideoTextureView.SetVideoSize( video.VideoWidth, video.VideoHeight );

      mVideoTextureView.setSurfaceTextureListener( VideoActivity.this );
   }

   public class RetrieveVideoDetailsTask extends AsyncTask<Uri, Void, Video> {

      @Override
      protected void onPreExecute() {

      }

      @Override
      protected Video doInBackground( Uri... uris ) {
         String filePath = GetFilePathFromUri( mVideoUri );
         Video video = Video.CreateFromFilePath( VideoActivity.this, filePath );
         return video;
      }

      @Override
      protected void onPostExecute( Video video ) {

         LoadVideo( video );
      }
   }

   @Override
   public boolean onSurfaceTextureDestroyed( SurfaceTexture surface ) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void onSurfaceTextureSizeChanged( SurfaceTexture surface, int width, int height ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onSurfaceTextureUpdated( SurfaceTexture surface ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onVideoSizeChanged( MediaPlayer arg0, int arg1, int arg2 ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onPrepared( MediaPlayer arg0 ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onCompletion( MediaPlayer arg0 ) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onBufferingUpdate( MediaPlayer arg0, int arg1 ) {
      // TODO Auto-generated method stub

   }
}
