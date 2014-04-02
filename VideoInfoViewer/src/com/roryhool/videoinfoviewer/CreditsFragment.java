package com.roryhool.videoinfoviewer;

import java.io.IOException;
import java.io.InputStream;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreditsFragment extends Fragment {

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

      View view = inflater.inflate( R.layout.fragment_credits, container, false );

      try {
         InputStream is = view.getContext().getAssets().open( "apache_license_2.txt" );
         int size = is.available();

         byte[] buffer = new byte[size];
         is.read( buffer );
         is.close();

         String str = new String( buffer );
         TextView textView = (TextView) view.findViewById( R.id.apache_license );
         textView.setText( str );
      } catch ( IOException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return view;
   }

}
