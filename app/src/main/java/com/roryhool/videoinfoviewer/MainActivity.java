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

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crittercism.app.Crittercism;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.UriHelper;
import com.roryhool.videoinfoviewer.utils.VideoCache;

public class MainActivity extends Activity {

   private int SELECT_VIDEO_CODE = 100;

   ListView mRecentVideosList;

   FrameLayout mAdFrame;

   RecentVideosAdapter mAdapter;

   private AdView mAdView;

   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );

      setContentView( R.layout.activity_main );

      mRecentVideosList = (ListView) findViewById( R.id.recentVideosList );

      mAdFrame = (FrameLayout) findViewById( R.id.adFrame );
   }

   @Override
   public void onStart() {
      super.onStart();

      String crittercismAppId = getString( R.string.crittercism_app_id );

      if ( crittercismAppId != null && !crittercismAppId.isEmpty() ) {
         Crittercism.initialize( getApplicationContext(), crittercismAppId );
      }

      List<Video> recentVideos = VideoCache.Instance( this ).getVideos();

      if ( recentVideos.size() == 0 ) {
         findViewById( R.id.onboarding_layout ).setVisibility( View.VISIBLE );
      } else {
         findViewById( R.id.video_properties_card ).setVisibility( View.VISIBLE );
      }

      mAdapter = new RecentVideosAdapter( this, R.layout.recent_video_layout, recentVideos );
      mRecentVideosList.setAdapter( mAdapter );
      mRecentVideosList.setOnItemClickListener( new OnVideoItemClickListener() );

      setupAds();

      VideoInfoViewerApp.getDefaultTracker().setScreenName( MainActivity.class.getSimpleName() );
      VideoInfoViewerApp.getDefaultTracker().send( new HitBuilders.ScreenViewBuilder().build() );
   }

   @Override
   public boolean onCreateOptionsMenu( Menu menu ) {
      getMenuInflater().inflate( R.menu.main, menu );
      return true;
   }

   @Override
   public boolean onOptionsItemSelected( MenuItem item ) {
      switch ( item.getItemId() ) {
      case R.id.action_select_video:
         launchVideoChooser();
         return true;
      case R.id.action_credits:
         launchCredits();
         return true;
      default:
         return super.onOptionsItemSelected( item );
      }
   }

   private void setupAds() {
      
      String admobAdUnitId = getString( R.string.main_activity_admob_ad_unit_id );
      
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

   private void launchCredits() {
      FragmentManager manager = getFragmentManager();
      FragmentTransaction fragTransaction = manager.beginTransaction();
      fragTransaction.add( R.id.fragment_frame, new CreditsFragment() );
      fragTransaction.addToBackStack( "Credits" );
      fragTransaction.commit();

      Analytics.Instance( this ).LogEvent( "App Action", "Opened Credits" );
   }

   @SuppressLint( "NewApi" )
   private void launchVideoChooser() {
      if ( Build.VERSION.SDK_INT < 19 ) {
         Intent intent = new Intent();
         intent.addCategory( Intent.CATEGORY_OPENABLE );
         intent.setType( "video/mp4" );
         intent.setAction( Intent.ACTION_GET_CONTENT );
         Intent chooser = Intent.createChooser( intent, getString( R.string.select_video ) );
         startActivityForResult( chooser, SELECT_VIDEO_CODE );
      } else {
         Intent intent = new Intent( Intent.ACTION_OPEN_DOCUMENT );
         intent.addCategory( Intent.CATEGORY_OPENABLE );
         intent.setType( "video/mp4" );
         intent.setAction( Intent.ACTION_GET_CONTENT );
         Intent chooser = Intent.createChooser( intent, getString( R.string.select_video ) );
         startActivityForResult( chooser, SELECT_VIDEO_CODE );
      }

      Analytics.Instance( this ).LogEvent( "App Action", "Launched Video Chooser" );
   }

   @SuppressLint( "NewApi" )
   @Override
   public void onActivityResult( int requestCode, int resultCode, Intent data ) {
      if ( data != null ) {
         Intent intent = new Intent( this, VideoActivity.class );

         Uri uri = data.getData();

         if ( uri.getScheme().equals( "content" ) ) {
            String path = UriHelper.ContentUriToFilePath( this, uri );
            intent.setData( Uri.parse( path ) );
         } else {
            intent.setData( data.getData() );
         }

         startActivity(intent);
      }
   }

   public class RecentVideosAdapter extends ArrayAdapter<Video> {

      public RecentVideosAdapter( Context context, int resource, List<Video> objects ) {
         super( context, resource, objects );
      }

      @Override
      public View getView( int position, View convertView, ViewGroup parent ) {

         Video video = getItem( position );

         RelativeLayout videoView = (RelativeLayout) convertView;

         if ( videoView == null ) {
            videoView = (RelativeLayout) View.inflate( MainActivity.this, R.layout.recent_video_layout, null );
         }

         ImageView thumbnailView = (ImageView) videoView.findViewById( R.id.video_thumbnail );
         TextView fileNameText = (TextView) videoView.findViewById( R.id.video_filename );
         TextView resolutionText = (TextView) videoView.findViewById( R.id.video_resolution );

         thumbnailView.setImageURI( Uri.parse( video.getThumbnailFilePath( MainActivity.this ) ) );
         fileNameText.setText( video.FileName );
         resolutionText.setText( String.format( "%dx%d", video.VideoWidth, video.VideoHeight ) );

         return videoView;
      }

   }
   
   public class OnVideoItemClickListener implements AdapterView.OnItemClickListener {

      @Override
      public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
         Video video = mAdapter.getItem( position );
         Intent intent = new Intent( MainActivity.this, VideoActivity.class );

         intent.putExtra( Extras.EXTRA_VIDEO_CACHE_ID, video.CacheId );
         startActivity( intent );

         Analytics.Instance( MainActivity.this ).LogEvent( "App Action", "Selected Video from Recent Videos List" );
      }
      
   }

}
