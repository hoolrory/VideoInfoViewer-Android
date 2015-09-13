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

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.coremedia.iso.boxes.Box;
import com.google.android.gms.analytics.HitBuilders;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.atomfragments.AtomInfoFragment;
import com.roryhool.videoinfoviewer.atomfragments.AtomStructureFragment;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.IsoFileCache;
import com.roryhool.videoinfoviewer.utils.VideoCache;

public class AtomActivity extends AppCompatActivity {

   protected AtomStructureFragment mAtomStructureFragment;
   protected AtomInfoFragment      mAtomInfoFragment;

   protected Video mVideo;
   protected Box   mBox;

   @Override
   public void onCreate( Bundle bundle ) {
      super.onCreate( bundle );

      setContentView( R.layout.activity_atom );

      setSupportActionBar( (Toolbar) findViewById( R.id.toolbar ) );
      getSupportActionBar().setDisplayHomeAsUpEnabled( true );
      getSupportActionBar().setDisplayShowHomeEnabled( true );

      Bundle extras = getIntent().getExtras();

      if ( extras == null ) {
         return;
      }

      if ( extras.containsKey( Extras.EXTRA_VIDEO_CACHE_ID ) ) {
         Analytics.logEvent( "App Action", "Opened Video in AtomActivity" );

         mVideo = VideoCache.Instance().getVideoById( extras.getInt( Extras.EXTRA_VIDEO_CACHE_ID ) );

         mAtomStructureFragment = new AtomStructureFragment();
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
   public void onStart() {
      super.onStart();

      VideoInfoViewerApp.getDefaultTracker().setScreenName( AtomActivity.class.getSimpleName() );
      VideoInfoViewerApp.getDefaultTracker().send( new HitBuilders.ScreenViewBuilder().build() );
   }
}
