package com.example.howard.mp3player.InterAPItools;

import com.example.howard.mp3player.Bean.PlayBean;
import com.example.howard.mp3player.Bean.SearchBean;
import com.example.howard.mp3player.Bean.SingerSongBean;
import com.example.howard.mp3player.Bean.SongDownloadBean;
import com.example.howard.mp3player.Bean.SongRankingBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Howard on 2016/7/11.
 */
public interface SongAPI {
    String BaseURL = "http://tingapi.ting.baidu.com/";

    @GET("v1/restserver/ting")
    Call<SongRankingBean> getSongRanking(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                         @Query("method") String method, @Query("type") int type, @Query("size") int size,
                                         @Query("offset") int offset);

    @GET("v1/restserver/ting")
    Call<SingerSongBean> getSingerSong(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                       @Query("method") String method, @Query("tinguid") String tinguid, @Query("limits") int limits,
                                       @Query("use_cluster") int use_cluster, @Query("order") int order);

    @GET("v1/restserver/ting")
    Call<SearchBean> getSearch(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                               @Query("method") String method, @Query("query") String query);

    @GET("v1/restserver/ting")
    Call<PlayBean> getPlay(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                           @Query("method") String method, @Query("songid") String songid);

    @GET("v1/restserver/ting")
    Call<SongDownloadBean> getdownloadsong(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                           @Query("method") String method, @Query("songid") long songid, @Query("bit") int bit, @Query("_t") long t);
}
