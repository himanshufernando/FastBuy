package project.superuniqueit.fastbuy.ui.fragment.splash

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.*
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.ui.activity.MainActivity


class SplashFragment : Fragment() {


    lateinit var job: Job
    lateinit var mainActivity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)
    }

    override fun onResume() {
        super.onResume()
        job = lifecycleScope.launch(context = Dispatchers.Main) {
            delay(3000)
            goToHome()
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

    private fun goToHome() {
        NavHostFragment.findNavController(this).navigate(R.id.fragment_splash_to_home)
    }


}