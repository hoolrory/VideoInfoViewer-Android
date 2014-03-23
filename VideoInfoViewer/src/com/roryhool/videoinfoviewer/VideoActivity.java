package com.roryhool.videoinfoviewer;

import java.io.File;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.FontManager;
import com.roryhool.videoinfoviewer.utils.FormatUtils;
import com.roryhool.videoinfoviewer.utils.RecentVideosManager;
import com.roryhool.videoinfoviewer.views.RobotoTextView;
import com.roryhool.videoinfoviewer.views.VideoPlayerView;
import com.roryhool.videoinfoviewer.views.VideoPlayerView.OnFullscreenListener;

@SuppressWarnings( "deprecation" )
@EActivity( R.layout.activity_video )
@NoTitle
public class VideoActivity extends Activity {
   
   public static final String EXTRA_VIDEO_JSON = "EXTRA_VIDEO_JSON";

   @ViewById( R.id.root_layout )
   RelativeLayout mRootLayout;

   @ViewById( R.id.scroll_view )
   ScrollView mScrollView;

   @ViewById( R.id.video_player )
   VideoPlayerView mVideoPlayer;
   
   @ViewById( R.id.view_atom_button )
   Button mButton;

   @ViewById( R.id.adFrame )
   FrameLayout mAdFrame;

   AdView mAdView;

   Uri mVideoUri;

   RetrieveVideoDetailsTask mRetrieveVideoDetailsTask;

   Video mVideo;

   boolean mLoaded = false;

   @Override
   public void onStart() {
      super.onStart();

      if ( mLoaded ) {
         return;
      }
      mLoaded = true;

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

      mVideoPlayer.setVideoUri( mVideoUri );
      mVideoPlayer.addFullscreenListener( mOnFullscreenListener );
      mVideoPlayer.setFullscreenFillView( mRootLayout );

      mButton.setOnClickListener( new OnClickListener() {

         @Override
         public void onClick( View view ) {

            Intent intent = new Intent( VideoActivity.this, AtomActivity_.class );

            Gson gson = new Gson();
            intent.putExtra( VideoActivity.EXTRA_VIDEO_JSON, gson.toJson( mVideo ) );
            startActivity( intent );
         }

      } );

      setupAds();

      EasyTracker.getInstance( this ).activityStart( this );
   }

   @Override
   public void onPause() {
      super.onPause();

      mVideoPlayer.pause();
   }

   @Override
   public void onStop() {
      super.onStart();

      EasyTracker.getInstance( this ).activityStop( this );
   }

   @Override
   public boolean onCreateOptionsMenu( Menu menu ) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate( R.menu.main, menu );
      return true;
   }

   @Override
   public void onConfigurationChanged( Configuration newConfig ) {
      super.onConfigurationChanged( newConfig );

      mVideoPlayer.handleResize();
   }

   private void setupAds() {

      String admobAdUnitId = getString( R.string.video_activity_admob_ad_unit_id );

      if ( admobAdUnitId != null && !admobAdUnitId.equals( ( "" ) ) ) {
         mAdView = new AdView( this );
         mAdView.setAdSize( AdSize.BANNER );
         mAdView.setAdUnitId( admobAdUnitId );

         mAdFrame.addView( mAdView );

         String[] testDeviceIds = getResources().getStringArray( R.array.admob_test_device_ids );

         AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
         adRequestBuilder.addTestDevice( AdRequest.DEVICE_ID_EMULATOR );

         for ( int i = 0; i < testDeviceIds.length; i++ ) {
            adRequestBuilder.addTestDevice( testDeviceIds[i] );
         }

         AdRequest adRequest = adRequestBuilder.build();
         mAdView.loadAd( adRequest );
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

      addKeyValueField( R.id.video_properties_layout, R.string.key_mimetype, video.MimeType );

      addKeyValueField( R.id.video_properties_layout, R.string.key_format, video.Format );
      addKeyValueField( R.id.video_properties_layout, R.string.key_format_profile, video.FormatProfile );
      addKeyValueField( R.id.video_properties_layout, R.string.key_codec_id, video.CodecID );

      String fileSizeString = FormatUtils.FormatFileSizeForDisplay( Float.parseFloat( video.FileSize ) );
      addKeyValueField( R.id.video_properties_layout, R.string.key_file_size, fileSizeString );

      String durationString = FormatUtils.FormatTimeForDisplay( Long.parseLong( video.Duration ) );
      addKeyValueField( R.id.video_properties_layout, R.string.key_duration, durationString );

      String kbps = FormatUtils.FormatBpsForDisplay( Long.parseLong( video.BitRate ) );
      addKeyValueField( R.id.video_properties_layout, R.string.key_bitrate, kbps );

      String dateString = FormatUtils.FormatZuluDateTimeForDisplay( video.Date );
      addKeyValueField( R.id.video_properties_layout, R.string.key_date, dateString );
   }

   OnFullscreenListener mOnFullscreenListener = new OnFullscreenListener() {

      @Override
      public void onFullscreenChanged( boolean fullscreen ) {

         if ( fullscreen ) {
            // TODO: need to make this smooth
            // getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
            // getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN );
            // TranslateAnimation animate = new TranslateAnimation( 0, 0, 0, view.getHeight() );

            TranslateAnimation animate = new TranslateAnimation( 0, 0, 0, mAdFrame.getHeight() );
            animate.setDuration( 500 );
            animate.setFillAfter( true );
            mAdFrame.startAnimation( animate );
            mAdFrame.setVisibility( View.GONE );
            if ( mAdView != null ) {
            	mAdView.setEnabled( false );
            	mAdView.setVisibility( View.INVISIBLE );
            }
            // TODO: need to subclass this to disable scrolling
            mScrollView.scrollTo( 0, 0 );
         } else {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN );
            getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
            TranslateAnimation animate = new TranslateAnimation( 0, 0, mAdFrame.getHeight(), 0 );
            animate.setDuration( 500 );
            animate.setFillAfter( true );
            mAdFrame.startAnimation( animate );
            mAdFrame.setVisibility( View.GONE );
            mAdFrame.setEnabled( true );
            if ( mAdView != null ) {
            	mAdView.setVisibility( View.VISIBLE );
            }
         }
      }

   };

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
}
