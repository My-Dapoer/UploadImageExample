package com.example.testapp2

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.testapp2.core.model.ResponseModel
import com.example.testapp2.core.network.ApiConfig
import com.example.testapp2.databinding.ActivityMainBinding
import com.example.testapp2.util.Constants.STORAGE_URL
import com.github.drjacky.imagepicker.ImagePicker
import com.inyongtisto.myhelper.extension.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainButton()
    }

    private fun mainButton() {
        binding.btnUpload.setOnClickListener {
            upload()
        }

        binding.image.setOnClickListener {
            imagePicker()
        }
    }

    private fun imagePicker() {
        ImagePicker.with(this)
            .crop()
            .maxResultSize(400, 600, true)
            .createIntentFromDialog {
                profileLauncher.launch(it)
            }
    }

    private var fileImage: File? = null
    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                fileImage = File(uri?.path ?: "")
                Picasso.get()
                    .load(fileImage!!)
                    .into(binding.image)
            }
        }

    private fun upload() {

        if (fileImage == null) {
            toastError("Ambil gambar dulu bro")
            return
        }

        // cenvert dari file ke multipartBody
        val mFile = fileImage.toMultipartBody("file") // "file" ganti dgn nama parameter

        // kirim ke server
        ApiConfig.provideApiService.upload(mFile).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) { // check jika success
                    // jika success ambil data dari server
                    // body yg didapat dari server
                    val body = response.body()!!

                    // file name kembalian dari server
                    val fileName = body.file

                    // imageUrl + File name
                    val urlImage = STORAGE_URL + "$fileName"
                    // tampilkan image by Url + Filename dari server
                    Picasso.get()
                        .load(urlImage)
                        .into(binding.imageServer)
                    binding.lyImageServer.toVisible()

                    showToast(body.message)

                } else { // error dari server
                    showToast("error:${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                showToast("error:${t.message}")
            }
        })
    }
}