/**
 * Copyright (c) 2016 Rory Hool
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.roryhool.videoinfoviewer.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.roryhool.videoinfoviewer.VideoInfoViewerApp;
import com.roryhool.videoinfoviewer.data.Video;

public class VideoCache {

   private String RECENT_VIDEO_PREFS = "RECENT_VIDEO_PREFS";
   private String RECENT_VIDEO_KEY   = "RECENT_VIDEO_KEY";

   private int CurrentCacheId = 0;

   private int MAX_VIDEOS = 5;

   private static VideoCache mInstance = null;

   public static VideoCache Instance() {
      if ( mInstance == null ) {
         mInstance = new VideoCache();
      }
      return mInstance;
   }

   ArrayList<Video> mVideoList = new ArrayList<>();

   public VideoCache() {
      initiateArray();
   }

   @SuppressWarnings( "unchecked" )
   public List<Video> getVideos() {
      ArrayList<Video> clone = (ArrayList<Video>) mVideoList.clone();
      return clone.subList( 0, Math.min( MAX_VIDEOS, mVideoList.size() ) );
   }

   public Video getVideoByIndex( int index ) {
      if ( index < mVideoList.size() ) {
         return mVideoList.get( index );
      }

      return null;
   }

   public void addVideo( Video video ) {
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

   public Video getVideoById( int id ) {
      for ( Video video : mVideoList ) {
         if ( video.CacheId == id ) {
            return video;
         }
      }

      return null;
   }

   @SuppressWarnings( "unchecked" )
   private void initiateArray() {
      SharedPreferences settings = VideoInfoViewerApp.getContext().getSharedPreferences( RECENT_VIDEO_PREFS, Context.MODE_PRIVATE );
      String json = settings.getString( RECENT_VIDEO_KEY, null );

      if ( json == null ) {
         return;
      }

      Type listOfVideoObject = new TypeToken<ArrayList<Video>>() {
      }.getType();

      Gson gson = new Gson();
      mVideoList = gson.fromJson( json, listOfVideoObject );

      for ( Video video : mVideoList ) {
         video.CacheId = CurrentCacheId;
         CurrentCacheId += 1;
      }

      Log.d( "Test", "From json " + json + " got list of length " + mVideoList.size() );
   }

   private void saveVideos() {
      String json = new Gson().toJson( mVideoList );
      SharedPreferences settings = VideoInfoViewerApp.getContext().getSharedPreferences( RECENT_VIDEO_PREFS, Context.MODE_PRIVATE );
      SharedPreferences.Editor editor = settings.edit();
      editor.putString( RECENT_VIDEO_KEY, json );
      editor.apply();
   }
}
