package com.susuryo.mymoviestar.presenter

import com.google.firebase.auth.FirebaseAuth
import com.susuryo.mymoviestar.contract.SettingContract

class SettingPresenter(val view: SettingContract.View): SettingContract.Presenter {
    override fun doSignOut() {
        FirebaseAuth.getInstance().signOut()
    }
}