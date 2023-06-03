package com.example.bloknot


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bloknot.databinding.EditActivityBinding
import com.example.bloknot.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false
    lateinit var binding: EditActivityBinding
    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>
    var tempImageUri = "empty"
    private val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMyIntents()
        Log.d("MyLog", getTime())
        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.imMainImage.setImageURI(result.data?.data)
                tempImageUri = result.data?.data.toString()
                contentResolver.takePersistableUriPermission(
                    result.data?.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickDeleteImage(view: View) {
        binding.fbAddImage.visibility = View.VISIBLE
        binding.mainImageLayout.visibility = View.GONE
        tempImageUri = "empty"
    }

    fun onClickAddImage(view: View) {
        binding.fbAddImage.visibility = View.GONE
        binding.mainImageLayout.visibility = View.VISIBLE
    }

    fun onClickChooseImage(view: View) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        someActivityResultLauncher.launch(intent)
    }

    fun onClickSave(view: View) {
        val myTitle = binding.edTitle.text.toString()
        val myDesc = binding.edDescr.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            if (myTitle != "" && myDesc != "") {
                if (isEditState) {
                    myDbManager.updateItem(myTitle, myDesc, tempImageUri, id, getTime())
                } else {
                    myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getTime())
                }
            }
            finish()
        }

    }

    private fun getMyIntents() {
        binding.fbEdit.visibility = View.GONE
        val i = intent
        if (i != null) {
            if (i.getStringExtra(MyIntenConstants.TITLE_KEY) != null) {
                binding.fbAddImage.visibility = View.GONE

                binding.edTitle.setText(i.getStringExtra(MyIntenConstants.TITLE_KEY))
                isEditState = true
                binding.edDescr.isEnabled = false
                binding.edTitle.isEnabled = false
                binding.fbEdit.visibility = View.VISIBLE
                binding.edDescr.setText(i.getStringExtra(MyIntenConstants.CONTENT_KEY))
                id = i.getIntExtra(MyIntenConstants.ID_KEY, 0)


                if (i.getStringExtra(MyIntenConstants.URI_KEY) != "empty") {
                    binding.mainImageLayout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntenConstants.URI_KEY)!!
                    binding.imMainImage.setImageURI(Uri.parse(tempImageUri))
                    binding.imButtonEditImage.visibility = View.GONE
                    binding.imButtonDeleteImage.visibility = View.GONE

                }
            }
        }
    }

    fun onEditEnable(view: View) {
        binding.edTitle.isEnabled = true
        binding.edDescr.isEnabled = true
        binding.fbEdit.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        if (tempImageUri == "empty") return
        binding.imButtonEditImage.visibility = View.VISIBLE
        binding.imButtonDeleteImage.visibility = View.VISIBLE
    }

    private fun getTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)
    }

}