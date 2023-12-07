package com.zrh.android.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.zrh.android.common.utils.databinding.ActivityWhatsappLinkBinding
import com.zrh.android.common.utils.onClick
import com.zrh.android.common.widgets.BindingActivity
import java.net.URLEncoder

/**
 *
 * @author zrh
 * @date 2023/11/30
 *
 */
class WhatsappActivity : BindingActivity<ActivityWhatsappLinkBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnSendText.onClick {
            sendText()
        }

        binding.btnSendUser.onClick {
            sendUserText()
        }

        binding.btnGooglePlay.onClick {
            val url = "https://play.google.com/store/apps/details?id=com.redland.i2fun&referrer=code%3D123456"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

    }

    private fun sendUserText() {
        sendToWhatsApp("I'm interested in your car for sale", "+086-(135)97333634")
    }

    private fun sendText() {
        sendToWhatsApp("I'm interested in your car for sale")
    }

    private fun sendToWhatsApp(text: String, phoneNumber: String = "") {
        try {
            val baseUrl = "https://wa.me/"
            val regex = Regex("[-+0()]")
            val formatPhoneNumber = phoneNumber.replace(regex, "")
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val url = "${baseUrl}${formatPhoneNumber}?text=${encodedText}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}