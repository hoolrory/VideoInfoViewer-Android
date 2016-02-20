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

package com.roryhool.videoinfoviewer;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;

import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.search.SearchItem;

public class SearchFragment extends Fragment implements OnQueryTextListener {

   protected ListView mListView;

   protected SearchItemAdapter mAdapter;

   protected Video mVideo;

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

      View view = inflater.inflate( R.layout.fragment_search, container, false );
      mListView = (ListView) view.findViewById( R.id.search_items_list );

      SearchItemAdapterPrepareTask task = new SearchItemAdapterPrepareTask();
      task.execute( mVideo );
      return view;
   }

   public void setVideo( Video video ) {
      mVideo = video;
   }

   @Override
   public boolean onQueryTextSubmit( String query ) {
      return false;
   }

   @Override
   public boolean onQueryTextChange( String newText ) {
      return false;
   }

   private class SearchItemAdapterPrepareTask extends AsyncTask<Video, Void, List<SearchItem>> {

      @Override
      protected List<SearchItem> doInBackground( Video... params ) {
         List<SearchItem> items = new ArrayList<>();

         return items;
      }

      @Override
      protected void onPostExecute( List<SearchItem> items ) {

         mAdapter = new SearchItemAdapter( VideoInfoViewerApp.getContext(), 0, items );
         mListView.setAdapter( mAdapter );
      }
   }

   private class SearchItemAdapter extends ArrayAdapter<SearchItem> {

      public SearchItemAdapter( Context context, int textViewResourceId, List<SearchItem> objects ) {
         super( context, textViewResourceId, objects );

      }

      @Override
      public long getItemId( int position ) {
         return -1;
      }

      @Override
      public boolean hasStableIds() {
         return true;
      }
   }
}
