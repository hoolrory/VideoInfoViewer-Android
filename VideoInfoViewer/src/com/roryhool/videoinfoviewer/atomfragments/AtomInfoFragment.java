package com.roryhool.videoinfoviewer.atomfragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.coremedia.iso.boxes.Box;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.views.BoxInfoView;

public class AtomInfoFragment extends Fragment {

   boolean mLoaded = false;

   Box mBox;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

      return inflater.inflate( R.layout.fragment_box_info, container, false );
   }

   public void setBox( Box box ) {
      mBox = box;
   }

   @Override
   public void onStart() {
      super.onStart();

      if ( !mLoaded ) {
         mLoaded = true;
         new RetrieveBoxInfoTask().execute( mBox );
      }
   }

   public class RetrieveBoxInfoTask extends AsyncTask<Box, Void, BoxInfoView> {

      @Override
      protected void onPreExecute() {

      }

      @Override
      protected BoxInfoView doInBackground( Box... boxes ) {
         Box box = boxes[0];

         return LoadBoxInfo( box );
      }

      @Override
      protected void onPostExecute( BoxInfoView view ) {

         LinearLayout layout = (LinearLayout) getActivity().findViewById( R.id.root_layout );
         layout.addView( view );
      }
   }

   private BoxInfoView LoadBoxInfo( Box box ) {

      BoxInfoView view = new BoxInfoView( getActivity() );

      return view;
   }
}
