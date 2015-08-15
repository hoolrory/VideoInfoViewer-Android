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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.coremedia.iso.boxes.Box;
import com.google.android.gms.analytics.HitBuilders;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.atomfragments.AtomInfoFragment;
import com.roryhool.videoinfoviewer.atomfragments.AtomStructureFragment;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.IsoFileCache;
import com.roryhool.videoinfoviewer.utils.VideoCache;
import com.roryhool.videoinfoviewer.views.BoxView.BoxViewOnClickListener;

public class AtomActivity extends FragmentActivity implements BoxViewOnClickListener {

   AtomStructureFragment mAtomStructureFragment;

   AtomInfoFragment mAtomInfoFragment;

   Video mVideo;

   Box mBox;

   @Override
   public void onCreate( Bundle bundle ) {
      super.onCreate( bundle );

      setContentView( R.layout.activity_atom );

      Bundle extras = getIntent().getExtras();

      if ( extras == null ) {
         return;
      }

      if ( extras.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {

         Analytics.Instance( this ).LogEvent( "App Action", "Opened Video in AtomActivity" );

         mVideo = VideoCache.Instance( this ).getVideoById( extras.getInt( Extras.EXTRA_VIDEO_CACHE_ID ) );

         mAtomStructureFragment = new AtomStructureFragment();
         mAtomStructureFragment.setBoxViewOnClickListener( this );
         mAtomStructureFragment.setVideo( mVideo );

         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         transaction.replace( R.id.fragment_container, mAtomStructureFragment );
         transaction.commit();
      } else if ( extras.containsKey( Extras.EXTRA_BOX_ID ) ) {
         int boxId = extras.getInt( Extras.EXTRA_BOX_ID );
         mBox = IsoFileCache.Instance().getBox( boxId );

         mAtomInfoFragment = new AtomInfoFragment();
         mAtomInfoFragment.setBox( mBox );

         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         transaction.replace( R.id.fragment_container, mAtomInfoFragment );
         transaction.commit();
      }
   }

   @Override
   public boolean onMenuItemSelected( int featureId, MenuItem item ) {

      int itemId = item.getItemId();
      switch ( itemId ) {
      case android.R.id.home:
         super.onBackPressed();
         break;
      }

      return true;
   }

   @Override
   public void onStart() {
      super.onStart();

      VideoInfoViewerApp.getDefaultTracker().setScreenName( AtomActivity.class.getSimpleName() );
      VideoInfoViewerApp.getDefaultTracker().send( new HitBuilders.ScreenViewBuilder().build() );

      getActionBar().setDisplayHomeAsUpEnabled( true );
      getActionBar().setDisplayShowHomeEnabled( true );
   }

   @Override
   public void onClickInfo( Box box ) {
      Intent intent = new Intent( this, AtomActivity.class );

      intent.putExtra( Extras.EXTRA_BOX_ID, IsoFileCache.Instance().cacheBox( box ) );
      startActivity( intent );
   }

}
