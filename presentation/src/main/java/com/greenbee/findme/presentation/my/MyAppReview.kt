package com.greenbee.findme.presentation.my

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.greenbee.findme.presentation.R

internal fun Context.openPlayStoreListing() {
    val packageName = packageName
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName"),
    )
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
    )

    when {
        marketIntent.resolveActivity(packageManager) != null -> {
            startActivity(marketIntent)
        }
        webIntent.resolveActivity(packageManager) != null -> {
            startActivity(webIntent)
        }
        else -> {
            Toast.makeText(
                this,
                getString(R.string.my_app_review_unavailable),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}
