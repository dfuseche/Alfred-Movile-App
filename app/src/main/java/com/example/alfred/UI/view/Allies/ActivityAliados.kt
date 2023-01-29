package com.example.alfred

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alfred.UI.view.Allies.CustomAdapterAllies
import com.example.alfred.UI.view.Home.ActivityHome
import com.example.alfred.UI.view.Home.PopUpNetworkActivity
import com.example.alfred.UI.viewmodel.ally.AllyViewModel
import com.example.alfred.data.model.Ally
import com.example.alfred.databinding.ActivityAliadosBinding
import com.example.alfred.databinding.ActivityHomeBinding
import com.example.alfred.utilities.UtilityInjector
import com.example.trapp.utilities.NetworkTracker
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class ActivityAliados : AppCompatActivity() {

    private lateinit var tempArray: ArrayList<Ally>
    var startTime: Long = 0
    var endTime: Long = 0
    private lateinit var binding: ActivityAliadosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTime = System.currentTimeMillis();
        binding = ActivityAliadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonClick2 = binding.devolverButton
        buttonClick2.setOnClickListener {
            val intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
        }
        lifecycleScope.launch{
            setupUI()}



    }
    override fun onDestroy() {
        endTime = System.currentTimeMillis()
        val timeSpend = endTime - startTime
        // Insert timeSpend in databse.
        Toast.makeText(baseContext, "Tiempo tomado en actividad$timeSpend" , Toast.LENGTH_SHORT).show()
//        Crashlytics.logException(new RuntimeException("Tiempo tomado en actividad$timeSpend"));
        super.onDestroy()
    }


    suspend fun setupUI() {
        val allyFactory = UtilityInjector.provideAllyViewModelFactory(applicationContext)
        val allyViewModel = ViewModelProviders.of(this, allyFactory)[AllyViewModel::class.java]



        var alliesList: List<Ally> = ArrayList()
        tempArray = arrayListOf<Ally>()


        allyViewModel.getAllies().observe(this, {
            allies ->
            alliesList = allies
        })

        tempArray.addAll(alliesList)



        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = CustomAdapterAllies(tempArray)
        val listaDeAliados = binding.aliadosList

        listaDeAliados.setHasFixedSize(true)
        listaDeAliados.adapter = adapter
        listaDeAliados.layoutManager = layoutManager
        val tracker= NetworkTracker.getInstance()
        adapter.setOnItemClickListener(object: CustomAdapterAllies.onItemClickListener{
            override fun onItemClick(position: Int) {
                if(!tracker.getInternet(applicationContext)){
                    setPopUp()
                }
                else{
                    showDetailAlly(tempArray[position].city?:"", tempArray[position].address?:"",tempArray[position].name?:"",tempArray[position].phoneNumber?:"", alliesList[position].id?:"")

                }
            }
        })

        //Favorites
        var showFav: Boolean = false
        var alliesFavList: List<Ally> = ArrayList()
        allyViewModel.getFavorites().observe(this, {
                services ->
            alliesFavList= services
        })

        val favButton = binding.showFav
        favButton.setOnClickListener{
            if(showFav == false)
            {
                tempArray.clear()
                tempArray.addAll(alliesFavList)

                adapter.notifyDataSetChanged()

                showFav = true
            }
            else if(showFav == true)
            {
                tempArray.clear()
                tempArray.addAll(alliesList)

                adapter.notifyDataSetChanged()

                showFav = false
            }

        }




        //Search Bar
        val filter = binding.alliesFilter

        filter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val searchText=p0!!.lowercase(Locale.getDefault())

                tempArray.clear()
                if(searchText.isNotEmpty()){
                    alliesList.forEach {
                        if(it.name?.lowercase(Locale.getDefault())?.contains(searchText)?:false ||it.address?.lowercase(Locale.getDefault())?.contains(searchText)?:false)
                        {
                            tempArray.add(it)
                        }
                    }

                    adapter.notifyDataSetChanged()


                }else{
                    tempArray.clear()
                    tempArray.addAll(alliesList)
                    adapter.notifyDataSetChanged()

                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                val searchText=p0!!.lowercase(Locale.getDefault())

                tempArray.clear()
                if(searchText.isNotEmpty()){
                    alliesList.forEach {
                       if(it.name?.lowercase(Locale.getDefault())?.contains(searchText)?:false ||it.address?.lowercase(Locale.getDefault())?.contains(searchText)?:false)
                       {
                           tempArray.add(it)
                       }
                    }

                    adapter.notifyDataSetChanged()


                }else{
                    tempArray.clear()
                    tempArray.addAll(alliesList)
                    adapter.notifyDataSetChanged()

                }

                return false
            }

        })

    }


    fun setPopUp()
    {
        val intent = Intent(this, PopUpNetworkActivity::class.java)
        startActivity(intent)
    }



    private fun showDetailAlly(city: String, address: String, name: String, cellphone: String, id: String)
    {
        val allyIntent = Intent(this, ActivityDetalleAliado::class.java).apply {
            putExtra("city", city)
            putExtra("address", address)
            putExtra("name", name)
            putExtra("cellphone", cellphone)
            putExtra("id", id)

        }
        startActivity(allyIntent)
    }


}