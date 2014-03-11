package com.roryhool.videoinfoviewer.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import android.util.Log;

import com.coremedia.iso.boxes.Box;

public class AtomHelper {

   public static HashMap<String, String> sTypeToNameMap = new HashMap<String, String>();

   public static HashMap<String, String> GetBoxProperties( Box box ) {
      HashMap<String, String> properties = new HashMap<String, String>();
      
      Log.d( "this", "Printing box type " + box.getType() );
      for (Field field : box.getClass().getDeclaredFields()) {
         //field.setAccessible(true); // if you want to modify private fields
         
         Log.d( "this", String.format( "------ Printing name %s, type %s", field.getName(), field.getType().getSimpleName() ) );
         /*System.out.println(field.getName()
                  + " - " + field.getType()
                  + " - " + field.get(obj));*/
     }

      return properties;
   }
   public static String GetNameForType( String type ) {
      return sTypeToNameMap.get( type );
   }

   private static final Collection<String> skipList = Arrays.asList(
           "class",
           "boxes",
           "deadBytes",
           "type",
           "userType",
           "size",
           "displayName",
           "contentSize",
           "header",
           "version",
           "flags",
           "isoFile",
           "parent",
           "data",
           "omaDrmData",
           "content",
           "tracks",
           "sampleSizeAtIndex",
           "numOfBytesToFirstChild");

   static {
      sTypeToNameMap.put( "�cmt", "iTunes Comment" );
      sTypeToNameMap.put( "skip", "Free Space Box" );
      sTypeToNameMap.put( "odda", "OMA DRM Content Object Box" );
      sTypeToNameMap.put( "hinf", "Hint Statistics Box" );
      sTypeToNameMap.put( "stsz", "Sample Size Box" );
      sTypeToNameMap.put( "edts", "Edit Box" );
      sTypeToNameMap.put( "ilst", "iTunes Meta Data" );
      sTypeToNameMap.put( "tvsn", "iTunes TV Season Box" );
      sTypeToNameMap.put( "�grp", "iTunes Grouping Box" );
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
      sTypeToNameMap.put( "�too", "iTunes Encoder Box" );
      sTypeToNameMap.put( "stsc", "Sample to Chunk Box" );
      sTypeToNameMap.put( "esds", "ES Descriptor Box" );
      sTypeToNameMap.put( "gnre", "Genre Box" );
      sTypeToNameMap.put( "soal", "iTunes Sort Album Box" );
      sTypeToNameMap.put( "�gen", "iTunes Custom Genre" );
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
      sTypeToNameMap.put( "�alb", "iTunes Album Title" );
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
      sTypeToNameMap.put( "�wrt", "iTunes Track Author" );
      sTypeToNameMap.put( "udta", "User Data Box" );
      sTypeToNameMap.put( "sfID", "iTunes Store Country Type Box" );
      sTypeToNameMap.put( "name", "Apple Name Box" );
      sTypeToNameMap.put( "sbgp", "Sample to Group Box" );
      sTypeToNameMap.put( "�ART", "iTunes Artist" );
      sTypeToNameMap.put( "mfra", "Movie Fragment Random Access Box" );
      sTypeToNameMap.put( "payt", "Payload Type" );
      sTypeToNameMap.put( "albr", "Album Artist Box" );
      sTypeToNameMap.put( "�nam", "iTunes Track Title" );
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
      sTypeToNameMap.put( "�day", "iTunes Recording Year" );
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
   }
}
