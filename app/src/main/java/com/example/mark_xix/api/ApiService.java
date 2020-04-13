package com.example.mark_xix.api;

import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.utils._ResponseBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("medicines")
    public Call<_ResponseBody> sendMedicineList(@Body List<Medicine> medicineList);
}
