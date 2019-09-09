package com.memoryDiary.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.android.stats.StatsTextView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memoryDiary.Activity.Memory.AddMemoryActivity
//import com.memoryDiary.Activity.Start.LoginActivity
import com.memoryDiary.Adapter.DiaryAdapter
import com.memoryDiary.Entity.Diary
import com.memoryDiary.Entity.Memory
import com.memoryDiary.Holder.DiaryDataHolder
import com.memoryDiary.Holder.MemoryDataHolder
import com.memoryDiary.Holder.UserDataHolder
import com.memoryDiary.InstantSearch.MyViewModel
import com.memoryDiary.R
import kotlinx.android.synthetic.main.fragment_memory.*

class MemoryFragment : Fragment() {
    private var mView: View? = null
    private var fabMenu: FloatingActionMenu? = null
    private var fabSearch: FloatingActionButton? = null
    private var fabAdd: FloatingActionButton? = null
    private var fabSettings: FloatingActionButton? = null
//    private var fabLogout: FloatingActionButton? = null
    private var memoryRecyclerView: RecyclerView? = null
    private var memories: Diary? = null
    private var diaryAdapter: DiaryAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var mData: DatabaseReference? = null
    private val connection = ConnectionHandler()
    //private var viewModel: MyViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.mView = inflater.inflate(R.layout.fragment_memory, container, false)
        initFields()
        initFireBase()
        return this.mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchBoxView = SearchBoxViewAppCompat(searchView)
        val statsView = StatsTextView(stats)
        val viewModel = ViewModelProviders.of(requireActivity())[MyViewModel::class.java]

        //val diaryAdapter = DiaryAdapter(this, memories)

        viewModel!!.memories.observe(this, Observer { hits ->
            run {
                viewModel.adapterMemory.submitList(hits)
                memories!!.clearMemories()
                for (mem in hits){
                    memories!!.addMemory(mem)
                }
            }
        })

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        memory_recyclerView.let {
            it.itemAnimator = null
            it.adapter = viewModel.adapterMemory
            it.layoutManager = GridLayoutManager(activity, 4)
            it.autoScrollToStart(viewModel.adapterMemory)
        }
        //filters.setOnClickListener { (requireActivity() as GettingStartedGuide).showFacetFragment() }
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private fun initFields() {
        this.memoryRecyclerView = this.mView!!.findViewById(R.id.memory_recyclerView)
        // sets layout manager for RecyclerView, to be able to draw the layout properly.
//        val manager = LinearLayoutManager(this.activity)
//        this.memoryRecyclerView!!.layoutManager = manager
//        this.memoryRecyclerView!!.setHasFixedSize(true)
//        this.memoryRecyclerView!!.layoutManager = GridLayoutManager(activity, 4)
        this.memories = Diary()
        this.diaryAdapter = DiaryAdapter(activity, memories)
        //this.memoryRecyclerView!!.adapter = diaryAdapter
        this.fabMenu = this.mView!!.findViewById(R.id.add_memory_fab_menu)
        this.fabSearch = this.mView!!.findViewById(R.id.add_memory_fab_search)
        this.fabAdd = this.mView!!.findViewById(R.id.add_memory_fab_add)
        this.fabSettings = this.mView!!.findViewById(R.id.add_memory_fab_settings)
//        this.fabLogout = this.mView!!.findViewById(R.id.add_memory_fab_logout)

        this.fabMenu!!.bringToFront()
        //color when not pressed
        this.fabAdd!!.colorNormal = resources.getColor(R.color.babyBlue)
        this.fabSearch!!.colorNormal = resources.getColor(R.color.babyBlue)
        this.fabSettings!!.colorNormal = resources.getColor(R.color.babyBlue)
//        this.fabLogout!!.colorNormal = resources.getColor(R.color.babyBlue)

        //color when pressed
        this.fabAdd!!.colorPressed = resources.getColor(R.color.maroon)
        this.fabSearch!!.colorPressed = resources.getColor(R.color.maroon)
        this.fabSettings!!.colorPressed = resources.getColor(R.color.maroon)
//        this.fabLogout!!.colorPressed = resources.getColor(R.color.maroon)

        this.fabSearch!!.setOnClickListener { searchActivity() }

        this.fabAdd!!.setOnClickListener { addMemoryActivity() }

        this.fabAdd!!.setOnClickListener { settingsActivity() }

//        this.fabLogout!!.setOnClickListener {
//            AlertDialog.Builder(activity!!)
//                    .setTitle("Logout")
//                    .setMessage("Sure you want to logout?")
//                    .setPositiveButton("Yes") { dialog, which -> logoutActivity() }
//                    .setNegativeButton("No") { dialog, which ->
//                        // user doesn't want to logout
//                    }
//                    .show()
//        }
    }

    private fun initFireBase() {
        this.mAuth = FirebaseAuth.getInstance()
        this.mData = FirebaseDatabase.getInstance().reference
    }

    /**
     * Initializing a recycle view.
     */
    fun initRecyclerView() {
        this.mData!!.child("Diary").child(this.mAuth!!.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {

            /**
             * It is triggered once when the listener is attached,
             * and again every time the data, including children, changes.
             * @param dataSnapshot the data.
             */
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChildren())
                    return
                memories!!.clearMemories()
                for (data in dataSnapshot.children) {
                    val memo = data.getValue(Memory::class.java)
                    memories!!.addMemory(memo)
                }
                diaryAdapter!!.notifyDataSetChanged()
            }

            /**
             * when an error occurs.
             * @param databaseError the errors.
             */
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    /**
     * When clicked on the add FAB will open the add new memory activity.
     */
    private fun addMemoryActivity() {
        val intent = Intent(this.activity, AddMemoryActivity::class.java)
        startActivity(intent)
    }

        /**
         * Connects to the settings activity and starts it.
         */
        private fun settingsActivity() {
            Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this.activity, SettingsActivity::class.java)
//            startActivity(intent)
        }

    /**
     * Connects to login activity and starts it.
     * An intent - basically a message to say you did or want something to happen.
     */
//    private fun logoutActivity() {
//        clearDataHolderes()
//        this.mAuth!!.signOut()
//        val intent = Intent(this.activity, LoginActivity::class.java)
//        startActivity(intent)
//        activity!!.finish()
//    }

    private fun searchActivity() {
        Toast.makeText(activity, "search", Toast.LENGTH_SHORT).show()

    }

    // for logout
    private fun clearDataHolderes() {
        UserDataHolder.getUserDataHolder().clearUser()
        DiaryDataHolder.getDiaryDataHolder().clearDiary()
        MemoryDataHolder.getMemoryDataHolder().clearMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.connection.disconnect()
    }

    companion object {
        fun newInstance(): MemoryFragment {
            return MemoryFragment()
        }
    }


}