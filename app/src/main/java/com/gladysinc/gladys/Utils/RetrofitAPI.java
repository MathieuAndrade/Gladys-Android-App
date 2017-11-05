package com.gladysinc.gladys.Utils;

import com.gladysinc.gladys.Models.Alarm;
import com.gladysinc.gladys.Models.BrainSentences;
import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.Models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RetrofitAPI {

    @GET("/event?")
    Call<List<Event>> getEvents(@Query("token") String token);

    @GET("/event/create")
    Call<Event> createEvents(@Query("code") String event, @Query("house") String house_id, @Query("user") String user_id, @Query("token") String token);


    @GET("/devicetype/room")
    Call<List<DevicetypeByRoom>> getDevicetypeByRoom(@Query("token") String token);

    @FormUrlEncoded
    @POST("/devicetype/{id}/exec")
    Call<Void> changeDevicestate(@Path("id") Long devicetype_id, @Field("value") Float value, @Field("token") String token);

    @GET("/alarm?")
    Call<List<Alarm>> getAlarms(@Query("token") String token);

    @FormUrlEncoded
    @POST("/alarm")
    Call<Alarm> createAlarmRec(@Field("name") String name, @Field("time") String time, @Field("dayofweek") String idOfDay, @Field("active") Boolean active, @Field("token") String token);

    @FormUrlEncoded
    @POST("/alarm")
    Call<Alarm> createAlarmSpe(@Field("name") String name, @Field("datetime") String datetime, @Field("active") Boolean active, @Field("token") String token);

    @FormUrlEncoded
    @POST("/alarm")
    Call<Alarm> createCronRule(@Field("name") String name, @Field("cronrule") String datetime, @Field("token") String token);

    @DELETE("/alarm/{id}")
    Call<Void> deleteAlarm(@Path("id") Long alarm_id, @Query("token") String token);

    @GET("/sentence?")
    Call<List<BrainSentences>> getBrainSentences(@Query("take") String take, @Query("token") String token);

    @FormUrlEncoded
    @PATCH("/sentence/{id}")
    Call<Void> setLabel(@Path("id") Long sentence_id, @Field("label") String label, @Query("token") String token);

    @FormUrlEncoded
    @PATCH("/sentence/{id}")
    Call<Void> setStatus(@Path("id") Long sentence_id, @Field("status") String status, @Query("token") String token);

}
