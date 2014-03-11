package com.roryhool.videoinfoviewer;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.coremedia.iso.boxes.Box;
import com.google.gson.Gson;
import com.roryhool.videoinfoviewer.atomfragments.AtomInfoFragment;
import com.roryhool.videoinfoviewer.atomfragments.AtomStructureFragment;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.IsoFileCache;
import com.roryhool.videoinfoviewer.views.BoxView.BoxViewOnClickListener;

@Fullscreen
@EActivity( R.layout.activity_atom )
public class AtomActivity extends FragmentActivity implements BoxViewOnClickListener {

   public static final String EXTRA_VIDEO_JSON = "EXTRA_VIDEO_JSON";

   public static final String EXTRA_BOX_ID = "EXTRA_BOX_ID";

   // @FragmentById( R.id.atom_fragment )
   AtomStructureFragment mAtomStructureFragment;

   AtomInfoFragment mAtomInfoFragment;

   Video mVideo;

   Box mBox;

   @Override
   public void onCreate( Bundle bundle ) {
      super.onCreate( bundle );

      Bundle extras = getIntent().getExtras();

      if ( extras == null ) {
         return;
      }

      if ( extras.containsKey( EXTRA_VIDEO_JSON ) ) {
         String videoJSON = extras.getString( EXTRA_VIDEO_JSON );
         mVideo = new Gson().fromJson( videoJSON, Video.class );

         mAtomStructureFragment = new AtomStructureFragment();
         mAtomStructureFragment.setBoxViewOnClickListener( this );
         mAtomStructureFragment.setVideo( mVideo );

         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         transaction.replace( R.id.fragment_container, mAtomStructureFragment );
         transaction.commit();
      } else if ( extras.containsKey( EXTRA_BOX_ID ) ) {
         int boxId = extras.getInt( EXTRA_BOX_ID );
         mBox = IsoFileCache.Instance().getBox( boxId );

         mAtomInfoFragment = new AtomInfoFragment();
         mAtomInfoFragment.setBox( mBox );

         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         transaction.replace( R.id.fragment_container, mAtomInfoFragment );
         transaction.commit();
      }
   }

   @Override
   public void onClickInfo( Box box ) {
      Intent intent = new Intent( this, AtomActivity_.class );

      intent.putExtra( AtomActivity.EXTRA_BOX_ID, IsoFileCache.Instance().cacheBox( box ) );
      startActivity( intent );
   }

}
