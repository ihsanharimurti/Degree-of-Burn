package com.example.degreeofburn.ui.imageresultpreview
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.degreeofburn.R
import com.example.degreeofburn.databinding.ActivityImageResultPreviewBinding


class ImageResultPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageResultPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageResultPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("IMAGE_URL")

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_imagepreviewbtn)
                .error(R.drawable.ic_app_logo)
                .into(binding.fullImageView)
        } else {
            Toast.makeText(this, "Tidak dapat memuat gambar", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Close button to go back
        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}