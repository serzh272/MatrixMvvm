package ru.serzh272.matrixmvvm.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.serzh272.matrixmvvm.data.AppSettings
import ru.serzh272.matrixmvvm.repositories.PreferencesRepository
import ru.serzh272.matrixmvvm.utils.Fraction
import ru.serzh272.matrixmvvm.utils.Matrix
import ru.serzh272.matrixmvvm.views.MatrixViewGroup


@ExperimentalUnsignedTypes
class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.MatrixViewHolder>() {
    private var matrices: MutableList<Matrix> = mutableListOf()
    private var matrixViews: MutableList<MatrixViewGroup> = mutableListOf()
    private var listener: OnDataChangedListener? = null

    class MatrixViewHolder(matrixView: MatrixViewGroup) : RecyclerView.ViewHolder(matrixView) {
        fun setMatrix(matrix: Matrix) {
            when (itemView) {
                is MatrixViewGroup -> itemView.matrix = matrix
            }
        }
    }

    fun updateMode(state: AppSettings) {
        matrixViews.forEach {

            it.requestLayout()
        }

    }

    fun updateData(data: List<Matrix?>) {
        matrices = data as MutableList<Matrix>
        notifyDataSetChanged()
    }

    fun updateMatrix(pos: Int, data: Matrix) {
        matrices[pos] = data
        notifyItemChanged(pos, data)
        listener?.onDataChanged(pos, data)
    }

    override fun getItemCount(): Int {
        return matrices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): MatrixViewHolder {
        val m = MatrixViewGroup(parent.context, null)
        m.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        m.setOnDataChangedListener(object : MatrixViewGroup.OnDataChangedListener {
            override fun onDataChanged(matrix: Matrix) {
                updateMatrix(pos, matrix)
            }

        })
        val prefs = PreferencesRepository.getSettings()
        m.frMode = if (prefs.mode == 0) {
            if (prefs.isMixedFraction) Fraction.FractionType.MIXED
            else Fraction.FractionType.COMMON
        } else {
            Fraction.FractionType.DECIMAL
        }
        m.precision = prefs.precision
        matrixViews.add(m)
        return MatrixViewHolder(m)
    }

    override fun onBindViewHolder(holder: MatrixViewHolder, position: Int) {
        holder.setMatrix(matrices[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnDataChangedListener {
        fun onDataChanged(pos: Int, matrix: Matrix)
    }

    fun setOnDataChangedListener(listener: OnDataChangedListener) {
        this.listener = listener
    }
}
