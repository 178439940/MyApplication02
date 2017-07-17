package com.example.howard.mp3player.Internet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.howard.mp3player.Bean.SongBySearchBean;
import com.example.howard.mp3player.InterAPItools.GetListBySearchSong;
import com.example.howard.mp3player.InterAPItools.GetListBySingerSong;
import com.example.howard.mp3player.MyApplication;
import com.example.howard.mp3player.R;
import com.example.howard.mp3player.Service.MusicPlayerService;
import com.example.howard.mp3player.Service.NetSongDownloadServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingerSongSearchActivity extends Activity {
    private EditText singersearchInfo;
    private ImageButton singersearchBtn;
    private ListView singersongBySearch;
    private SingerSongListViewAdapter myAdapter;
    public AdapterView.OnItemClickListener listener;
    private String searchstr=null;
    private GetListBySingerSong getListBySingerSong;
    private List<SongBySearchBean> singersonglist=new ArrayList<>();
    private MyApplication myApplication;
    private MusicPlayerService musicPlayerService;
    private int mypesition=-1;
    private List<String> songidlist=new ArrayList<>();
    private InternetSearchTab internetSearchTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singer_song_search);
        myApplication= (MyApplication) getApplication();
        musicPlayerService=new MusicPlayerService();
        internetSearchTab=myApplication.internetSearchTab;
        setView();
        myAdapter = new SingerSongListViewAdapter(this,singersonglist, new SingerSongListViewAdapter.Callback() {
            @Override
            public void downloadsingersongcallback(String songid) {
                dialog(songid);
            }
        });
        singersearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String string=searchInfo.getText().toString();
//                searchstr= URLDecoder.decode(string ,"UTF-8");
                searchstr=singersearchInfo.getText().toString();
                if (!(searchstr ==null)){
                    recieveList(searchstr);

                }
            }
        });
    }

    //刷新ListView
    private void refreshListView() {
        musicPlayerService=myApplication.musicPlayerService;
        if (musicPlayerService!=null){
            musicPlayerService.pesition=-1;
        }
        singersongBySearch.setAdapter(myAdapter);
        listener= singerresetLickListener();
        singersongBySearch.setOnItemClickListener(listener);
        myAdapter.notifyDataSetChanged();
    }

    public static final int CHANG_TO_PLAY=321;
    public static final int CHANG_TO_PAUSE=322;

    private AdapterView.OnItemClickListener singerresetLickListener(){
        AdapterView.OnItemClickListener songListclickListener = new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Log.e("click","true");
                Intent intent = new Intent(SingerSongSearchActivity.this,
                        MusicPlayerService.class);
                intent.putExtra("Activity","singer");
                intent.putExtra("pesition", arg2);
                musicPlayerService=myApplication.musicPlayerService;
                internetSearchTab=myApplication.internetSearchTab;
                if (musicPlayerService==null){
                    intent.putExtra("what", "play");
                    Message message=new Message();
                    message.what=CHANG_TO_PAUSE;
                    internetSearchTab.interHandler.sendMessage(message);
                }else {
                    mypesition=musicPlayerService.pesition;
                    if (mypesition!=arg2){
                        mypesition=arg2;
                        intent.putExtra("what", "play");
                        Message message=new Message();
                        message.what=CHANG_TO_PAUSE;
                        internetSearchTab.interHandler.sendMessage(message);
                    }else if (mypesition==arg2&&!musicPlayerService.mediaPlayer.isPlaying()){
                        intent.putExtra("what","restart");
                        Message message=new Message();
                        message.what=CHANG_TO_PAUSE;
                        internetSearchTab.interHandler.sendMessage(message);
                    }else {
                        intent.putExtra("what", "pause");
                        Message message=new Message();
                        message.what=CHANG_TO_PLAY;
                        internetSearchTab.interHandler.sendMessage(message);
                    }
                }

                startService(intent);

            }
        };
        return songListclickListener;
    }



    private void setView(){
        singersearchInfo= (EditText) findViewById(R.id.singersearchText);
        singersearchBtn= (ImageButton) findViewById(R.id.singersearchbtn);
        singersongBySearch= (ListView) findViewById(R.id.singersongbysearch);
    }

    //获得歌曲列表
    private void recieveList(String searchstr){
       getListBySingerSong=new GetListBySingerSong(searchstr, new GetListBySingerSong.Callback() {
           @Override
           public void getsingersonglist() {
               singersonglist.clear();
               if (singersonglist!=null){
                   singersonglist.addAll(getListBySingerSong.singersonglist);
                   int i;
                   songidlist.clear();
                   for (i=0;i<singersonglist.size();i++){

                       songidlist.add(singersonglist.get(i).getSongid());
                   }
                   myApplication.setSongidList(songidlist);
                   Log.e("listmain", String.valueOf(singersonglist.size()));
                   refreshListView();
               }

           }

           @Override
           public void singernull() {
               Toast toast = Toast.makeText(SingerSongSearchActivity.this, "未搜索到歌手",Toast.LENGTH_SHORT);
               toast.show();
           }
       });
        getListBySingerSong.setsingerdata();
    }

    protected void dialog(final String songid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认要下载歌曲到本地吗？");

        builder.setTitle("请求下载歌曲");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(SingerSongSearchActivity.this, NetSongDownloadServer.class);
//                Log.e("Activity",rankingsongbeanlist.get(position).getSong_id());
                intent.putExtra("songID",songid);
                startService(intent);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
