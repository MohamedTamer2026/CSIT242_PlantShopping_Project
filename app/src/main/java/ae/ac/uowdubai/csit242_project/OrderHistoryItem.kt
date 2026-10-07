package ae.ac.uowdubai.csit242_project

data class OrderHistoryItem(
    val orderId: String = "",
    val date: String = "",
    val itemsSummary: String = "",
    val status: String = "Delivered",
    val totalAmount: String = ""
)
