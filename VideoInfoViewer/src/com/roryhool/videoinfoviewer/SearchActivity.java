package com.roryhool.videoinfoviewer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SearchActivity extends Activity {

   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );

      setContentView( R.layout.activity_video );
   }

   @Override
   protected void onNewIntent( Intent intent ) {
      handleIntent( intent );
   }

   private void handleIntent( Intent intent ) {

      if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) ) {
         String query = intent.getStringExtra( SearchManager.QUERY );
         Log.d( "test", "XAJM - search query " + query );
      }
   }
}
