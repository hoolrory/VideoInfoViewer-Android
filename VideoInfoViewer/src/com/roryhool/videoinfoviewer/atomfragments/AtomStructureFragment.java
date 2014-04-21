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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.AtomHelper;
import com.roryhool.videoinfoviewer.views.BoxView;
import com.roryhool.videoinfoviewer.views.BoxView.BoxViewOnClickListener;

public class AtomStructureFragment extends Fragment {

   BoxViewOnClickListener mBoxViewOnClickListener;

   Video mVideo;

   boolean mLoaded;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

      return inflater.inflate( R.layout.fragment_atom_structure, container, false );
   }

   @Override
   public void onStart() {
      super.onStart();

      if ( !mLoaded ) {
         mLoaded = true;
         new RetrieveAtomStructureTask().execute( mVideo );
      }
   }

   public void setVideo( Video video ) {
      mVideo = video;
   }

   public class RetrieveAtomStructureTask extends AsyncTask<Video, Void, List<BoxView>> {

      @Override
      protected void onPreExecute() {

      }

      @Override
      protected List<BoxView> doInBackground( Video... videos ) {
         Video video = videos[0];

         return LoadBoxStructure( video );
      }

      @Override
      protected void onPostExecute( List<BoxView> boxViews ) {

         Activity activity = getActivity();
         if ( activity != null ) {
            activity.findViewById( R.id.loading_progress ).setVisibility( View.GONE );
         }
         for ( BoxView boxView : boxViews ) {

            LinearLayout layout = (LinearLayout) getActivity().findViewById( R.id.atom_layout );
            layout.addView( boxView );
         }
      }
   }

   public void setBoxViewOnClickListener( BoxViewOnClickListener listener ) {
      mBoxViewOnClickListener = listener;
   }

   private List<BoxView> LoadBoxStructure( Video video ) {
      
      List<BoxView> views = new ArrayList<BoxView>();

      /*
       * IsoFile isoFile = null; try { isoFile = new IsoFile( video.FilePath ); } catch ( IOException e ) { e.printStackTrace(); }
       */

      IsoFile isoFile = video.getIsoFile();
      if ( isoFile == null ) {
         return views;
      }

      for ( Box box : isoFile.getBoxes() ) {

         Activity activity = getActivity();
         if ( activity != null ) {
            AtomHelper.LogEventsForBox( activity, box );
         }

         BoxView view = BoxView.CreateBoxViewAndChildren( getActivity(), mBoxViewOnClickListener, box );
         view.setTag( isoFile );
         views.add( view );
      }

      // TODO: Close this somewhere, cache issues?
      /*
       * try {
       * 
       * isoFile.close(); } catch ( IOException e ) { // TODO Auto-generated catch block e.printStackTrace(); }
       */

      return views;
   }
}
