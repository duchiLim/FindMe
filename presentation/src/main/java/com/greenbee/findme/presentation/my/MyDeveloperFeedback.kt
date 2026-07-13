package com.greenbee.findme.presentation.my

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.greenbee.findme.presentation.R

internal fun Context.launchDeveloperFeedback(
    recipientEmail: String,
    subject: String,
    body: String,
) {
    val mailtoUri = buildString {
        append("mailto:")
        if (recipientEmail.isNotBlank()) {
            append(Uri.encode(recipientEmail))
        }
        append("?subject=")
        append(Uri.encode(subject))
        append("&body=")
        append(Uri.encode(body))
    }

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(mailtoUri)
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(
            this,
            getString(R.string.my_feedback_no_email_app),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
