package net.blakelee.coinprofits.dialogs

import android.content.Context
import com.yarolegovich.lovelydialog.LovelyProgressDialog
import net.blakelee.coinprofits.R

fun DownloadCoinsDialog(context: Context) = LovelyProgressDialog(context)
        .setTopColorRes(R.color.colorPrimary)
        .setTitle("Downloading Images")
        .setIcon(R.drawable.ic_file_download)
        .create()
