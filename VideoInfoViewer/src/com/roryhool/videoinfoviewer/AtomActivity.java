package com.roryhool.videoinfoviewer;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.Fullscreen;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.roryhool.videoinfoviewer.atomfragments.AtomStructureFragment;
import com.roryhool.videoinfoviewer.data.Video;

@Fullscreen
@EActivity( R.layout.activity_atom )
public class AtomActivity extends FragmentActivity {

   public static final String EXTRA_VIDEO_JSON = "EXTRA_VIDEO_JSON";

   @FragmentById( R.id.atom_fragment )
   AtomStructureFragment mAtomFragment;

   Video mVideo;

   @Override
   public void onStart() {
      super.onStart();

      Bundle extras = getIntent().getExtras();

      Video video = null;

      if ( extras != null ) {
         if ( extras.containsKey( EXTRA_VIDEO_JSON ) ) {
            String videoJSON = extras.getString( EXTRA_VIDEO_JSON );
            video = new Gson().fromJson( videoJSON, Video.class );
         }
      }

      if ( video != null ) {
         LoadVideo( video );
      }
   }

   private void LoadVideo( Video video ) {
      mVideo = video;

      mAtomFragment.LoadVideo( mVideo );
   }

}
