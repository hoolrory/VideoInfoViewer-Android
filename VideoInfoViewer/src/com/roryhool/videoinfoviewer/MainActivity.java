package com.roryhool.videoinfoviewer;

import java.util.List;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.RecentVideosManager;

@EActivity( R.layout.activity_main )
public class MainActivity extends Activity {


   private int SELECT_VIDEO_CODE = 100;

   @ViewById( R.id.recentVideosList )
   ListView mRecentVideosList;

   RecentVideosAdapter mAdapter;

   @SuppressWarnings( "deprecation" )
   @Override
   public void onStart() {
      super.onStart();
      mAdapter = new RecentVideosAdapter( this, R.layout.recent_video_layout, RecentVideosManager.Instance( this ).getRecentVideos() );
      mRecentVideosList.setAdapter( mAdapter );
      mRecentVideosList.setOnItemClickListener( new OnVideoItemClickListener() );
   }

   @Override
   public boolean onCreateOptionsMenu( Menu menu ) {
      getMenuInflater().inflate( R.menu.main, menu );
      return true;
   }

   @Override
   public boolean onOptionsItemSelected( MenuItem item ) {
      switch ( item.getItemId() ) {
      case R.id.action_select_video:
         launchVideoChooser();
         return true;
      default:
         return super.onOptionsItemSelected( item );
      }
   }

   private void launchVideoChooser() {
      Intent intent = new Intent();
      intent.addCategory( Intent.CATEGORY_OPENABLE );
      intent.setType( "video/*" );
      intent.setAction( Intent.ACTION_GET_CONTENT );
      Intent chooser = Intent.createChooser( intent, getString( R.string.select_video ) );
      startActivityForResult( chooser, SELECT_VIDEO_CODE );
   }

   @Override
   public void onActivityResult( int requestCode, int resultCode, Intent data ) {
      if ( requestCode == SELECT_VIDEO_CODE && data != null ) {
         Intent intent = new Intent( this, VideoActivity_.class );
         intent.setData( data.getData() );
         startActivity(intent);
      }
   }

   public class RecentVideosAdapter extends ArrayAdapter<Video> {

      public RecentVideosAdapter( Context context, int resource, List<Video> objects ) {
         super( context, resource, objects );
      }

      @Override
      public View getView( int position, View convertView, ViewGroup parent ) {

         Video video = getItem( position );

         RelativeLayout videoView = (RelativeLayout) convertView;

         if ( videoView == null ) {
            videoView = (RelativeLayout) View.inflate( MainActivity.this, R.layout.recent_video_layout, null );
         }

         ImageView thumbnailView = (ImageView) videoView.findViewById( R.id.video_thumbnail );
         TextView fileNameText = (TextView) videoView.findViewById( R.id.video_filename );
         TextView resolutionText = (TextView) videoView.findViewById( R.id.video_resolution );

         thumbnailView.setImageURI( Uri.parse( video.getThumbnailFilePath( MainActivity.this ) ) );
         fileNameText.setText( video.FileName );
         resolutionText.setText( String.format( "%dx%d", video.VideoWidth, video.VideoHeight ) );

         return videoView;
      }

   }
   
   public class OnVideoItemClickListener implements AdapterView.OnItemClickListener {

      @Override
      public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
         Video video = mAdapter.getItem( position );
         Intent intent = new Intent( MainActivity.this, VideoActivity_.class );

         Gson gson = new Gson();
         intent.putExtra( VideoActivity.EXTRA_VIDEO_JSON, gson.toJson( video ) );
         startActivity( intent );
      }
      
   }

}
