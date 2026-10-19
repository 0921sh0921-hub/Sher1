package space.balehroon.daftarsher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var offline: TextView

    private val homeUrl = "https://balehroonhelper.space/sher/"

    private fun showView(view: View, visible: Boolean) {
        view.setVisibility(
            if (visible) View.VISIBLE else View.GONE
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(23, 23, 19)
        window.navigationBarColor = Color.rgb(23, 23, 19)

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(245, 240, 230))

        progress = ProgressBar(this)
        progress.isIndeterminate = true
        showView(progress, true)

        offline = TextView(this)
        offline.text = "اتصال به اینترنت برقرار نیست.\n\nبرای تلاش دوباره لمس کنید."
        offline.textSize = 18f
        offline.gravity = Gravity.CENTER
        offline.setTextColor(Color.rgb(36, 35, 30))
        offline.setPadding(32, 32, 32, 32)
        showView(offline, false)

        webView = WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.javaScriptCanOpenWindowsAutomatically = false
        webView.settings.setSupportMultipleWindows(false)

        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                showView(progress, true)
                showView(offline, false)
            }

            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) {
                showView(progress, false)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) {
                    showView(progress, false)
                    showView(offline, true)
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {

                val uri: Uri = request.url

                val internal =
                    uri.scheme == "https" &&
                    (
                        uri.host == "balehroonhelper.space" ||
                        uri.host == "www.balehroonhelper.space"
                    )

                if (internal) {
                    return false
                }

                if (uri.scheme == "https") {
                    try {
                        startActivity(
                            Intent(Intent.ACTION_VIEW, uri)
                        )
                    } catch (_: Exception) {
                        // لینک قابل باز شدن نیست
                    }
                }

                return true
            }
        }

        offline.setOnClickListener {
            showView(offline, false)
            showView(progress, true)
            webView.reload()
        }

        root.addView(
            webView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        root.addView(
            progress,
            FrameLayout.LayoutParams(
                64,
                64,
                Gravity.CENTER
            )
        )

        root.addView(
            offline,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)

        if (savedInstanceState == null) {
            webView.loadUrl(homeUrl)
        } else {
            val restored = webView.restoreState(savedInstanceState)

            if (restored == null) {
                webView.loadUrl(homeUrl)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::webView.isInitialized) {
            webView.saveState(outState)
        }

        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.destroy()
        }

        super.onDestroy()
    }
}