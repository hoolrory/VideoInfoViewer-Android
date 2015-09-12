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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.UriHelper;
import com.roryhool.videoinfoviewer.utils.VideoCache;

import java.util.List;

public class RecentVideosFragment extends Fragment implements OnClickListener {

   protected int SELECT_VIDEO_CODE = 100;

   protected ListView             mRecentVideosList;
   protected FloatingActionButton mFab;

   protected RecentVideosAdapter mAdapter;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
      View view = inflater.inflate( R.layout.fragment_recent_videos, container, false );

      mRecentVideosList = (ListView) view.findViewById( R.id.recentVideosList );

      mFab = (FloatingActionButton) view.findViewById( R.id.fab );
      mFab.setOnClickListener( this );

      List<Video> recentVideos = VideoCache.Instance( view.getContext() ).getVideos();

      int onboardingVisibility = recentVideos.size() == 0 ? View.VISIBLE : View.GONE;

      view.findViewById( R.id.onboarding_text ).setVisibility( onboardingVisibility );
      view.findViewById( R.id.onboarding_image ).setVisibility(onboardingVisibility );

      view.findViewById( R.id.recent_videos_card ).setVisibility( recentVideos.size() > 0 ? View.VISIBLE : View.GONE );

      mAdapter = new RecentVideosAdapter( view.getContext(), R.layout.recent_video_layout, recentVideos );
      mRecentVideosList.setAdapter( mAdapter );
      mRecentVideosList.setOnItemClickListener( new OnVideoItemClickListener() );

      setHasOptionsMenu( true );

      return view;
   }

   @Override
   public void onResume() {
      super.onResume();
      mFab.setVisibility( View.VISIBLE );

      mAdapter.clear();
      mAdapter.addAll( VideoCache.Instance( getActivity() ).getVideos() );
   }

   @Override
   public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
      menu.clear();
      inflater.inflate( R.menu.main, menu );
   }

   @Override
   public boolean onOptionsItemSelected( MenuItem item ) {
      switch ( item.getItemId() ) {
      case R.id.action_credits:
         launchCredits();
         return true;
      default:
         return super.onOptionsItemSelected( item );
      }
   }

   @Override
   public void onClick( View v ) {
      if ( v.getId() == R.id.fab ) {
         launchVideoChooser();
      }
   }

   @Override
   public void onActivityResult( int requestCode, int resultCode, Intent data ) {
      FragmentActivity activity = getActivity();
      if ( activity != null && data != null ) {
         Intent intent = new Intent( activity, VideoActivity.class );

         Uri uri = data.getData();

         if ( uri.getScheme().equals( "content" ) ) {
            String path = UriHelper.ContentUriToFilePath( activity, uri );
            intent.setData( Uri.parse( path ) );
         } else {
            intent.setData( data.getData() );
         }

         startActivity( intent );
      }
   }

   private void launchCredits() {
      FragmentActivity activity = getActivity();
      if ( activity != null ) {
         FragmentManager manager = activity.getSupportFragmentManager();
         FragmentTransaction fragTransaction = manager.beginTransaction();
         fragTransaction.add( R.id.fragment_frame, new CreditsFragment() );
         fragTransaction.addToBackStack( "Credits" );
         fragTransaction.commit();

         Analytics.Instance( activity ).LogEvent( "App Action", "Opened Credits" );
      }
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

      FragmentActivity activity = getActivity();
      if ( activity != null ) {
         Analytics.Instance( activity ).LogEvent( "App Action", "Launched Video Chooser" );
      }
   }

   public class RecentVideosAdapter extends ArrayAdapter<Video> {

      protected Context mContext;

      public RecentVideosAdapter( Context context, int resource, List<Video> objects ) {
         super( context, resource, R.id.video_filename, objects );
         mContext = context;
      }

      @Override
      public View getView( int position, View convertView, ViewGroup parent ) {
         View videoView = super.getView( position, convertView, parent );

         Video video = getItem( position );

         ImageView thumbnailView = (ImageView) videoView.findViewById( R.id.video_thumbnail );
         TextView fileNameText = (TextView) videoView.findViewById( R.id.video_filename );
         TextView resolutionText = (TextView) videoView.findViewById( R.id.video_resolution );

         thumbnailView.setImageURI( Uri.parse( video.getThumbnailFilePath( mContext ) ) );
         fileNameText.setText( video.FileName );
         resolutionText.setText( String.format( "%dx%d", video.VideoWidth, video.VideoHeight ) );

         return videoView;
      }
   }

   public class OnVideoItemClickListener implements AdapterView.OnItemClickListener {

      @Override
      public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
         FragmentActivity activity = getActivity();
         if ( activity != null ) {
            Video video = mAdapter.getItem( position );
            Intent intent = new Intent( activity, VideoActivity.class );

            intent.putExtra( Extras.EXTRA_VIDEO_CACHE_ID, video.CacheId );
            startActivity( intent );

            Analytics.Instance( activity ).LogEvent( "App Action", "Selected Video from Recent Videos List" );
         }
      }
   }
}
