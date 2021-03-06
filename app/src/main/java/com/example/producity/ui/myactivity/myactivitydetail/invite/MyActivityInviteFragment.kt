package com.example.producity.ui.myactivity.myactivitydetail.invite

import android.app.Dialog
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.producity.R
import com.example.producity.ServiceLocator
import com.example.producity.SharedViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModel
import com.example.producity.ui.friends.my_friends.FriendListViewModelFactory
import com.example.producity.ui.myactivity.myactivitydetail.MyActivityDetailViewModel
import com.example.producity.ui.myactivity.myactivitydetail.MyActivityDetailViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyActivityInviteFragment : BottomSheetDialogFragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val activityDetailViewModel: MyActivityDetailViewModel by activityViewModels() {
        MyActivityDetailViewModelFactory(
            ServiceLocator.provideParticipantRepository(),
            ServiceLocator.provideActivityRepository()
        )
    }

    private val friendListViewModel: FriendListViewModel by activityViewModels {
        FriendListViewModelFactory(
            ServiceLocator.provideUserRepository(),
            ServiceLocator.provideActivityRepository()
        )
    }

    override fun getTheme(): Int {
        return R.style.SheetDialog
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.activity_detail_invite, null)

        val recyclerView = contentView.findViewById<RecyclerView>(R.id.friend_recycle_view)

        val currentUser = sharedViewModel.currentUser.value

        val adapter = MyActivityInviteAdapter(this, currentUser!!, activityDetailViewModel)
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val currentUsername = sharedViewModel.currentUser.value!!.username
        friendListViewModel.getAllFriends(currentUsername).observe(this) {
            adapter.submitList(it.filter { x ->
                Log.d("Main", activityDetailViewModel.currentActivity!!.participant.toString())
                !activityDetailViewModel.currentActivity!!.participant.contains(x.username)
            })
        }


        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

    }
}