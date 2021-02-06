package ru.serzh272.matrixmvvm.adapters

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import ru.serzh272.matrixmvvm.repositories.Repository
import ru.serzh272.matrixmvvm.utils.Matrix
import ru.serzh272.matrixmvvm.viewmodels.MatrixViewModel
import ru.serzh272.matrixmvvm.views.MatrixViewGroup
@ExperimentalUnsignedTypes
class ViewPagerAdapter : PagerAdapter {
    var matrixViews:List<MatrixViewGroup>
    val titles = Repository.getTitles()
    constructor(context: Context){
        matrixViews = listOf(MatrixViewGroup(context, null),MatrixViewGroup(context, null), MatrixViewGroup(context, null))
    }
    override fun getCount(): Int {
        return matrixViews.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(matrixViews[position])
        return matrixViews[position]
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
