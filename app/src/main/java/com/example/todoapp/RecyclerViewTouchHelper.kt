import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.ToDoAdapter
import com.example.todoapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

@Suppress("DEPRECATION")
class RecyclerViewTouchHelper(private val adapter: ToDoAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false // No moving of items
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.RIGHT) {
            AlertDialog.Builder(viewHolder.itemView.context)
                .setTitle("Delete Task")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ ->
                    adapter.deleteTask(position)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    adapter.notifyItemChanged(position)
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            adapter.editItem(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(
                ContextCompat.getColor(
                    adapter.getContext(),
                    R.color.PrimaryDark// Replace with your color resource
                )
            )
            .addSwipeLeftActionIcon(R.drawable.baseline_edit_24) // Replace with your edit icon resource
            .addSwipeRightBackgroundColor(Color.RED)
            .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
