package com.comtip.shuffleyoutube;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView YoutubePlaylist;
    Button getPlay,getShuffle,getLoad;

    ArrayList<String> shuffle = new ArrayList<>();
    Set<String> saveShuffleList  = new HashSet<>();

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    // ตัวแปรสำหรับดึง Playlist ได้ทั้งหมด ไม่มีขีดจำกัด ไม่ต้องกำหนดล่วงหน้า

    final String  googleapis =  "https://www.googleapis.com/youtube/v3/playlistItems?";
    String  pageToken = "";
    String  pageTokenBuffer ="YouTUBE";
    final String snippet = "part=snippet&playlistId=";
    String playlistID = "FLwaneH6gg3PLNIDCEmhlzoQ" ; //  สามารถรับ Input จากข้างนอกได้
    final  String keyAPI  =  "&maxResults=50&key=Youtube API Key";
    String queryYTPL;
    String playlistPage;
    String showPlaylist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareWidget();

    }

    //โหลดข้อมูลล่าสุด

    @Override
    protected void onStart() {
        super.onStart();

        sp = this.getSharedPreferences("Save Mode", Context.MODE_PRIVATE);
        editor = sp.edit();

        saveShuffleList = sp.getStringSet("saveShuffleList",null);

        if(saveShuffleList != null) {
            shuffle.addAll(saveShuffleList);
        }
        showPlaylist = sp.getString("showPlaylist","ไม่มีข้อมูล กรุณากดปุ่ม Load");
        YoutubePlaylist.setText(showPlaylist);

    }


    // เซฟข้อมูลก่อนปิดแอพ (รวมถึงตอนไปใใช้หน้า Youtube ด้วย)
    @Override
    protected void onPause() {
        super.onPause();

        if (shuffle.isEmpty())  {
            //nothing
        }else
        {
        saveShuffleList = new HashSet<>(shuffle);
        editor.putStringSet("saveShuffleList", saveShuffleList);
        editor.putString("showPlaylist", showPlaylist);
        editor.commit();
        }

    }



    public void prepareWidget () {
          // แสดงรายชื่อวีดีโอทั้งหมด  // ไว้ตรวจสอบรายชื่อวีดีโอ
        YoutubePlaylist = (TextView) findViewById(R.id.YoutubePlaylist);
        YoutubePlaylist.setMovementMethod(new ScrollingMovementMethod());

           //  ปุ่ม Play

        getPlay = (Button) findViewById(R.id.getPlay);
        getPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (shuffle.isEmpty())  {
                    Toast toast = Toast.makeText(MainActivity.this, "ไม่มีข้อมูล กรุณากดปุ่ม Load", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,40);
                    toast.show();

                }else
                {

                    Intent intent = new Intent(MainActivity.this, PlayYoutube.class);
                    intent.putExtra("shuffle", shuffle);
                    startActivity(intent);
                }

            }
        });

          // ปุ่ม Shuffle
        getShuffle = (Button) findViewById(R.id.getShuffle);
        getShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shuffle.isEmpty())  {
                    Toast toast = Toast.makeText(MainActivity.this, "ไม่มีข้อมูล กรุณากดปุ่ม Load", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,40);
                    toast.show();

                }else
                {
                    Collections.shuffle(shuffle);

                    //แสดงข้อความบอกว่ามีการ Shufflte เกิดขึ้น
                    Toast toast = Toast.makeText(MainActivity.this, "ทำการ Shuffle เรียบร้อย !!!!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,40);
                    toast.show();
                }
            }
        });

          // ปุ่ม Load
        getLoad = (Button) findViewById(R.id.getLoad);
        getLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDB = new AlertDialog.Builder(MainActivity.this);
                alertDB.setTitle("ต้องการโหลดข้อมูล Playlist จาก Youtube หรือไม่");
                alertDB.setMessage("คำเตือน : เวลาในการโหลดขึ้นอยู่กับจำนวนวิดีโอใน Playlist");

                alertDB.setNegativeButton("✘ ไม่ ✘", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          // ปิดหน้าต่าง
                    }
                });

                alertDB.setPositiveButton("✔ ใช่ ✔" ,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // โหลดข้อมูล PlayList จาก youtube
                        new GetYoutubePlaylistAllPage().execute();
                    }
                });
                AlertDialog alert = alertDB.create();
                alert.show();
            }
        });

    }


    // โหลดข้อมูล  Playlist จาก Youtube  จะมีกี่ Page  กี่ Video  มีเท่าไรดึงได้หมด

    private class  GetYoutubePlaylistAllPage  extends AsyncTask<Void,Integer,Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPlaylist = "";
            pageTokenBuffer = "YOUTUBE";
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("กำลังโหลด Playlist จาก Youtube");
            pd.setMessage("รอสักครู่ . . .");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            while (pageTokenBuffer != null) {

                queryYTPL = googleapis + pageToken + snippet + playlistID + keyAPI;

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(queryYTPL).build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful())  {
                        playlistPage =  response.body().string();

                        // เข้ากระบวนการใช้ Gson  ดึง Playlist ทั้งหมด
                        Gson gsonYoutube = new Gson();
                        GsonGetYoutube obj =  gsonYoutube.fromJson(playlistPage,GsonGetYoutube.class);

                        //  ใส่ค่าตัวแปรให้ pageToken  สำหรับใช้ในการดึงข้อมูลหน้าต่อไป
                        pageTokenBuffer = obj.getNextPageToken();
                        if(pageTokenBuffer != null) {
                            pageToken = "pageToken=" + pageTokenBuffer + "&";
                        }

                        //  ใส่ข้อมูลชื่อวีดีโอและรหัสวีดีโอ
                        for (int i = 0; i < obj.getItems().size(); i++) {
                            showPlaylist += "[Title] : " + obj.getItems().get(i).getSnippet().getTitle()
                                    + " [VideoId] : " + obj.getItems().get(i).getSnippet().getResourceId().getVideoId() + "\n";

                            shuffle.add(obj.getItems().get(i).getSnippet().getResourceId().getVideoId());
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            YoutubePlaylist.setText(showPlaylist );

            // shuffle แล้วส่งข้อมูลไปให้หน้า youtube เล่นทันที
            Collections.shuffle(shuffle);

            Intent intent = new Intent(MainActivity.this,PlayYoutube.class);
            intent.putExtra("shuffle",shuffle);
            startActivity(intent);

        }
    }

}
