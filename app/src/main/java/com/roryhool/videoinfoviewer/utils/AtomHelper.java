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

package com.roryhool.videoinfoviewer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.coremedia.iso.boxes.BitRateBox;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.FreeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.UnknownBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.h264.AvcConfigurationBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.apple.PixelAspectRationAtom;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.roryhool.videoinfoviewer.VideoInfoViewerApp;
import com.roryhool.videoinfoviewer.analytics.Analytics;

public class AtomHelper {

   public static List<Class<?>> sKnownBoxes = new ArrayList<>();

   public static void logEventsForBox( Context context, Box box ) {
      
      if ( sKnownBoxes.contains( box.getClass() ) ) {
         
      } else if ( UnknownBox.class.isInstance( box ) ) {
         Analytics.logEvent( "Video Info", "Found UnknownBox", box.getType() );
      } else {
         Analytics.logEvent( "Video Info", "Found box not in list", box.getType() );
      }
   }

   public static HashMap<String, String> sTypeToNameMap = new HashMap<>();

   public static String getNameForType( String type ) {
      String name = sTypeToNameMap.get( type );
      Analytics.logEvent( "Video Info", "Failed to get name for type ", type );
      return name;
   }

   static {
      sTypeToNameMap.put( "\u00A9cmt", "iTunes Comment" );
      sTypeToNameMap.put( "skip", "Free Space Box" );
      sTypeToNameMap.put( "odda", "OMA DRM Content Object Box" );
      sTypeToNameMap.put( "hinf", "Hint Statistics Box" );
      sTypeToNameMap.put( "stsz", "Sample Size Box" );
      sTypeToNameMap.put( "edts", "Edit Box" );
      sTypeToNameMap.put( "ilst", "iTunes Meta Data" );
      sTypeToNameMap.put( "tvsn", "iTunes TV Season Box" );
      sTypeToNameMap.put( "\u00A9grp", "iTunes Grouping Box" );
      sTypeToNameMap.put( "stss", "Sync Sample Box" );
      sTypeToNameMap.put( "dref", "Data Reference Box" );
      sTypeToNameMap.put( "tvsh", "iTunes TV Show Box" );
      sTypeToNameMap.put( "mvex", "Movie Extends Box" );
      sTypeToNameMap.put( "infu", "Info URL Box" );
      sTypeToNameMap.put( "pmax", "Largest Hint Packet" );
      sTypeToNameMap.put( "ipro", "Item Protection Box" );
      sTypeToNameMap.put( "trak", "Track Box" );
      sTypeToNameMap.put( "kywd", "Keywords Box" );
      sTypeToNameMap.put( "minf", "Media Information Box" );
      sTypeToNameMap.put( "free", "Free Space Box" );
      sTypeToNameMap.put( "icnu", "Icon URI Box" );
      sTypeToNameMap.put( "styp", "Segment Type Box" );
      sTypeToNameMap.put( "traf", "Track Fragment Box" );
      sTypeToNameMap.put( "stsd", "Sample Description Box" );
      sTypeToNameMap.put( "smhd", "Sound Media Header Box" );
      sTypeToNameMap.put( "\u00A9too", "iTunes Encoder Box" );
      sTypeToNameMap.put( "stsc", "Sample to Chunk Box" );
      sTypeToNameMap.put( "esds", "ES Descriptor Box" );
      sTypeToNameMap.put( "gnre", "Genre Box" );
      sTypeToNameMap.put( "soal", "iTunes Sort Album Box" );
      sTypeToNameMap.put( "\u00A9gen", "iTunes Custom Genre" );
      sTypeToNameMap.put( "----", "Some iTunes Generic dunno Box" );
      sTypeToNameMap.put( "purd", "iTunes Purchase Date Box" );
      sTypeToNameMap.put( "data", "iTunes Data Box" );
      sTypeToNameMap.put( "tmax", "Largest Relative Transmission Time" );
      sTypeToNameMap.put( "mfhd", "Movie Fragment Header Box" );
      sTypeToNameMap.put( "desc", "iTunes Description Box" );
      sTypeToNameMap.put( "vmhd", "Video Media Header Box" );
      sTypeToNameMap.put( "tkhd", "Track Header Box" );
      sTypeToNameMap.put( "mehd", "Movie Extends Header Box" );
      sTypeToNameMap.put( "damr", "AMR Specific Box" );
      sTypeToNameMap.put( "mdhd", "Media Header Box" );
      sTypeToNameMap.put( "sinf", "Protection Scheme Information Box" );
      sTypeToNameMap.put( "odtt", "OMA DRM Tranaction Tracking Box" );
      sTypeToNameMap.put( "tves", "iTunes TV Episode Box" );
      sTypeToNameMap.put( "\u00A9alb", "iTunes Album Title" );
      sTypeToNameMap.put( "titl", "Title Box" );
      sTypeToNameMap.put( "meta", "Meta Box" );
      sTypeToNameMap.put( "dscp", "Description Box" );
      sTypeToNameMap.put( "odhe", "OMA DRM Discrete Headers Box" );
      sTypeToNameMap.put( "covr", "iTunes Cover" );
      sTypeToNameMap.put( "tven", "iTunes TV Episode Number Box" );
      sTypeToNameMap.put( "mdat", "Media Data Box" );
      sTypeToNameMap.put( "mean", "Apple Meaning Box" );
      sTypeToNameMap.put( "pgap", "iTunes Gapless Playback" );
      sTypeToNameMap.put( "grpi", "OMA DRM Group ID Box" );
      sTypeToNameMap.put( "wave", "iTunes Proprietary Wave Box" );
      sTypeToNameMap.put( "avcC", "AVC Configuration Box" );
      sTypeToNameMap.put( "tfhd", "Track Fragment Header Box" );
      sTypeToNameMap.put( "tfdt", "Track Fragment Media Decode Time Box" );
      sTypeToNameMap.put( "trex", "Track Extends Box" );
      sTypeToNameMap.put( "tims", "Time Scale Entry" );
      sTypeToNameMap.put( "schm", "Schema Type Box" );
      sTypeToNameMap.put( "hdlr", "Handler Reference Box" );
      sTypeToNameMap.put( "sdtp", "Independent and Disposable Samples Box" );
      sTypeToNameMap.put( "urn ", "Data Entry URN Box" );
      sTypeToNameMap.put( "cpil", "iTunes Compilation Box" );
      sTypeToNameMap.put( "schi", "Scheme Information Box" );
      sTypeToNameMap.put( "odaf", "OMA DRM Access Unit Format Box" );
      sTypeToNameMap.put( "auth", "Author Box" );
      sTypeToNameMap.put( "trkn", "iTunes Track Number" );
      sTypeToNameMap.put( "ctts", "Composition Time to Sample Box" );
      sTypeToNameMap.put( "sdp ", "RTP Track SDP Hint Information" );
      sTypeToNameMap.put( "tref", "Track Reference Box" );
      sTypeToNameMap.put( "rmra", "Apple Reference Movie Box" );
      sTypeToNameMap.put( "lrcu", "Lyrics URI Box" );
      sTypeToNameMap.put( "yrrc", "RecordingYearBox" );
      sTypeToNameMap.put( "mfro", "Movie Fragment Random Access Offset Box" );
      sTypeToNameMap.put( "\u00A9wrt", "iTunes Track Author" );
      sTypeToNameMap.put( "udta", "User Data Box" );
      sTypeToNameMap.put( "sfID", "iTunes Store Country Type Box" );
      sTypeToNameMap.put( "name", "Apple Name Box" );
      sTypeToNameMap.put( "sbgp", "Sample to Group Box" );
      sTypeToNameMap.put( "\u00A9ART", "iTunes Artist" );
      sTypeToNameMap.put( "mfra", "Movie Fragment Random Access Box" );
      sTypeToNameMap.put( "payt", "Payload Type" );
      sTypeToNameMap.put( "albr", "Album Artist Box" );
      sTypeToNameMap.put( "\u00A9nam", "iTunes Track Title" );
      sTypeToNameMap.put( "mdri", "Mutable DRM Information Box" );
      sTypeToNameMap.put( "albm", "Album Box" );
      sTypeToNameMap.put( "odrm", "OMA DRM Container Box" );
      sTypeToNameMap.put( "ldes", "iTunes Synopsis Box" );
      sTypeToNameMap.put( "stco", "Chunk Offset Box" );
      sTypeToNameMap.put( "co64", "Chunk Offset Box (64Bit)" );
      sTypeToNameMap.put( "loci", "Location Information Box" );
      sTypeToNameMap.put( "perf", "Performer Box" );
      sTypeToNameMap.put( "rmdr", "Apple Data Rate Box" );
      sTypeToNameMap.put( "maxr", "Maximum Data Rate" );
      sTypeToNameMap.put( "moov", "Movie Box" );
      sTypeToNameMap.put( "mvhd", "Movie Header Box" );
      sTypeToNameMap.put( "odrb", "OMA DRM Rights Object Box" );
      sTypeToNameMap.put( "aART", "iTunes Album Artist Box" );
      sTypeToNameMap.put( "stik", "iTunes Media Type Box" );
      sTypeToNameMap.put( "cvru", "Cover URI Box" );
      sTypeToNameMap.put( "url ", "Data Entry Url Box" );
      sTypeToNameMap.put( "ftyp", "File Type Box" );
      sTypeToNameMap.put( "cdis", "Content Distributor ID Box" );
      sTypeToNameMap.put( "rdrf", "Apple Data Reference Box" );
      sTypeToNameMap.put( "ohdr", "OMA DRM Common Headers Box" );
      sTypeToNameMap.put( "btrt", "Bit Rate Box" );
      sTypeToNameMap.put( "moof", "Movie Fragment Box" );
      sTypeToNameMap.put( "apID", "iTunes Apple Id Box" );
      sTypeToNameMap.put( "rmda", "Apple Reference Movie Descriptor Box" );
      sTypeToNameMap.put( "tfra", "Track Fragment Random Access Box" );
      sTypeToNameMap.put( "dmax", "Largest Hint Packet Duration" );
      sTypeToNameMap.put( "odkm", "Oma Drm Key Management System Box" );
      sTypeToNameMap.put( "subs", "Sub-Sample Information Box" );
      sTypeToNameMap.put( "tmpo", "iTunes Tempo Box" );
      sTypeToNameMap.put( "alac", "Apple Lossless Codec Params" );
      sTypeToNameMap.put( "frma", "Original Format Box" );
      sTypeToNameMap.put( "hnti", "Hint Information Box" );
      sTypeToNameMap.put( "trun", "Track Fragment Run Box" );
      sTypeToNameMap.put( "stbl", "Sample Table Box" );
      sTypeToNameMap.put( "hmhd", "Hint Media Header Box" );
      sTypeToNameMap.put( "tvnn", "iTunes TV Network Box" );
      sTypeToNameMap.put( "\u00A9day", "iTunes Recording Year" );
      sTypeToNameMap.put( "ccid", "Content Id Sub Box" );
      sTypeToNameMap.put( "dinf", "Data Information Box" );
      sTypeToNameMap.put( "stts", "Decoding Time to Sample Box" );
      sTypeToNameMap.put( "rtng", "iTunes Rating Box" );
      sTypeToNameMap.put( "cprt", "Copyright Box" );
      sTypeToNameMap.put( "akID", "iTunes Store Account Type Box" );
      sTypeToNameMap.put( "elst", "Edit List Box" );
      sTypeToNameMap.put( "tmin", "Smallest Relative Transmission Time" );
      sTypeToNameMap.put( "rtp ", "RTP Movie Hint Information" );
      sTypeToNameMap.put( "clsf", "Classification Box" );
      sTypeToNameMap.put( "clsg", "Composition Shift Least Greatest Atom" );
      sTypeToNameMap.put( "pdin", "Progressive Download Information Box" );
      sTypeToNameMap.put( "gmhd", "Base Media Information Header" );
      sTypeToNameMap.put( "pssh", "Protection System Specific Header Box" );
      sTypeToNameMap.put( "uuid[A2394F525A9B4F14A2446C427C648DF4]", "PIFF Sample Encryption Box" );
      sTypeToNameMap.put( "uuid[8974DBCE7BE74C5184F97148F9882554]", "PIFF Track Encryption Box" );
      sTypeToNameMap.put( "uuid[D4807EF2CA3946958E5426CB9E46A79F]", "PIFF TfrfBox" );
      sTypeToNameMap.put( "uuid[6D1D9B0542D544E680E2141DAFF757B2]", "PIFF TfxdBox" );
      sTypeToNameMap.put( "uuid[D08A4F1810F34A82B6C832D8ABA183D3]", "Protection System Specific Header Box (UUID)" );
      sTypeToNameMap.put( "dtsc", "DTS Core Sample Entry" );
      sTypeToNameMap.put( "dtsh", "DTS-HD Sample Entry" );
      sTypeToNameMap.put( "dtsl", "DTS-HD Lossless Sample Entry" );
      sTypeToNameMap.put( "dtse", "DTS Express (LBR) Sample Entry" );
      sTypeToNameMap.put( "ec-3", "EC3 Sample Entry" );
      sTypeToNameMap.put( "dec3", "EC3 Specific Box" );
      sTypeToNameMap.put( "tmcd", "Apple Time Code Box" );
      sTypeToNameMap.put( "sidx", "Segment Index Box" );
      sTypeToNameMap.put( "mdia", "Media Box" );
      sTypeToNameMap.put( "sgpd", "Sample Group Description Box" );
      sTypeToNameMap.put( "bloc", "UltraViolet Base Location Box" );
      sTypeToNameMap.put( "ainf", "UltraViolet Asset Information Box" );
      sTypeToNameMap.put( "xml ", "XML Box" );
      sTypeToNameMap.put( "iloc", "Item Location Box" );
      sTypeToNameMap.put( "idat", "Item Data Box" );
      sTypeToNameMap.put( "clef", "Track Clean Aperture Dimensions Atom" );
      sTypeToNameMap.put( "prof", "Track Production Aperture Dimensions Atom" );
      sTypeToNameMap.put( "enof", "Track Encoded Pixels Dimensions Atom" );
      sTypeToNameMap.put( "tapt", "Track Aperture Mode Dimensions Atom" );
      sTypeToNameMap.put( "saio", "Sample Auxiliary Information Offsets Box" );
      sTypeToNameMap.put( "senc", "Sample Encryption Box" );
      sTypeToNameMap.put( "saiz", "Sample Auxiliary Information Sizes Box" );
      sTypeToNameMap.put( "disk", "Apple Disk Number Box" );
      sTypeToNameMap.put( "tkrn", "Apple Track Number Box" );
      sTypeToNameMap.put( "default", "Unknown Box" );

      sKnownBoxes.add( FileTypeBox.class );
      sKnownBoxes.add( MovieBox.class );
      sKnownBoxes.add( MovieHeaderBox.class );
      sKnownBoxes.add( TrackBox.class );
      sKnownBoxes.add( TrackHeaderBox.class );
      sKnownBoxes.add( MediaBox.class );
      sKnownBoxes.add( MediaHeaderBox.class );
      sKnownBoxes.add( HandlerBox.class );
      sKnownBoxes.add( MediaInformationBox.class );
      sKnownBoxes.add( VideoMediaHeaderBox.class );
      sKnownBoxes.add( SoundMediaHeaderBox.class );
      sKnownBoxes.add( DataInformationBox.class );
      sKnownBoxes.add( DataReferenceBox.class );
      sKnownBoxes.add( DataEntryUrlBox.class );
      sKnownBoxes.add( SampleTableBox.class );
      sKnownBoxes.add( SampleDescriptionBox.class );
      sKnownBoxes.add( VisualSampleEntry.class );
      sKnownBoxes.add( AudioSampleEntry.class );
      sKnownBoxes.add( AvcConfigurationBox.class );
      sKnownBoxes.add( BitRateBox.class );
      sKnownBoxes.add( ESDescriptorBox.class );
      sKnownBoxes.add( TimeToSampleBox.class );
      sKnownBoxes.add( CompositionTimeToSample.class );
      sKnownBoxes.add( SyncSampleBox.class );
      sKnownBoxes.add( SampleToChunkBox.class );
      sKnownBoxes.add( SampleSizeBox.class );
      sKnownBoxes.add( StaticChunkOffsetBox.class );
      sKnownBoxes.add( UserDataBox.class );
      sKnownBoxes.add( MetaBox.class );
      sKnownBoxes.add( AppleItemListBox.class );
      sKnownBoxes.add( MediaDataBox.class );
      sKnownBoxes.add( EditBox.class );
      sKnownBoxes.add( EditListBox.class );
      sKnownBoxes.add( SampleDependencyTypeBox.class );
      sKnownBoxes.add( PixelAspectRationAtom.class );
      sKnownBoxes.add( FreeBox.class );
   }
}
