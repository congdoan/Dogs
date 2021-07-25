package com.cdoan.dogs.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cdoan.dogs.R
import com.cdoan.dogs.databinding.FragmentDetailBinding
import com.cdoan.dogs.databinding.SendSmsDialogBinding
import com.cdoan.dogs.model.DogBreed
import com.cdoan.dogs.model.DogPalette
import com.cdoan.dogs.model.SmsInfo
import com.cdoan.dogs.viewmodel.DetailViewModel


class DetailFragment : Fragment() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding

    private var currentDog: DogBreed? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                shareDogInfo()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun shareDogInfo() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
            putExtra(Intent.EXTRA_TEXT, currentDog?.nameWithPurpose)
            putExtra(Intent.EXTRA_STREAM, Uri.parse(currentDog?.imageUrl))
        }
        startActivity(
            Intent.createChooser(intent, "Share with")
        )
    }

    fun onSmsPermissionResult(granted: Boolean) {
        if (!granted) return

        val ctx = context
        val dog = currentDog
        if (ctx == null || dog == null) return

        val smsInfo = SmsInfo(
            "",
            dog.nameWithPurpose,
            dog.imageUrl
        )

        val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
            LayoutInflater.from(ctx),
            R.layout.send_sms_dialog,
            null,
            false
        )

        dialogBinding.smsInfo = smsInfo

        AlertDialog.Builder(ctx)
            .setView(dialogBinding.root)
            .setPositiveButton("Send SMS") { _, _ ->
                val toText = dialogBinding.smsDestination.text
                if (!dialogBinding.smsDestination.text.isNullOrBlank()) {
                    smsInfo.to = toText.toString()
                    sendSms(smsInfo)
                }
            }
            .show()
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        SmsManager.getDefault().sendTextMessage(
            smsInfo.to,
            null,
            smsInfo.text,
            pi,
            null
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dogUuid = DetailFragmentArgs.fromBundle(arguments!!).dogUuid

        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        detailViewModel.fetchData(dogUuid)
        observeDetailViewModel()
    }

    private fun observeDetailViewModel() {
        detailViewModel.data.observe(this) {
            dataBinding.dog = it
            currentDog = it

            it.imageUrl?.let { url ->
                setBackgroundColor(url)
            }
        }
    }

    private fun setBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            dataBinding.palette = DogPalette(intColor)
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

}

private val DogBreed.nameWithPurpose get() = "$dogBreed bred for $bredFor"