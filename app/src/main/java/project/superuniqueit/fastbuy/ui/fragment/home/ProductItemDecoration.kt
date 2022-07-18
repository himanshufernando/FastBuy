package project.superuniqueit.fastbuy.ui.fragment.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ProductItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        var twoRemaining = parent.getChildLayoutPosition(view) % 2
        when (twoRemaining) {
            0 -> {
                outRect.top = 2
                outRect.bottom = 2
                outRect.right = 2
            }
            1 -> {
                outRect.top = 2
                outRect.bottom = 2
                outRect.left = 2
            }
        }
    }
}