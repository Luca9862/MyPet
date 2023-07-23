package Visit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.pdm.app.databinding.ItemVisitBinding
import pets.Visit

class VisitAdapter(private val list: MutableList<Visit>) : RecyclerView.Adapter<VisitAdapter.visitViewHolder>() {

    private var listener: VisitAdapter.adapterClickInterface?= null
    fun setListener(listener : VisitAdapter.adapterClickInterface){
        this.listener = listener
    }

    inner class visitViewHolder(val binding: ItemVisitBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitAdapter.visitViewHolder {
        val binding = ItemVisitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return visitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisitAdapter.visitViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.tvNoteItem.text = this.textVisit //questo prende il tvNoteItem dal binding del itemRecycleView
                binding.tvDateItem.text = this.dateVisit

                binding.deleteNote.setOnClickListener{
                    listener?.onDeleteNoteBtnClicked(this)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface adapterClickInterface{
        fun onDeleteNoteBtnClicked(visit: Visit)
    }
}