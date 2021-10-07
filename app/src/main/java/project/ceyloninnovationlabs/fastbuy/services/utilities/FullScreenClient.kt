package project.ceyloninnovationlabs.fastbuy.services.utilities

import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.FrameLayout

class FullScreenClient (val parent: ViewGroup, val content: ViewGroup) : WebChromeClient() {

    private val matchParentLayout: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    )
    private var customView: View? = null

    override fun onShowCustomView(view: View, callback: CustomViewCallback?) {
        customView = view
        view.layoutParams = matchParentLayout
        parent.addView(view)
        content.visibility = View.GONE
    }

    override fun onHideCustomView() {
        content.visibility = View.VISIBLE
        parent.removeView(customView)
        customView = null
    }
}