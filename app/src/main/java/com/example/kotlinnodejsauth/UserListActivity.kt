package com.example.kotlinnodejsauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinnodejsauth.adapter.UserAdapter
import com.example.kotlinnodejsauth.retrofit.INodeJS
import com.example.kotlinnodejsauth.retrofit.RetrofitClient
import com.mancj.materialsearchbar.MaterialSearchBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    internal lateinit var myAPI:INodeJS
    internal var compositeDisposable = CompositeDisposable()
    internal lateinit var layoutManager: LinearLayoutManager
    internal lateinit var adapter: UserAdapter
    internal var suggestList:MutableList<String> = ArrayList()

    //Replace init API in onCreate
    private val api:INodeJS
    get() = RetrofitClient.instance.create(INodeJS::class.java)

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        //Init API
        myAPI = api

        //View
        recycler_search.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recycler_search.layoutManager = layoutManager
        recycler_search.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))

        search_bar.setCardViewElevation(10)
        addSuggestList() //
        search_bar.addTextChangeListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Add result found in searchbar to lastSuggestions
                val suggest = ArrayList<String>()
                for(search_term in suggestList)
                    if(search_term.toLowerCase().contentEquals(search_bar.text.toLowerCase()))
                        suggest.add(search_term)
                search_bar.lastSuggestions = suggest
            }

        })
        search_bar.setOnSearchActionListener(object:MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {
                if(buttonCode == MaterialSearchBar.BUTTON_BACK)
                    search_bar.disableSearch()
            }

            override fun onSearchStateChanged(enabled: Boolean) {
               if(!enabled)
                   getAllUsers()
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })


        //Default first load all users
        getAllUsers()

    }

    private fun getAllUsers() {
        compositeDisposable.addAll(myAPI.userList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({users ->
                //Log.v("USERS", users.toString());
                adapter = UserAdapter(baseContext,users)
                recycler_search.adapter = adapter
            }, {
                //Log.v("USERS", "not found");
                Toast.makeText(this@UserListActivity, "User not found", Toast.LENGTH_SHORT).show();
            }))
    }

    private fun startSearch(query:String){
        compositeDisposable.addAll(myAPI.searchPerson(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({users ->
                adapter = UserAdapter(baseContext,users)
                recycler_search.adapter = adapter
            }, {
                Toast.makeText(this@UserListActivity, "User not found", Toast.LENGTH_SHORT).show();
            }))
    }

    private fun addSuggestList() {
        //Here you can cache suggest list
        //But in this example I will add manually
        suggestList.add("Nicolas")
        suggestList.add("Pedro")
        suggestList.add("Maria")
        suggestList.add("Juan")

        search_bar.lastSuggestions = suggestList
    }
}
