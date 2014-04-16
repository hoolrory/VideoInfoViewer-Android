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

package com.roryhool.videoinfoviewer;

import java.io.File;
import java.io.IOException;

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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.coremedia.iso.IsoFile;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.FontManager;
import com.roryhool.videoinfoviewer.utils.FormatUtils;
import com.roryhool.videoinfoviewer.utils.VideoCache;
import com.roryhool.videoinfoviewer.utils.ViewUtils;
import com.roryhool.videoinfoviewer.views.DisableableScrollView;
import com.roryhool.videoinfoviewer.views.RobotoTextView;
import com.roryhool.videoinfoviewer.views.VideoPlayerView;
import com.roryhool.videoinfoviewer.views.VideoPlayerView.OnFullscreenListener;

public class VideoActivity extends Activity {

   SearchView mSearchView;

   RelativeLayout mRootLayout;

   DisableableScrollView mScrollView;

   VideoPlayerView mVideoPlayer;
   
   Button mButton;

   FrameLayout mAdFrame;

   AdView mAdView;

   Uri mVideoUri;

   RetrieveVideoDetailsTask mRetrieveVideoDetailsTask;

   RetrieveIsoFileTask mRetrieveIsoFileTask;

   Video mVideo;

   boolean mLoaded = false;
   
   int mBaseSystemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
   
   SearchFragment mSearchFragment;

   int mFragmentId;

   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );

      setContentView( R.layout.activity_video );
      getWindow().getDecorView().setSystemUiVisibility( mBaseSystemUiVisibility );
      mRootLayout = (RelativeLayout) findViewById( R.id.root_layout );

      mScrollView = (DisableableScrollView) findViewById( R.id.scroll_view );
      mVideoPlayer = (VideoPlayerView) findViewById( R.id.video_player );
      mButton = (Button) findViewById( R.id.view_atom_button );
      mAdFrame = (FrameLayout) findViewById( R.id.adFrame );

      mSearchFragment = new SearchFragment();

      mVideoPlayer.setPadding( 0, ViewUtils.GetStatusBarHeight( VideoActivity.this ) + ViewUtils.GetActionBarHeight( VideoActivity.this ), 0, 0 );
   }

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
         if ( extras.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {
            int cacheId = extras.getInt( Extras.EXTRA_VIDEO_CACHE_ID );
            video = VideoCache.Instance( this ).getVideoById( cacheId );
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

            Intent intent = new Intent( VideoActivity.this, AtomActivity.class );

            intent.putExtra( Extras.EXTRA_VIDEO_CACHE_ID, mVideo.CacheId );
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

      getMenuInflater().inflate( R.menu.video, menu );

      /*
      MenuItem searchMenuItem = menu.findItem( R.id.search );

      searchMenuItem.setOnActionExpandListener( new OnActionExpandListener() {

         @Override
         public boolean onMenuItemActionExpand( MenuItem item ) {
            // TODO Auto-generated method stub
            return true;
         }

         @Override
         public boolean onMenuItemActionCollapse( MenuItem item ) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction fragTransaction = manager.beginTransaction();
            fragTransaction.remove( mSearchFragment );
            fragTransaction.commit();

            return true;
         }

      } );
      
      mSearchView = (SearchView) searchMenuItem.getActionView();
      mSearchView.setOnQueryTextListener( mSearchFragment );
      */

      /*
       * mSearchView.setOnCloseListener( new OnCloseListener() {
       * 
       * @Override public boolean onClose() {
       * 
       * FragmentManager manager = getFragmentManager(); FragmentTransaction fragTransaction = manager.beginTransaction(); fragTransaction.remove( mSearchFragment ); fragTransaction.commit();
       * 
       * Log.d( "this", "SearchView onClose" ); return false; }
       * 
       * } );
       */

      /*
      mSearchView.setOnSearchClickListener( new View.OnClickListener() {

         @Override
         public void onClick( View v ) {
            Log.d( "this", "SearchView onClick" );
            FragmentManager manager = getFragmentManager();
            FragmentTransaction fragTransaction = manager.beginTransaction();
            fragTransaction.add( R.id.fragment_frame, mSearchFragment );
            fragTransaction.commit();
         }
      } );
      */

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

      RobotoTextView valueView = new RobotoTextView( new ContextThemeWrapper( this, R.style.CardValueOneLine ) );
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

      if ( mVideo.getIsoFile() == null ) {

         mRetrieveIsoFileTask = new RetrieveIsoFileTask();
         mRetrieveIsoFileTask.execute( mVideo );
         return;
      }

      findViewById( R.id.loading_progress ).setVisibility( View.GONE );

      findViewById( R.id.video_properties_card ).setVisibility( View.VISIBLE );
      findViewById( R.id.view_atom_button ).setVisibility( View.VISIBLE );

      mSearchFragment.setVideo( video );

      VideoCache.Instance( VideoActivity.this ).addVideo( mVideo );

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
            getWindow().getDecorView().setSystemUiVisibility( mBaseSystemUiVisibility | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN );

            mVideoPlayer.setPadding( 0, 0, 0, 0 );

            TranslateAnimation animate = new TranslateAnimation( 0, 0, 0, mAdFrame.getHeight() );
            animate.setDuration( 500 );
            animate.setFillAfter( true );

            mAdFrame.startAnimation( animate );
            mAdFrame.setVisibility( View.GONE );
            if ( mAdView != null ) {
            	mAdView.setEnabled( false );
               mAdView.setVisibility( View.INVISIBLE );
            }

            mScrollView.scrollTo( 0, 0 );
            mScrollView.setEnabled( false );

            getActionBar().hide();
         } else {
            getWindow().getDecorView().setSystemUiVisibility( mBaseSystemUiVisibility );

            mVideoPlayer.setPadding( 0, ViewUtils.GetStatusBarHeight( VideoActivity.this ) + ViewUtils.GetActionBarHeight( VideoActivity.this ), 0, 0 );

            TranslateAnimation animate = new TranslateAnimation( 0, 0, mAdFrame.getHeight(), 0 );
            animate.setDuration( 500 );
            animate.setFillAfter( true );

            mAdFrame.startAnimation( animate );
            mAdFrame.setVisibility( View.VISIBLE );
            mAdFrame.setEnabled( true );
            if ( mAdView != null ) {
            	mAdView.setVisibility( View.VISIBLE );
            }

            mScrollView.setEnabled( true );

            getActionBar().show();
         }
      }

   };

   public class RetrieveIsoFileTask extends AsyncTask<Video, Void, IsoFile> {

      @Override
      protected void onPreExecute() {

      }

      @Override
      protected IsoFile doInBackground( Video... videos ) {

         IsoFile isoFile = null;
         try {
            isoFile = new IsoFile( mVideo.FilePath );
         } catch ( IOException e ) {
            e.printStackTrace();
         }
         return isoFile;
      }

      @Override
      protected void onPostExecute( IsoFile isoFile ) {
         mVideo.setIsoFile( isoFile );
         LoadVideo( mVideo );
      }
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
}
