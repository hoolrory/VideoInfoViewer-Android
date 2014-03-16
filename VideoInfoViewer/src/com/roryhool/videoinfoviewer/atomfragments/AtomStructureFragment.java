package com.roryhool.videoinfoviewer.atomfragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

      IsoFile isoFile = null;
      try {
         isoFile = new IsoFile( video.FilePath );
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      if ( isoFile == null ) {
         return views;
      }

      for ( Box box : isoFile.getBoxes() ) {
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
