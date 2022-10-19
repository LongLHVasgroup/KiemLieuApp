package com.example.vasclientv2.apiInterface;

import com.example.vasclientv2.kiemlieu.kiemlieu.FcmResponse;
import com.example.vasclientv2.message.MesageModel;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.model.entities.BargeModel;
import com.example.vasclientv2.model.entities.CheckingScrapDTO;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.KLCheckingScrap;
import com.example.vasclientv2.model.entities.SaveVehicle;
import com.example.vasclientv2.model.entities.TagInfoModel;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.VehicleModel;
import com.example.vasclientv2.model.entities.WarehouseModel;
import com.example.vasclientv2.model.entities.WeightScaleModel;
import com.example.vasclientv2.ui.WareHouse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    /**
     * Gọi API từ .net core
     *
     * @author Hoang NM
     * @version 1.0
     * @since 2020-10-20
     */
    @GET("api/user?")
    Call<SingleResponeMessage<UserModel>> GetUser(@Header("key") String key, @Header("token") String token, @Query("id") String id, @Query("password") String password);

    @GET("api/vehicle?")
    Call<SingleResponeMessage<VehicleModel>> GetVehicleInfo(@Header("key") String key, @Header("token") String token, @Query("vehicleNumber") String vehicleNumber);

    @GET("api/gatelist?")
    Call<ListResponeMessage<GateModel>> GetGateList(@Header("key") String key, @Header("token") String token);

//    get list Ban Can
    @GET("api/WeightScale/GetListWeightScaleModel")
    Call<ListResponeMessage<WeightScaleModel>> GetWeightScaleList(@Header("key") String key, @Header("token") String token);

    //    /**
//     * edit ngày 21/10/2020
//     * */
    @POST("api/CheckingScrap?")
    /// <summary>
    /// lưu xe vào
    /// </summary>
    /// <param name="id"></param>
    Call<AcctionMessage> SaveVehicleIn(@Header("key") String key, @Header("token") String token, @Body SaveVehicle input, @Query("userID") String userID);

    @PUT("api/CheckingScrap/{rfid}?")
        /// <summary>
        /// lưu xe ra
        /// </summary>
        /// <param name="id"></param>
    Call<AcctionMessage> SaveVehicleOut(@Header("key") String key, @Header("token") String token, @Path(value = "rfid", encoded = true) String rfid, @Query("outGate") String outGate, @Query("userID") String userID);

    @GET("api/CheckingScrap/{rfid}")
    Call<SingleResponeMessage<CheckingScrapModel>> GetVehicleNumber(@Header("key") String key, @Header("token") String token, @Path(value = "rfid", encoded = true) String rfid);

    //    /**
//     * edit ngày 21/10/2020
//     * */
    @GET("api/CheckingScrap/kiemlieu/{rfid}")
    /// <summary>
    /// lấy thông tin phiếu cân cho kiểm liệu
    /// </summary>
    /// <param name="id"></param>
    Call<SingleResponeMessage<KLCheckingScrap>> GetCheckingScrapKL(@Header("key") String key, @Header("token") String token, @Path(value = "rfid", encoded = true) String rfid);

    //    /**
    //     * edit ngày 19/11/2020
    //      * lấy list theo step
    //    * */
    @GET("api/CheckingScrap/truongkiemlieu/{rfid}?")
    Call<ListResponeMessage<CheckingScrapModel>> GetListCheckingScrapKL(@Header("key") String key, @Header("token") String token, @Path(value = "rfid", encoded = true) String rfid, @Query("step") int step, @Query("gateCode") String gateCode);

    //    /**
//     * edit ngày 19/11/2020
//     * */
    @POST("api/CheckingScrap/kiemlieu/save?")
    /// <summary>
    /// lưu kiểm liệu
    /// </summary>
    /// <param name="id"></param>
    Call<AcctionMessage> LuuKiemLieu(@Header("key") String key, @Header("token") String token, @Body CheckingScrapDTO input, @Query("userID") String userID, @Query("step") int step);

    @GET("api/warehouses/swapwarehouse")
    /// <summary>
    /// Lê Hoàng Long đổi kho
    /// </summary>
    /// <param name="id"></param>
    Call<AcctionMessage> SwapWareHouse(@Header("key") String key, @Header("token") String token, @Query("ScaleTicketId") String ScaleTicketId ,@Query("userID") String userID, @Query("step") int step);

    @POST("api/user/token/update")
    Call<AcctionMessage> UpdateToken(@Header("key") String key, @Header("token") String token, @Body UserModel value);

    /**
     * Phat nhap
     * api send notification by Google Cloud Messaging
     */

    @POST("fcm/send")
    Call<FcmResponse> sendNotifycation(@Header("Content-Type") String contentType, @Header("Authorization") String autheKey, @Body MesageModel body);

    /**
     * api lấy thông tin phiếu cân đã kiểm liệu cho màn hình trưởng kiểm liệu (onitemclick)
     */
    @GET("api/CheckingScrap/truongkiemlieu/info/{rfid}?")
    Call<SingleResponeMessage<KLCheckingScrap>> GetCheckingScrapTKL(@Header("key") String key, @Header("token") String token, @Path(value = "rfid", encoded = true) String rfid, @Query("scaleticketcode") String scaleticketcode);

    /**
     * api lấy thông token của user và trưởng kiểm liệu
     */
    @GET("api/user/token?")
    Call<SingleResponeMessage<TempTable>> GetTokenForNoti(@Header("key") String key, @Header("token") String token, @Query("id") String id, @Query("group") String group);

    /**
     * api đổi mật khẩu
     */
    @PUT("api/user/password/change")
    Call<AcctionMessage> PasswordChange(@Header("key") String key, @Header("token") String token, @Body TempTable value);

    /**
     * api reset mật khẩu
     */
    @PUT("api/user/password/reset")
    Call<AcctionMessage> ResetPassword(@Header("key") String key, @Header("token") String token, @Header("userid") String userID, @Body TempTable value);

    /*
     *api lấy danh sách user
     *  */
    @GET("api/user/getlist")
    Call<ListResponeMessage<UserModel>> GetListUser(@Header("key") String key, @Header("token") String token, @Header("userid") String userID);

    /**
     * api cập nhật mật khẩu
     */
    @PUT("api/user/password/update")
    Call<AcctionMessage> PasswordUpdate(@Header("key") String key, @Header("token") String token, @Body TempTable value);

    /**
     * api tạo mới user
     */
    @POST("api/user/create")
    Call<AcctionMessage> CreateUser(@Header("key") String key, @Header("token") String token, @Header("userid") String userID, @Body UserModel value);

    /**
     * api lấy danh sách xe mỗi khi bảo vệ nhập vào trường biển số xe
     */
    @GET("api/vehicle/{id}")
    Call<ListResponeMessage<VehicleModel>> getListVehicalNumber(@Header("key") String key, @Header("token") String token,@Path(value = "id", encoded = true) String id);

    /**
     * api lấy thông tin thẻ từ rfid
     */
    @GET("api/RFID?")
    Call<ListResponeMessage<TagInfoModel>> GetTagInfo(@Header("key") String key, @Header("token") String token, @Query("rfid") String rfid);

    /**
     * API update thong tin tag NFC
     * @param key
     * @param token
     * @return
     */
    @POST("api/RFID")
    Call<AcctionMessage> UpdateTagInfo(@Header("key") String key, @Header("token") String token, @Body TagInfoModel list);


    /**
     * API Lấy danh sách các xe trong bãi đã báo mất thẻ
     * @param key
     * @param token
     * @param status = 1 nếu là tìm xe mất thẻ
     * @param isdone
     * @return
     */
    @GET("api/rfid/vehiclelosttag")
    Call<ListResponeMessage<CheckingScrapModel>> GetListVehicleLostTag(@Header("key") String key, @Header("token") String token, @Query("status") int status, @Query("isdone") Boolean isdone);

    /**
     * API Cho Xe mất thẻ ra khỏi bãi
     * @param key
     * @param token
     * @param userID
     * @param vehicleNumber
     * @return
     */
    @PUT("api/rfid/taglost?")
    Call<AcctionMessage> VehicleLostTag(@Header("key") String key, @Header("token") String token, @Query("userID") String userID, @Query("vehiclenumber") String vehicleNumber);

    @GET("api/barge")
    Call<ListResponeMessage<BargeModel>> GetListBarge(@Header("key") String key, @Header("token") String token);


    /**
     * Lấy danh sách kho nhập
     * @param key
     * @param token
     * @param text
     * @return
     */
    @GET("api/WareHouses/find")
    Call<ListResponeMessage<WarehouseModel>> GetListWarehouse(@Header("key") String key, @Header("token") String token, @Query("WareHouse") String text);

    /**
     * Cập nhật kho nhập cho phiếu cân
     * @param key
     * @param token
     * @param scaleticketID
     * @param warehouse
     * @return
     */
    @PUT("api/WareHouses/update/{scaleticketID}")
    Call<AcctionMessage> UpdateKhoNhapPhieuCan(@Header("key") String key, @Header("token") String token, @Path(value = "scaleticketID", encoded = true) String scaleticketID, @Body WarehouseModel warehouse);



    /**
     * Lấy thông tin xe đã đăng ký qua web
     * @param key
     * @param token
     * @param vehicleNumber
     * @return
     */
    @GET("api/register/find?")
    Call<SingleResponeMessage<VehicleModel>> GetListVehicleRegister(@Header("key") String key, @Header("token") String token, @Query("vehicleNumber") String vehicleNumber);

    /**
     * Hủy phiếu kiểm liệu đã duyệt
     *
     */
    @POST("api/PhieuKiemLieu/cancel/{id}?")
    Call<AcctionMessage> DestroyPhieuKL(@Header("key") String key, @Header("token") String token,  @Path(value = "id", encoded = true) String id);


}
