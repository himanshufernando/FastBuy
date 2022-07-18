package project.superuniqueit.fastbuy.ui.dialog


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_info.*
import project.superuniqueit.fastbuy.R


class InfoDialog : DialogFragment() {

   lateinit var dialogListener : InfoDialogListener

    fun setListener (listener : InfoDialogListener){
        dialogListener = listener
    }
    companion object {
        private const val MESSAGE = "message"
        private const val CODE = "code"
        fun newInstance(message: String = "",code:Int): InfoDialog {
            val dialog = InfoDialog()
            val args = Bundle().apply {
                putString(MESSAGE, message)
                putInt(CODE,code)
            }
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_info, container,false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = arguments?.getString(MESSAGE)
        val code = arguments?.getInt(CODE)

        txt_dialog_message.text = message

        txt_dialog_positive.setOnClickListener {
            if(::dialogListener.isInitialized){
                dialogListener.onInfoDialogPositive(code!!)
            }
            dismiss()
        }

    }


    interface InfoDialogListener {
        fun onInfoDialogPositive(code : Int)
    }
}