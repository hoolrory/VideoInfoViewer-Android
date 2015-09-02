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

import com.coremedia.iso.boxes.Box;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.roryhool.videoinfoviewer.BuildConfig;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.views.BoxInfoView;

public class AtomInfoFragment extends Fragment {

   protected boolean mLoaded = false;

   protected Box mBox;

   protected FrameLayout mAdFrame;
   protected BoxInfoView mBoxInfoView;
   protected AdView mAdView;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
      View view = inflater.inflate( R.layout.fragment_box_info, container, false );

      mAdFrame = (FrameLayout) view.findViewById( R.id.adFrame );

      return view;
   }

   public void setBox( Box box ) {
      mBox = box;
   }

   @Override
   public void onStart() {
      super.onStart();

      if ( !mLoaded ) {
         mLoaded = true;

         Activity activity = getActivity();
         if ( activity != null ) {
            Analytics.Instance( activity ).LogEvent( "Video Info", "Load Atom Info", mBox.getType() );
            if ( !BuildConfig.DEBUG ) {
               setupAds( activity );
            }
         }
         new RetrieveBoxInfoTask().execute( mBox );
      }
   }

   @Override
   public void onStop() {
      super.onStop();

      if ( mBoxInfoView != null ) {
         mBoxInfoView.stop();
      }
   }

   private void setupAds( Context context ) {
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

   public class RetrieveBoxInfoTask extends AsyncTask<Box, Void, BoxInfoView> {

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

         Activity activity = getActivity();
         if ( activity != null ) {
            activity.findViewById( R.id.loading_progress ).setVisibility( View.GONE );
            LinearLayout layout = (LinearLayout) activity.findViewById( R.id.root_layout );
            layout.addView( mBoxInfoView );
         }
      }
   }

   private BoxInfoView LoadBoxInfo( Box box ) {
      BoxInfoView view = null;

      Activity activity = getActivity();
      if ( activity != null ) {
         view = new BoxInfoView( activity );
         view.LoadBox( box );
      }

      return view;
   }
}
