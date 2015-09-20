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

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.UriHelper;
import com.roryhool.videoinfoviewer.utils.VideoCache;
import com.roryhool.videoinfoviewer.utils.ViewUtils;
import com.roryhool.videoinfoviewer.views.VideoPlayerView.OnFullscreenListener;

import java.util.HashMap;
import java.util.Stack;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

public class VideoActivity extends AppCompatActivity implements OnFullscreenListener, OnPageChangeListener {

   public static class CancelFullscreenEvent {
   }

   protected Toolbar        mToolbar;
   protected RelativeLayout mRootLayout;
   protected FrameLayout    mAdFrame;
   protected AdView         mAdView;
   protected ViewPager      mViewPager;
   protected TabLayout      mTabLayout;

   protected VideoFragmentAdapter mPagerAdapter;

   protected int mBaseSystemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

   protected boolean mFullscreen;

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

      mViewPager = (ViewPager) findViewById( R.id.view_pager );
      mViewPager.setOffscreenPageLimit( 5 );
      mViewPager.addOnPageChangeListener( this );

      if ( extras != null && extras.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {
         setCurrentVideo( VideoCache.Instance().getVideoById( extras.getInt( Extras.EXTRA_VIDEO_CACHE_ID ) ) );
      } else {
         Uri videoUri = getIntent().getData();
         if ( videoUri == null && getIntent().hasExtra( Intent.EXTRA_STREAM ) ) {
            videoUri = getIntent().getParcelableExtra( Intent.EXTRA_STREAM );
         }
         AppObservable.bindActivity( this, Observable.create( new RetrieveVideoDetailsTask( videoUri ) ) )
                      .subscribeOn( Schedulers.io() )
                      .subscribe(
                         new Subscriber<Video>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError( Throwable e ) {
                               Toast.makeText( VideoActivity.this, R.string.failed_to_open_video, Toast.LENGTH_LONG ).show();
                               finish();
                            }

                            @Override
                            public void onNext( Video video ) {
                               setCurrentVideo( video );
                            }
                         } );
      }

      TypedValue secondaryTextColor = new TypedValue();
      getTheme().resolveAttribute( android.R.attr.textColorSecondary, secondaryTextColor, true );
      TypedValue primaryTextColor = new TypedValue();
      getTheme().resolveAttribute( android.R.attr.textColorPrimary, primaryTextColor, true );

      mTabLayout.setTabTextColors( secondaryTextColor.data, primaryTextColor.data );

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

   @Override
   public void onBackPressed() {
      Video video = VideoCache.Instance().getVideoByIndex( mViewPager.getCurrentItem() );
      if ( mPagerAdapter.interceptOnBackPressed( video ) ) {

      } else if ( mFullscreen ) {
         exitFullscreen();
      } else {
         super.onBackPressed();
      }
   }

   public void addFragmentToVideoTab( Video video, Class<? extends Fragment> fragmentClass, Bundle args ) {
      mPagerAdapter.setFragment( video, fragmentClass, args, true );
   }

   protected void setCurrentVideo( Video video ) {
      VideoCache.Instance().addVideo( video );

      mPagerAdapter = new VideoFragmentAdapter( getSupportFragmentManager() );
      mViewPager.setAdapter( mPagerAdapter );
      mTabLayout.setupWithViewPager( mViewPager );
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
      mFullscreen = fullscreen;

      if ( mFullscreen ) {
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
         mTabLayout.setVisibility( View.GONE );
      } else {
         exitFullscreen();
      }
   }

   protected void exitFullscreen() {
      VideoInfoViewerApp.getBus().post( new CancelFullscreenEvent() );

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
      mTabLayout.setVisibility( View.VISIBLE );
   }

   @Override
   public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
      if ( mFullscreen ) {
         exitFullscreen();
      }
   }

   @Override
   public void onPageSelected( int position ) {
      mPagerAdapter.getItem( position );
   }

   @Override
   public void onPageScrollStateChanged( int state ) {

   }

   protected class VideoFragmentAdapter extends FragmentStatePagerAdapter {

      protected class FragmentState {
         public Class<? extends Fragment> fragmentClass;
         public Bundle args = new Bundle();
      }

      private HashMap<Video, Fragment> mCurrentFragments = new HashMap<>();

      private HashMap<Video, FragmentState>        mCurrentFragmentState = new HashMap<>();
      private HashMap<Video, Stack<FragmentState>> mBackStack            = new HashMap<>();

      public VideoFragmentAdapter( FragmentManager fragmentManager ) {
         super( fragmentManager );
      }

      @Override
      public Fragment getItem( int index ) {
         Video video = VideoCache.Instance().getVideoByIndex( index );

         Fragment fragment = null;
         if ( mCurrentFragmentState.containsKey( video ) ) {
            FragmentState state = mCurrentFragmentState.get( video );
            try {
               fragment = state.fragmentClass.newInstance();
               fragment.setArguments( state.args );
            } catch ( InstantiationException e ) {
               e.printStackTrace();
            } catch ( IllegalAccessException e ) {
               e.printStackTrace();
            }
         }

         if ( fragment == null ) {
            Bundle args = new Bundle();
            if ( video != null ) {
               args.putInt( Extras.EXTRA_VIDEO_CACHE_ID, video.CacheId );
            }
            fragment = new VideoFragment();
            fragment.setArguments( args );
         }

         FragmentState state = new FragmentState();
         state.fragmentClass = fragment.getClass();
         state.args = fragment.getArguments();

         mCurrentFragments.put( video, fragment );
         mCurrentFragmentState.put( video, state );

         return fragment;
      }

      public boolean interceptOnBackPressed( Video video ) {
         Stack<FragmentState> stack = mBackStack.get( video );
         if ( stack != null && stack.size() > 0 ) {
            FragmentState state = mBackStack.get( video ).pop();
            setFragment( video, state.fragmentClass, state.args, false );
            return true;
         }
         return false;
      }

      public void setFragment( Video video, Class<? extends Fragment> fragmentClass, Bundle args, boolean addCurrentToBackStack ) {
         if ( mCurrentFragments.containsKey( video ) && addCurrentToBackStack ) {
            if ( mBackStack.get( video ) == null ) {
               mBackStack.put( video, new Stack<FragmentState>() );
            }
            Fragment currentFragment = mCurrentFragments.get( video );
            FragmentState previousFragmentState = new FragmentState();
            previousFragmentState.fragmentClass = currentFragment.getClass();
            currentFragment.onSaveInstanceState( previousFragmentState.args );
            mBackStack.get( video ).push( previousFragmentState );
         }

         FragmentState currentFragmentState = new FragmentState();
         currentFragmentState.fragmentClass = fragmentClass;
         currentFragmentState.args = args;

         mCurrentFragmentState.put( video, currentFragmentState );
         notifyDataSetChanged();
      }

      @Override
      public int getCount() {
         return VideoCache.Instance().getVideos().size();
      }

      @Override
      public int getItemPosition( Object object ) {
         if ( object instanceof Fragment ) {
            Fragment fragment = (Fragment) object;
            Bundle args = fragment.getArguments();
            if ( args != null && args.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {
               Video video = VideoCache.Instance().getVideoById( args.getInt( Extras.EXTRA_VIDEO_CACHE_ID ) );
               if ( fragment.getClass() != mCurrentFragmentState.get( video ).fragmentClass ) {
                  return POSITION_NONE;
               }
            }
         }
         return POSITION_UNCHANGED;
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

   public static class RetrieveVideoDetailsTask implements OnSubscribe<Video> {

      private final Uri mUri;

      public RetrieveVideoDetailsTask( Uri uri ) {
         mUri = uri;
      }

      @Override
      public void call( Subscriber<? super Video> subscriber ) {
         try {

            String filePath = UriHelper.getFilePathFromUri( mUri );
            Video video = Video.CreateFromFilePath( filePath );
            subscriber.onNext( video );
         } catch ( Exception e ) {
            subscriber.onError( e );
         }
         subscriber.onCompleted();
      }
   }
}
