package space.balehroon.daftarsher

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.Gravity
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.graphics.Color
import android.content.Intent

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var offline: TextView

    private val homeUrl = "https://balehroon.space/sher/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(23, 23, 19)
        window.navigationBarColor = Color.rgb(23, 23, 19)

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(245, 240, 230))

        progress = ProgressBar(this)
        progress.isIndeterminate = true
        progress.visibility = View.VISIBLE

        offline = TextView(this).apply {
            text = "اتصال به اینترنت برقرار نیست.\n\nبرای تلاش دوباره لمس کنید."
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(36, 35, 30))
            setPadding(32, 32, 32, 32)
            setVisibility(View.GONE)

            setOnClickListener {
                setVisibility(View.GONE)
                progress.setVisibility(View.VISIBLE)
                webView.reload()
            }
        }

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.setSupportMultipleWindows(false)

            webViewClient = object : WebViewClient() {

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    progress.setVisibility(View.VISIBLE)
                    offline.setVisibility(View.GONE)
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    progress.setVisibility(View.GONE)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        progress.setVisibility(View.GONE)
                        offline.setVisibility(View.VISIBLE)
                    }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val uri = request.url

                    return if (
                        uri.scheme == "https" &&
                        (
                            uri.host == "balehroon.space" ||
                            uri.host == "www.balehroon.space"
                        )
                    ) {
                        false
                    } else {
                        if (uri.scheme == "https") {
                            startActivity(
                                Intent(Intent.ACTION_VIEW, uri)
                            )
                        }
                        true
                    }
                }
            }

            webChromeClient = WebChromeClient()
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
            FrameLayout.LayoutParams(64, 64, Gravity.CENTER)
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
            webView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::webView.isInitialized) {
            webView.saveState(outState)
        }
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Android API; retained for broad device compatibility")
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