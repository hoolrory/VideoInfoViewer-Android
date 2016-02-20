/**
   Copyright (c) 2016 Rory Hool
   
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

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreditsFragment extends Fragment {

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
      View view = inflater.inflate( R.layout.fragment_credits, container, false );

      setHasOptionsMenu( true );

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
         e.printStackTrace();
      }

      TextView iconCreditText = (TextView) view.findViewById( R.id.icon_credit );
      iconCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView mp4parserCreditText = (TextView) view.findViewById( R.id.mp4parser_credit );
      mp4parserCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView gsonCreditText = (TextView) view.findViewById( R.id.gson_credit );
      gsonCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView reactiveXCreditText = (TextView) view.findViewById( R.id.reactivex_credit );
      reactiveXCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView reactiveXAndroidCreditText = (TextView) view.findViewById( R.id.reactivexandroid_credit );
      reactiveXAndroidCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView ottoCreditText = (TextView) view.findViewById( R.id.otto_credit );
      ottoCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView retrolambdaCreditText = (TextView) view.findViewById( R.id.retrolambda_credit );
      retrolambdaCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      TextView gradleRetrolambdaCreditText = (TextView) view.findViewById( R.id.gradle_retrolambda_credit );
      gradleRetrolambdaCreditText.setMovementMethod( LinkMovementMethod.getInstance() );

      return view;
   }

   @Override
   public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
      super.onCreateOptionsMenu( menu, inflater );
      menu.clear();
   }
}
