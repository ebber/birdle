package com.birdle.pranay.birdle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;

import org.cmc.music.metadata.ImageData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by pranaygp on 9/1/15.
 */
public class Song {

    // DATA

    private long ID;

    private static SongDBHelper mDBHelper;

    private String YTN;
    private String YTURL;
    private String title;
    private String artist;
    private String album;

    private static final String TAG = "Birdle";

    // CONSTRUCTORS

    public Song(Context context, String YTURL){
        // Initialize DB Helper
        mDBHelper = new SongDBHelper(context);

        this.YTURL = YTURL;
        fetchYTN();
    }

    public Song(Context context, long id){
        // Initialize DB Helper
        mDBHelper = new SongDBHelper(context);

        // Initialize song with data from DB
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String testColumns = SongContract.SongSchema._ID + " = ?";
        String[] testColumnsArgs = {
                String.valueOf(id)
        };

        Cursor c = db.query(SongContract.SongSchema.TABLE_NAME, SongContract.SongSchema.COLUMN_NAMES, testColumns, testColumnsArgs, null, null, null);
        assignFieldsFromCursor(c);
        c.close();
    }

    // PUBLIC FUNCTIONS

    public void download(NotificationHelper mNotificationHelper) throws IOException{

        Integer BUFFER_SIZE = 262144;

        // fetchYTN
        fetchYTN();
        // Download to staging area

        //set the download URL, a url that points to a file on the internet
        //this is the file to be downloaded

        URL url = new URL("http://youtubeinmp3.com/fetch/?video=" + YTURL);

        //create the new connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //set up some things on the connection
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);

        InputStream inputStream = urlConnection.getInputStream();
        urlConnection.connect();

        //set the path where we want to save the file
        //in this case, going to save it on the birdle directory under MUSIC

        File BirdleDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Birdle/");
        BirdleDirectory.mkdirs(); //attempt to make the directory

        //create a new temp STAGING file, specifying the path, and the filename
        //which we want to save the file as.
        File file = new File(BirdleDirectory, YTN + "_temp.mp3");
        Log.i("Birdle", "Song name: " + YTN);

        //this will be used to write the downloaded data into the file we created
        FileOutputStream fileOutput = new FileOutputStream(file);

        //this will be used in reading the data from the internet

        //this is the total size of the file
        int totalSize = urlConnection.getContentLength();
        //variable to store total downloaded bytes
        int downloadedSize = 0;

        //create a buffer...
        byte[] buffer = new byte[BUFFER_SIZE];
        int bufferLength = 0; //used to store a temporary size of the buffer

        //now, read through the input buffer and write the contents to the file
        while ((bufferLength = inputStream.read(buffer)) > 0) {
            //add the data in the buffer to the file in the file output stream (the file on the sd card
            fileOutput.write(buffer, 0, bufferLength);
            //add up the size so we know how much is downloaded
            downloadedSize += bufferLength;
            //this is where you would do something to report the prgress, like this maybe
            //updateProgress(downloadedSize, totalSize);
            onProgressUpdate(mNotificationHelper, (int) ((downloadedSize * 100) / totalSize));

        }
        //close the output stream when done
        fileOutput.close();

        // Update file field

    }

    public void pullMeta(){
        // Call getMetaFromPuller here
        // Assign its values to the class fields
        // Call saveMetaToDB
    }

    public static Song[] list(){
        // Returns array of Songs from DB
        Cursor songsCursor = getListOfItems();
        Song[] songList;

        return null;
    }

    public static ArrayList<Song> listAsArrayList(){
        // Returns an ArrayList containing a list of all the songs from the database
        Cursor songsCursor = getListOfItems();
        return null;
    }

    public static Cursor listAsCursor(){
        // Returns a Cursor containing a list of all the songs from the database
        Cursor songsCursor = getListOfItems();
        return songsCursor;
    }

    public void delete(){
        // Delete meta from db
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String[] whereArgs = {String.valueOf(ID)};

        db.delete(SongContract.SongSchema.TABLE_NAME, SongContract.SongSchema._ID + "=?",whereArgs);
    }

    public void save(){
        //helper to call saveMetaToDB and then save data on file
        saveMetaToDB();
    }

    public void saveMetaToDB(){
        // Save meta to SQL Database using current fields
    }

    // INTERNAL HELPER FUNCTIONS

    protected void onProgressUpdate(NotificationHelper mNotificationHelper, Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
        Log.i(TAG, String.valueOf(progress[0]));
    }

    private void assignFieldsFromCursor(Cursor c){
        c.moveToFirst();
        this.ID = c.getInt(c.getColumnIndexOrThrow(SongContract.SongSchema._ID));;
        this.YTN = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_YTN));
        this.YTURL = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_YTURL));
        this.title = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_TITLE));
        this.artist = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_ARTIST));
        this.album = c.getString(c.getColumnIndexOrThrow(SongContract.SongSchema.COLUMN_NAME_SONG_ALBUM));
    }

    private static Cursor getListOfItems(){
        // Get List of songs as cursor
        return null;
    }

    private ArrayMap<String, String> getMetaFromPuller(){
        // Uses the metadataPuller class to return an ArrayMap
        // Example: return metadataPuller.pull(YTN);
        return null;
    }

    private void saveMetaToDB(ArrayMap<String, String> metaArrayMap){
        // Parse and save meta to SQL Database
    }


    private void fetchYTN(){
        // Call youtubeinmp3 advanced API to get YTN using YTURL
    }

    private void saveMetaToFile(){
        // Save meta from the object to an mp3 file and deletes the temporary birdle file
    }

    // GETTERS AND SETTERS


    public long getID() {
        return ID;
    }

    public File getFile() {
        File BirdleDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Birdle/");
        File file = new File(BirdleDirectory, YTN + "_temp.mp3");
        return file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public ImageData getAlbumArt() {
        // Get File and return it as ImageData
        return null;
    }

    public void setAlbumArt(String albumArt) {
        // Parse String as URL and save the downloaded image to File
    }

}