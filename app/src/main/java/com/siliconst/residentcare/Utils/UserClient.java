package com.siliconst.residentcare.Utils;


import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserClient {

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part file, @Part("photo") RequestBody name

    );

    @POST("api/uploadFileToUploads")
    @Multipart
    Call<ResponseBody> uploadFileToUploads(
            @Part MultipartBody.Part file, @Part("photo") RequestBody name

    );


    @Headers("Content-Type: application/json")
    @POST("api/user/login")
    Call<ApiResponse> login(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/staff/assignedTickets")
    Call<ApiResponse> assignedTickets(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/deleteAd")
    Call<ApiResponse> deleteAd(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/register")
    Call<ApiResponse> register(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/social")
    Call<ApiResponse> social(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/completeProfile")
    Call<ApiResponse> completeProfile(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/createAd")
    Call<ApiResponse> createAd(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/editAd")
    Call<ApiResponse> editAd(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/feedback/submitFeedback")
    Call<ApiResponse> submitFeedback(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/allAds")
    Call<ApiResponse> allAds(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/payment/payForAd")
    Call<ApiResponse> payForAd(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/search")
    Call<ApiResponse> search(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/getMyAds")
    Call<ApiResponse> getMyAds(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ad/adDetails")
    Call<ApiResponse> adDetails(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/createPost")
    Call<ApiResponse> createPost(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/allPosts")
    Call<ApiResponse> allPosts(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/createRoom")
    Call<ApiResponse> createRoom(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ticket/createTicket")
    Call<ApiResponse> createTicket(
            @Body JsonObject jsonObject

    );


    @Headers("Content-Type: application/json")
    @POST("api/ticket/getDepartments")
    Call<ApiResponse> getDepartments(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/appFaqs")
    Call<ApiResponse> appFaqs(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/updateFcmKey")
    Call<ApiResponse> updateFcmKey(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/resetpassword")
    Call<ApiResponse> resetpassword(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/resetpasswordNow")
    Call<ApiResponse> resetpasswordNow(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @GET("corporate_sms2/api/auth.jsp")
    Call<ResponseBody> loginSMS(@Query("msisdn") String msisdn, @Query("password") String password);

    @Headers("Content-Type: application/json")
    @GET("corporate_sms2/api/sendsms.jsp")
    Call<ResponseBody> sendSMS(@Query("session_id") String session_id, @Query("to") String to, @Query("text") String text, @Query("mask") String mask);

    @Headers("Content-Type: application/json")
    @POST("api/user/updateProfile")
    Call<ApiResponse> updateProfile(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ticket/allTickets")
    Call<ApiResponse> allTickets(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ticket/homeTickets")
    Call<ApiResponse> homeTickets(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ticket/getReplies")
    Call<ApiResponse> getReplies(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ticket/notices")
    Call<ApiResponse> notices(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/ticket/sendReply")
    Call<ApiResponse> sendReply(
            @Body JsonObject jsonObject

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadAudioFile(
            @Part MultipartBody.Part file, @Part("audio") RequestBody name

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadVideoFile(
            @Part MultipartBody.Part file, @Part("video") RequestBody name

    );


    @Headers("Content-Type: application/json")
    @POST("api/comments/getAllComments")
    Call<ApiResponse> getAllComments(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/comments/addComment")
    Call<ApiResponse> addComment(
            @Body JsonObject jsonObject

    );

}
