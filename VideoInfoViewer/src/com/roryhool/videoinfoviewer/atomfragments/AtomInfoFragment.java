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
      view.LoadBox( box );

      return view;
   }
}
