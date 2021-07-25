package com.cdoan.dogs.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding

    private var currentDog: DogBreed? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onSmsPermissionResult(granted: Boolean) {
        if (!granted) return

        context?.let {
            val smsInfo = SmsInfo(
                "",
                "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                currentDog?.imageUrl
            )

            val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                LayoutInflater.from(it),
                R.layout.send_sms_dialog,
                null,
                false
            )

            dialogBinding.smsInfo = smsInfo

            AlertDialog.Builder(it)
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
    }

    private fun sendSms(smsInfo: SmsInfo) {
        TODO("Not yet implemented")
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}