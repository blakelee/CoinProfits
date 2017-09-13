package net.blakelee.coinprofits.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import kotlinx.android.synthetic.main.activity_about.*
import net.blakelee.coinprofits.R
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.Toolbar

class AboutActivity : AppCompatActivity() {

    private val github = "http://github.com/blakelee/coinprofits"
    private val coinmarketcap = "http://coinmarketcap.com"
    private val ethplorer = "http://ethplorer.io"
    private val icons8 = "http://icons8.com"
    private val address = "0x6263b06a107879dd4375165ab232b761e003b72c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        LinkBuilder.on(about_links)
                .addLinks(setupLinks())
                .build()
    }

    private fun setupLinks(): MutableList<Link> {
        val links: MutableList<Link> = mutableListOf()

        links.add(Link(github)
                .setText("github")
                .setOnClickListener { openLink(github) })

        links.add(Link(coinmarketcap)
                .setText("CoinMarketCap")
                .setOnClickListener { openLink(coinmarketcap) })

        links.add(Link(ethplorer)
                .setText("Ethplorer")
                .setOnClickListener { openLink(ethplorer) })

        links.add(Link(icons8)
                .setText("Icons8")
                .setOnClickListener { openLink(icons8) })

        links.add(Link(address)
                .setText(address)
                .setOnClickListener { setClipboard(this, address) })

        return links
    }

    private fun openLink(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

    private fun setClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Ethereum donation address", text)
        clipboard.primaryClip = clip
    }
}