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

package com.roryhool.videoinfoviewer.atomfragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.coremedia.iso.boxes.Box;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.roryhool.videoinfoviewer.BuildConfig;
import com.roryhool.videoinfoviewer.Extras;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.utils.IsoFileCache;
import com.roryhool.videoinfoviewer.views.BoxInfoView;

public class AtomInfoFragment extends Fragment {

   protected boolean mLoaded = false;

   protected Box mBox;

   protected LinearLayout mRootLayout;
   protected ProgressBar mLoadingProgress;
   protected FrameLayout mAdFrame;
   protected BoxInfoView mBoxInfoView;
   protected AdView      mAdView;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
      View view = inflater.inflate( R.layout.fragment_box_info, container, false );

      if ( savedInstanceState == null ) {
         Analytics.logEvent( "App Action", "Opened Box in AtomInfoFragment" );
         mBox = getBox( getArguments() );
      } else {
         mBox = getBox( savedInstanceState );
      }

      mRootLayout = (LinearLayout) view.findViewById( R.id.root_layout );
      mLoadingProgress = (ProgressBar) view.findViewById( R.id.loading_progress );
      mAdFrame = (FrameLayout) view.findViewById( R.id.adFrame );

      return view;
   }

   @Override
   public void onStart() {
      super.onStart();

      if ( !mLoaded ) {
         mLoaded = true;

         Activity activity = getActivity();
         if ( activity != null ) {
            Analytics.logEvent( "Video Info", "Load Atom Info", mBox.getType() );
            if ( !BuildConfig.DEBUG ) {
               setupAds( activity );
            }
         }
         new RetrieveBoxInfoTask().execute( mBox );
      }
   }

   @Override
   public void onSaveInstanceState( Bundle outState ) {
      super.onSaveInstanceState( outState );
      outState.putAll( getArguments() );
   }

   @Override
   public void onStop() {
      super.onStop();

      if ( mBoxInfoView != null ) {
         mBoxInfoView.stop();
      }
   }

   protected Box getBox( Bundle bundle ) {
      return IsoFileCache.Instance().getBox( bundle.getInt( Extras.EXTRA_BOX_ID ) );
   }

   protected void setupAds( Context context ) {
      String admobAdUnitId = getString( R.string.atom_info_admob_ad_unit_id );

      if ( admobAdUnitId != null && !admobAdUnitId.equals( ( "" ) ) ) {
         mAdView = new AdView( context );
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

   protected BoxInfoView LoadBoxInfo( Box box ) {
      BoxInfoView view = null;

      Activity activity = getActivity();
      if ( activity != null ) {
         view = new BoxInfoView( activity );
         view.LoadBox( box );
      }

      return view;
   }

   protected class RetrieveBoxInfoTask extends AsyncTask<Box, Void, BoxInfoView> {

      @Override
      protected void onPreExecute() {
      }

      @Override
      protected BoxInfoView doInBackground( Box... boxes ) {
         return LoadBoxInfo( boxes[0] );
      }

      @Override
      protected void onPostExecute( BoxInfoView view ) {
         mBoxInfoView = view;

         mLoadingProgress.setVisibility( View.GONE );
         mRootLayout.addView( mBoxInfoView );
      }
   }
}
