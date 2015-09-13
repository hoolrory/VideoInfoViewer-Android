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

package com.roryhool.videoinfoviewer;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.UriHelper;
import com.roryhool.videoinfoviewer.utils.VideoCache;
import com.roryhool.videoinfoviewer.utils.ViewUtils;
import com.roryhool.videoinfoviewer.views.VideoPlayerView.OnFullscreenListener;

public class VideoActivity extends AppCompatActivity implements OnFullscreenListener, OnPageChangeListener {

   protected Toolbar        mToolbar;
   protected RelativeLayout mRootLayout;
   protected FrameLayout    mAdFrame;
   protected AdView         mAdView;
   protected ViewPager      mViewPager;
   protected TabLayout      mTabLayout;

   protected VideoFragmentAdapter mPagerAdapter;

   protected RetrieveVideoDetailsTask mRetrieveVideoDetailsTask;

   protected int mBaseSystemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );

      setContentView( R.layout.activity_video );
      getWindow().getDecorView().setSystemUiVisibility( mBaseSystemUiVisibility );
      mRootLayout = (RelativeLayout) findViewById( R.id.root_layout );

      mToolbar = (Toolbar) findViewById( R.id.toolbar );
      mTabLayout = (TabLayout) findViewById( R.id.tab_layout );
      mTabLayout.setTabMode( TabLayout.MODE_SCROLLABLE );

      setSupportActionBar( mToolbar );
      getSupportActionBar().setDisplayHomeAsUpEnabled( true );
      getSupportActionBar().setDisplayShowHomeEnabled( true );

      mAdFrame = (FrameLayout) findViewById( R.id.ad_frame );

      mRootLayout.setPadding( 0, ViewUtils.GetStatusBarHeight( VideoActivity.this ), 0, 0 );

      Bundle extras = getIntent().getExtras();
      Bundle args = new Bundle();

      mViewPager = (ViewPager) findViewById( R.id.view_pager );
      mViewPager.addOnPageChangeListener( this );

      if ( extras.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {
         setCurrentVideo( VideoCache.Instance().getVideoById( extras.getInt( Extras.EXTRA_VIDEO_CACHE_ID ) ) );
      } else {
         Uri videoUri = args.getParcelable( Extras.EXTRA_URI );
         if ( videoUri != null ) {
            mRetrieveVideoDetailsTask = new RetrieveVideoDetailsTask();
            mRetrieveVideoDetailsTask.execute( videoUri );
         }
      }

      TypedValue secondaryTextColor = new TypedValue();
      getTheme().resolveAttribute( android.R.attr.textColorSecondary, secondaryTextColor, true );
      TypedValue primaryTextColor = new TypedValue();
      getTheme().resolveAttribute( android.R.attr.textColorPrimary, primaryTextColor, true );

      mTabLayout.setTabTextColors( secondaryTextColor.data, primaryTextColor.data );
      mTabLayout.setupWithViewPager( mViewPager );

      if ( !BuildConfig.DEBUG ) {
         setupAds();
      }

      VideoInfoViewerApp.getDefaultTracker().setScreenName( VideoActivity.class.getSimpleName() );
      VideoInfoViewerApp.getDefaultTracker().send( new HitBuilders.ScreenViewBuilder().build() );
   }

   @Override
   public boolean onCreateOptionsMenu( Menu menu ) {
      getMenuInflater().inflate( R.menu.video, menu );

      return true;
   }

   @Override
   public boolean onOptionsItemSelected( MenuItem item ) {
      int itemId = item.getItemId();
      switch ( itemId ) {
      case android.R.id.home:
         super.onBackPressed();
         break;
      }

      return true;
   }

   protected void setCurrentVideo( Video video ) {
      VideoCache.Instance().addVideo( video );

      mPagerAdapter = new VideoFragmentAdapter( getSupportFragmentManager() );
      mViewPager.setAdapter( mPagerAdapter );
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

   @Override
   public void onFullscreenChanged( boolean fullscreen ) {
      if ( fullscreen ) {
         getWindow().getDecorView().setSystemUiVisibility( mBaseSystemUiVisibility | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN );

         mRootLayout.setPadding( 0, 0, 0, 0 );

         TranslateAnimation animate = new TranslateAnimation( 0, 0, 0, mAdFrame.getHeight() );
         animate.setDuration( 2000 );
         animate.setFillAfter( true );

         mAdFrame.startAnimation( animate );
         mAdFrame.setVisibility( View.GONE );
         if ( mAdView != null ) {
            mAdView.setEnabled( false );
            mAdView.setVisibility( View.INVISIBLE );
         }

         getSupportActionBar().hide();
      } else {
         getWindow().getDecorView().setSystemUiVisibility( mBaseSystemUiVisibility );

         mRootLayout.setPadding( 0, ViewUtils.GetStatusBarHeight( VideoActivity.this ), 0, 0 );

         TranslateAnimation animate = new TranslateAnimation( 0, 0, mAdFrame.getHeight(), 0 );
         animate.setDuration( 2000 );
         animate.setFillAfter( true );

         mAdFrame.startAnimation( animate );
         mAdFrame.setVisibility( View.VISIBLE );
         mAdFrame.setEnabled( true );
         if ( mAdView != null ) {
            mAdView.setVisibility( View.VISIBLE );
         }

         getSupportActionBar().show();
      }
   }

   @Override
   public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

   }

   @Override
   public void onPageSelected( int position ) {

   }

   @Override
   public void onPageScrollStateChanged( int state ) {

   }

   protected class VideoFragmentAdapter extends FragmentStatePagerAdapter {

      public VideoFragmentAdapter( FragmentManager fragmentManager ) {
         super( fragmentManager );
      }

      @Override
      public Fragment getItem( int index ) {

         Bundle args = new Bundle();
         Video video = VideoCache.Instance().getVideoByIndex( index );
         if ( video != null ) {
            args.putInt( Extras.EXTRA_VIDEO_CACHE_ID, video.CacheId );
         }

         VideoFragment videoFragment = new VideoFragment();
         videoFragment.setArguments( args );

         return videoFragment;
      }

      @Override
      public int getCount() {
         return VideoCache.Instance().getVideos().size();
      }

      @Override
      public CharSequence getPageTitle( int index ) {
         Video video = VideoCache.Instance().getVideoByIndex( index );
         if ( video != null ) {
            return video.FileName;
         }

         return "";
      }
   }


   public class RetrieveVideoDetailsTask extends AsyncTask<Uri, Void, Video> {

      @Override
      protected void onPreExecute() {
      }

      @Override
      protected Video doInBackground( Uri... uris ) {
         String filePath = UriHelper.getFilePathFromUri( uris[0] );
         Video video = Video.CreateFromFilePath( filePath );
         return video;
      }

      @Override
      protected void onPostExecute( Video video ) {
         setCurrentVideo( video );
      }
   }
}
