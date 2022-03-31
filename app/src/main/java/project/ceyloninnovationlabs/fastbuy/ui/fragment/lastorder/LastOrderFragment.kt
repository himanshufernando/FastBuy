package project.ceyloninnovationlabs.fastbuy.ui.fragment.lastorder

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_last_order.*
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.databinding.FragmentLastOrderBinding
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_last_order.cl_facebook
import kotlinx.android.synthetic.main.fragment_last_order.cl_google
import project.ceyloninnovationlabs.fastbuy.FastBuy
import project.ceyloninnovationlabs.fastbuy.services.listeners.OnBackListener

class LastOrderFragment : Fragment(),View.OnClickListener, OnBackListener {


    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs
    lateinit var binding: FragmentLastOrderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_last_order, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.order = viewmodel.lastOrder.value

        cl_google.setOnClickListener(this)
        cl_facebook.setOnClickListener(this)
        img_navigation_last_order.setOnClickListener(this)
        FastBuy.setOnBackResponseListener(this)

        saveUserData()
        googleSignObserver()


    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }


    private fun googleSignObserver() {
        viewmodel.googleSign.observe(viewLifecycleOwner, Observer {
            if (it.facebook_id.isNotEmpty() || it.google_id.isNotEmpty()) {
                cl_account_last.visibility = View.GONE
            } else {
                cl_account_last.visibility = View.VISIBLE
            }
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.fragment_last_to_home)

        })

    }


    private fun saveUserData(){
        viewmodel.saveUserData().observe(viewLifecycleOwner, Observer {_user ->
            if (_user.facebook_id.isNotEmpty() || _user.google_id.isNotEmpty()) {
               cl_account_last.visibility = View.GONE
            }else{
                cl_account_last.visibility = View.VISIBLE
            }
        })

    }

    override fun onClick(p0: View) {
       when(p0.id){
           R.id.cl_google -> mainActivity.googleSignIn()
           R.id.cl_facebook ->mainActivity.facebooklogin()
           R.id.img_navigation_last_order -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_last_to_home)


       }
    }

    override fun onBackListenerResponse(fragment: Int) {
          if(fragment==1){
              println("xxxxxxxxxxxxxxx 012")
              NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_last_to_home)
          }
    }


}