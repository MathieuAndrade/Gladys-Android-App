package com.gladysinc.gladys.Utils;

import com.gladysinc.gladys.Models.Alarm;
import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.Models.Event;

import java.util.List;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

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

    @DELETE("/alarm/{id}")
    Call<Void> deleteAlarm(@Path("id") Long alarm_id, @Query("token") String token);

}
