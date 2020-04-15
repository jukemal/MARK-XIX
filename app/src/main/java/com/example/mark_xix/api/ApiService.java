package com.example.mark_xix.api;

import com.example.mark_xix.models.Medicine;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("medicines")
    public Call<Object> sendMedicineList(@Body List<Medicine> medicineList);

    @GET("progress")
    public Call<Object> getProgress();
}
