package project.superuniqueit.fastbuy.ui.fragment.pastorderdetails

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_last_order.*
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.databinding.FragmentPastOrderDetailsBinding
import project.superuniqueit.fastbuy.services.perfrences.AppPrefs
import project.superuniqueit.fastbuy.ui.activity.MainActivity

import project.superuniqueit.fastbuy.viewmodels.home.HomeViewModel

class PastOrderDetailsFragment : Fragment(),View.OnClickListener {


    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs

    lateinit var binding: FragmentPastOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past_order_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.order = viewmodel.pastOrder.value

        img_navigation_last_order.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.img_navigation_last_order -> mainActivity.onBackPressed()

        }
    }

}