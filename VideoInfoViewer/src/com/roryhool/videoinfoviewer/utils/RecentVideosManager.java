package com.roryhool.videoinfoviewer.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.roryhool.videoinfoviewer.data.Video;

public class RecentVideosManager {

   private String RECENT_VIDEO_PREFS = "RECENT_VIDEO_PREFS";
   private String RECENT_VIDEO_KEY = "RECENT_VIDEO_KEY";

   private int CurrentCacheId = 0;

   private int MAX_VIDEOS = 5;

   private static RecentVideosManager mInstance = null;

   public static RecentVideosManager Instance( Context context ) {
      if ( mInstance == null ) {
         mInstance = new RecentVideosManager( context );
      }
      return mInstance;
   }

   private Context mContext;

   ArrayList<Video> mVideoList = new ArrayList<Video>();

   public RecentVideosManager( Context context ) {
      mContext = context;
      initiateArray();
   }

   @SuppressWarnings( "unchecked" )
   public List<Video> getRecentVideos() {
      ArrayList<Video> clone = (ArrayList<Video>) mVideoList.clone();
      return clone.subList( 0, Math.min( MAX_VIDEOS, mVideoList.size() ) );
   }

   public void addRecentVideo( Video video ) {
      for ( Video existingVideo : mVideoList ) {
         if ( video.compareTo( existingVideo ) == 0 ) {
            mVideoList.remove( existingVideo );
            break;
         }
      }
      if ( video.CacheId == -1 ) {
         video.CacheId = CurrentCacheId;
         CurrentCacheId += 1;
      }
      mVideoList.add( 0, video );
      saveVideos();
   }

   public Video getRecentVideoById( int id ) {
      for ( Video video : mVideoList ) {
         if ( video.CacheId == id ) {
            return video;
         }
      }

      return null;
   }

   @SuppressWarnings( "unchecked" )
   private void initiateArray() {

      SharedPreferences settings = mContext.getSharedPreferences( RECENT_VIDEO_PREFS, Context.MODE_PRIVATE );
      String json = settings.getString( RECENT_VIDEO_KEY, null );

      if ( json == null ) {
         return;
      }

      Type listOfVideoObject = new TypeToken<ArrayList<Video>>() {}.getType();
      
      Gson gson = new Gson();
      mVideoList = (ArrayList<Video>) gson.fromJson( json, listOfVideoObject );

      for ( Video video : mVideoList ) {
         video.CacheId = CurrentCacheId;
         CurrentCacheId += 1;
      }

      Log.d( "Test", "From json " + json + " got list of length " + mVideoList.size() );
   }

   private void saveVideos() {

      Gson gson = new Gson();
      
      String json = gson.toJson( mVideoList );
      SharedPreferences settings = mContext.getSharedPreferences( RECENT_VIDEO_PREFS, Context.MODE_PRIVATE );
      SharedPreferences.Editor editor = settings.edit();
      editor.putString( RECENT_VIDEO_KEY, json );
      editor.apply();
   }
}
