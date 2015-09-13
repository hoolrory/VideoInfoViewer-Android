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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coremedia.iso.IsoFile;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.FontManager;
import com.roryhool.videoinfoviewer.utils.FormatUtils;
import com.roryhool.videoinfoviewer.utils.VideoCache;
import com.roryhool.videoinfoviewer.views.DisableableScrollView;
import com.roryhool.videoinfoviewer.views.RobotoTextView;
import com.roryhool.videoinfoviewer.views.VideoPlayerView;
import com.roryhool.videoinfoviewer.views.VideoPlayerView.OnFullscreenListener;

import java.io.IOException;
import java.util.Locale;

public class VideoFragment extends Fragment implements OnClickListener, OnFullscreenListener {

   protected DisableableScrollView mScrollView;
   protected VideoPlayerView       mVideoPlayer;
   protected Button                mButton;
   protected View                  mLoadingProgress;
   protected View                  mVideoPropertiesCard;
   protected Button                mViewAtomButton;
   protected LinearLayout          mVideoPropertiesLayout;

   protected Video mVideo;

   protected RetrieveIsoFileTask mRetrieveIsoFileTask;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
      View view = inflater.inflate( R.layout.fragment_video, container, false );

      mScrollView = (DisableableScrollView) view.findViewById( R.id.scroll_view );
      mVideoPlayer = (VideoPlayerView) view.findViewById( R.id.video_player );
      mButton = (Button) view.findViewById( R.id.view_atom_button );
      mLoadingProgress = view.findViewById( R.id.loading_progress );
      mVideoPropertiesCard = view.findViewById( R.id.video_properties_card );
      mVideoPropertiesLayout = (LinearLayout) view.findViewById( R.id.video_properties_layout );
      mViewAtomButton = (Button) view.findViewById( R.id.view_atom_button );

      mViewAtomButton.setOnClickListener( this );

      mVideoPlayer.addFullscreenListener( this );
      if ( getActivity() instanceof OnFullscreenListener ) {
         mVideoPlayer.addFullscreenListener( (OnFullscreenListener) getActivity() );
      }

      Video video = null;

      Bundle args = getArguments();
      if ( args != null && args.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {
         int cacheId = args.getInt( Extras.EXTRA_VIDEO_CACHE_ID );
         video = VideoCache.Instance().getVideoById( cacheId );
      }

      if ( video != null ) {
         LoadVideo( video );
         mVideoPlayer.setVideo( video );
      }

      return view;
   }

   @Override
   public void onPause() {
      super.onPause();
      onPausedOrHidden();
   }

   @Override
   public void setUserVisibleHint( boolean isVisibleToUser ) {
      super.setUserVisibleHint( isVisibleToUser );

      if ( !getUserVisibleHint() ) {
         onPausedOrHidden();
      }
   }

   protected void onPausedOrHidden() {
      if ( mVideoPlayer != null ) {
         mVideoPlayer.shutdownMediaPlayer();
      }
   }

   @Override
   public void onConfigurationChanged( Configuration newConfig ) {
      super.onConfigurationChanged( newConfig );

      mVideoPlayer.handleResize();
   }

   @Override
   public void onClick( View v ) {
      if ( v.getId() == R.id.view_atom_button ) {
         Activity activity = getActivity();
         if ( activity != null ) {
            Intent intent = new Intent( activity, AtomActivity.class );

            intent.putExtra( Extras.EXTRA_VIDEO_CACHE_ID, mVideo.CacheId );
            startActivity( intent );
         }
      }
   }

   protected void LoadVideo( Video video ) {
      mVideo = video;
      if ( mVideo.getIsoFile() == null ) {
         mRetrieveIsoFileTask = new RetrieveIsoFileTask();
         mRetrieveIsoFileTask.execute( mVideo );
         return;
      }

      Analytics.logEvent( "App Action", "Opened Video in VideoActivity" );

      mLoadingProgress.setVisibility( View.GONE );
      mVideoPropertiesCard.setVisibility( View.VISIBLE );
      mViewAtomButton.setVisibility( View.VISIBLE );

      addKeyValueField( R.string.key_file_name, mVideo.FileName );
      addKeyValueField( R.string.key_resolution, String.format( "%dx%d", mVideo.VideoWidth, mVideo.VideoHeight ) );
      addKeyValueField( R.string.key_mimetype, mVideo.MimeType );
      addKeyValueField( R.string.key_frame_rate, String.format( Locale.US, "%s fps", mVideo.FrameRate ) );
      // addKeyValueField( R.string.key_file_path, video.FilePath );
      // addKeyValueField( R.string.key_format, video.Format );
      // addKeyValueField( R.string.key_format_profile, video.FormatProfile );
      // addKeyValueField( R.string.key_codec_id, video.CodecID );

      String fileSizeString = "N/A";
      if ( mVideo.FileSize != null ) {
         fileSizeString = FormatUtils.FormatFileSizeForDisplay( Float.parseFloat( mVideo.FileSize ) );
      }
      addKeyValueField( R.string.key_file_size, fileSizeString );

      String durationString = "N/A";
      if ( mVideo.Duration != null ) {
         durationString = FormatUtils.FormatTimeForDisplay( Long.parseLong( mVideo.Duration ) );
      }
      addKeyValueField( R.string.key_duration, durationString );

      String kbps = "N/A";
      if ( mVideo.BitRate != null ) {
         kbps = FormatUtils.FormatBpsForDisplay( Long.parseLong( mVideo.BitRate ) );
      }
      addKeyValueField( R.string.key_bitrate, kbps );

      String dateString = FormatUtils.FormatZuluDateTimeForDisplay( mVideo.Date );
      addKeyValueField( R.string.key_date, dateString );
   }

   private void addKeyValueField( int keyStringId, String value ) {
      Activity activity = getActivity();
      if ( activity != null ) {
         LinearLayout keyLayout = (LinearLayout) mVideoPropertiesLayout.getChildAt( 0 );
         LinearLayout valueLayout = (LinearLayout) mVideoPropertiesLayout.getChildAt( 1 );

         RobotoTextView keyView = new RobotoTextView( activity );
         keyView.setTextAppearance( activity, R.style.CardKey );

         RobotoTextView valueView = new RobotoTextView( activity );
         valueView.setTextAppearance( activity, R.style.CardValueOneLine );
         FontManager.get( activity ).setRobotoLight( valueView );

         keyView.setText( keyStringId );
         valueView.setText( value );

         keyLayout.addView( keyView );
         valueLayout.addView( valueView );
      }
   }

   @Override
   public void onFullscreenChanged( boolean fullscreen ) {
      if ( fullscreen ) {
         mScrollView.scrollTo( 0, 0 );
         mScrollView.setEnabled( false );
      } else {
         mScrollView.setEnabled( true );
      }
   }

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
}
