package ae.ac.uowdubai.csit242_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryAdapter(private val orderList: List<OrderHistoryItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderItems: TextView = view.findViewById(R.id.tvOrderItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.tvOrderId.text = "Order #${order.orderId}"
        holder.tvOrderDate.text = order.date
        holder.tvOrderStatus.text = order.status
        holder.tvTotalAmount.text = order.totalAmount
        holder.tvOrderItems.text = order.itemsSummary
    }

    override fun getItemCount(): Int = orderList.size
}
